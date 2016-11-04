package org.devocative.devolcano.xml.plan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("template")
public class XTemplate {
	@XStreamAsAttribute
	private String file;

	@XStreamAsAttribute
	private String prefix;

	@XStreamAsAttribute
	private String suffix;

	@XStreamAsAttribute
	private String genFileType;

	@XStreamAsAttribute
	private String overwrite;

	private String overwriteCheckString;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getPrefix() {
		return prefix != null ? prefix : "";
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix != null ? suffix : "";
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getGenFileType() {
		return genFileType;
	}

	public void setGenFileType(String genFileType) {
		this.genFileType = genFileType;
	}

	public String getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(String overwrite) {
		this.overwrite = overwrite;
	}

	public String getOverwriteCheckString() {
		return overwriteCheckString;
	}

	public void setOverwriteCheckString(String overwriteCheckString) {
		this.overwriteCheckString = overwriteCheckString;
	}
}
