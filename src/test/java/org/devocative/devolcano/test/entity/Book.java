package org.devocative.devolcano.test.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

public class Book implements Serializable {
	@Id
	private Long id;

	@Column
	private String name;

	@ManyToOne
	private Person author;

	// ------------------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getAuthor() {
		return author;
	}

	public void setAuthor(Person author) {
		this.author = author;
	}
}
