/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.io.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.ModelElementNames;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.impl.ResourceDefImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceDeserializer implements ModelElementNames {
	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
    public static final String JDK_TRANSFORMER_CLASS = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
    
    protected static boolean useJDKTransformerFactory = false;//需要规避bug

	static {
		TransformerFactory transformerFactory = TransformerFactory
		.newInstance();
		if (JDK_TRANSFORMER_CLASS.equals(transformerFactory.getClass().getName())){
			useJDKTransformerFactory = true;
		}
		documentBuilderFactory.setNamespaceAware(true);
	}

	public static List<ResourceDef> deserialize(Document document)
			throws IOException, DeserializerException {
		Element resourcesElement = document.getDocumentElement();

		List<ResourceDef> resources = new ArrayList<ResourceDef>();
		loadResources(resources, resourcesElement);

		return resources;
	}

	public static List<ResourceDef> deserialize(InputStream in)
			throws IOException, DeserializerException {
		try {

			DocumentBuilder docBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = docBuilder.parse(in);

			return deserialize(document);
		} catch (ParserConfigurationException e) {
			throw new DeserializerException(e);
		} catch (SAXException e) {
			throw new DeserializerException(e);
		} finally {
		}
	}

	public static void loadResources(List<ResourceDef> resources,
			Element resourcesElem) throws DeserializerException {
		if (resourcesElem == null)
			return;
		resources.clear();
		List<Element> rscElms = Util4Deserializer.children(resourcesElem,
				RESOURCE);
		if (rscElms == null)
			return;
		for (Element rscElm : rscElms) {
			ResourceDefImpl resource = new ResourceDefImpl();
			resource.setName(rscElm.getAttribute(NAME));
			resource.setDisplayName(rscElm.getAttribute(DISPLAY_NAME));

			String resourceType = rscElm.getAttribute(RESOURCE_TYPE);
			resource.setResourceType(ResourceType.fromValue(resourceType));

			resource.setDescription(loadCDATA(Util4Deserializer.child(rscElm,
					DESCRIPTION)));
			
			resource.setValue(rscElm.getAttribute(VALUE));

			Element resolverElm = Util4Deserializer.child(rscElm, RESOLVER);
			if (resolverElm != null) {

				resource.setResolverBeanName(resolverElm
						.getAttribute(BEAN_NAME));
				resource.setResolverClassName(resolverElm
						.getAttribute(CLASS_NAME));

			}// if (resolverElm!=null)

			loadExtendedAttributes(resource.getExtendedAttributes(),
					Util4Deserializer.child(rscElm, EXTENDED_ATTRIBUTES));

			resources.add(resource);
		}// for (Element rscElm : rscElms)
	}

	protected static void loadExtendedAttributes(
			Map<String, String> extendedAttributes, Element element)
			throws DeserializerException {

		if (element == null) {
			return;
		}
		extendedAttributes.clear();
		List<Element> extendAttributeElementsList = Util4Deserializer.children(
				element, EXTENDED_ATTRIBUTE);
		Iterator<Element> iter = extendAttributeElementsList.iterator();
		while (iter.hasNext()) {
			Element extAttrElement = iter.next();
			String name = extAttrElement.getAttribute(NAME);
			String value = extAttrElement.getAttribute(VALUE);

			extendedAttributes.put(name, value);

		}
	}
	
	protected static String loadCDATA(Element cdataElement){
		if (cdataElement==null){
			return "";
		}else{
			String data = cdataElement.getTextContent();
			if (data==null)return data;
			else{
				if (useJDKTransformerFactory){
					if (data.startsWith(" ")){
						return data.substring(1);//去掉一个空格
					}
				}
				return data;
			}
		}
	}
	

}
