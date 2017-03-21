package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("info")
public class XMetaInfoClass extends XMetaInfo {
	@XStreamAsAttribute
	private Boolean hasAdd;

	// ------------------------------

	public Boolean getHasAdd() {
		return hasAdd != null ? hasAdd : true;
	}

	public void setHasAdd(Boolean hasAdd) {
		this.hasAdd = hasAdd;
	}
}
