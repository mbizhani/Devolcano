package org.devocative.devolcano;

import com.thoughtworks.xstream.XStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FileUtils;
import org.devocative.devolcano.vo.ClassVO;
import org.devocative.devolcano.vo.FieldVO;
import org.devocative.devolcano.xml.metadata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MetaHandler {
	private static final Logger logger = LoggerFactory.getLogger(MetaHandler.class);
	private static final String META_FILE_STR = "/dlava/Metadata.xml";

	//TODO find a better way!
	private static final List<String> IGNORED_FIELDS = Arrays.asList("creatorUserId", "modifierUserId");
	private static final List<String> READ_ONLY_FIELDS = Arrays.asList(
		"rowMode", "creatorUser", "creationDate",
		"modifierUser", "modificationDate");
	private static final List<String> LIST_ONLY_FIELDS = Arrays.asList("version");
	private static final XStream X_STREAM;

	private static File META_FILE;
	private static XMeta X_META;

	private static ClassLoader CLASS_LOADER;

	private static final GroovyShell GROOVY_SHELL = new GroovyShell();
	private static Script FILTER_CLASS_CHECK;

	// ------------------------------

	static {
		X_STREAM = new XStream();
		X_STREAM.processAnnotations(XMeta.class);
	}

	// ------------------------------

	public static void init(String baseDir) throws Exception {
		logger.info("MetaHandler: Base Dir = {}", baseDir);

		META_FILE = new File(baseDir + META_FILE_STR);

		if (META_FILE.exists()) {
			logger.info("Metadata file: {}", META_FILE.getCanonicalPath());
			X_META = (XMeta) X_STREAM.fromXML(META_FILE);
		} else {
			logger.info("Metadata file not exist!");

			X_META = new XMeta();
			X_META.setClasses(new ArrayList<>());
		}

		CLASS_LOADER = Thread.currentThread().getContextClassLoader();
	}

	public static Map<XMetaClass, List<XMetaField>> scan(String pkg, boolean includeSubPackages) {
		logger.info("Start Scanning Package: {}", pkg);

		Map<XMetaClass, List<XMetaField>> result = new HashMap<>();

		Collection<Class> classes = processPackage(pkg, includeSubPackages);
		for (Class aClass : classes) {
			ClassVO classVO = new ClassVO(aClass);

			logger.info("\tscanning class: {}", aClass.getName());
			XMetaClass xMetaClass = X_META.findXMetaClass(aClass.getName());

			if (xMetaClass == null) {
				xMetaClass = new XMetaClass();
				xMetaClass.setFqn(aClass.getName());
				xMetaClass.setFields(new ArrayList<>());
				X_META.getClasses().add(xMetaClass);

				result.put(xMetaClass, null); // New Class Added!
			}

			if (xMetaClass.getInfo() == null) {
				xMetaClass.setInfo(new XMetaInfoClass());
			}

			List<XMetaField> idFields = new ArrayList<>();
			List<XMetaField> newXMetaFields = scanFields(classVO, xMetaClass, idFields);

			if (!newXMetaFields.isEmpty() && !result.containsKey(xMetaClass)) {
				result.put(xMetaClass, newXMetaFields);
			}

			if (xMetaClass.getId() == null) {
				xMetaClass.setId(new XMetaId());
			}

			xMetaClass.getId().setRef(toCSV(idFields));
		}

		logger.info("Finished Scanning Package: {}", pkg);

		return result;
	}

	public static void write() throws IOException {
		META_FILE.getParentFile().mkdirs();
		logger.info("Writing to Metadata.xml: {}", META_FILE.getCanonicalPath());

		try {
			String xml = X_STREAM.toXML(X_META);
			xml = xml.replaceAll("  ", "\t");

			FileWriter writer = new FileWriter(META_FILE);
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
			writer.write("<!DOCTYPE meta PUBLIC\n");
			writer.write("\t\t\"Meta Data\"\n");
			writer.write("\t\t\"http://www.devocative.org/dtd/devolcano-metadata.dtd\">\n\n");
			writer.write(xml);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static XMetaClass findXMetaClass(String fqn) {
		return X_META.findXMetaClass(fqn);
	}

	public static Set<Class> processPackage(String packageName, Boolean includeSubPackages) {
		if (X_META.getFilterClass() != null) {
			FILTER_CLASS_CHECK = GROOVY_SHELL.parse(X_META.getFilterClass());
		}

		try {
			String path = packageName.replace('.', '/');
			return findClasses(path, includeSubPackages);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toCSV(Collection col) {
		if (col.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (Object o : col) {
				builder
					.append(o.toString())
					.append(",");
			}
			String s = builder.toString();
			return s.substring(0, s.length() - 1);
		}

		return "";
	}

	// ------------------------------

	private static List<XMetaField> scanFields(ClassVO classVO, XMetaClass xMetaClass, List<XMetaField> idFields) {
		List<XMetaField> result = new ArrayList<>();

		for (FieldVO fieldVO : classVO.getDeclaredFieldsMap().values()) {
			if (!fieldVO.isStatic() && !fieldVO.isFinal()) {
				XMetaField xMetaField = xMetaClass.findXMetaField(fieldVO.getName());
				if (xMetaField == null) {
					xMetaField = new XMetaField();
					xMetaField.setName(fieldVO.getName());

					if (IGNORED_FIELDS.contains(fieldVO.getName()) || fieldVO.isStatic()) {
						xMetaField.getInfo().setIgnore(true);
					}

					if (READ_ONLY_FIELDS.contains(fieldVO.getName()) || LIST_ONLY_FIELDS.contains(fieldVO.getName())) {
						xMetaField.getInfo().setHasForm(false);
					}

					if (LIST_ONLY_FIELDS.contains(fieldVO.getName())) {
						xMetaField.getInfo().setHasFVO(false);
					}

					if (fieldVO.isOf(Date.class)) {
						xMetaField.getInfo().setHasTimePart(true);
					}

					xMetaClass.getFields().add(xMetaField);
					result.add(xMetaField);
				}

				if (xMetaField.getInfo() == null) {
					xMetaField.setInfo(new XMetaInfoField());
				}

				if (fieldVO.isId()) {
					idFields.add(xMetaField);
				}
			}
		}

		return result;
	}

	private static Set<Class> findClasses(String dirStr, boolean recursive) throws Exception {
		String[] extensions = new String[]{"class"};

		List<String> list = new ArrayList<>();
		Enumeration<URL> resources = CLASS_LOADER.getResources(dirStr);
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			switch (url.getProtocol()) {
				case "file":
					File dir = new File(url.toURI());
					if (dir.isDirectory()) {
						logger.info("\tFind Classes Under Directory: {}", dir.getAbsolutePath());
						Collection<File> files = FileUtils.listFiles(new File(url.getPath()), extensions, recursive);
						for (File f : files) {
							String path = f.getAbsolutePath().replace('\\', '/');
							int i = path.indexOf(dirStr);
							list.add(path.substring(i));
						}
					} else {
						throw new RuntimeException("Invalid Directory: " + dir);
					}
					break;

				case "jar":
					String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
					logger.info("\tFind Classes in JAR = {}", jarPath);

					try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry jarEntry = entries.nextElement();
							String name = jarEntry.getName();
							if (!jarEntry.isDirectory() && name.startsWith(dirStr) && hasExtension(name, extensions)) {
								if (recursive) {
									list.add(name);
								} else if (!name.substring(dirStr.length() + 1).contains("/")) {
									list.add(name);
								}
							}
						}
					}
					break;

				default:
					throw new RuntimeException("Unsupported Protocol: " + url.getProtocol());
			}
		}

		Set<Class> result = new HashSet<>();
		for (String s : list) {
			Class<?> cls = Class.forName(s.substring(0, s.length() - 6).replace('/', '.'), true, CLASS_LOADER);
			if (FILTER_CLASS_CHECK != null) {
				ClassVO classVO = new ClassVO(cls);
				Binding binding = new Binding();
				binding.setVariable("targetClass", classVO);
				FILTER_CLASS_CHECK.setBinding(binding);
				Boolean isValid = (Boolean) FILTER_CLASS_CHECK.run();
				if (isValid) {
					result.add(cls);
				}
			} else {
				result.add(cls);
			}
		}
		return result;
	}

	private static boolean hasExtension(String dir, String[] exts) {
		if (exts != null) {
			for (String ext : exts) {
				if (dir.endsWith("." + ext)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
}
