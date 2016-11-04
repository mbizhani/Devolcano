package org.devocative.devolcano.xml.plan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("volcano")
public class XVolcano {
	@XStreamAsAttribute
	private String name;

	private String precondition;

	private XTemplate template;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrecondition() {
		return precondition;
	}

	public void setPrecondition(String precondition) {
		this.precondition = precondition;
	}

	public XTemplate getTemplate() {
		return template;
	}

	public void setTemplate(XTemplate template) {
		this.template = template;
	}
}
