package org.fireflow.service.human;

import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HumanServiceParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.human";
	public static final String FORM_URL = "form-url";
	public static final String WORKITEM_SUBJECT = "workitem-subject";
	
	public ServiceDef deserializeService(Element element) throws DeserializerException{
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();
		
		if (!equalStrings(localName_1,SERVICE_NAME) ||
				!equalStrings(namespaceUri_1,SERVICE_NS_URI)){
			throw new DeserializerException("The element is not a java service, the element name is '"+localName_1+"'");
		}
		
		HumanService humanService = new HumanService();
		
		this.loadCommonServiceAttribute(humanService, element);
		
		
		String formUrl = Util4Deserializer.elementAsString(element, FORM_URL);
		humanService.setFormUrl(formUrl);
		
		Element workItemSubjectElement = Util4Deserializer.child(element, WORKITEM_SUBJECT);
		if (workItemSubjectElement!=null){
			humanService.setWorkItemSubject(this.createExpression(Util4Deserializer.child(workItemSubjectElement,EXPRESSION)));
		}
		
		Element interfaceElement = Util4Deserializer.child(element, INTERFACE_COMMON);
		if (interfaceElement!=null){
			InterfaceDef _interface = this.loadCommonInterface(humanService, interfaceElement);
			humanService.setInterface(_interface);
		}
		
		this.loadExtendedAttributes(humanService.getExtendedAttributes(), Util4Deserializer.child(element, EXTENDED_ATTRIBUTES));
		
		
		return humanService;
	}

	public void serializeService(ServiceDef service,Element parentElement) {
		if (!(service instanceof HumanService)){
			return ;
		}
		
		HumanService humanSvc = (HumanService)service;
		Document document = parentElement.getOwnerDocument();
		Element svcElem = document.createElementNS(SERVICE_NS_URI,SERVICE_NS_PREFIX+":"+SERVICE_NAME );
		parentElement.appendChild(svcElem);
		
		this.writeCommonServiceAttribute(humanSvc, svcElem);

		
		Util4Serializer.addElement(svcElem,FORM_URL,humanSvc.getFormUrl());
		
		Element workItemSubject = Util4Serializer.addElement(svcElem, WORKITEM_SUBJECT);
		this.writeExpression(humanSvc.getWorkItemSubject(), workItemSubject);
		
		InterfaceDef _interface = humanSvc.getInterface();
		if (_interface!=null){
			this.writeCommonInterface(_interface, svcElem);
		}
		
		this.writeExtendedAttributes(humanSvc.getExtendedAttributes(), svcElem);
	}


}
