package org.devocative.devolcano.vo;

import org.apache.commons.beanutils.PropertyUtils;
import org.devocative.devolcano.MetaHandler;
import org.devocative.devolcano.xml.metadata.XMetaClass;
import org.devocative.devolcano.xml.metadata.XMetaInfoClass;

import javax.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassVO {
	private Class cls;
	private XMetaClass xMetaClass;
	private Type[] realizedTypeParams;

	private Map<String, FieldVO> allFieldsMap;
	private Map<String, FieldVO> supperFieldsMap;
	private Map<String, FieldVO> declaredFieldsMap;
	private Map<String, PropertyVO> propertiesMap;
	private List<MethodVO> methodsList;

	// ------------------------------

	public ClassVO(Class cls) {
		this(cls, null);
	}

	// Main Constructor
	public ClassVO(Class cls, Type[] realizedTypeParams) {
		this.cls = cls;
		this.realizedTypeParams = realizedTypeParams;
	}

	// ------------------------------

	public String getName() {
		return cls.getName();
	}

	public String getSimpleName() {
		return cls.getSimpleName();
	}

	public boolean hasAnnotation(Class<? extends Annotation> annotClass) {
		return cls.isAnnotationPresent(annotClass);
	}

	public Annotation getAnnotation(Class<? extends Annotation> aClass) {
		return cls.getAnnotation(aClass);
	}

	public ClassVO getSuperclass() {
		return new ClassVO(cls.getSuperclass());
	}

	public boolean isNormal() {
		return !cls.isEnum()
			&& !cls.isAnnotation()
			&& !cls.isInterface()
			&& !Modifier.isAbstract(cls.getModifiers());
	}

	public boolean isPrimitive() {
		return cls.isPrimitive();
	}

	public boolean isOf(Class clss) {
		return clss.isAssignableFrom(cls);
	}

	public Map<String, FieldVO> getAllFieldsMap() {
		if (allFieldsMap == null) {
			allFieldsMap = new LinkedHashMap<>();
			allFieldsMap.putAll(getSupperFieldsMap());
			allFieldsMap.putAll(getDeclaredFieldsMap());
		}
		return allFieldsMap;
	}

	public Map<String, FieldVO> getSupperFieldsMap() {
		if (supperFieldsMap == null) {
			supperFieldsMap = new LinkedHashMap<>();
			List<Class> allSuperClasses = new ArrayList<Class>();
			Class superclass = cls.getSuperclass();
			while (!Object.class.equals(superclass)) {
				allSuperClasses.add(superclass);
				superclass = superclass.getSuperclass();
			}

			for (int i = allSuperClasses.size() - 1; i >= 0; i--) {
				populateDeclaredFields(supperFieldsMap, allSuperClasses.get(i), cls);
			}
		}
		return supperFieldsMap;
	}

	public Map<String, FieldVO> getDeclaredFieldsMap() {
		if (declaredFieldsMap == null) {
			declaredFieldsMap = new LinkedHashMap<>();
			populateDeclaredFields(declaredFieldsMap, cls, cls);
		}
		return declaredFieldsMap;
	}

	public Map<String, PropertyVO> getPropertiesMap() {
		if (propertiesMap == null) {
			propertiesMap = new LinkedHashMap<String, PropertyVO>();
			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(cls);
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				if (!"class".equals(descriptor.getName())) {
					FieldVO field = null;
					try {
						Class<?> declaringClass = descriptor.getReadMethod().getDeclaringClass();
						field = new FieldVO(cls.getDeclaredField(descriptor.getName()), new ClassVO(declaringClass));
					} catch (NoSuchFieldException e) {
						//throw new RuntimeException(e);
					}
					propertiesMap.put(descriptor.getName(), new PropertyVO(descriptor, field));
				}
			}
		}
		return propertiesMap;
	}

	public List<MethodVO> getMethodsList() {
		if (methodsList == null) {
			methodsList = new ArrayList<MethodVO>();

			List<Class> allSuperClasses = new ArrayList<Class>();
			Class superclass = cls.getSuperclass();
			while (!Object.class.equals(superclass)) {
				allSuperClasses.add(superclass);
				superclass = superclass.getSuperclass();
			}

			for (int i = allSuperClasses.size() - 1; i >= 0; i--)
				populateDeclaredMethods(methodsList, allSuperClasses.get(i), cls);

			populateDeclaredMethods(methodsList, cls, cls);
		}
		return methodsList;
	}

	public Type[] getRealizedTypeParams() {
		return realizedTypeParams;
	}

	public FieldVO getIdField() {
		for (FieldVO fieldVO : getAllFieldsMap().values()) {
			if (fieldVO.isId())
				return fieldVO;
		}
		return null;
	}

	public XMetaInfoClass getInfo() {
		return getXMetaClass().getInfo();
	}

	public XMetaClass getXMetaClass() {
		if (xMetaClass == null) {
			xMetaClass = MetaHandler.findXMetaClass(cls.getName());
		}
		return xMetaClass;
	}

	// ---------------- XMetaInfoClass Helper Methods ----------------

	public boolean isOk() {
		return getInfo() == null || !getInfo().getIgnore();
	}

	public boolean getHasForm() {
		return isOk() && (getInfo() == null || getInfo().getHasForm());
	}

	public boolean getHasList() {
		return isOk() && (getInfo() == null || (getInfo().getHasList() && getInfo().getHasFVO()));
	}

	public boolean getHasFVO() {
		return isOk() && (getInfo() == null || getInfo().getHasFVO());
	}

	public boolean getHasAdd() {
		return isOk() && (getInfo() == null || (getInfo().getHasForm() && getInfo().getHasAdd()));
	}

	/*public boolean hasView() {
		return isOk() && (cg == null || cg.hasView());
	}*/

	/*public boolean hasSearch() {
		return isOk() && (cg == null || cg.hasSearch());
	}*/


	// ---------------- Hibernate & Spring Helper Methods ----------------

	public boolean isEntity() {
		return isNormal() && cls.isAnnotationPresent(Entity.class);
	}

	/*public boolean isService() {
		return cls.isAnnotationPresent(Service.class);
	}*/

	// ------------------------------

	private void populateDeclaredFields(Map<String, FieldVO> result, Class cls, Class owner) {
		for (Field field : cls.getDeclaredFields())
			result.put(field.getName(), new FieldVO(field, new ClassVO(owner)));
	}

	private void populateDeclaredMethods(List<MethodVO> result, Class cls, Class owner) {
		for (Method method : cls.getDeclaredMethods()) {
			MethodVO methodVO = new MethodVO(method, new ClassVO(owner));
			if (!result.contains(methodVO))
				result.add(methodVO);
		}
	}
}
