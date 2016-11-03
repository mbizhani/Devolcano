package org.devocative.devolcano.vo;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class FieldVO {
	private Field field;
	private ClassVO owner;

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

	public Annotation getAnnotation(Class<? extends Annotation> annotClass) {
		return field.getAnnotation(annotClass);
	}

	public boolean isOf(Class<?> cls) {
		return cls.isAssignableFrom(field.getType());
	}

	// ----------------- Hibernate/JPA Helper Methods -----------------

	public boolean isAutoIncId() {
		return field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class);
	}

	public boolean isId() {
		return field.isAnnotationPresent(Id.class);
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
}
