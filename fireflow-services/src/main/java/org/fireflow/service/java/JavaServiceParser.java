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
package org.fireflow.service.java;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public class JavaServiceParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.java";
	public static final String INTERFACE = "interface.java";
	
	public static final String INTERFACE_CLASS_NAME = "interface-class";
	
	public static final String JAVA_BEAN_NAME = "java-bean-name";
	public static final String JAVA_CLASS_NAME = "java-class-name";
	
	
	/* (non-Javadoc)
	 * @see org.firesoa.service.io.ServiceParser#deserializer(org.dom4j.Element)
	 */
	public ServiceDef deserializeService(Element element) throws DeserializerException{
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();
		
		if (!equalStrings(localName_1,SERVICE_NAME) ||
				!equalStrings(namespaceUri_1,SERVICE_NS_URI)){
			throw new DeserializerException("The element is not a java service, the element name is '"+localName_1+"'");
		}
		JavaService javaService = new JavaService();
		this.loadCommonServiceAttribute(javaService, element);
		
		InterfaceDef _interface = loadInterface(Util4Deserializer.child(element,INTERFACE));
		javaService.setInterface(_interface);		
		
		String javaBeanName = Util4Deserializer.elementAsString(element, JAVA_BEAN_NAME);
		if (!StringUtils.isEmpty(javaBeanName)){
			javaService.setJavaBeanName(javaBeanName);
		}
		
		String javaClassName = Util4Deserializer.elementAsString(element, JAVA_CLASS_NAME);
		if (!StringUtils.isEmpty(javaClassName)){
			javaService.setJavaClassName(javaClassName);
		}

		return javaService;
	}

	/* (non-Javadoc)
	 * @see org.firesoa.service.io.ServiceParser#serializer(org.firesoa.service.def.Service)
	 */
	public void serializeService(ServiceDef service,Element parentElement) {
		if (!(service instanceof JavaService)){
			return ;
		}
		JavaService javaSvc = (JavaService)service;

		Document document = parentElement.getOwnerDocument();
		
		Element svcElem = document.createElementNS(SERVICE_NS_URI,SERVICE_NS_PREFIX+":"+SERVICE_NAME );

		this.writeCommonServiceAttribute(javaSvc, svcElem);
		
		Util4Serializer.addElement(svcElem,JAVA_BEAN_NAME,javaSvc.getJavaBeanName());
		
		Util4Serializer.addElement(svcElem,JAVA_CLASS_NAME,javaSvc.getJavaClassName());
		
		writeInterface(service.getInterface(),svcElem);
		
		parentElement.appendChild(svcElem);
		
		this.writeExtendedAttributes(javaSvc.getExtendedAttributes(), svcElem);
	}

	public InterfaceDef loadInterface(Element element){
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName(Util4Deserializer.elementAsString(element, INTERFACE_CLASS_NAME));
		return javaInterface;
	}
	
	public void writeInterface(InterfaceDef _interface,Element svcElm){
		if (_interface==null || !(_interface instanceof JavaInterfaceDef)){
			return ;
		}
		JavaInterfaceDef javaInterface = (JavaInterfaceDef)_interface;
		Document document = svcElm.getOwnerDocument();
		Element interfaceElement = document.createElementNS(SERVICE_NS_URI,SERVICE_NS_PREFIX+":"+INTERFACE);
		svcElm.appendChild(interfaceElement);
		Util4Serializer.addElement(interfaceElement,INTERFACE_CLASS_NAME,javaInterface.getInterfaceClassName());
	}
	

}
