package org.devocative.devolcano.test.entity;

import java.io.Serializable;
import java.util.List;

public class EBookType implements Serializable {
	private Integer id;

	public EBookType() {
	}

	public EBookType(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static List<EBookType> list() {
		return null;
	}
}
