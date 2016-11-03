package org.devocative.devolcano.xml.metadata;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XStreamAlias("class")
public class XMetaClass implements Serializable {
	@XStreamAsAttribute
	private String fqn;

	private XMetaInfoClass info;

	private XMetaId id;

	private List<XMetaField> fields;

	private List<XMetaField> superFields;

	// ------------------------------

	public String getFqn() {
		return fqn;
	}

	public void setFqn(String fqn) {
		this.fqn = fqn;
	}

	public XMetaInfoClass getInfo() {
		return info;
	}

	public void setInfo(XMetaInfoClass info) {
		this.info = info;
	}

	public XMetaId getId() {
		return id;
	}

	public void setId(XMetaId id) {
		this.id = id;
	}

	public List<XMetaField> getFields() {
		return fields;
	}

	public void setFields(List<XMetaField> fields) {
		this.fields = fields;
	}

	public List<XMetaField> getSuperFields() {
		return superFields;
	}

	public void setSuperFields(List<XMetaField> superFields) {
		this.superFields = superFields;
	}

	// ------------------------------

	@XStreamOmitField
	private Map<String, XMetaField> fieldsMap;

	@XStreamOmitField
	private Map<String, XMetaField> supperFieldsMap;

	public XMetaField findXMetaField(String name) {
		if (fieldsMap == null) {
			fieldsMap = new HashMap<>();

			if (getFields() != null) {
				for (XMetaField xMetaField : getFields()) {
					fieldsMap.put(xMetaField.getName(), xMetaField);
				}
			}
		}
		return fieldsMap.get(name);
	}

	public XMetaField findSupperXMetaField(String fqn) {
		if (supperFieldsMap == null) {
			supperFieldsMap = new HashMap<>();

			if (getSuperFields() != null) {
				for (XMetaField xMetaField : getSuperFields()) {
					supperFieldsMap.put(xMetaField.getFqn(), xMetaField);
				}
			}
		}
		return supperFieldsMap.get(fqn);
	}

	// ------------------------------

	@Override
	public String toString() {
		return getFqn();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XMetaClass)) return false;

		XMetaClass that = (XMetaClass) o;

		return !(getFqn() != null ? !getFqn().equals(that.getFqn()) : that.getFqn() != null);

	}

	@Override
	public int hashCode() {
		return getFqn() != null ? getFqn().hashCode() : 0;
	}
}
