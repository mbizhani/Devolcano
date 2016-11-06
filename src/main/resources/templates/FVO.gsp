<%
    org.devocative.devolcano.vo.ClassVO cls = targetClass
    org.devocative.devolcano.ImportHelper imp = importHelper
    org.devocative.devolcano.ContextVO context = context %>
package ${targetVO.pkg};

@IMPORT@

@${imp.add(org.devocative.demeter.iservice.persistor.Filterer)}
public class ${targetVO.name} implements ${imp.add(Serializable)} {
<%
    StringBuilder setterGetterBuilder = new StringBuilder()

    cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
        if (field.ok && field.hasSVO) {
            String type
            String searchAnnot = null

            if (field.isOf(Number) || field.isOf(Date)) {
                type = "${imp.add(org.devocative.adroit.vo.RangeVO)}<${imp.add(field.type)}>"
//TODO		} else if (field.searchFieldsAsJoin != null) {
//				def svo = context.getGenTarget(field.type, "SVO")
//				type = "${imp.add(svo)}"
//				searchAnnot = "@${imp.add(ir.fanap.common.hibernate.Search)}(useJoin = true)"
            } else if (field.embedded || (field.association && field.listType == "simple")) {
                if (field.isOf(Collection)) {
                    type = "${imp.add(field.type)}"
                } else {
                    type = "${imp.add(List)}<${imp.add(field.type)}>"
                }
//TODO		} else if(field.association && field.listType == s2s.anot.ListType.Searchable) {
//				type = "${imp.add(Serializable)}"
            } else {
                type = "${imp.add(field.type)}"
            }

//			if(field.association && field.listType == s2s.anot.ListType.Searchable)
//				out << """	/**
//	 * It is used in SearchableListInput:
//	 * The assigned value is List<${field.type.simpleName}> or ${field.type.simpleName}SVO.
//	 */
//"""
            out << "\tprivate ${type} ${name};\n"

            if (searchAnnot != null)
                setterGetterBuilder << "\n\t${searchAnnot}"

            setterGetterBuilder << """
	public ${type} get${name.toCapital()}() {
		return ${name};
	}

	public void set${name.toCapital()}(${type} ${name}) {
		this.${name} = ${name};
	}\n"""
        }
    }

    out << "\n\t// ------------------------------\n"
    out << setterGetterBuilder.toString()
%>
}