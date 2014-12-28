/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.firesoa.common.schema;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupMember;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaAttributeOrGroupRef;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContent;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaContentModel;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.utils.XmlSchemaRef;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class DOMInitializer {
	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private static final TransformerFactory transformerFactory = TransformerFactory
			.newInstance();

	public static String dom2String(Document dom) {
		try {
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, Charset.defaultCharset().name());
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// transformer.transform()方法 将 XML Source转换为 Result
			transformer.transform(new DOMSource(dom), new StreamResult(
					outputStream));
			return outputStream.toString();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	public static Document generateDocument(
			XmlSchemaCollection xmlSchemaCollection, QName rootElementQName)
			throws ParserConfigurationException {
		return generateDocument(xmlSchemaCollection, rootElementQName, false);
	}

	/**
	 * 根据schema创建一个示例Document
	 * 
	 * @param xmlSchemaCollection
	 *            Schema
	 * @param rootElementQName
	 *            根元素的QName
	 * @param createChoice
	 *            遇到XSD中的schema指示器时，是否创建choice中的元素。默认应该不创建。
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document generateDocument(
			XmlSchemaCollection xmlSchemaCollection, QName rootElementQName,
			boolean createChoice) throws ParserConfigurationException {
		
		XmlSchemaElement xmlSchemaElement = xmlSchemaCollection
		.getElementByQName(rootElementQName);
		
		if (xmlSchemaElement==null) return null;
		
		DocumentBuilder docBuilder = documentBuilderFactory
				.newDocumentBuilder();

		Document doc = docBuilder.newDocument();

		
		createElement(doc, doc, null, xmlSchemaElement, xmlSchemaCollection,
				createChoice);
		return doc;
	}

	/**
	 * 创建一个Element节点
	 * @param doc
	 * @param parentNode
	 * @param childSchemaElement
	 * @param xmlSchemaCollection
	 * @param createChoice
	 */
	protected static void createElement(Document doc, Node parentNode,
			XmlSchemaElement parentSchemaElement,
			XmlSchemaElement childSchemaElement,
			XmlSchemaCollection xmlSchemaCollection, boolean createChoice) {
		
		// 1、创建根节点
		XmlSchemaRef<XmlSchemaElement> ref = childSchemaElement.getRef();
		if (ref != null && ref.getTarget() != null) {
			childSchemaElement = ref.getTarget();
		}
		
		if (childSchemaElement.getName().equals("foo_1")){
			System.out.println("this is foo_1");
		}
		
		QName parentElmTypeQName = null;
		if (parentSchemaElement!=null){
			parentElmTypeQName = parentSchemaElement.getSchemaTypeName();
			if (parentElmTypeQName==null){
				parentElmTypeQName = parentSchemaElement.getSchemaType().getQName();
			}
			
		}
		
		
		XmlSchemaForm schemaElementForm = childSchemaElement.getForm();
		
		QName childElmQName = childSchemaElement.getQName();
		Element childElem = null;		
		if (schemaElementForm!=null && XmlSchemaForm.QUALIFIED.equals(schemaElementForm)){
			childElem = doc.createElementNS(childElmQName.getNamespaceURI(),
					getQualifiedName(doc, childElmQName));
		}else{
			if (parentElmTypeQName==null || !equalStrings(parentElmTypeQName.getNamespaceURI(),childElmQName.getNamespaceURI()) ){
				//根节点
				childElem = doc.createElementNS(childElmQName.getNamespaceURI(),
						getQualifiedName(doc, childElmQName));
			}
			else{
				//其他节点都不需要namespace prefix
				childElem = doc.createElement(childElmQName.getLocalPart());
			}
		}

		parentNode.appendChild(childElem);

		// 2、初始化属性及下级节点
		XmlSchemaType xmlSchemaType = childSchemaElement.getSchemaType();
		if (xmlSchemaType == null) {
			QName qname = childSchemaElement.getSchemaTypeName();
			xmlSchemaType = xmlSchemaCollection.getTypeByQName(qname);
		}
		if (xmlSchemaType instanceof XmlSchemaSimpleType) {
			// 简单类型
			XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) xmlSchemaType;
			String value = "";
			if (!StringUtils.isEmpty(childSchemaElement.getFixedValue())) {
				value = childSchemaElement.getFixedValue();
			} else if (!StringUtils.isEmpty(childSchemaElement.getDefaultValue())) {
				value = childSchemaElement.getDefaultValue();
			}
			if (!StringUtils.isEmpty(value)) {
				childElem.appendChild(doc.createTextNode(value));
			}
		} else if (xmlSchemaType instanceof XmlSchemaComplexType) {
			XmlSchemaComplexType complexType = (XmlSchemaComplexType) xmlSchemaType;

			createChildNode4ComplexType(doc, childElem,childSchemaElement, complexType,
					xmlSchemaCollection, createChoice);
		}
	}

	/**
	 * 创建Element的子Node，包括创建属性和子Element
	 * @param doc
	 * @param parentElement
	 * @param parentSchemaElementComplexType
	 * @param xmlSchemaCollection
	 * @param createChoice
	 */
	protected static void createChildNode4ComplexType(Document doc,
			Element parentElement,XmlSchemaElement parentSchemaElement, XmlSchemaComplexType parentSchemaElementComplexType,
			XmlSchemaCollection xmlSchemaCollection, boolean createChoice) {
		// 2.1 初始化attribute
		List<XmlSchemaAttributeOrGroupRef> attributeSchemaList = parentSchemaElementComplexType
				.getAttributes();
		for (XmlSchemaAttributeOrGroupRef attrOrGroupRef : attributeSchemaList) {
			createAttribute(doc, parentElement,
					(XmlSchemaAttributeGroupMember) attrOrGroupRef);
		}

		// 2.2初始化子节点
		// TODO 仅处理了Particle的情况，ComplextContent的情况待处理
		XmlSchemaParticle particle = parentSchemaElementComplexType.getParticle();
		if (particle != null) {
			if (particle instanceof XmlSchemaAll) {
				XmlSchemaAll schemaAll = (XmlSchemaAll) particle;
				List<XmlSchemaElement> items = schemaAll.getItems();
				for (XmlSchemaElement item : items) {
					createElement(doc, parentElement,parentSchemaElement, item, xmlSchemaCollection,
							createChoice);
				}
			} else if (particle instanceof XmlSchemaSequence) {
				XmlSchemaSequence schemaSeq = (XmlSchemaSequence) particle;
				createChildElement4Sequence(doc, parentElement,parentSchemaElement, schemaSeq,
						xmlSchemaCollection, createChoice);
			} else if (particle instanceof XmlSchemaChoice) {
				createChildElement4Choice(doc, parentElement,parentSchemaElement,
						(XmlSchemaChoice) particle, xmlSchemaCollection,
						createChoice);

			} else if (particle instanceof XmlSchemaGroupRef) {
				// TODO 如何处理？
			}
		} else {
			XmlSchemaContentModel contentModel = parentSchemaElementComplexType.getContentModel();
			if (contentModel instanceof XmlSchemaComplexContent) {
				XmlSchemaComplexContent complexContentModel = (XmlSchemaComplexContent) contentModel;
				XmlSchemaContent complexContent = complexContentModel
						.getContent();
				if (complexContent instanceof XmlSchemaComplexContentExtension) {
					XmlSchemaComplexContentExtension complexContentExtension = (XmlSchemaComplexContentExtension) complexContent;
					createChildElement4ComplexContentExtension(doc, parentElement,parentSchemaElement,
							complexContentExtension, xmlSchemaCollection,
							createChoice);
				} else if (complexContent instanceof XmlSchemaComplexContentRestriction) {
					//TODO 待处理
				}
			} else if (contentModel instanceof XmlSchemaSimpleContent) {
				// TODO simple
			}
		}
	}

	protected static void createChildElement4ComplexContentExtension(
			Document doc, Element parentElement,XmlSchemaElement parentSchemaElement,
			XmlSchemaComplexContentExtension complexContentExtension,
			XmlSchemaCollection xmlSchemaCollection, boolean createChoice) {
		QName baseTypeQName = complexContentExtension.getBaseTypeName();
		XmlSchemaComplexType comlexBaseType = (XmlSchemaComplexType) xmlSchemaCollection
				.getTypeByQName(baseTypeQName);
		createChildNode4ComplexType(doc, parentElement,parentSchemaElement, comlexBaseType,
				xmlSchemaCollection, createChoice);

		// 2.1 初始化attribute
		List<XmlSchemaAttributeOrGroupRef> attributeSchemaList = complexContentExtension
				.getAttributes();
		for (XmlSchemaAttributeOrGroupRef attrOrGroupRef : attributeSchemaList) {
			createAttribute(doc, parentElement,
					(XmlSchemaAttributeGroupMember) attrOrGroupRef);
		}

		// 2.2初始化子节点
		// TODO 仅处理了Particle的情况，ComplextContent的情况待处理
		XmlSchemaParticle particle = complexContentExtension.getParticle();
		if (particle != null) {
			if (particle instanceof XmlSchemaAll) {
				XmlSchemaAll schemaAll = (XmlSchemaAll) particle;
				List<XmlSchemaElement> items = schemaAll.getItems();
				for (XmlSchemaElement item : items) {
					createElement(doc, parentElement, parentSchemaElement,item, xmlSchemaCollection,
							createChoice);
				}
			} else if (particle instanceof XmlSchemaSequence) {
				XmlSchemaSequence schemaSeq = (XmlSchemaSequence) particle;
				createChildElement4Sequence(doc, parentElement, parentSchemaElement,schemaSeq,
						xmlSchemaCollection, createChoice);
			} else if (particle instanceof XmlSchemaChoice) {
				createChildElement4Choice(doc, parentElement,parentSchemaElement,
						(XmlSchemaChoice) particle, xmlSchemaCollection,
						createChoice);

			} else if (particle instanceof XmlSchemaGroupRef) {
				// TODO 如何处理？
			}
		}

	}

	protected static void createChildElement4Sequence(Document doc,
			Element parentElement, XmlSchemaElement parentSchemaElement,XmlSchemaSequence sequence,
			XmlSchemaCollection xmlSchemaCollection, boolean createChoice) {

		List<XmlSchemaSequenceMember> items = sequence.getItems();
		for (XmlSchemaSequenceMember item : items) {
			if (item instanceof XmlSchemaElement) {
				createElement(doc, parentElement,parentSchemaElement, ((XmlSchemaElement) item),
						xmlSchemaCollection, createChoice);
			} else if (item instanceof XmlSchemaSequence) {
				createChildElement4Sequence(doc, parentElement,parentSchemaElement,
						((XmlSchemaSequence) item), xmlSchemaCollection,
						createChoice);
			} else if (item instanceof XmlSchemaChoice) {
				createChildElement4Choice(doc, parentElement, parentSchemaElement,
						((XmlSchemaChoice) item), xmlSchemaCollection,
						createChoice);
			} else if (item instanceof XmlSchemaAny) {
				// TODO
			} else if (item instanceof XmlSchemaGroup) {
				// TODO
			} else if (item instanceof XmlSchemaGroupRef) {
				// TODO
			}
		}

	}

	protected static void createChildElement4Choice(Document doc,
			Element parentElement, XmlSchemaElement parentSchemaElement, XmlSchemaChoice choice,
			XmlSchemaCollection xmlSchemaCollection, boolean createChoice) {
		if (!createChoice)
			return;
		List<XmlSchemaObject> choiceMember = choice.getItems();
		for (XmlSchemaObject schemaObj : choiceMember) {
			if (schemaObj instanceof XmlSchemaElement) {
				createElement(doc, parentElement, parentSchemaElement,((XmlSchemaElement) schemaObj),
						xmlSchemaCollection, createChoice);
			} else if (schemaObj instanceof XmlSchemaSequence) {
				createChildElement4Sequence(doc, parentElement,parentSchemaElement,
						((XmlSchemaSequence) schemaObj), xmlSchemaCollection,
						createChoice);
			} else if (schemaObj instanceof XmlSchemaChoice) {
				createChildElement4Choice(doc, parentElement,parentSchemaElement,
						((XmlSchemaChoice) schemaObj), xmlSchemaCollection,
						createChoice);
			}
		}
	}

	//TODO 需要进一步区分attribute qualified和unqualified
	protected static void createAttribute(Document doc, Element elem,
			XmlSchemaAttributeGroupMember attrMember) {
		
		if (attrMember instanceof XmlSchemaAttribute) {
			XmlSchemaAttribute attrSchema = (XmlSchemaAttribute) attrMember;

			XmlSchemaForm attrForm = attrSchema.getForm();
			
			Attr attr = null;
			QName attrSchemaQName = attrSchema.getQName();
			if (XmlSchemaForm.QUALIFIED.equals(attrForm)){
				String attrQualifiedName = getQualifiedName(doc,attrSchemaQName);
				attr = doc.createAttributeNS(attrSchemaQName.getNamespaceURI(),attrQualifiedName);
			}else{
				attr = doc.createAttribute(attrSchema.getName());
			}
			
			elem.setAttributeNode(attr);
			String value = "";
			if (!StringUtils.isEmpty(attrSchema.getFixedValue())) {
				value = attrSchema.getFixedValue();
			} else if (!StringUtils.isEmpty(attrSchema.getDefaultValue())) {
				value = attrSchema.getDefaultValue();
			}
			if (!StringUtils.isEmpty(value)) {
				attr.setValue(value);
			}
		} else if (attrMember instanceof XmlSchemaAttributeGroupRef) {
			XmlSchemaAttributeGroupRef attrGroupRef = (XmlSchemaAttributeGroupRef) attrMember;
			List<XmlSchemaAttributeGroupMember> groupMembers = attrGroupRef
					.getRef().getTarget().getAttributes();
			for (XmlSchemaAttributeGroupMember groupMember : groupMembers) {
				createAttribute(doc, elem, groupMember);
			}

		} else if (attrMember instanceof XmlSchemaAttributeGroup) {
			XmlSchemaAttributeGroup attrGroup = (XmlSchemaAttributeGroup) attrMember;
			List<XmlSchemaAttributeGroupMember> groupMembers = attrGroup
					.getAttributes();
			for (XmlSchemaAttributeGroupMember groupMember : groupMembers) {
				createAttribute(doc, elem, groupMember);
			}
		}

	}

	private static boolean equalStrings(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		s1 = s1 == null ? "" : s1.trim();
		s2 = s2 == null ? "" : s2.trim();
		return s1.equals(s2);
	}

	private static String getQualifiedName(Document doc, QName qName) {
		Element rootElement = (Element) doc.getDocumentElement();

		
		if (rootElement != null
				&& !equalStrings(qName.getNamespaceURI(),
						rootElement.getNamespaceURI())) {
//			Attr attrTmp = rootElement.getAttributeNodeNS(Constants.XMLNS_ATTRIBUTE_NS_URI, "ns0");
//			System.out.println("===========found attrTmp=="+attrTmp);
			
			String nsPrefix = null;
			nsPrefix = rootElement.lookupPrefix(qName.getNamespaceURI());
			if (nsPrefix==null || nsPrefix.trim().equals("")){
				int nsNumber = 1;
				NamedNodeMap attrMap = rootElement.getAttributes();
				int length = attrMap.getLength();
				
				for (int i = 0; i < length; i++) {
					Attr attr = (Attr) attrMap.item(i);
					String name = attr.getName();
					if (name.startsWith(Constants.XMLNS_ATTRIBUTE)) {
						if (attr.getValue().equals(qName.getNamespaceURI())) {
							// Namespace已经声明了
							nsPrefix = attr.getLocalName();
							break;
						}
						nsNumber++;
					}
				}
				if (nsPrefix == null) {
					nsPrefix = "ns" + nsNumber;
				}
			}


			Attr attr = doc.createAttributeNS(Constants.XMLNS_ATTRIBUTE_NS_URI,
					Constants.XMLNS_ATTRIBUTE + ":" + nsPrefix);
			attr.setValue(qName.getNamespaceURI());
			rootElement.setAttributeNode(attr);

			return nsPrefix + ":" + qName.getLocalPart();
		} else {
			return "ns0:"+qName.getLocalPart();
		}

	}
	
	protected static XmlSchema findXmlSchema(XmlSchemaCollection schemaCollection ,String targetNsUri){
		XmlSchema[] xmlSchemas = schemaCollection.getXmlSchemas();
		String tmpUri = targetNsUri;
		if (tmpUri==null){
			tmpUri="";
		}
		for (XmlSchema schema : xmlSchemas){
			if (tmpUri.equals(schema.getTargetNamespace())){
				return schema;
			}
		}
		return null;
	}
	
//	protected static XmlSchemaForm findElementFormDefault(XmlSchemaCollection schemaCollection ,String targetNsUri){
//		XmlSchema schema = findXmlSchema(schemaCollection,targetNsUri);
//		if (schema!=null){
//			return schema.getElementFormDefault();
//		}
//		return XmlSchemaForm.UNQUALIFIED;
//	}
	
	
}
