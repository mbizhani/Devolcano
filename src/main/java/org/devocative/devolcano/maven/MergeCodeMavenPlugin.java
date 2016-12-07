package org.devocative.devolcano.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.devocative.devolcano.MergeCode;

import java.io.File;

@Mojo(name = "mergecode", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class MergeCodeMavenPlugin extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File baseDir = project.getBasedir();
		try {
			MergeCode.merge(baseDir);
		} catch (Exception e) {
			throw new MojoExecutionException("Merge", e);
		}
	}
}
