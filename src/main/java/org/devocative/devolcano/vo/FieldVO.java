package org.devocative.devolcano.vo;

import org.devocative.devolcano.xml.metadata.XMetaField;
import org.devocative.devolcano.xml.metadata.XMetaInfoField;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;

public class FieldVO {
	private Field field;
	private ClassVO owner;
	private XMetaField xMetaField;

	// ------------------------------

	public FieldVO(Field field, ClassVO owner) {
		this.field = field;
		this.owner = owner;
	}

	// ------------------------------

	public String getName() {
		return field.getName();
	}

	public ClassVO getType() {
		if (field.getGenericType() instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
			return new ClassVO(field.getType(), parameterizedType.getActualTypeArguments());
		}
		return new ClassVO(field.getType());
	}

	public ClassVO getMainType() {
		if (field.getGenericType() instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
			return new ClassVO((Class) parameterizedType.getActualTypeArguments()[0]);
		}
		return new ClassVO(field.getType());
	}

	public boolean hasAnnotation(Class<? extends Annotation> annotClass) {
		return field.isAnnotationPresent(annotClass);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotClass) {
		return field.getAnnotation(annotClass);
	}

	public boolean isOf(Class<?> cls) {
		return cls.isAssignableFrom(field.getType());
	}

	public boolean isReal() {
		return isOf(Float.class) || isOf(Double.class) || isOf(BigDecimal.class);
	}

	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(field.getModifiers());
	}

	public XMetaInfoField getInfo() {
		if (xMetaField == null) {
			xMetaField = owner.getXMetaClass().findXMetaField(getName());
		}
		return xMetaField.getInfo();
	}

	// ---------------- CG4F Annot Helper Methods ----------------

	public boolean isOk() {
		return !isId() && !isStatic() && (getInfo() == null || !getInfo().getIgnore());
	}

	public boolean getHasForm() {
		return owner.getHasForm() && (getInfo() == null || getInfo().getHasForm());
	}

	public boolean getHasList() {
		return owner.getHasList() && (getInfo() == null || getInfo().getHasList());
	}

	public boolean getHasFVO() {
		return owner.getHasFVO() && (getInfo() == null || getInfo().getHasFVO());
	}

	public String getListType() {
		if (!isAssociation()) {
			throw new RuntimeException(String.format("Defining ListType for field [%s] which is not association!", getName()));
		}
		return getInfo() != null ? getInfo().getListType() : "simple";
	}

	// ----------------- Hibernate/JPA Helper Methods -----------------

	public boolean isAutoIncId() {
		return field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class);
	}

	public boolean isId() {
		return field.isAnnotationPresent(Id.class);
	}

	public boolean isRequired() {
		boolean result = false;
		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			result = !column.nullable();
		} else if (field.isAnnotationPresent(JoinColumn.class)) {
			JoinColumn column = field.getAnnotation(JoinColumn.class);
			result = !column.nullable();
		} else if(getInfo() != null) {
			result = getInfo().getRequired();
		}
		return result;
	}

	public boolean isAssociation() {
		return isOneToMany() || isOneToOne() || isManyToOne() || isManyToMany();
	}

	public boolean isOneToOne() {
		return field.isAnnotationPresent(OneToOne.class);
	}

	public boolean isOneToMany() {
		return field.isAnnotationPresent(OneToMany.class);
	}

	public boolean isManyToOne() {
		return field.isAnnotationPresent(ManyToOne.class);
	}

	public boolean isManyToMany() {
		return field.isAnnotationPresent(ManyToMany.class);
	}

	public boolean isEmbedded() {
		return field.isAnnotationPresent(Embedded.class);
	}

	public String getColumnName() {
		if (hasAnnotation(Column.class)) {
			Column column = getAnnotation(Column.class);
			if (column.name() != null) {
				return column.name();
			} else {
				return getName();
			}
		}
		return null;
	}

	public boolean isUnique() {
		boolean result = false;
		if (hasAnnotation(Column.class)) {
			Column column = getAnnotation(Column.class);
			result = column.unique();
		}

		if (owner.hasAnnotation(Table.class)) {
			Table table = (Table) owner.getAnnotation(Table.class);
			if (table.uniqueConstraints() != null) {
				for (UniqueConstraint constraint : table.uniqueConstraints()) {
					if (constraint.columnNames() != null && constraint.columnNames().length == 1 &&
						constraint.columnNames()[0].equals(getColumnName())) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}
}
