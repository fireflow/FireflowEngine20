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
package org.fireflow.service.call;

import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class CallServiceParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.call";
	public static final String PROCESS_ID = "process-id";
	public static final String SUBPROCESS_ID = "subprocess-id";
	// public static final String PROCESS_TYPE = "process-type";
	public static final String PROCESS_VERSION = "process-version";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.model.io.service.ServiceParser#deserializeService(org.w3c
	 * .dom.Element)
	 */
	public ServiceDef deserializeService(Element element)
			throws DeserializerException {
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();

		if (!equalStrings(localName_1, SERVICE_NAME)
				|| !equalStrings(namespaceUri_1, SERVICE_NS_URI)) {
			throw new DeserializerException(
					"The element is not a subflow service, the element name is '"
							+ localName_1 + "'");
		}
		CallServiceDef subflowService = new CallServiceDef();
		
		this.loadCommonServiceAttribute(subflowService, element);

		
		InterfaceDef _interface = this.loadCallServiceInterfaceDef(subflowService,
				Util4Deserializer.child(element, CallServiceInterfaceDef.INTERFACE_CALL_SERVICE));
		subflowService.setInterface(_interface);
		
		subflowService.setPackageId(Util4Deserializer.elementAsString(element,
				PACKAGE_ID));
		subflowService.setProcessId(Util4Deserializer.elementAsString(element,
				PROCESS_ID));
		subflowService.setSubProcessId(Util4Deserializer.elementAsString(element,
				SUBPROCESS_ID));
		subflowService.setProcessVersion(Util4Deserializer.elementAsInteger(
				element, PROCESS_VERSION));

		this.loadExtendedAttributes(subflowService.getExtendedAttributes(),
				Util4Deserializer.child(element, EXTENDED_ATTRIBUTES));

		return subflowService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.model.io.service.ServiceParser#serializeService(org.fireflow
	 * .model.servicedef.ServiceDef, org.w3c.dom.Element)
	 */
	public void serializeService(ServiceDef service, Element parentElement)
			throws SerializerException {
		if (!(service instanceof CallServiceDef)) {
			return;
		}
		CallServiceDef callService = (CallServiceDef) service;

		Document document = parentElement.getOwnerDocument();

		Element svcElem = document.createElementNS(SERVICE_NS_URI,
				SERVICE_NS_PREFIX + ":" + SERVICE_NAME);

		this.writeCommonServiceAttribute(callService, svcElem);

		
		this.writeCallServiceInterfaceDef(service.getInterface(), svcElem);
		
		Util4Serializer.addElement(svcElem, PACKAGE_ID,
				callService.getPackageId());
		
		Util4Serializer.addElement(svcElem, PROCESS_ID,
				callService.getProcessId());
		Util4Serializer.addElement(svcElem, SUBPROCESS_ID,
				callService.getSubProcessId());
		Util4Serializer.addElement(svcElem, PROCESS_VERSION, callService
				.getProcessVersion() == null ? Integer.toString(CallServiceDef.THE_LATEST_VERSION) : callService
				.getProcessVersion().toString());

		this.writeExtendedAttributes(callService.getExtendedAttributes(),
				svcElem);

		parentElement.appendChild(svcElem);

	}
	
	private Element writeCallServiceInterfaceDef(InterfaceDef _interface,Element svcElem){
		CallServiceInterfaceDef callInterface = (CallServiceInterfaceDef) _interface;
		
		Element element = Util4Serializer.addElement(svcElem, CallServiceInterfaceDef.INTERFACE_CALL_SERVICE);
		
		element.setAttribute(NAME, callInterface.getName());
		return element;
	}
	
	
	private InterfaceDef loadCallServiceInterfaceDef(ServiceDef service,Element element) {
		CallServiceInterfaceDef _interface = new CallServiceInterfaceDef();
		if (element!=null){
			_interface.setName(element.getAttribute(NAME));
		}
		
		
		return _interface;
	}

}
