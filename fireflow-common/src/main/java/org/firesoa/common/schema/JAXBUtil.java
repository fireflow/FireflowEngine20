package org.firesoa.common.schema;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class JAXBUtil {
	public static PojoSchema generatePojoSchema(Class pojoClass)
			throws Exception {
		if (pojoClass == null) {
			return null;
		}
		PojoSchema pojoSchemaWraper = new PojoSchema();
		pojoSchemaWraper.setPojoClass(pojoClass);
		pojoSchemaWraper.setQname(generatePojoQname(pojoClass));
		
		final String targetNamespaceUri = pojoSchemaWraper.getQname().getNamespaceURI();
		final Map<String,ByteArrayOutputStream> _allSchemas = new HashMap<String,ByteArrayOutputStream>();		
		final List<String> mainSchemaFile = new ArrayList<String>();		
		
		JAXBContext context = JAXBContext.newInstance(pojoClass);

		context.generateSchema(new SchemaOutputResolver() {

			@Override
			public Result createOutput(String namespaceUri,
					String suggestedFileName) throws IOException {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();

				StreamResult result = new StreamResult(outStream);
				
				result.setSystemId(suggestedFileName);
				
				_allSchemas.put(suggestedFileName, outStream);
				
				if (targetNamespaceUri.equals(namespaceUri)){
					//主schema
					mainSchemaFile.add(suggestedFileName);
				}

				return result;
			}

		});
		
		if (mainSchemaFile.isEmpty()){
			throw new Exception("No schema file found for target namespace: "+targetNamespaceUri);
		}
		String mainSchemaFileName = mainSchemaFile.get(0);
		
		Map<String,String> allSchemasAsString = new HashMap<String,String>();
		
		Iterator<String> keys = _allSchemas.keySet().iterator();
		
		while(keys.hasNext()){
			String key = keys.next();
			ByteArrayOutputStream outStream = _allSchemas.get(key);
			String schemaString = outStream.toString(Charset.defaultCharset().name());

			allSchemasAsString.put(key, schemaString);
		}
		
		pojoSchemaWraper.setMainSchemaFileName(mainSchemaFileName);
		pojoSchemaWraper.setAllSchemas(allSchemasAsString);

		return pojoSchemaWraper;
	}

	/**
	 * 根据JAXB-2.2规范产生POJO的xsd QName
	 * 
	 * @return
	 */
	public static QName generatePojoQname(Class pojoClass) throws Exception {
		if (pojoClass == null) {
			return null;
		}
		String localName = null;
		String targetNamespace = "";

		XmlType xmlType = (XmlType) pojoClass.getAnnotation(XmlType.class);// 首先检查是否有@XmlType标注

		// 获取localName
		if (xmlType != null) {
			String name = xmlType.name();
			if (name == null || name.trim().equals("")) {
				throw new Exception(
						"The @XmlType.name() is ““, it cant NOT be a top level XSD type.");
			} else if (name.trim().equals("##default")) {
				localName = java.beans.Introspector.decapitalize(pojoClass
						.getName());
			} else {
				localName = xmlType.name();
			}
		} else {
			localName = java.beans.Introspector.decapitalize(pojoClass
					.getSimpleName());
		}

		// 获取Target NameSpace
		if (xmlType != null && !xmlType.namespace().trim().equals("##default")) {
			targetNamespace = xmlType.namespace();
		} else {
			// 查找package-info.java
			String packageName = pojoClass.getPackage().getName();
			String package_info_class_name = "package-info";
			if (packageName != null && !packageName.trim().equals("")) {
				package_info_class_name = packageName + "."
						+ package_info_class_name;
			}
			try {
				Class pkg_Info_clz = Class.forName(package_info_class_name);
				XmlSchema xmlSchema = (XmlSchema) pkg_Info_clz
						.getAnnotation(XmlSchema.class);
				if (xmlSchema != null) {
					targetNamespace = xmlSchema.namespace();
				}
			} catch (Exception e) {

			}
		}
		return new QName(targetNamespace, localName);

	}
}
