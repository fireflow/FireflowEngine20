/**
 * Copyright 2007-2011 非也
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
package org.fireflow.model.io.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.ModelElementNames;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.ScriptLanguages;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author 非也 www.firesoa.com
 * 
 * 
 */
public abstract class ServiceParser implements ModelElementNames {
	protected static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private static String CDATA_SECTION_ELEMENT_LIST = "";
    public static final String JDK_TRANSFORMER_CLASS = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
    
    protected static boolean useJDKTransformerFactory = false;//需要规避bug

	static {
		TransformerFactory transformerFactory = TransformerFactory
		.newInstance();
		if (JDK_TRANSFORMER_CLASS.equals(transformerFactory.getClass().getName())){
			useJDKTransformerFactory = true;
		}
		documentBuilderFactory.setNamespaceAware(true);
    	StringBuffer buf = new StringBuffer();
    	buf.append("{").append(ModelElementNames.SERVICE_NS_URI).append("}").append(ModelElementNames.DESCRIPTION).append(" " )
		.append("{").append(ModelElementNames.SERVICE_NS_URI).append("}").append(ModelElementNames.BODY).append(" ")

		.append("{").append(ModelElementNames.RESOURCE_NS_URI).append("}").append(ModelElementNames.DESCRIPTION).append(" " )
		.append("{").append(ModelElementNames.RESOURCE_NS_URI).append("}").append(ModelElementNames.BODY).append(" ");
    	CDATA_SECTION_ELEMENT_LIST = buf.toString();
	}
	private static Map<String, ServiceParser> parserCache = new HashMap<String, ServiceParser>();

	public abstract ServiceDef deserializeService(Element element)
			throws DeserializerException;
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 将service序列化成element，并将该element作为parentElement的子元素。
	 * 
	 * @param service
	 * @param parentElement
	 * @throws SerializerException
	 */
	public abstract void serializeService(ServiceDef service,
			Element parentElement) throws SerializerException;

	/**
	 * 将service定义文件解析成ServiceDef列表
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws InvalidModelException
	 */
	public static List<ServiceDef> deserialize(InputStream in)
			throws IOException, DeserializerException, InvalidModelException {
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(in);
			return deserialize(document);
		} catch (ParserConfigurationException e) {
			throw new DeserializerException(e);
		} catch (SAXException e) {
			throw new DeserializerException(e);
		}

	}

	public static List<ServiceDef> deserialize(Document document)
			throws DeserializerException, InvalidModelException {
		Element servicesElem = document.getDocumentElement();
		List<ServiceDef> result = new ArrayList<ServiceDef>();
		loadServices(result, servicesElem);
		return result;
	}

	public static Document serializeToDOM(List<ServiceDef> services)
			throws InvalidModelException, SerializerException {
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element servicesElement = document.createElementNS(SERVICE_NS_URI,
					SERVICE_NS_PREFIX + ":" + SERVICES);
			document.appendChild(servicesElement);

			// servicesElement.addNamespace(SERVICE_NS_PREFIX, SERVICE_NS_URI);
			// servicesElement.addNamespace(XSD_NS_PREFIX, XSD_URI);
			// servicesElement.addNamespace(XSI_NS_PREFIX, XSI_URI);

			// QName qname = df.createQName("schemaLocation", "xsi", XSI_URI);
			// servicesElement.addAttribute(qname, SERVICE_SCHEMA_LOCATION);

			for (ServiceDef svc : services) {
				ServiceParser parser = ServiceParser.getInstance(svc);
				parser.serializeService(svc, servicesElement);
			}

			return document;
		} catch (ParserConfigurationException e) {
			throw new SerializerException(e);
		} finally {

		}
	}

	public static String serializeToXmlString(List<ServiceDef> services,String charset)
	throws InvalidModelException, SerializerException{

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			serialize(services, out,charset);
		} catch (IOException e) {
			throw new SerializerException(e);
		}
		return out.toString();
	}
	
	public static String serializeToXmlString(List<ServiceDef> services)
			throws InvalidModelException, SerializerException {
		return serializeToXmlString(services,DEFAULT_CHARSET);

	}

	public static void writeServices(List<ServiceDef> services,
			Element parentElement) throws SerializerException,
			InvalidModelException {
		Document document = parentElement.getOwnerDocument();
		Element servicesElement = document.createElementNS(SERVICE_NS_URI,
				SERVICE_NS_PREFIX + ":" + SERVICES);
		parentElement.appendChild(servicesElement);

		for (ServiceDef svc : services) {
			ServiceParser parser = ServiceParser.getInstance(svc);
			parser.serializeService(svc, servicesElement);
		}
	}
	
	public static void serialize(List<ServiceDef> services, OutputStream out,String charset)
		throws IOException, InvalidModelException, SerializerException {
		try {
			Document document = serializeToDOM(services);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, charset);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, CDATA_SECTION_ELEMENT_LIST);
			transformer.transform(new DOMSource(document),
					new StreamResult(out));
			out.flush();
		} catch (TransformerConfigurationException e) {
			throw new SerializerException(e);
		} catch (TransformerException e) {
			throw new SerializerException(e);
		} finally {

		}
	}

	public static void serialize(List<ServiceDef> services, OutputStream out)
			throws IOException, InvalidModelException, SerializerException {

		serialize(services,out,DEFAULT_CHARSET);
	}
	

	public static void loadServices(List<ServiceDef> servicesList,
			Element servicesElement) throws DeserializerException,
			InvalidModelException {
		if (servicesElement == null)
			return;
		servicesList.clear();

		NodeList children = servicesElement.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node node = children.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element svcElm = (Element) node;
			ServiceParser parser = ServiceParser.getInstance(svcElm);
			ServiceDef svcDef = parser.deserializeService(svcElm);

			servicesList.add(svcDef);
		}
	}

	public static ServiceParser getInstance(Element element)
			throws InvalidModelException {
		String parserClass = element.getAttribute(PARSER_CLASS_NAME);
		if (StringUtils.isEmpty(parserClass)) {
			throw new InvalidModelException(
					"Can not find the parser-class from the service element.");
		}

		return getConcretServiceParser(parserClass);
	}

	public static ServiceParser getInstance(ServiceDef service)
			throws InvalidModelException {
		String parserClass = service.getParserClassName();
		if (StringUtils.isEmpty(parserClass)) {
			throw new InvalidModelException(
					"Parser class of the Service can NOT be empty.");
		}
		return getConcretServiceParser(parserClass);
	}

	protected static ServiceParser getConcretServiceParser(
			String parserClassName) throws InvalidModelException {
		ServiceParser parser = parserCache.get(parserClassName);
		if (parser != null)
			return parser;
		try {
			Class clz = Class.forName(parserClassName);
			parser = (ServiceParser) clz.newInstance();
			parserCache.put(parserClassName, parser);
			return parser;
		} catch (ClassNotFoundException e) {
			throw new InvalidModelException(e);
		} catch (InstantiationException e) {
			throw new InvalidModelException(e);
		} catch (IllegalAccessException e) {
			throw new InvalidModelException(e);
		} finally {

		}

	}

	protected void writeCommonServiceAttribute(AbstractServiceDef service,
			Element svcElem) {
		svcElem.setAttribute(ID, service.getId());
		svcElem.setAttribute(NAME, service.getName());
		svcElem.setAttribute(PACKAGE_ID, service.getBizCategory());
		svcElem.setAttribute(DISPLAY_NAME, service.getDisplayName());
		svcElem.setAttribute(TARGET_NAMESPACE, service.getTargetNamespaceUri());
		
		if (!StringUtils.isEmpty(service.getInvokerBeanName())) {
			svcElem.setAttribute(INVOKER_BEAN_NAME,
					service.getInvokerBeanName());
		} 
		
		else if (!StringUtils.isEmpty(service.getInvokerClassName()) ) {
			svcElem.setAttribute(INVOKER_CLASS_NAME,
					service.getInvokerClassName());
		}

		svcElem.setAttribute(PARSER_CLASS_NAME, service.getParserClassName());
		this.writeDescription(svcElem, service.getDescription());

	}

	protected void loadCommonServiceAttribute(AbstractServiceDef absService,
			Element svcElem) {
		absService.setName(svcElem.getAttribute(NAME));
		absService.setDisplayName(svcElem.getAttribute(DISPLAY_NAME));
		absService.setBizCategory(svcElem.getAttribute(PACKAGE_ID));
		absService.setDescription(this.loadCDATA(Util4Deserializer.child(svcElem,
				DESCRIPTION)));
		String beanName = svcElem.getAttribute(INVOKER_BEAN_NAME);
		if (!StringUtils.isEmpty(beanName)) {
			absService.setInvokerBeanName(beanName);
		}
		String className = svcElem.getAttribute(INVOKER_CLASS_NAME);
		if (!StringUtils.isEmpty(className)) {
			absService.setInvokerClassName(className);
		}
		absService.setParserClassName(svcElem.getAttribute(PARSER_CLASS_NAME));

		absService.setTargetNamespaceUri(svcElem.getAttribute(TARGET_NAMESPACE));
		Element extendedAttributesElement = Util4Deserializer.child(svcElem,
				EXTENDED_ATTRIBUTES);
		this.loadExtendedAttributes(absService.getExtendedAttributes(),
				extendedAttributesElement);
	}

	/**
	 * 实现common interface的反序列化
	 */
	protected InterfaceDef loadCommonInterface(ServiceDef service,Element element) {
		CommonInterfaceDef _interface = new CommonInterfaceDef();
		
		_interface.setName(element.getAttribute(NAME));
		
		List<OperationDef> operations = _interface.getOperations();

		List<Element> operationElements = Util4Deserializer.children(element,
				OPERATION);

		if (operationElements != null) {
			for (Element operationElm : operationElements) {
				OperationDefImpl op = new OperationDefImpl();
				op.setOperationName(operationElm.getAttribute(NAME));
				
				// 解析inputs
				List<Input> inputs = op.getInputs();
				Element inputsElement = Util4Deserializer.child(operationElm,
						INPUTS);
				List<Element> inputElements = Util4Deserializer.children(
						inputsElement, INPUT);
				if (inputElements != null) {
					for (Element inputElm : inputElements) {
						InputImpl input = new InputImpl();
						input.setName(inputElm.getAttribute(NAME));
						input.setDisplayName(inputElm.getAttribute(DISPLAY_NAME));
						input.setDefaultValueAsString(
								inputElm.getAttribute(DEFAULT_VALUE));
						input.setDataPattern(inputElm.getAttribute(DATA_PATTERN));

						String dataTypeStr = inputElm.getAttribute(DATA_TYPE);
						if (dataTypeStr.charAt(0) == '{') {
							input.setDataType(javax.xml.namespace.QName
									.valueOf(dataTypeStr));
						} else {
							//TODO 待进一步处理 
//							service.getXmlSchemaCollection().getXmlSchemas()[0].get
//							int index = dataTypeStr.indexOf(":");
//							String prefix = dataTypeStr.substring(0, index);
//							String localName = dataTypeStr.substring(index + 1);
//							inputElm.getOwnerDocument().getn
//							Namespace dom4jNs = inputElm
//									.getNamespaceForPrefix(prefix);
//							input.setDataType(new javax.xml.namespace.QName(
//									dom4jNs.getURI(), localName, dom4jNs
//											.getPrefix()));

						}

						inputs.add(input);
					}
				}

				// 解析output
				List<Output> outputs = op.getOutputs();
				Element outputsElement = Util4Deserializer.child(operationElm,
						OUTPUTS);
				List<Element> outputElements = Util4Deserializer.children(
						outputsElement, OUTPUT);
				if (outputElements != null) {
					for (Element outputElm : outputElements) {
						OutputImpl output = new OutputImpl();
						output.setName(outputElm.getAttribute(NAME));
						output.setDisplayName(outputElm.getAttribute(DISPLAY_NAME));

						String dataTypeStr = outputElm.getAttribute(DATA_TYPE);
						if (dataTypeStr.charAt(0) == '{') {
							output.setDataType(javax.xml.namespace.QName
									.valueOf(dataTypeStr));
						} else {
							//待进一步处理
//							int index = dataTypeStr.indexOf(":");
//							String prefix = dataTypeStr.substring(0, index);
//							String localName = dataTypeStr.substring(index + 1);
//							Namespace dom4jNs = outputElm
//									.getNamespaceForPrefix(prefix);
//							output.setDataType(new javax.xml.namespace.QName(
//									dom4jNs.getURI(), localName, dom4jNs
//											.getPrefix()));

						}

						outputs.add(output);
					}
				}

				operations.add(op);
			}
		}

		return _interface;
	}

	/**
	 * 实现common interface的序列化
	 */
	protected Element writeCommonInterface(InterfaceDef _interface,Element svcElem) {
		CommonInterfaceDef commonInterface = (CommonInterfaceDef) _interface;
		
		Element element = Util4Serializer.addElement(svcElem, INTERFACE_COMMON);
		
		element.setAttribute(NAME, commonInterface.getName());
		List<OperationDef> operations = commonInterface.getOperations();
		if (operations != null) {
			for (OperationDef op : operations) {
				Element opElm = Util4Serializer.addElement(element, OPERATION);
				opElm.setAttribute(NAME, op.getOperationName());

				List<Input> inputs = op.getInputs();
				if (inputs != null) {
					Element inputsElm = Util4Serializer.addElement(opElm,
							INPUTS);
					for (Input in : inputs) {
						Element inElm = Util4Serializer.addElement(inputsElm,
								INPUT);

						inElm.setAttribute(NAME, in.getName());
						if (!StringUtils.isEmpty(in.getDisplayName())) {
							inElm.setAttribute(DISPLAY_NAME,
									in.getDisplayName());
						}
						if (!StringUtils.isEmpty(in.getDataPattern())) {
							inElm.setAttribute(DATA_PATTERN,
									in.getDataPattern());
						}
						if (!StringUtils.isEmpty(in.getDefaultValueAsString())) {
							inElm.setAttribute(DEFAULT_VALUE,
									in.getDefaultValueAsString());
						}
						if (in.getDataType() != null) {
							inElm.setAttribute(DATA_TYPE, in.getDataType()
									.toString());
						}

					}
				}

				List<Output> outputs = op.getOutputs();
				if (outputs != null) {
					Element outputsElm = Util4Serializer.addElement(opElm,
							OUTPUTS);
					for (Output out : outputs) {
						Element outElm = Util4Serializer.addElement(outputsElm,
								OUTPUT);

						outElm.setAttribute(NAME, out.getName());
						if (!StringUtils.isEmpty(out.getDisplayName())) {
							outElm.setAttribute(DISPLAY_NAME,
									out.getDisplayName());
						}

						if (out.getDataType() != null) {
							outElm.setAttribute(DATA_TYPE, out.getDataType()
									.toString());
						}
					}
				}
			}
		}

		return element;
	}

	protected void loadExtendedAttributes(
			Map<String, String> extendedAttributes,
			Element extendedAttributesElement) {
		if (extendedAttributesElement != null) {
			List<Element> children = Util4Deserializer.children(
					extendedAttributesElement, EXTENDED_ATTRIBUTE);
			for (Element propElem : children) {
				extendedAttributes.put(propElem.getAttribute(NAME),
						propElem.getAttribute(VALUE));
			}
		}
	}

	protected void writeExtendedAttributes(
			Map<String, String> extendedAttributes, Element serviceElement) {
		if (extendedAttributes == null || extendedAttributes.isEmpty()) {
			return;
		}

		Element extendedAttributesElement = Util4Serializer.addElement(
				serviceElement, EXTENDED_ATTRIBUTES);

		Iterator<String> iterator = extendedAttributes.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Element propElm = Util4Serializer.addElement(
					extendedAttributesElement, EXTENDED_ATTRIBUTE);
			propElm.setAttribute(NAME, key);
			propElm.setAttribute(VALUE, extendedAttributes.get(key));

		}
	}

	protected Expression createExpression(Element expressionElement){
		if (expressionElement!=null){
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(expressionElement.getAttribute(LANGUAGE));
			exp.setName(expressionElement.getAttribute(NAME));
			exp.setDisplayName(expressionElement.getAttribute(DISPLAY_NAME));
			String dataTypeStr = expressionElement.getAttribute(DATA_TYPE);
			QName qname = QName.valueOf(dataTypeStr);
			exp.setDataType(qname);
			Element bodyElement = Util4Deserializer.child(expressionElement, BODY);

			if (bodyElement==null){
				exp.setBody("");
			}else{
				exp.setBody(this.loadCDATA(bodyElement));
			}

			
			Element namespacePrefixUriMapElem = Util4Deserializer.child(expressionElement, NAMESPACE_PREFIX_URI_MAP);
	        
			if (namespacePrefixUriMapElem!=null){
				List<Element> children = Util4Deserializer.children(namespacePrefixUriMapElem, ENTRY);
				if (children!=null && children.size()>0){
					for (Element elem : children){
						String prefix = elem.getAttribute(NAME);
						String uri = elem.getAttribute(VALUE);
						exp.getNamespaceMap().put(prefix, uri);
					}
				}
			}
			return exp;
		}else{
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
			exp.setDataType(new QName(NameSpaces.JAVA.getUri(), String.class
					.getName(), NameSpaces.JAVA.getPrefix()));
			exp.setName("WorkItemSubject");
			exp.setDisplayName("工作项主题");// 需国际化
			exp.setBody("");
			return exp;
		}
	}
	
    protected void writeExpression(Expression exp,Element parent){
    	if (exp==null) return;
    	Element expressionElem = Util4Serializer.addElement(parent, EXPRESSION);
        if (exp.getName()!=null && !exp.getName().trim().equals("")){
        	expressionElem.setAttribute(NAME, exp.getName());
        }
        if (exp.getDisplayName()!=null && !exp.getDisplayName().trim().equals("")){
        	expressionElem.setAttribute(DISPLAY_NAME, exp.getDisplayName());
        }
        expressionElem.setAttribute(LANGUAGE, exp.getLanguage());
        Document doc = parent.getOwnerDocument();
        String body = exp.getBody()==null?"":exp.getBody();
        CDATASection cdata = doc.createCDATASection(useJDKTransformerFactory?(" "+body):body);
        Element bodyElem = Util4Serializer.addElement(expressionElem, BODY);
        bodyElem.appendChild(cdata);
        
        if(exp.getNamespaceMap()!=null && exp.getNamespaceMap().size()>0){
        	
        	Element namespaceMapElem = Util4Serializer.addElement(expressionElem, NAMESPACE_PREFIX_URI_MAP);
        	Iterator<Map.Entry<String,String>> entrys = exp.getNamespaceMap().entrySet().iterator();
        	while(entrys.hasNext()){
        		Map.Entry<String,String> entry = entrys.next();
        		Element entryElem = Util4Serializer.addElement(namespaceMapElem, ENTRY);
        		entryElem.setAttribute(NAME, entry.getKey());
        		entryElem.setAttribute(VALUE, entry.getValue());
        	}
        }
           
    }
	protected static boolean equalStrings(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		s1 = s1 == null ? "" : s1.trim();
		s2 = s2 == null ? "" : s2.trim();
		return s1.equals(s2);
	}
	
	protected String loadCDATA(Element cdataElement){
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
	
	protected void writeDescription(Element parent, String desc) {
		if(desc==null || desc.trim().equals(""))return;
		Document doc = parent.getOwnerDocument();
		Element descElem = Util4Serializer.addElement(parent, DESCRIPTION);

		CDATASection cdata = doc.createCDATASection(useJDKTransformerFactory?(" "+desc):desc);
		descElem.appendChild(cdata);
	}
}
