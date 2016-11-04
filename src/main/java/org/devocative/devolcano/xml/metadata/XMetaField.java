package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

@XStreamAlias("field")
public class XMetaField implements Serializable {
	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String ownerFqn;

	@XStreamAlias("finfo")
	private XMetaInfoField info;

	// ------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerFqn() {
		return ownerFqn;
	}

	public void setOwnerFqn(String ownerFqn) {
		this.ownerFqn = ownerFqn;
	}

	public XMetaInfoField getInfo() {
		return info;
	}

	public void setInfo(XMetaInfoField info) {
		this.info = info;
	}

	// ------------------------------

	public String getFqn() {
		return String.format("%s:%s", getName(), getOwnerFqn());
	}

	// ------------------------------

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XMetaField)) return false;

		XMetaField that = (XMetaField) o;

		return !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);

	}

	@Override
	public int hashCode() {
		return getName() != null ? getName().hashCode() : 0;
	}
}
