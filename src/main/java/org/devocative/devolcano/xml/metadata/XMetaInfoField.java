package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("info")
public class XMetaInfoField extends XMetaInfo {
	@XStreamAsAttribute
	private String listType;

	@XStreamAsAttribute
	private Boolean hasTimePart;

	// ------------------------------

	public String getListType() {
		return listType != null ? listType : "simple";
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public Boolean getHasTimePart() {
		if (hasTimePart == null) {
			hasTimePart = true;
		}
		return hasTimePart;
	}

	public void setHasTimePart(Boolean hasTimePart) {
		this.hasTimePart = hasTimePart;
	}
}
