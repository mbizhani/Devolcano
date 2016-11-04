package org.devocative.devolcano.xml.plan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("from")
public class XPackageFrom {
	@XStreamAsAttribute
	private String pkg;

	@XStreamAsAttribute
	private String includePattern;

	@XStreamAsAttribute
	private String excludePattern;

	@XStreamAsAttribute
	private Boolean ignore;

	@XStreamAsAttribute
	private Boolean includeSubPackages;

	@XStreamImplicit
	private List<XPackageTo> tos;

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getIncludePattern() {
		return includePattern;
	}

	public void setIncludePattern(String includePattern) {
		this.includePattern = includePattern;
	}

	public String getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(String excludePattern) {
		this.excludePattern = excludePattern;
	}

	public Boolean getIgnore() {
		return ignore != null ? ignore : false;
	}

	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}

	public Boolean getIncludeSubPackages() {
		return includeSubPackages != null ? includeSubPackages : true;
	}

	public void setIncludeSubPackages(Boolean includeSubPackages) {
		this.includeSubPackages = includeSubPackages;
	}

	public List<XPackageTo> getTos() {
		return tos;
	}

	public void setTos(List<XPackageTo> tos) {
		this.tos = tos;
	}
}
