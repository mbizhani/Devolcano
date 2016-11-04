package org.devocative.devolcano.xml.plan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("to")
public class XPackageTo {
	@XStreamAsAttribute
	private String pkgReplace;

	@XStreamAsAttribute
	private String generatorRef;

	@XStreamAsAttribute
	private String genDir;

	@XStreamAsAttribute
	private Boolean ignore;

	public String getPkgReplace() {
		return pkgReplace;
	}

	public void setPkgReplace(String pkgReplace) {
		this.pkgReplace = pkgReplace;
	}

	public String getGeneratorRef() {
		return generatorRef;
	}

	public void setGeneratorRef(String generatorRef) {
		this.generatorRef = generatorRef;
	}

	public String getGenDir() {
		return genDir;
	}

	public void setGenDir(String genDir) {
		this.genDir = genDir;
	}

	public Boolean getIgnore() {
		return ignore != null ? ignore : false;
	}

	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}
}
