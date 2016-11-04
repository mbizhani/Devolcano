package org.devocative.devolcano;

import org.devocative.devolcano.vo.ClassVO;
import org.devocative.devolcano.xml.plan.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextVO {
	private Map<String, XVolcano> generatorMap = new HashMap<>();
	private List<XPackageFrom> packageMap;

	ContextVO(XPlan xPlan) {

		for (XVolcano generator : xPlan.getVolcanoes()) {
			generatorMap.put(generator.getName(), generator);
		}

		packageMap = xPlan.getPackageMap();
	}

	Map<String, XVolcano> getGeneratorMap() {
		return generatorMap;
	}

	GenTargetVO getGenTarget(Class cls, XPackageFrom packageFrom, XPackageTo packageTo) {
		String pkg = cls.getPackage().getName();
		String name = cls.getSimpleName();
		XTemplate template = generatorMap.get(packageTo.getGeneratorRef()).getTemplate();
		String newPkg = calcNewPackage(pkg, packageFrom, packageTo);
		String newName = template.getPrefix() + name + template.getSuffix();
		return new GenTargetVO(newPkg, newName);
	}

	public GenTargetVO getGenTarget(ClassVO classVO, String generatorRef) {
		String classFQN = classVO.getName();
		int lastDot = classFQN.lastIndexOf('.');
		String pkg = classFQN.substring(0, lastDot);
		String name = classFQN.substring(lastDot + 1);

		for (XPackageFrom packageFrom : packageMap) {
			if (pkg.startsWith(packageFrom.getPkg())) {
				for (XPackageTo packageTo : packageFrom.getTos()) {
					if (generatorRef.equals(packageTo.getGeneratorRef())) {
						XVolcano xGenerator = generatorMap.get(generatorRef);
						if (CodeEruption.checkPrecondition(xGenerator, classVO)) {
							XTemplate template = xGenerator.getTemplate();
							//String newPkg = pkg.replaceAll(packageFrom.getPkg(), packageTo.getPkgReplace());
							String newPkg = calcNewPackage(pkg, packageFrom, packageTo);
							String newName = template.getPrefix() + name + template.getSuffix();
							return new GenTargetVO(newPkg, newName);
						}
						return null;
					}
				}
			}
		}
		throw new RuntimeException("No GenTarget for [" + classFQN + "], GenRef=" + generatorRef);
	}

	private String calcNewPackage(String pkg, XPackageFrom packageFrom, XPackageTo packageTo) {
		String[] split = packageTo.getPkgReplace().split(":");
		String newPkg;
		if (split.length == 1)
			newPkg = pkg.replaceAll(packageFrom.getPkg(), packageTo.getPkgReplace());
		else
			newPkg = pkg.replaceAll(split[0], split[1]);
		return newPkg;
	}
}
