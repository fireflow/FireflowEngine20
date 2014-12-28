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
package org.fireflow.service.webservice;

import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.call.CallServiceDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WebServiceDefParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.webservice";
	public static final String WSDL_URL = "wsdl-url";
	public static final String PORT_NAME = "port-name";

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#deserializeService(org.w3c.dom.Element)
	 */
	@Override
	public ServiceDef deserializeService(Element element)
			throws DeserializerException {
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();

		if (!equalStrings(localName_1, SERVICE_NAME)
				|| !equalStrings(namespaceUri_1, SERVICE_NS_URI)) {
			throw new DeserializerException(
					"The element is not a web service, the element name is '"
							+ localName_1 + "'");
		}
		WebServiceDef webserviceDef = new WebServiceDef();
		this.loadCommonServiceAttribute(webserviceDef, element);


		webserviceDef.setWsdlURL(Util4Deserializer.elementAsString(element,
				WSDL_URL));
		webserviceDef.setPortName(Util4Deserializer.elementAsString(element,
				PORT_NAME));


		this.loadExtendedAttributes(webserviceDef.getExtendedAttributes(),
				Util4Deserializer.child(element, EXTENDED_ATTRIBUTES));

		try {
			webserviceDef.afterPropertiesSet();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return webserviceDef;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#serializeService(org.fireflow.model.servicedef.ServiceDef, org.w3c.dom.Element)
	 */
	@Override
	public void serializeService(ServiceDef service, Element parentElement)
			throws SerializerException {
		if (!(service instanceof WebServiceDef)) {
			return;
		}
		WebServiceDef webserviceDef = (WebServiceDef) service;

		Document document = parentElement.getOwnerDocument();

		Element svcElem = document.createElementNS(SERVICE_NS_URI,
				SERVICE_NS_PREFIX + ":" + SERVICE_NAME);

		this.writeCommonServiceAttribute(webserviceDef, svcElem);

		Util4Serializer.addElement(svcElem, WSDL_URL,
				webserviceDef.getWsdlURL());
		Util4Serializer.addElement(svcElem, PORT_NAME,
				webserviceDef.getPortName());

		this.writeExtendedAttributes(webserviceDef.getExtendedAttributes(),
				svcElem);

		parentElement.appendChild(svcElem);

		
	}

}
