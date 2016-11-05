<%
    org.devocative.devolcano.vo.ClassVO cls = targetClass
    org.devocative.devolcano.ImportHelper imp = importHelper
    org.devocative.devolcano.ContextVO context = context

    org.devocative.devolcano.GenTargetVO svo = context.getGenTarget(cls, "SVO")

    if (svo != null) {
        imp.add(svo)
    }
    imp.add(cls)
    imp.add(List.class)
%>
package ${targetVO.pkg};

@IMPORT@

public interface ${targetVO.name} {
	void saveOrUpdate(${cls.simpleName} entity);

${cls.simpleName} load(${imp.add(cls.idField.type)} ${cls.idField.name});

	List<${cls.simpleName}> list();
<% if (svo != null) { %>
List<${cls.simpleName}> search(${svo.name} filter, long pageIndex, long pageSize);

	long count(${svo.name} filter);
<% }
cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
    if (field.ok && field.association && (field.hasSVO || field.hasForm)) {
        String type = imp.add(field.mainType)
%>
List<${type}> get${field.name.toCapital()}List();
<%
            }
    }
%>
}
