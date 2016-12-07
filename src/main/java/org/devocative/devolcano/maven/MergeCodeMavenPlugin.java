package org.devocative.devolcano.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.devocative.devolcano.MergeCode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mojo(name = "mergecode", requiresDependencyResolution = ResolutionScope.TEST)
public class MergeCodeMavenPlugin extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			exec(MergeCode.class);
		} catch (Exception e) {
			throw new MojoExecutionException("Merge", e);
		}
	}

	public void exec(Class klass) throws Exception {
		String javaHome = System.getProperty("java.home");

		String javaBin = javaHome +
			File.separator + "bin" +
			File.separator + "java";


		List<URL> urls = new ArrayList<>();

		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		Collections.addAll(urls, urlClassLoader.getURLs());

		addAll(urls, project.getRuntimeClasspathElements());
		addAll(urls, project.getCompileClasspathElements());
		addAll(urls, project.getTestClasspathElements());

		String classpath = System.getProperty("java.class.path");
		for (URL url : urls) {
			classpath += ";" + url.getFile().substring(1);
		}
		System.out.println("classpath = " + classpath);

		String className = klass.getCanonicalName();
		System.out.println("className = " + className);

		ProcessBuilder builder = new ProcessBuilder(
			javaBin, "-classpath", classpath, className, project.getBasedir().getCanonicalPath());

		builder.start();
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
