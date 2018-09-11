<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	imp.add(cls)
%>
package ${targetVO.pkg};

@IMPORT@

public class ${targetVO.name} implements ${imp.add(Serializable)} {
	private static final long serialVersionUID = ${targetVO.fqn.hashCode()}L;
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (!field.isStatic()) {
%>
private ${imp.add(field.type)} ${field.name};
<%
			}
	}
%>
// ------------------------------

public ${targetVO.name}(${cls.simpleName} ent) {
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (!field.isStatic()) {
%>
set${field.name.toCapital()}(ent.get${field.name.toCapital()}());
<%
			}
	}
%>
}

// ------------------------------
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (!field.isStatic()) {
%>
public ${field.type.simpleName} get${field.name.toCapital()}() {
		return ${field.name};
	}

	public void set${field.name.toCapital()}(${field.type.simpleName} ${field.name}) {
		this.${field.name} = ${field.name};
	}
<%
			}
	}
%>
// ---------------

public ${cls.simpleName} to${cls.simpleName}() {
${cls.simpleName} ent = new ${cls.simpleName}();
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (!field.isStatic()) {
%>
ent.set${field.name.toCapital()}(get${field.name.toCapital()}());
<%
			}
	}
%>
return ent;
}
}
