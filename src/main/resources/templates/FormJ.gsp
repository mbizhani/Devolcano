<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO iservice = context.getGenTarget(cls, "ServiceI")
	org.devocative.devolcano.GenTargetVO service = context.getGenTarget(cls, "ServiceM")
	org.devocative.devolcano.GenTargetVO listJ = context.getGenTarget(cls, "ListJ")

	imp.add(cls)
	imp.add(iservice)
	imp.add(listJ)

	imp.add(List)
	imp.add(Collections)

	imp.add(javax.inject.Inject)

	imp.add(org.devocative.demeter.web.DPage)
	imp.add(org.devocative.wickomp.html.WFloatTable)
	imp.add(org.devocative.demeter.web.component.DAjaxButton)
	imp.add(org.devocative.demeter.web.UrlUtil)

	imp.add(org.apache.wicket.markup.html.form.Form)
	imp.add(org.apache.wicket.model.CompoundPropertyModel)
	imp.add(org.apache.wicket.model.ResourceModel)
	imp.add(org.apache.wicket.ajax.AjaxRequestTarget)
	imp.add(org.devocative.wickomp.html.window.WModalWindow)
%>
package ${targetVO.pkg};

@IMPORT@

public class ${targetVO.name} extends DPage {
	private static final long serialVersionUID = ${targetVO.fqn.hashCode()}L;

	@Inject
	private ${iservice.name} ${service.name.toUncapital()};

	private ${cls.simpleName} entity;

	// ------------------------------

	public ${targetVO.name}(String id) {
		this(id, new ${cls.simpleName}());
	}

	// Main Constructor - For Ajax Call
	public ${targetVO.name}(String id, ${cls.simpleName} entity) {
		super(id, Collections.<String>emptyList());

		this.entity = entity;
	}

	// ---------------

	// Main Constructor - For REST Call
	public ${targetVO.name}(String id, List<String> params) {
		super(id, params);

		this.entity = params != null && !params.isEmpty() ?
			${service.name.toUncapital()}.load(Long.valueOf(params.get(0))) :
			new ${cls.simpleName}();
	}

	// ------------------------------

	@Override
	protected void onInitialize() {
		super.onInitialize();

		WFloatTable floatTable = new WFloatTable("floatTable");
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasForm && field.textType == "simple") {
			String component

			if (field.isOf(Number)) {
				component = """${imp.add(org.devocative.wickomp.form.WNumberInput)}("${name}", ${imp.add(field.type)}.class)"""
			} else if (field.isOf(Date)) {
				component = """${imp.add(org.devocative.wickomp.form.WDateInput)}("${name}")\n\t\t\t.setTimePartVisible(${field.info.hasTimePart})"""
			} else if (field.isOf(Boolean)) {
				component = """${imp.add(org.devocative.wickomp.form.WBooleanInput)}("${name}")"""
			} else if (field.enumeration) {
				component = """${imp.add(org.devocative.wickomp.form.WSelectionInput)}("${name}", ${imp.add(field.mainType)}.list(), false)"""
			} else if (field.association) {
				boolean isMultiple = field.oneToMany || field.manyToMany
				if (field.listType == "simple") {
					component = """${imp.add(org.devocative.wickomp.form.WSelectionInput)}("${name}", ${service.name.toUncapital()}.get${name.toCapital()}List(), ${isMultiple})"""
				} else {
					throw new RuntimeException("'Search' is not implemented: field = ${name}")
				}
			} else {
				component = """${imp.add(org.devocative.wickomp.form.WTextInput)}("${name}")""";
			}

			if(field.required) {
				component += "\n\t\t\t.setRequired(true)"
			}

			out << """\t\tfloatTable.add(new ${component}\n\t\t\t.setLabel(new ResourceModel("${cls.simpleName}.${name}", "${name}")));\n"""
		}
	}
%>
		Form<${cls.simpleName}> form = new Form<>("form", new CompoundPropertyModel<>(entity));
		form.add(floatTable);
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasForm && field.isOf(String) && field.textType != "simple") {
			String component

			if(field.textType == "multiline") {
				component = """${imp.add(org.apache.wicket.markup.html.form.TextArea)}("${name}")""";
			} else if (field.textType == "html") {
				//TODO implement HTML
				throw new RuntimeException("HTML not implemented: ${name}")
			} else if (field.textType == "code") {
				component = """${imp.add(org.devocative.wickomp.form.code.WCodeInput)}("${name}", new ${imp.add(org.devocative.wickomp.form.code.OCode)}(${imp.add(org.devocative.wickomp.form.code.OCodeMode)}.${field.codeType}))""";
			} else {
				component = "Unknown textType=[${field.textType}] for field=[${name}]";
			}

			if(field.required) {
				component += "\n\t\t\t.setRequired(true)"
			}

			out << """\t\tform.add(new ${component}\n\t\t\t.setLabel(new ResourceModel("${cls.simpleName}.${name}", "${name}")));\n"""
		}
	}
%>
		form.add(new DAjaxButton("save", new ResourceModel("label.save"), ${imp.add(params["iconClass"])}.SAVE) {
			private static final long serialVersionUID = ${(targetVO.fqn + ".DAjaxButton").hashCode()}L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				${service.name.toUncapital()}.saveOrUpdate(entity);

				if (!WModalWindow.closeParentWindow(${targetVO.name}.this, target)) {
					UrlUtil.redirectTo(${listJ.name}.class);
				}
			}
		});
		add(form);
	}
}