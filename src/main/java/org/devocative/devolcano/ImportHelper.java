package org.devocative.devolcano;

import org.devocative.devolcano.vo.ClassVO;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Set;
import java.util.TreeSet;

public class ImportHelper {
	private Set<String> set = new TreeSet<String>();

	public String add(String name, String fqn) {
		set.add(fqn);
		return name;
	}

	public String add(Class cls) {
		if (!cls.isPrimitive())
			set.add(cls.getName());
		return cls.getSimpleName();
	}

	public String add(ClassVO cls) {
		StringBuilder builder = new StringBuilder();
		builder.append(cls.getSimpleName());
		if (!cls.isPrimitive()) {
			set.add(cls.getName());
			Type[] realizedTypeParams = cls.getRealizedTypeParams();
			if (realizedTypeParams != null) {
				Class c = null;
				boolean wildcard = false;
				if (realizedTypeParams[0] instanceof Class)
					c = (Class) realizedTypeParams[0];
				else if (realizedTypeParams[0] instanceof WildcardType) {
					WildcardType wt = (WildcardType) realizedTypeParams[0];
					c = (Class) wt.getUpperBounds()[0];
					wildcard = true;
				}

				builder.append("<");
				if (wildcard)
					builder.append("? extends ");
				builder.append(add(c));
				for (int i = 1; i < realizedTypeParams.length; i++) {
					c = (Class) realizedTypeParams[i];
					builder.append(",").append(add(c));
				}
				builder.append(">");
			}
		}
		return builder.toString();
	}

	public String add(GenTargetVO targetVO) {
		set.add(targetVO.getFqn());
		return targetVO.getName();
	}

	public String generateImports(String classPkg) {
		StringBuilder builder = new StringBuilder();
		String prevFirstPkg = null;
		for (String cls : set) {
			int lastDotIdx = cls.lastIndexOf(".");
			String pkgName = cls.substring(0, lastDotIdx);
			if (!pkgName.equals("java.lang") && !pkgName.equals(classPkg)) {
				int firstPkgIdx = cls.indexOf(".");
				String firstPkg = cls.substring(0, firstPkgIdx);
				if (prevFirstPkg != null && !prevFirstPkg.equals(firstPkg))
					builder.append("\n");
				builder.append("import ").append(cls).append(";\n");
				prevFirstPkg = firstPkg;
			}
		}
		return builder.toString();
	}
}
