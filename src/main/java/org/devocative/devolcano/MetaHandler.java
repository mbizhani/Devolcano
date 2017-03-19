package org.devocative.devolcano;

import com.thoughtworks.xstream.XStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.devocative.devolcano.vo.ClassVO;
import org.devocative.devolcano.vo.FieldVO;
import org.devocative.devolcano.xml.metadata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MetaHandler {
	private static final Logger logger = LoggerFactory.getLogger(MetaHandler.class);
	private static final String META_FILE_STR = "/dlava/Metadata.xml";

	//TODO find a better way!
	private static final List<String> IGNORED_FIELDS = Arrays.asList("creatorUserId", "modifierUserId");
	private static final List<String> READ_ONLY_FIELDS = Arrays.asList(
		"rowMod", "creatorUser", "creationDate",
		"modifierUser", "modificationDate");
	private static final List<String> LIST_ONLY_FIELDS = Arrays.asList("version");

	private static File META_FILE;
	private static XMeta X_META;

	private static ClassLoader CLASS_LOADER;

	private static final GroovyShell GROOVY_SHELL = new GroovyShell();
	private static Script FILTER_CLASS_CHECK;

	// ------------------------------

	public static void init(String baseDir) throws Exception {
		logger.info("MetaHandler: Base Dir = {}", baseDir);

		META_FILE = new File(baseDir + META_FILE_STR);

		if (META_FILE.exists()) {
			logger.info("Metadata file: {}", META_FILE.getCanonicalPath());

			XStream xStream = getXStream();
			X_META = (XMeta) xStream.fromXML(META_FILE);
		} else {
			logger.info("Metadata file not exist!");

			X_META = new XMeta();
			X_META.setClasses(new ArrayList<XMetaClass>());
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
				xMetaClass.setFields(new ArrayList<XMetaField>());
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

		XStream xStream = getXStream();
		try {
			String xml = xStream.toXML(X_META);
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
			Enumeration<URL> resources = CLASS_LOADER.getResources(path);
			List<File> dirs = new ArrayList<>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			Set<Class> classes = new LinkedHashSet<>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName, includeSubPackages));
			}
			return classes;
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

	private static Set<Class> findClasses(File directory, String packageName, Boolean includeSubPackages) throws ClassNotFoundException {
		Set<Class> classes = new LinkedHashSet<>();
		if (!directory.exists()) {
			return classes;
		}

		if (!directory.isDirectory()) {
			throw new RuntimeException(String.format("%s not a directory!", directory.getName()));
		}

		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && includeSubPackages) {
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName(), true));
				} else if (file.getName().endsWith(".class")) {
					String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
					Class<?> cls = Class.forName(className, true, CLASS_LOADER);

					if (FILTER_CLASS_CHECK != null) {
						ClassVO classVO = new ClassVO(cls);
						Binding binding = new Binding();
						binding.setVariable("targetClass", classVO);
						FILTER_CLASS_CHECK.setBinding(binding);
						Boolean isValid = (Boolean) FILTER_CLASS_CHECK.run();
						if (isValid) {
							classes.add(cls);
						}
					} else {
						classes.add(cls);
					}
				}
			}
		}
		return classes;
	}

	private static XStream getXStream() {
		XStream xStream = new XStream();
		xStream.processAnnotations(XMeta.class);
		return xStream;
	}
}
