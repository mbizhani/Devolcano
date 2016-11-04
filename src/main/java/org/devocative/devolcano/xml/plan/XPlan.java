package org.devocative.devolcano.xml.plan;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("plan")
public class XPlan {
	private List<XPackageFrom> packageMap;

	private List<XVolcano> volcanoes;

	public List<XPackageFrom> getPackageMap() {
		return packageMap;
	}

	public void setPackageMap(List<XPackageFrom> packageMap) {
		this.packageMap = packageMap;
	}

	public List<XVolcano> getVolcanoes() {
		return volcanoes;
	}

	public void setVolcanoes(List<XVolcano> volcanoes) {
		this.volcanoes = volcanoes;
	}
}
