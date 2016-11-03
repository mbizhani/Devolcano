package org.devocative.devolcano.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Person implements Serializable {
	@Id
	private Long id;

	@Column
	private String name;

	@Column
	private Date birthDate;

}
