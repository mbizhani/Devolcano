package org.devocative.devolcano.test.entity;

import java.util.List;

public enum EBookType {
	TEST(1);

	private Integer id;

	EBookType(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static List<EBookType> list() {
		return null;
	}
}
