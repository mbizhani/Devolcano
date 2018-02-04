<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO fvo = context.getGenTarget(cls, "FVO")
	org.devocative.devolcano.GenTargetVO iservice = context.getGenTarget(cls, "ServiceI")
	org.devocative.devolcano.GenTargetVO service = context.getGenTarget(cls, "ServiceM")
	org.devocative.devolcano.GenTargetVO formJ = context.getGenTarget(cls, "FormJ")

	Class prvlg = params["privilegeClass"];
	if(prvlg != null) {
		imp.add(prvlg)
	}

	List commonFields = ["rowMod", "creationDate", "creatorUser", "modificationDate", "modifierUser", "version"]

	imp.add(cls)
	imp.add(fvo)
	imp.add(iservice)
	if (formJ != null) {
		imp.add(formJ)
	}

	imp.add(javax.inject.Inject)
	imp.add(List)
	imp.add(Collections)

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

	private ${fvo.name} filter;
	private boolean formVisible = true;
	private String[] invisibleFormItems;

	private WDataGrid<${cls.simpleName}> grid;
	private String[] removeColumns;

	private Boolean gridFit;
	private boolean gridEnabled = false;
	private OSize gridHeight = OSize.fixed(500);
	private OSize gridWidth = OSize.percent(100);

	// ------------------------------

	// Panel Call - New Filter
	public ${targetVO.name}(String id) {
		this(id, Collections.<String>emptyList(), new ${fvo.name}());
	}

	// Panel Call - Open Filter
	public ${targetVO.name}(String id, ${fvo.name} filter) {
		this(id, Collections.<String>emptyList(), filter);
	}

	// REST Call - New Filter
	public ${targetVO.name}(String id, List<String> params) {
		this(id, params, new ${fvo.name}());
	}

	// Main Constructor
	private ${targetVO.name}(String id, List<String> params, ${fvo.name} filter) {
		super(id, params);

		this.filter = filter;
	}

	// ------------------------------

	@Override
	protected void onInitialize() {
		super.onInitialize();
<%
    if(params["ajaxEditColumn"] && formJ != null) { %>
		final ${imp.add(org.devocative.wickomp.html.window.WModalWindow)} window = new WModalWindow("window");
		add(window);
<%
    	if(cls.hasAdd) { %>
		add(new ${imp.add(org.devocative.wickomp.html.WAjaxLink)}("add", ${imp.add(params["iconClass"])}.ADD) {
			private static final long serialVersionUID = ${(targetVO.fqn + ".WAjaxLink").hashCode()}L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				window.setContent(new ${formJ.name}(window.getContentId()));
				window.show(target);
			}
		}<%= prvlg != null ? ".setVisible(hasPermission(${prvlg.simpleName}.${cls.simpleName}Add))":"" %>);
<% 		}
	} %>
		WFloatTable floatTable = new WFloatTable("floatTable");
<%
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.hasFVO) {
			String component

			if (field.isOf(Number)) {
				component = """${imp.add(org.devocative.wickomp.form.range.WNumberRangeInput)}("${name}", ${imp.add(field.type)}.class)"""
			} else if (field.isOf(Date)) {
				component = """${imp.add(org.devocative.wickomp.form.range.WDateRangeInput)}("${name}")\n\t\t\t.setTimePartVisible(${field.info.hasTimePart})"""
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

			String visibility = ""
			if(field.isOf(org.devocative.demeter.entity.ERowMod)) {
				visibility = "\n\t\t\t.setVisible(getCurrentUser().isRoot())"
			}

			out << """\t\tfloatTable.add(new ${component}\n\t\t\t.setLabel(new ResourceModel("${commonFields.contains(name) ? "entity" : cls.simpleName}.${name}", "${name}"))${visibility});\n"""
		}
	}
%>
		Form<${fvo.name}> form = new Form<>("form", new CompoundPropertyModel<>(filter));
		form.add(floatTable);
		form.add(new DAjaxButton("search", new ResourceModel("label.search"), ${imp.add(params["iconClass"])}.SEARCH) {
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
			String genericType = ""

			if (field.isOf(Date)) {
				cellFormatter = "\n\t\t\t"
				genericType = cls.simpleName;

				if (field.info.hasTimePart) {
					cellFormatter += ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ODateFormatter)}.getDateTimeByUserPreference())"
				} else {
					cellFormatter += ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ODateFormatter)}.getDateByUserPreference())"
				}

				cellFormatter += """\n\t\t\t.setStyle("direction:ltr")""";
			} else if (field.isOf(Boolean)) {
				cellFormatter = "\n\t\t\t"
				genericType = cls.simpleName;
				cellFormatter += ".setFormatter(${imp.add(org.devocative.wickomp.formatter.OBooleanFormatter)}.bool())"
			} else if (field.isOf(Number)) {
				cellFormatter = "\n\t\t\t"
				genericType = cls.simpleName;
				if (field.real) {
					cellFormatter += ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ONumberFormatter)}.real())"
				} else {
					cellFormatter += ".setFormatter(${imp.add(org.devocative.wickomp.formatter.ONumberFormatter)}.integer())"
				}
				cellFormatter += """\n\t\t\t.setStyle("direction:ltr")""";
			}

			if(field.isOf(org.devocative.demeter.entity.ERowMod)) {
				out << "\t\tif(getCurrentUser().isRoot()) {\n\t"
			}
			out << """\t\tcolumnList.add(new OPropertyColumn<${genericType}>(new ResourceModel("${commonFields.contains(name) ? "entity" : cls.simpleName}.${name}", "${name}"), "${name}")${cellFormatter});\n"""
			if(field.isOf(org.devocative.demeter.entity.ERowMod)) {
				out << "\t\t}\n"
			}
		}
	}

	if(formJ != null) {
		if(params["ajaxEditColumn"]) {
			if(prvlg != null) {
%>
		if (hasPermission(${prvlg.simpleName}.${cls.simpleName}Edit)) {
			columnList.add(new ${imp.add(org.devocative.demeter.web.component.grid.OEditAjaxColumn)}<${cls.simpleName}>() {
				private static final long serialVersionUID = ${(targetVO.fqn + ".OEditAjaxColumn").hashCode()}L;

				@Override
				public void onClick(AjaxRequestTarget target, IModel<${cls.simpleName}> rowData) {
					window.setContent(new ${formJ.name}(window.getContentId(), rowData.getObject()));
					window.show(target);
				}
			});
		}
<%
			} else {
%>
		columnList.add(new ${imp.add(org.devocative.demeter.web.component.grid.OEditAjaxColumn)}<${cls.simpleName}>() {
			private static final long serialVersionUID = ${(targetVO.fqn + ".OEditAjaxColumn").hashCode()}L;

			@Override
			public void onClick(AjaxRequestTarget target, IModel<${cls.simpleName}> rowData) {
				window.setContent(new ${formJ.name}(window.getContentId(), rowData.getObject()));
				window.show(target);
			}
		});
<%
			}
		} else {
%>
		columnList.add(new ${imp.add(org.devocative.demeter.web.component.grid.ORESTLinkColumn)}<${cls.simpleName}>(new Model<String>(), ${formJ.name}.class, "${cls.idField.name}", ${imp.add(params["iconClass"])}.EDIT));
<%
		}
	}
%>
		OGrid<${cls.simpleName}> oGrid = new OGrid<>();
		oGrid
			.setColumns(columnList)
			.setMultiSort(false)
			.setHeight(gridHeight)
			.setWidth(gridWidth)
			.setFit(gridFit);

		grid = new WDataGrid<>("grid", oGrid, this);
		add(grid);

		// ---------------

		form.setVisible(formVisible);
		grid.setEnabled(gridEnabled || !formVisible);

		if (invisibleFormItems != null) {
			for (String formItem : invisibleFormItems) {
				floatTable.get(formItem).setVisible(false);
			}
		}

		if (removeColumns != null) {
			for (String column : removeColumns) {
				columnList.removeColumn(column);
			}
		}
	}

	// ------------------------------

	public ${targetVO.name} setFormVisible(boolean formVisible) {
		this.formVisible = formVisible;
		return this;
	}

	public ${targetVO.name} setInvisibleFormItems(String... invisibleFormItems) {
		this.invisibleFormItems = invisibleFormItems;
		return this;
	}

	public ${targetVO.name} setGridHeight(OSize gridHeight) {
		this.gridHeight = gridHeight;
		return this;
	}

	public ${targetVO.name} setGridWidth(OSize gridWidth) {
		this.gridWidth = gridWidth;
		return this;
	}

	public ${targetVO.name} setGridFit(Boolean gridFit) {
		this.gridFit = gridFit;
		return this;
	}

	public ${targetVO.name} setGridEnabled(boolean gridEnabled) {
		this.gridEnabled = gridEnabled;
		return this;
	}

	public ${targetVO.name} setRemoveColumns(String... removeColumns) {
		this.removeColumns = removeColumns;
		return this;
	}

	// ------------------------------ IGridDataSource

	@Override
	public List<${cls.simpleName}> list(long pageIndex, long pageSize, List<WSortField> sortFields) {
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