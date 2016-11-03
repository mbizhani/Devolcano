package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

public abstract class XMetaInfo implements Serializable {
	@XStreamAsAttribute
	private Boolean ignore;

	@XStreamAsAttribute
	private Boolean hasForm;

	@XStreamAsAttribute
	private Boolean hasList;

	// ------------------------------

	public Boolean getIgnore() {
		return ignore;
	}

	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}

	public Boolean getHasForm() {
		return hasForm;
	}

	public void setHasForm(Boolean hasForm) {
		this.hasForm = hasForm;
	}

	public Boolean getHasList() {
		return hasList;
	}

	public void setHasList(Boolean hasList) {
		this.hasList = hasList;
	}
}
