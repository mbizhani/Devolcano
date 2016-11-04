<%
    org.devocative.devolcano.vo.ClassVO cls = targetClass
    org.devocative.devolcano.ImportHelper imp = importHelper
    org.devocative.devolcano.ContextVO context = context %>
package ${targetVO.pkg};

@IMPORT@

public class ${targetVO.name} implements ${imp.add(Serializable)}{
<%
    cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
%>
private ${imp.add(field.type)} ${name.toCapital()};
<%
    }

    cls.propertiesMap.each { String key, org.devocative.devolcano.vo.PropertyVO prop ->
        out << "    // Prop: ${key}\n"
    }
%>
}
