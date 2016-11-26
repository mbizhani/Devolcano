<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO fvo = context.getGenTarget(cls, "FVO")
	org.devocative.devolcano.GenTargetVO iservice = context.getGenTarget(cls, "ServiceI")
	org.devocative.devolcano.GenTargetVO service = context.getGenTarget(cls, "ServiceM")
	org.devocative.devolcano.GenTargetVO formJ = context.getGenTarget(cls, "FormJ")

	List commonFields = ["creationDate", "creatorUser", "modificationDate", "modifierUser", "version"]

	imp.add(cls)
	imp.add(fvo)
	imp.add(iservice)
	imp.add(formJ)

	imp.add(javax.inject.Inject)
	imp.add(List)

	imp.add(org.devocative.demeter.web.DPage)
	imp.add(org.devocative.demeter.web.component.DAjaxButton)

	imp.add(org.devocative.wickomp.grid.IGridDataSource)
	imp.add(org.devocative.wickomp.grid.WDataGrid)
	imp.add(org.devocative.wickomp.html.WFloatTable)
	imp.add(org.devocative.wickomp.grid.column.OColumnList)
	imp.add(org.devocative.wickomp.grid.column.OPropertyColumn)
	imp.add(org.devocative.wickomp.grid.OGrid)
	imp.add(org.devocative.wickomp.WModel)
	imp.add(org.devocative.wickomp.opt.OSize)
	imp.add(org.devocative.wickomp.grid.WSortField)

	imp.add(org.apache.wicket.markup.html.form.Form)
	imp.add(org.apache.wicket.model.CompoundPropertyModel)
	imp.add(org.apache.wicket.model.ResourceModel)
	imp.add(org.apache.wicket.ajax.AjaxRequestTarget)
	imp.add(org.apache.wicket.model.IModel)
	imp.add(org.apache.wicket.model.Model)
%>
package ${targetVO.pkg};

@IMPORT@

public class ${targetVO.name} extends DPage implements IGridDataSource<${cls.simpleName}> {
	private static final long serialVersionUID = ${targetVO.fqn.hashCode()}L;

	@Inject
	private ${iservice.name} ${service.name.toUncapital()};

	private ${fvo.name} filter = new ${fvo.name}();
	private WDataGrid<${cls.simpleName}> grid;

	// ------------------------------

	public ${targetVO.name}(String id, List<String> params) {
		super(id, params);
	}

	// ------------------------------

	@Override
	protected void onInitialize() {
		super.onInitialize();
<% 	if(params["ajaxEditColumn"]) { %>
		final ${imp.add(org.devocative.wickomp.html.window.WModalWindow)} window = new WModalWindow("window");
		window.getOptions().setHeight(OSize.percent(80)).setWidth(OSize.percent(80));
		add(window);
<% } %>
		WFloatTable floatTable = new WFloatTable("floatTable");
		floatTable.setEqualWidth(true);
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasSVO) {
			String component

			if (field.isOf(Number)) {
				component = """${imp.add(org.devocative.wickomp.form.WNumberRangeInput)}("${name}", ${imp.add(field.type)}.class)"""
			} else if (field.isOf(Date)) {
				component = """${imp.add(org.devocative.wickomp.form.WDateRangeInput)}("${name}")"""
			} else if (field.isOf(Boolean)) {
				component = """${imp.add(org.devocative.wickomp.form.WBooleanInput)}("${name}")"""
			} else if (field.embedded) {
				component = """${imp.add(org.devocative.wickomp.form.WSelectionInput)}("${name}", ${imp.add(field.mainType)}.list(), true)"""
			} else if (field.association) {
				if (field.listType == "simple") {
					component = """${imp.add(org.devocative.wickomp.form.WSelectionInput)}("${name}", ${service.name.toUncapital()}.get${name.toCapital()}List(), true)"""
				} else {
					throw new RuntimeException("'search' is not implemented: field = ${name}")
				}
			} else {
				component = """${imp.add(org.devocative.wickomp.form.WTextInput)}("${name}")""";
			}

			out << """\t\tfloatTable.add(new ${component}.setLabel(new ResourceModel("${commonFields.contains(name) ? "entity" : cls.simpleName}.${name}")));\n"""
		}
	}
%>
		Form<${fvo.name}> form = new Form<>("form", new CompoundPropertyModel<>(filter));
		form.add(floatTable);
		form.add(new DAjaxButton("search", new ResourceModel("label.search")) {
			private static final long serialVersionUID = ${(targetVO.fqn + ".DAjaxButton").hashCode()}L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				grid.setEnabled(true);
				grid.loadData(target);
			}
		});
		add(form);

		OColumnList<${cls.simpleName}> columnList = new OColumnList<>();
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasList) {
			String cellFormatter = ""

			if (field.isOf(Date)) {
				cellFormatter = ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ODateFormatter)}.getDateTimeByUserPreference())"
			} else if (field.isOf(Boolean)) {
				cellFormatter = ".setFormatter(${imp.add(org.devocative.wickomp.formatter.OBooleanFormatter)}.bool())"
			} else if (field.isOf(Number)) {
				if (field.real) {
					cellFormatter = ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ONumberFormatter)}.real())"
				} else {
					cellFormatter = ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ONumberFormatter)}.integer())"
				}
			}

			out << """\t\tcolumnList.add(new OPropertyColumn<${cls.simpleName}>(new ResourceModel("${commonFields.contains(name) ? "entity" : cls.simpleName}.${name}"), "${name}")${cellFormatter});\n"""
		}
	}

	if(params["ajaxEditColumn"]) {
%>
		columnList.add(new ${imp.add(org.devocative.wickomp.grid.column.link.OAjaxLinkColumn)}<${cls.simpleName}>(new Model<String>(), ${imp.add(params["iconClass"])}.EDIT) {
			private static final long serialVersionUID = ${(targetVO.fqn + ".OAjaxLinkColumn").hashCode()}L;

			@Override
			public void onClick(AjaxRequestTarget target, IModel<${cls.simpleName}> rowData) {
				window.setContent(new ${formJ.name}(window.getContentId(), rowData.getObject()));
				window.show(target);
			}
		});
<% } else { %>
		columnList.add(new ${imp.add(org.devocative.demeter.web.component.grid.ORESTLinkColumn)}<${cls.simpleName}>(new Model<String>(), ${formJ.name}.class, "${cls.idField.name}", ${imp.add(params["iconClass"])}.EDIT));
<% } %>
		OGrid<${cls.simpleName}> oGrid = new OGrid<>();
		oGrid
			.setColumns(columnList)
			.setMultiSort(false)
			.setHeight(OSize.fixed(500))
			.setWidth(OSize.percent(100));

		grid = new WDataGrid<>("grid", oGrid, this);
		grid.setEnabled(false);
		add(grid);
	}

	public List<${cls.simpleName}> list(long pageIndex, long pageSize, List<WSortField>sortFields) {
		return ${service.name.toUncapital()}.search(filter, pageIndex, pageSize);
	}

	@Override
	public long count() {
		return ${service.name.toUncapital()}.count(filter);
	}

	@Override
	public IModel<${cls.simpleName}> model(${cls.simpleName} object) {
		return new WModel<>(object);
	}
}