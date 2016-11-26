package org.devocative.devolcano;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(
	name = "erupt",
	requiresDependencyResolution = ResolutionScope.RUNTIME,
	defaultPhase = LifecyclePhase.TEST
)
public class DevolcanoMavenPlugin extends AbstractMojo {
	private static final Logger logger = LoggerFactory.getLogger(DevolcanoMavenPlugin.class);

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter
	private boolean useTestClasspath = false;

	// ------------------------------

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File baseDir = project.getBasedir();

		try {
			List<URL> urls = new ArrayList<>();

			if (project.hasParent()) {
				MavenProject parent = project.getParent();
				baseDir = parent.getBasedir();

				logger.info("Maven Modules: {}", parent.getModules());
				for (String mod : parent.getModules()) {
					File f = new File(String.format("%s/%s/target/classes/", baseDir.getAbsolutePath(), mod));
					if (f.exists()) {
						add(urls, f.getAbsolutePath());
					}
				}

				logger.info("\n\nMaven Parent: {}", parent);
				addAll(urls, parent.getRuntimeClasspathElements());
				addAll(urls, parent.getCompileClasspathElements());

				if (useTestClasspath) {
					addAll(urls, parent.getTestClasspathElements());
				}
			}

			addAll(urls, project.getRuntimeClasspathElements());
			addAll(urls, project.getCompileClasspathElements());

			if (useTestClasspath) {
				addAll(urls, project.getTestClasspathElements());
			}

			ClassLoader contextClassLoader = URLClassLoader.newInstance(
				urls.toArray(new URL[urls.size()]),
				Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			CodeEruption.init(baseDir);
			CodeEruption.erupt();
		} catch (Exception e) {
			throw new MojoExecutionException("Generation", e);
		}
	}

	// ------------------------------

	private void addAll(List<URL> urls, List<String> elements) throws MalformedURLException {
		logger.info("ClassLoader: {}", elements);

		for (String element : elements) {
			URL url = new File(element).toURI().toURL();
			if (!urls.contains(url)) {
				urls.add(url);
			}
		}
	}

	private void add(List<URL> urls, String element) throws MalformedURLException {
		logger.info("ClassLoader: {}", element);

		URL url = new File(element).toURI().toURL();
		if (!urls.contains(url)) {
			urls.add(url);
		}
	}
}
