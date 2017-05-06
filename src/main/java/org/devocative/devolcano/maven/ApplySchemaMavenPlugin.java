package org.devocative.devolcano.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.devocative.demeter.core.DemeterCore;

@Mojo(name = "applyschema", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class ApplySchemaMavenPlugin extends AbstractDemeterMavenPlugin {

	@Parameter
	private String filters;

	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		String[] filterArray = null;
		if (filters != null) {
			filterArray = filters.split("[,]");
		}
		DemeterCore.applySQLSchemas(filterArray);
	}
}
