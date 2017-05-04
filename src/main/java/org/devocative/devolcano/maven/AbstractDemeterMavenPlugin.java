package org.devocative.devolcano.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDemeterMavenPlugin extends AbstractMojo {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractDemeterMavenPlugin.class);

	@Parameter(defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	public abstract void doExecute() throws MojoExecutionException, MojoFailureException;

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		updateClassLoader();
		doExecute();
	}

	protected void updateClassLoader() throws MojoExecutionException {
		List<URL> urls = new ArrayList<>();

		try {
			addAll(urls, project.getRuntimeClasspathElements());
			addAll(urls, project.getCompileClasspathElements());
			addAll(urls, project.getTestClasspathElements());

			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			for (URL url : urls) {
				method.invoke(urlClassLoader, new Object[]{url});
			}
			logger.info("AbstractDemeterMavenPlugin.updateClassLoader: size of URLS = {}", urls.size());
		} catch (Exception e) {
			throw new MojoExecutionException("Schema", e);
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
