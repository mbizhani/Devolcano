package org.devocative.devolcano.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.devocative.devolcano.CodeEruption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "codegen", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class DevolcanoMavenPlugin extends AbstractMojo {
	private Logger logger = LoggerFactory.getLogger(DevolcanoMavenPlugin.class);

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	// ------------------------------

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			List<URL> urls = new ArrayList<>();

			addAll(urls, project.getRuntimeClasspathElements());
			addAll(urls, project.getCompileClasspathElements());
			addAll(urls, project.getTestClasspathElements());

			ClassLoader contextClassLoader = URLClassLoader.newInstance(
				urls.toArray(new URL[urls.size()]),
				Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			CodeEruption.init(project.getBasedir());
			CodeEruption.erupt();
		} catch (Exception e) {
			logger.error("DevolcanoMavenPlugin: ", e);
			throw new MojoExecutionException("Generation", e);
		}
	}

	// ------------------------------

	private void addAll(List<URL> urls, List<String> elements) throws MalformedURLException {
		for (String element : elements) {
			URL url = new File(element).toURI().toURL();
			if (!urls.contains(url)) {
				urls.add(url);
			}
		}
	}
}
