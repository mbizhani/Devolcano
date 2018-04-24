<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO fvo = context.getGenTarget(cls, "FVO")

	if (fvo != null) {
		imp.add(fvo)
	}
	imp.add(cls)
	imp.add(List)
	imp.add(org.devocative.demeter.iservice.IEntityService)
%>
package ${targetVO.pkg};

@IMPORT@

public interface ${targetVO.name} extends IEntityService<${cls.simpleName}> {
	void saveOrUpdate(${cls.simpleName} entity);

	${cls.simpleName} load(${imp.add(cls.idField.type)} ${cls.idField.name});
<%
	// Generating load by unique fields
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.unique) {
%>
	${cls.simpleName} loadBy${field.name.toCapital()}(${imp.add(field.mainType)} ${field.name});
<%
			}
	}
%>
	List<${cls.simpleName}> list();
<% if (fvo != null) { %>
	List<${cls.simpleName}> search(${fvo.name} filter, long pageIndex, long pageSize);

	long count(${fvo.name} filter);
<% }

	// Generating list for associations
    cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.association && (field.hasFVO || field.hasForm)) {
			String type = imp.add(field.mainType)
%>
	List<${type}> get${field.name.toCapital()}List();
<%
		}
	}
%>
	// ==============================
}
