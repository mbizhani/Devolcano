<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO formJ = context.getGenTarget(cls, "FormJ")
%>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:wicket="http://wicket.apache.org">

<wicket:panel xmlns:wicket="http://wicket.apache.org">
<%
    if(params["ajaxEditColumn"] && formJ != null) { %>
	<div wicket:id="window"></div>
<%
    	if(cls.hasAdd) {%>
	<button wicket:id="add"></button>
<% 		}
	} %>
	<form wicket:id="form">
		<div class="dmt-form">
			<div wicket:id="floatTable">
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasFVO) {
			out << """\t\t\t\t<div wicket:id="${name}"></div>\n"""
		}
	} %>
			</div>

			<button wicket:id="search"></button>
			<button type="reset">
				<wicket:message key="label.reset"/>
				<i class="fa fa-history"></i>
			</button>
		</div>
	</form>

	<div wicket:id="grid"></div>

</wicket:panel>

</html>