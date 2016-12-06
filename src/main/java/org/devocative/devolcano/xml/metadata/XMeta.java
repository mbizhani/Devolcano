package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XStreamAlias("meta")
public class XMeta implements Serializable {
	private String filterClass;
	private List<XMetaClass> classes;

	// ------------------------------

	public String getFilterClass() {
		return filterClass;
	}

	public void setFilterClass(String filterClass) {
		this.filterClass = filterClass;
	}

	public List<XMetaClass> getClasses() {
		return classes;
	}

	public void setClasses(List<XMetaClass> classes) {
		this.classes = classes;
	}

	// ------------------------------

	@XStreamOmitField
	private Map<String, XMetaClass> classesMap;

	public XMetaClass findXMetaClass(String fqn) {
		if (classesMap == null) {
			classesMap = new HashMap<>();

			if (getClasses() != null) {
				for (XMetaClass xMetaClass : getClasses()) {
					classesMap.put(xMetaClass.getFqn(), xMetaClass);
				}
			}
		}
		return classesMap.get(fqn);
	}
}
