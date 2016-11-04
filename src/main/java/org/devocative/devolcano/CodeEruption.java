package org.devocative.devolcano;

import com.thoughtworks.xstream.XStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.text.SimpleTemplateEngine;
import org.devocative.devolcano.vo.ClassVO;
import org.devocative.devolcano.xml.metadata.XMetaClass;
import org.devocative.devolcano.xml.metadata.XMetaField;
import org.devocative.devolcano.xml.plan.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeEruption {
	private static final Logger logger = LoggerFactory.getLogger(CodeEruption.class);
	private static final GroovyShell GROOVY_SHELL = new GroovyShell();
	private static final String PLAN_FILE = "dlaval/Plan.xml";

	private static XPlan X_PLAN;

	private static File BASE_DIR;
	private static ContextVO CONTEXT;
	private static ClassLoader CLASS_LOADER;

	// ------------------------------

	public static void init() throws Exception {
		File file = new File(PLAN_FILE);

		if (!file.exists()) {
			throw new RuntimeException("Plan file not exist: " + PLAN_FILE);
		}

		logger.info("Plan file: {}", file.getAbsolutePath());

		XStream xstream = new XStream();
		xstream.processAnnotations(XPlan.class);
		X_PLAN = (XPlan) xstream.fromXML(file);
		CONTEXT = new ContextVO(X_PLAN);

		BASE_DIR = file.getAbsoluteFile().getParentFile().getParentFile().getCanonicalFile();
		logger.info("Project Base Directory: {}", BASE_DIR.getAbsolutePath());

		CLASS_LOADER = Thread.currentThread().getContextClassLoader();

		Map<XMetaClass, List<XMetaField>> changes = new HashMap<>();
		MetaHandler.init();
		if (X_PLAN.getPackageMap().size() > 0) {
			for (XPackageFrom packageFrom : X_PLAN.getPackageMap()) {
				if (!packageFrom.getIgnore()) { //TODO check pattern!
					changes.putAll(MetaHandler.scan(packageFrom.getPkg(), packageFrom.getIncludeSubPackages()));
				}
			}
		}
		MetaHandler.write();

		if (!changes.isEmpty()) {
			logger.info("###################");
			logger.info("### NEW CHANGES ###");

			for (Map.Entry<XMetaClass, List<XMetaField>> entry : changes.entrySet()) {
				if (entry.getValue() == null) {
					logger.info("New Class: {}", entry.getKey().getFqn());
				} else {
					logger.info("New Fields: Class={}, Fields: {}",
						entry.getKey().getFqn(), MetaHandler.toCSV(entry.getValue()));
				}
			}

			throw new RuntimeException("Pay Attention To Changes!");
		}
	}

	public static void erupt() throws Exception {
		if (X_PLAN.getPackageMap().size() > 0) {
			for (XPackageFrom packageFrom : X_PLAN.getPackageMap()) {
				if (packageFrom.getIgnore()) {
					logger.warn("/!\\From[{}]", packageFrom.getPkg());
				} else {
					generatePackageFrom(packageFrom);
				}
			}
		} else {
			logger.warn("No package map!");
		}
	}

	public static Boolean checkPrecondition(XVolcano generator, ClassVO classVO) {
		String meta =
			"String.metaClass.toCapital() {\n" +
				"substring(0,1).toUpperCase() + substring(1)\n" +
				"}\n" +
				"String.metaClass.toUncapital() {\n" +
				"substring(0,1).toLowerCase() + substring(1)\n" +
				"}";

		Script preconditionScript = GROOVY_SHELL.parse(meta + generator.getPrecondition());
		Binding binding = new Binding();
		binding.setVariable("targetClass", classVO);
		preconditionScript.setBinding(binding);
		return (Boolean) preconditionScript.run();
	}

	// ------------------------------

	private static void generatePackageFrom(XPackageFrom packageFrom) throws Exception {
		List<Class> classes = MetaHandler.processPackage(packageFrom.getPkg(), packageFrom.getIncludeSubPackages());
		if (classes.size() > 0) {
			for (Class cls : classes) {
				String name = cls.getName();
				if ((packageFrom.getIncludePattern() == null || name.contains(packageFrom.getIncludePattern())) &&
					(packageFrom.getExcludePattern() == null || !name.contains(packageFrom.getExcludePattern()))) {
					logger.info("[{}]", name);
					for (XPackageTo packageTo : packageFrom.getTos()) {
						if (packageTo.getIgnore())
							logger.warn("\t[{}] Ignored!", name, packageTo.getGeneratorRef());
						else {
							//logger.info("____To[{}]", packageTo.getPkgReplace());
							generateClass(cls, packageFrom, packageTo);
						}
					}
				} else
					logger.warn("[{}] Ignored!", name);
			}
		} else
			logger.warn("No class found in {} with sub={}", packageFrom.getPkg(), packageFrom.getIncludeSubPackages());
	}

	private static void generateClass(Class cls, XPackageFrom packageFrom, XPackageTo packageTo) throws Exception {
		XVolcano generator = CONTEXT.getGeneratorMap().get(packageTo.getGeneratorRef());
		XTemplate template = generator.getTemplate();

		ClassVO classVO = new ClassVO(cls);

		Boolean preCond = checkPrecondition(generator, classVO);
		boolean doGenFile = false;

		GenTargetVO genTarget = CONTEXT.getGenTarget(cls, packageFrom, packageTo);
		String dest4log = genTarget.getFqnDir() + "." + template.getGenFileType();

		if (preCond) {
			String dest = BASE_DIR.getAbsolutePath()
				+ "/"
				+ packageTo.getGenDir()
				+ "/"
				+ genTarget.getFqnDir()
				+ "."
				+ template.getGenFileType();

			File destFile = new File(dest);

			doGenFile = !destFile.exists() || "force".equals(template.getOverwrite());

			if ("check".equals(template.getOverwrite()) && destFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(destFile));
				String firstLine = reader.readLine();
				doGenFile = firstLine.contains(template.getOverwriteCheckString());
				reader.close();
			}

			//logger.info("________[{}] (doGen: {})", cls.getName(), doGenFile);
			if (doGenFile) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("targetClass", classVO);
				params.put("targetVO", genTarget);
				params.put("context", CONTEXT);
				params.put("importHelper", new ImportHelper());

				URL templateURL;
				if (template.getFile().startsWith("/"))
					templateURL = CodeEruption.class.getResource(template.getFile());
				else
					templateURL = new File(String.format("%s/%s", BASE_DIR.getAbsolutePath(), template.getFile())).toURI().toURL();

				SimpleTemplateEngine ste = new SimpleTemplateEngine(CLASS_LOADER);
				groovy.text.Template gTemplate = ste.createTemplate(templateURL);
				String genContent = gTemplate.make(params).toString();

				ImportHelper importHelper = (ImportHelper) params.get("importHelper");
				String importsStr = importHelper.generateImports(genTarget.getPkg()).trim();

				if (!importsStr.equals(""))
					genContent = genContent.replace("@IMPORT@", importsStr);
				else
					genContent = genContent.replace("\n@IMPORT@\n", "");

				destFile.getParentFile().mkdirs();
				FileWriter writer = new FileWriter(destFile, false);
				if ("check".equals(template.getOverwrite()))
					writer.append(template.getOverwriteCheckString()).append("\n");
				writer.write(genContent.trim());
				writer.close();
				//logger.info("++++++++Generated: [{}]", destFile.getCanonicalPath());
			}
		}
		logger.info("\t[{}] (pre:{}, gen:{})", packageTo.getGeneratorRef(), preCond, doGenFile);
		if (doGenFile)
			logger.info("\t+ {}", dest4log);
		else if (preCond)
			logger.info("\t! {}", dest4log);
		else
			logger.info("\t- {}", dest4log);
	}
}
