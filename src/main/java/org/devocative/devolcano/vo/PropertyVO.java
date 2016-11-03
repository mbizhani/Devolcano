package org.devocative.devolcano.vo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class PropertyVO {
	private PropertyDescriptor descriptor;
	private FieldVO field;

	public PropertyVO(PropertyDescriptor descriptor, FieldVO field) {
		this.descriptor = descriptor;
		this.field = field;
	}

	public String getName() {
		return descriptor.getName();
	}

	public FieldVO getField() {
		return field;
	}

	public ClassVO getType() {
		return new ClassVO(descriptor.getPropertyType());
	}

	public boolean isGenericCollection() {
		Class<?> propertyType = descriptor.getReadMethod().getReturnType();
		return Collection.class.isAssignableFrom(propertyType) &&
			descriptor.getReadMethod().getGenericReturnType() instanceof ParameterizedType;
	}

	public ClassVO getGenericCollectionType() {
		ParameterizedType parameterizedType = (ParameterizedType) descriptor.getReadMethod().getGenericReturnType();
		return new ClassVO((Class) parameterizedType.getActualTypeArguments()[0]);
	}
}
