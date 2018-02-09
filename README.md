# Devolcano

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.devocative/demeter-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.devocative/demeter-maven-plugin)

>**Effective development needs efficient tools**

This project consists of multiple maven plugins to help developer to handle the following issues:
- **Code Generation**: generates `Service`, `VO`, `List Page` and `Form Page` for each entity
- **Merge Code**: if you prevent some classes to be overwritten by the Code Generation, this goal helps you to merge your code to generated ones
- **Schema Diff**: call Hibernate schema update methods to just output the SQL update scripts in the console
- **Apply Schema**: if the SQL scripts for database migration are set, this goal apply them in the database
- **Keytool**: creates an JKS file with a defined secret key which can be used by `StringEncryptorUtil`

### `mvn demeter:codegen`
Suppose the `my.pkg.store.entity.Book` entity in the [Store example](https://github.com/mbizhani/Demeter). The following table lists the generated classes and files:

Module | File | Description
------ | ---- | -----------
common | my.pkg.store.iservice.IBookService | interface for the `Service` class
common | my.pkg.store.vo.filter.BookFVO | a filter-value-object used in the filter panel of `List Page`
service | my.pkg.store.service.BookService | the `Service` class for the entity
web | my.pkg.store.web.dpage.BookListDPage | the Wicket's `DPage` for the `List Page` of the entity
web | my/pkg/store/web/dpage/BookListDPage.html | the HTML template for `BookListDPage`
web | my.pkg.store.web.dpage.BookFormDPage | the Wicket's `DPage` for the `Form Page` of the entity
web | my/pkg/store/web/dpage/BookFormDPage.html | the HTML template for `BookFormDPage`

In the root of the project, there is a directory called `dlava`. In this directory there are two files: `Metadata.xml` and `Plan.xml`

The `Metadata.xml` file has code-generated-relative metadata for the class. For now, the file for the [Store example](https://github.com/mbizhani/Demeter) is:
```xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE meta PUBLIC
		"Meta Data"
		"http://www.devocative.org/dtd/devolcano-metadata.dtd">

<meta>
	<filterClass>targetClass.entity</filterClass>
	<classes>
		<class fqn="my.pkg.store.entity.Book">
			<cinfo/>
			<id ref="id"/>
			<fields>
				<field name="id">
					<finfo/>
				</field>
				<field name="name">
					<finfo/>
				</field>
				<field name="creationDate">
					<finfo hasForm="false" hasTimePart="true"/>
				</field>
				<field name="creatorUser">
					<finfo hasForm="false"/>
				</field>
				<field name="creatorUserId">
					<finfo ignore="true"/>
				</field>
				<field name="modificationDate">
					<finfo hasForm="false" hasTimePart="true"/>
				</field>
				<field name="modifierUser">
					<finfo hasForm="false"/>
				</field>
				<field name="modifierUserId">
					<finfo ignore="true"/>
				</field>
				<field name="version">
					<finfo hasForm="false" hasFVO="false"/>
				</field>
			</fields>
		</class>
	</classes>
</meta>
```
Through this file, you can modify the metadata which are necessary for code generation, e.g. the field `creatorUser` has `hasForm="false"`,
which means this field is not generated in the `Form Page`.

Another file, `Plan.xml` shows the overall plan of the generation. A summary of this file is:
```xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE plan PUBLIC
		"Plan"
		"http://www.devocative.org/dtd/devolcano-plan.dtd">

<plan>
	<pre>
		<![CDATA[
			def params = [
				moduleShortName : "STR",
				ajaxEditColumn  : true,
				iconClass       : my.pkg.store.web.StoreIcon,
				privilegeClass  : my.pkg.store.StorePrivilegeKey
			]
		]]>
	</pre>

	<packageMap>
		<from pkg="my.pkg.store.entity" includeSubPackages="true">
			<to pkgReplace="entity:vo.filter" generatorRef="FVO" genDir="common/src/main/java"/>

			<to pkgReplace="entity:iservice" generatorRef="ServiceI" genDir="common/src/main/java"/>
			<to pkgReplace="entity:service" generatorRef="ServiceM" genDir="service/src/main/java"/>

			<to pkgReplace="entity:web.dpage" generatorRef="ListH" genDir="web/src/main/java"/>
			<to pkgReplace="entity:web.dpage" generatorRef="ListJ" genDir="web/src/main/java"/>

			<to pkgReplace="entity:web.dpage" generatorRef="FormH" genDir="web/src/main/java"/>
			<to pkgReplace="entity:web.dpage" generatorRef="FormJ" genDir="web/src/main/java"/>
		</from>
	</packageMap>

	<volcanoes>

		<volcano name="FVO">
			<precondition>
				<![CDATA[
				targetClass.hasFVO && targetClass.entity
				]]>
			</precondition>
			<template file="/templates/FVO.gsp"
					  suffix="FVO"
					  genFileType="java"
					  overwrite="check">
				<overwriteCheckString>//overwrite</overwriteCheckString>
			</template>
		</volcano>

		...

	</volcanoes>
</plan>
```

The plan consists of two parts. In the first part, the code generator scans classes in `pkg` package and for every found class it is passed to a `volcano`.
If the `precondition` for the class passes, the code is generated based on the specified Groovy GSP file. Besides, in the first part, the package replacement and the target directory are defined.
In the second part, the list of `volcano`s are defined, which are addressed by `generatorRef` in the first part.

Every generated file has a code-related comment at the first line: `//overwrite` for Java and `<!--overwrite-->` for the HTML. As these comments are set in the `volcano`,
the code generator first check the presence of the comment at the first line. If the comment is found, the code generator overwrites the file, otherwise the code is generated
in the `dlava` directory as conflicts. Later calling the `mergecode` shows the [JMeld](https://github.com/albfan/jmeld) diff tool with all the conflicted files.

### `mvn demeter:mergecode`
If the code generator can not overwrite the files, it stores the conflict list in the `dlava/diffResolve.txt`. Calling this goal presents the following window:
![JMeld Diff Tool](/doc/img/jmeld-diff.png)
Now you can merge codes and just add what you want from the generated ones.

### `mvn demeter:schemadiff`

### `mvn demeter:applyschema`

### `mvn demeter:keytool`
