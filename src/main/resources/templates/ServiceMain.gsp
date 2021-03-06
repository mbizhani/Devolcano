<%
	org.devocative.devolcano.vo.ClassVO cls = targetClass
	org.devocative.devolcano.ImportHelper imp = importHelper
	org.devocative.devolcano.ContextVO context = context

	org.devocative.devolcano.GenTargetVO fvo = context.getGenTarget(cls, "FVO")
	org.devocative.devolcano.GenTargetVO iservice = context.getGenTarget(cls, "ServiceI")

	if (fvo != null) {
		imp.add(fvo)
	}
	imp.add(cls)
	imp.add(List.class)

	imp.add(iservice)
	imp.add(org.devocative.demeter.iservice.persistor.IPersistorService)
	imp.add(org.springframework.stereotype.Service)
	imp.add(org.springframework.beans.factory.annotation.Autowired)

	imp.add(org.slf4j.Logger)
	imp.add(org.slf4j.LoggerFactory)

%>
package ${targetVO.pkg};

@IMPORT@

@Service("${params["moduleShortName"]}${targetVO.name}")
public class ${targetVO.name} implements ${iservice.name} {
	private static final Logger logger = LoggerFactory.getLogger(${targetVO.name}.class);

	@Autowired
	private IPersistorService persistorService;

	// ------------------------------

	@Override
	public void saveOrUpdate(${cls.simpleName} entity) {
		persistorService.saveOrUpdate(entity);
	}

	@Override
	public ${cls.simpleName} load(${imp.add(cls.idField.type)} ${cls.idField.name}) {
		return persistorService.get(${cls.simpleName}.class, ${cls.idField.name});
	}
<%
	// Generating load by unique fields
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if(field.ok && field.unique) {
%>
	@Override
	public ${cls.simpleName} loadBy${field.name.toCapital()}(${imp.add(field.mainType)} ${field.name}) {
		return persistorService
			.createQueryBuilder()
			.addFrom(${cls.simpleName}.class, "ent")
			.addWhere("and ent.${field.name} = :${field.name}")
			.addParam("${field.name}", ${field.name})
			.object();
	}
<%
			}
	}
%>
	@Override
	public List<${cls.simpleName}> list() {
		return persistorService.list(${cls.simpleName}.class);
	}
<% if (fvo != null) { %>
	@Override
	public List<${cls.simpleName}> search(${fvo.name} filter, long pageIndex, long pageSize) {
		return persistorService
			.createQueryBuilder()
			.addSelect("select ent")
			.addFrom(${cls.simpleName}.class, "ent")
			.applyFilter(${cls.simpleName}.class, "ent", filter)
			.list((pageIndex - 1) * pageSize, pageSize);
	}

	@Override
	public long count(${fvo.name} filter) {
		return persistorService
			.createQueryBuilder()
			.addSelect("select count(1)")
			.addFrom(${cls.simpleName}.class, "ent")
			.applyFilter(${cls.simpleName}.class, "ent", filter)
			.object();
	}
<% }
	// Generating list for associations
	cls.allFieldsMap.each { String name, org.devocative.devolcano.vo.FieldVO field ->
		if (field.ok && field.association && (field.hasFVO || field.hasForm)) {
			String type = imp.add(field.mainType)
%>
	@Override
	public List<${type}> get${field.name.toCapital()}List() {
		return persistorService.list(${type}.class);
	}
<%
		}
	}
%>
	// ==============================
}
