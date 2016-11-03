package org.devocative.devolcano.vo;

public class MethodParamVO {
	private String name;
	private ClassVO type;

	public MethodParamVO(String name, ClassVO type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public ClassVO getType() {
		return type;
	}
}
