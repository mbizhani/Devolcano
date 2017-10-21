package org.devocative.devolcano.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.devocative.demeter.core.DemeterCore;

@Mojo(name = "schemadiff", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class SchemaDiffMavenPlugin extends AbstractDemeterMavenPlugin {

	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		DemeterCore.get().generatePersistorSchemaDiff();
	}
}
