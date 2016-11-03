package org.devocative.devolcano.test;

import org.devocative.devolcano.MetaHandler;

public class GenerateTest {
	public static void main(String[] args) {
		MetaHandler.init();
		MetaHandler.scan();
		MetaHandler.write();
	}
}
