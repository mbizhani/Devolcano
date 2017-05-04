package org.devocative.devolcano.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.devocative.adroit.KeyTool;

import java.io.File;

@Mojo(name = "keytool", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class KeytoolMavenPlugin extends AbstractDemeterMavenPlugin {
	@Parameter
	private File keyStoreDir;

	@Parameter
	private String keyStorePass;

	@Parameter
	private String key;

	@Parameter
	private String keyEntry;

	@Parameter
	private String protectionParam;

	@Override
	public void doExecute() throws MojoExecutionException, MojoFailureException {
		if (keyStoreDir == null || keyStorePass == null || key == null || protectionParam == null || keyEntry == null) {
			throw new MojoExecutionException("Invalid configuration! all parameters are mandatory!");
		}

		if (!keyStoreDir.exists() || !keyStoreDir.isDirectory()) {
			throw new MojoExecutionException("'keyStoreDir' must be a valid directory!");
		}

		logger.info("Generating demeter.ks: keyStoreDir={} keyEntry(alias)={}", keyStoreDir, keyEntry);
		File keyStore = new File(keyStoreDir.getAbsolutePath() + "/demeter.ks");
		logger.info("Generating demeter.ks: final file = {}", keyStore.getPath());

		if (!keyStore.exists()) {
			KeyTool.generatedKeyStoreWithSecureKey(keyStore, keyStorePass, key, keyEntry, protectionParam);
		} else {
			throw new MojoExecutionException("Keystore file already exists!");
		}
	}
}
