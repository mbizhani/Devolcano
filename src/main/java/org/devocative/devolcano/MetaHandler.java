package org.devocative.devolcano;

import com.thoughtworks.xstream.XStream;
import org.devocative.devolcano.vo.ClassVO;
import org.devocative.devolcano.vo.FieldVO;
import org.devocative.devolcano.xml.metadata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class MetaHandler {
	private static final Logger logger = LoggerFactory.getLogger(MetaHandler.class);
	private static final String META_FILE = "dlaval/Metadata.xml";
	private static XMeta X_META;

	private static ClassLoader CLASS_LOADER;

	public static void init() {
		File file = new File(META_FILE);

		if (file.exists()) {
			logger.info("Metadata file: {}", file.getAbsolutePath());

			XStream xStream = getXStream();
			X_META = (XMeta) xStream.fromXML(file);
		} else {
			logger.info("Metadata file not exist!");

			X_META = new XMeta();
			X_META.setClasses(new ArrayList<XMetaClass>());
			X_META.setScans(new ArrayList<XMetaPackage>());
		}

		CLASS_LOADER = Thread.currentThread().getContextClassLoader();
	}

	public static void scan() {
		logger.info("Start Scanning ...");

		for (XMetaPackage xMetaPackage : X_META.getScans()) {
			List<Class> classes = processPackage(xMetaPackage.getFqn(), true);
			for (Class aClass : classes) {
				if (aClass.isAnnotationPresent(Entity.class)) { //TODO
					logger.info("\tscanning class: {}", aClass.getName());
					XMetaClass xMetaClass = X_META.findXMetaClass(aClass.getName());

					if (xMetaClass == null) {
						xMetaClass = new XMetaClass();
						xMetaClass.setFqn(aClass.getName());
						xMetaClass.setFields(new ArrayList<XMetaField>());
						X_META.getClasses().add(xMetaClass);
					}

					if (xMetaClass.getInfo() == null) {
						xMetaClass.setInfo(new XMetaInfoClass());
					}

					List<XMetaField> idFields = scanFields(aClass, xMetaClass);

					if (xMetaClass.getId() == null) {
						xMetaClass.setId(new XMetaId());
					}

					xMetaClass.getId().setRef(toCSV(idFields));
				}
			}
		}

		logger.info("Scanning Finished!");
	}

	public static void write() {
		File file = new File(META_FILE);
		file.getParentFile().mkdirs();
		logger.info("Writing to Metadata.xml: {}", file.getAbsolutePath());

		XStream xStream = getXStream();
		try {
			xStream.toXML(X_META, new FileWriter(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Class> processPackage(String packageName, Boolean includeSubPackages) {
		try {
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = CLASS_LOADER.getResources(path);
			List<File> dirs = new ArrayList<>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			List<Class> classes = new ArrayList<>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName, includeSubPackages));
			}
			return classes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ------------------------------

	private static List<XMetaField> scanFields(Class aClass, XMetaClass xMetaClass) {
		List<XMetaField> idFields = new ArrayList<>();
		ClassVO classVO = new ClassVO(aClass);
		for (FieldVO fieldVO : classVO.getDeclaredFieldsMap().values()) {
			XMetaField xMetaField = xMetaClass.findXMetaField(fieldVO.getName());
			if (xMetaField == null) {
				xMetaField = new XMetaField();
				xMetaField.setName(fieldVO.getName());
				xMetaClass.getFields().add(xMetaField);
			}

			if (xMetaField.getInfo() == null) {
				xMetaField.setInfo(new XMetaInfoField());
			}

			if (fieldVO.isId()) {
				idFields.add(xMetaField);
			}
		}
		return idFields;
	}

	private static List<Class> findClasses(File directory, String packageName, Boolean includeSubPackages) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}

		if (!directory.isDirectory())
			throw new RuntimeException(String.format("%s not a directory!", directory.getName()));

		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && includeSubPackages) {
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName(), true));
				} else if (file.getName().endsWith(".class")) {
					String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
					Class<?> cls = Class.forName(className, true, CLASS_LOADER);
					classes.add(cls);
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

	private static String toCSV(Collection col) {
		StringBuilder builder = new StringBuilder();
		for (Object o : col) {
			builder
				.append(o.toString())
				.append(",");
		}
		String s = builder.toString();
		return s.substring(0, s.length() - 1);
	}
}
