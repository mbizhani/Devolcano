package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

@XStreamAlias("pkg")
public class XMetaPackage implements Serializable {
	@XStreamAsAttribute
	private String fqn;

	// ------------------------------

	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}
}
