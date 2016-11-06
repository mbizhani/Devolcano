package org.devocative.devolcano;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(
	name = "generate",
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
	defaultPhase = LifecyclePhase.TEST
)
public class DevolcanoMavenPlugin extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter
	private boolean useTestClasspath = false;

	// ------------------------------

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Set<URL> urls = new HashSet<>();

			addAll(urls, project.getRuntimeClasspathElements());
			addAll(urls, project.getCompileClasspathElements());

			if (useTestClasspath) {
				addAll(urls, project.getTestClasspathElements());
			}

			ClassLoader contextClassLoader = URLClassLoader.newInstance(
				urls.toArray(new URL[urls.size()]),
				Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			CodeEruption.init();
			CodeEruption.erupt();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Generation", e);
		}
	}

	// ------------------------------

	private void addAll(Set<URL> urls, List<String> elements) throws MalformedURLException {
		for (String element : elements) {
			urls.add(new File(element).toURI().toURL());
		}
	}
}
