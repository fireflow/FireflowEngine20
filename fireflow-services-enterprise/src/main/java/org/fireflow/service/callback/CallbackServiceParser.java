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
package org.fireflow.service.callback;

import org.fireflow.model.data.Expression;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.java.JavaService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallbackServiceParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.callback";
	public static final String CORRELATION = "correlation";
	public static final String START_PROCESS = "start-process";
	public static final String PROCESS_ID = "process-id";
	public static final String PROCESS_TYPE = "process-type";
	public static final String PROCESS_VERSION = "process-version";

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#deserializeService(org.w3c.dom.Element)
	 */
	@Override
	public ServiceDef deserializeService(Element element)
			throws DeserializerException {
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();
		
		if (!equalStrings(localName_1,SERVICE_NAME) ||
				!equalStrings(namespaceUri_1,SERVICE_NS_URI)){
			throw new DeserializerException("The element is not a callback service, the element name is '"+localName_1+"'");
		}
		CallbackService callbackService = new CallbackService();
		this.loadCommonServiceAttribute(callbackService, element);
		
		InterfaceDef _interface = this.loadCommonInterface(callbackService, Util4Deserializer.child(element,INTERFACE_COMMON));
		callbackService.setInterface(_interface);		
		
		Element correlationElem = Util4Deserializer.child(element, CORRELATION);
		if (correlationElem!=null){
			Expression exp = this.createExpression(Util4Deserializer.child(correlationElem,EXPRESSION));
			callbackService.setCorrelation(exp);
			
		}

//		Boolean startProcess = Util4Deserializer.elementAsBoolean(element, START_PROCESS);
//		if(startProcess!=null && startProcess){
//			callbackService.setStartProcess(startProcess);
//			
//			callbackService.setWorkflowProcessId(Util4Deserializer.elementAsString(element, PROCESS_ID));
//			callbackService.setWorkflowProcessType(Util4Deserializer.elementAsString(element, PROCESS_TYPE));
//			callbackService.setWorkflowProcessVersion(Util4Deserializer.elementAsInteger(element, PROCESS_VERSION));
//		}
		
		this.loadExtendedAttributes(callbackService.getExtendedAttributes(), Util4Deserializer.child(element, EXTENDED_ATTRIBUTES));
		
		//所有的属性设置后，进行初始化工作
		//TODO 应该放在这个位置吗？
		try {
			callbackService.afterPropertiesSet();
		} catch (Exception e) {
			throw new DeserializerException(e);
		}
		return callbackService;
	}
	
	

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#serializeService(org.fireflow.model.servicedef.ServiceDef, org.w3c.dom.Element)
	 */
	@Override
	public void serializeService(ServiceDef service, Element parentElement)
			throws SerializerException {
		if (!(service instanceof CallbackService)){
			return ;
		}
		CallbackService javaSvc = (CallbackService)service;

		Document document = parentElement.getOwnerDocument();
		
		Element svcElem = document.createElementNS(SERVICE_NS_URI,SERVICE_NS_PREFIX+":"+SERVICE_NAME );

		this.writeCommonServiceAttribute(javaSvc, svcElem);
		

		this.writeCommonInterface(service.getInterface(),svcElem);
		Element correlationElement = Util4Serializer.addElement(svcElem, CORRELATION);
		this.writeExpression(javaSvc.getCorrelation(), correlationElement);
		
//		if (javaSvc.isStartProcess()){
//			Util4Serializer.addElement(svcElem, START_PROCESS, javaSvc.isStartProcess().toString());
//			Util4Serializer.addElement(svcElem, PROCESS_ID,javaSvc.getWorkflowProcessId());
//			Util4Serializer.addElement(svcElem, PROCESS_TYPE,javaSvc.getWorkflowProcessType());
//			Util4Serializer.addElement(svcElem,PROCESS_VERSION,
//					javaSvc.getWorkflowProcessVersion()==null?"-1":javaSvc.getWorkflowProcessVersion().toString());
//		}
		
		this.writeExtendedAttributes(javaSvc.getExtendedAttributes(), svcElem);
		
		parentElement.appendChild(svcElem);
		
	}

}
