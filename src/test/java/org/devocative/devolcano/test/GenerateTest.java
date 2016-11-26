package org.devocative.devolcano.test;

import org.devocative.devolcano.CodeEruption;

import java.io.File;

public class GenerateTest {
	public static void main(String[] args) throws Exception {
		CodeEruption.init(new File("./"));
		CodeEruption.erupt();
	}
}
