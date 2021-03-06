package org.devocative.devolcano.test.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "t_book",
	uniqueConstraints = {@UniqueConstraint(name = "uk", columnNames = {"c_name"})}
)
public class BookStore implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_description", nullable = false)
	private String description;

	@Column(name = "c_sample_code", nullable = false)
	private String sampleCode;

	@Column
	private Integer publishYear;

	@Embedded
	private EBookType bookType;

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

	public Integer getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(Integer publishYear) {
		this.publishYear = publishYear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EBookType getBookType() {
		return bookType;
	}

	public void setBookType(EBookType bookType) {
		this.bookType = bookType;
	}

	public String getSampleCode() {
		return sampleCode;
	}

	public BookStore setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
		return this;
	}
}
