package org.devocative.devolcano;

public class GenTargetVO {
	private String pkg;

	private String name;

	public GenTargetVO(String pkg, String name) {
		this.pkg = pkg;
		this.name = name;
	}

	public String getPkg() {
		return pkg;
	}

	public String getName() {
		return name;
	}

	public String getFqn() {
		return pkg + "." + name;
	}

	public String getFqnDir() {
		return pkg.replace('.', '/') + "/" + name;
	}
}
