package org.devocative.devolcano.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.devocative.demeter.core.ModuleLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "schemadiff", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class SchemaDiffMavenPlugin extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
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
		} catch (Exception e) {
			throw new MojoExecutionException("Schema", e);
		}

		ModuleLoader.generatePersistorSchemaDiff();
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
