<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
%>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:wicket="http://wicket.apache.org">

<wicket:panel xmlns:wicket="http://wicket.apache.org">

	<form wicket:id="form">
		<div class="dmt-form">
			<div wicket:id="floatTable">
<%
    boolean hasNotSimpleText = false
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasForm) {
			if (!field.isOf(String) || field.textType == "simple") {
				out << """\t\t\t\t<div><div wicket:id="${name}" ${field.info.htmlAttr}></div></div>\n"""
			} else {
				hasNotSimpleText = true
			}
		}
	}
%>
			</div>
<%
    if(hasNotSimpleText) {
		out << "\t\t\t<table>\n"
		cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
			if(field.ok && field.hasForm && field.isOf(String) && field.textType != "simple") {
				out << "\t\t\t\t<tr>\n"
				out << "\t\t\t\t\t<th><wicket:message key=\"${cls.simpleName}.${name}\"/> ${field.required ? '*' : ''}</th>\n"
				String cell
				if(field.textType == "multiline") {
					cell = """<textarea wicket:id="${name}" ${field.info.htmlAttr}></textarea>"""
				} else if(field.textType == "html") {
					//TODO implement HTML
					throw new RuntimeException("HTML not implemented: ${name}")
				} else if(field.textType == "code") {
					cell = """<div wicket:id="${name}" ${field.info.htmlAttr}></div>"""
				} else {
					cell = "Unknown textType=[${field.textType}] for field=[${name}]"
				}
				out << "\t\t\t\t\t<td>${cell}</td>\n"
				out << "\t\t\t\t</tr>\n"
			}
		}
		out << "\t\t\t</table>\n"
	}
%>
			<button wicket:id="save"></button>
			<button type="reset">
				<wicket:message key="label.reset"/>
				<i class="fa fa-history"></i>
			</button>
		</div>
	</form>

</wicket:panel>

</html>