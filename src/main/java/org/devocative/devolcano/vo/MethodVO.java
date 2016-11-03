package org.devocative.devolcano.vo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class MethodVO {
	private Method method;
	private ClassVO owner;
	private List<MethodParamVO> params;
	private List<ClassVO> exceptionTypes;

	public MethodVO(Method method, ClassVO owner) {
		this.method = method;
		this.owner = owner;
	}

	public ClassVO getOwner() {
		return owner;
	}

	public String getName() {
		return method.getName();
	}

	public ClassVO getReturnType() {
		if (method.getGenericReturnType() instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
			return new ClassVO(method.getReturnType(), parameterizedType.getActualTypeArguments());
		}
		return new ClassVO(method.getReturnType());
	}

	/*public List<MethodParamVO> getParams() {
		if (params == null) {
			params = new ArrayList<MethodParamVO>();
			Paranamer paranamer = new BytecodeReadingParanamer();
			String[] paramNames = paranamer.lookupParameterNames(method);

			if (paramNames == null)
				throw new RuntimeException("Null paramNames: " + method.getName());
			else if (paramNames.length != method.getGenericParameterTypes().length)
				throw new RuntimeException("Unequal params: " + paramNames.length + "<>" + method.getGenericParameterTypes().length);

			for (int i = 0; i < method.getGenericParameterTypes().length; i++) {
				Type gpType = method.getGenericParameterTypes()[i];
				Class<?> type = method.getParameterTypes()[i];
				ClassVO typeVO;
				if (gpType instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) gpType;
					typeVO = new ClassVO(type, parameterizedType.getActualTypeArguments());
				} else
					typeVO = new ClassVO(type);
				params.add(new MethodParamVO(paramNames[i], typeVO));
			}
		}
		return params;
	}*/

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	public boolean isPublic() {
		return Modifier.isPublic(method.getModifiers());
	}

	public List<ClassVO> getExceptionTypes() {
		if (exceptionTypes == null) {
			exceptionTypes = new ArrayList<ClassVO>();
			for (Class<?> exc : method.getExceptionTypes())
				exceptionTypes.add(new ClassVO(exc));
		}
		return exceptionTypes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MethodVO)) return false;

		MethodVO methodVO = (MethodVO) o;

		if (method != null && methodVO.method != null && method.getName().equals(methodVO.method.getName())) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			Class<?>[] parameterTypes1 = methodVO.method.getParameterTypes();
			if (parameterTypes.length == parameterTypes1.length) {
				for (int i = 0; i < parameterTypes.length; i++) {
					if (!parameterTypes[i].equals(parameterTypes1[i]))
						return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return method != null ? method.hashCode() : 0;
	}
}
