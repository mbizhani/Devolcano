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

	@XStreamAsAttribute
	private Boolean hasFVO;

	// ------------------------------

	public Boolean getIgnore() {
		return ignore != null ? ignore : false;
	}

	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}

	public Boolean getHasForm() {
		return hasForm != null ? hasForm : true;
	}

	public void setHasForm(Boolean hasForm) {
		this.hasForm = hasForm;
	}

	public Boolean getHasList() {
		return hasList != null ? hasList : true;
	}

	public void setHasList(Boolean hasList) {
		this.hasList = hasList;
	}

	public Boolean getHasFVO() {
		return hasFVO != null ? hasFVO : true;
	}

	public void setHasFVO(Boolean hasFVO) {
		this.hasFVO = hasFVO;
	}
}
