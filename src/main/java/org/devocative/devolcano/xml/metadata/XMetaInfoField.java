package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("info")
public class XMetaInfoField extends XMetaInfo {
	// listType (simple|search)
	@XStreamAsAttribute
	private String listType;

	@XStreamAsAttribute
	private Boolean hasTimePart;

	@XStreamAsAttribute
	private Boolean required;

	// textType (simple|multiline|code)
	@XStreamAsAttribute
	private String textType;

	@XStreamAsAttribute
	private String codeType;

	private String htmlAttr;
	// ------------------------------

	public String getListType() {
		return listType != null ? listType : "simple";
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public Boolean getHasTimePart() {
		return hasTimePart == null ? true : hasTimePart;
	}

	public void setHasTimePart(Boolean hasTimePart) {
		this.hasTimePart = hasTimePart;
	}

	public Boolean getRequired() {
		return required != null ? required : false;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getTextType() {
		return textType != null ? textType : "simple";
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getHtmlAttr() {
		return htmlAttr != null ? htmlAttr : "";
	}

	public void setHtmlAttr(String htmlAttr) {
		this.htmlAttr = htmlAttr;
	}
}
