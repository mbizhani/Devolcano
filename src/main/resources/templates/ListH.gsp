<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
%>
<wicket:panel xmlns:wicket="http://wicket.apache.org">
	<div wicket:id="window"></div>

	<form wicket:id="form">
		<div class="dmt-form">
			<div wicket:id="floatTable">
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasSVO) {
			out << """\t\t\t\t<div><div wicket:id="${name}"></div></div>\n"""
		}
	}
%>
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