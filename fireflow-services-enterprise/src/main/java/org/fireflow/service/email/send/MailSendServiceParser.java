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
package org.fireflow.service.email.send;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.service.email.MailTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailSendServiceParser extends ServiceParser {	
	//邮件模板字段
	public static final String MAIL_TEMPLATE = "mail-template";
	public static final String FROM = "email-from";
	public static final String MAILTO_LIST = "mail-to-list";
	public static final String CARBONCOPY_LIST = "carbon-copy-list";
	public static final String SUBJECT = "subject";
	public static final String EMAIL_BODY = "email-body";
	public static final String BODY_IS_HTML = "body-is-html";
	//链接字段
	public static final String CONNECT_INFO = "connect-info";	
	public static final String SERVICE_NAME = "service.mailsend";
	public static final String PROTOCOL = "protocol";
	public static final String SERVER_URL = "server-url";
	public static final String PORT = "port";
	public static final String NEED_AUTH = "need-auth";
	public static final String USER_NAME = "user-name";
	public static final String PASSWORD = "password";
	public static final String USE_SSL = "use-ssl";
	public static final String CHARSET = "charset";

	

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#deserializeService(org.w3c.dom.Element)
	 */
	@Override
	public ServiceDef deserializeService(Element svcElem)
			throws DeserializerException {
		String localName_1 = svcElem.getLocalName();
		String namespaceUri_1 = svcElem.getNamespaceURI();
		
		if (!equalStrings(localName_1,SERVICE_NAME) ||
				!equalStrings(namespaceUri_1,SERVICE_NS_URI)){
			throw new DeserializerException("The element is not a java service, the element name is '"+localName_1+"'");
		}
		
		MailSendServiceDef mailSendServiceDef = new MailSendServiceDef();
		
		this.loadCommonServiceAttribute(mailSendServiceDef, svcElem);
		
		//邮件体信息
		Element mailTemplateElem = Util4Deserializer.child(svcElem, this.MAIL_TEMPLATE);
		if (mailTemplateElem!=null){
			String from = Util4Deserializer.elementAsString(mailTemplateElem, FROM);
			mailSendServiceDef.setFrom(from);
			MailTemplate mailTemplate = new MailTemplate();
			mailSendServiceDef.setMailTemplate(mailTemplate);
			
			Element _childElm = Util4Deserializer.child(mailTemplateElem,MAILTO_LIST);
			if (_childElm!=null){
				Element expElem =  Util4Deserializer.child(_childElm,EXPRESSION);
				mailTemplate.setMailToList(this.createExpression(expElem));
			}
			_childElm = Util4Deserializer.child(mailTemplateElem,CARBONCOPY_LIST);
			if (_childElm!=null){
				Element expElem =  Util4Deserializer.child(_childElm,EXPRESSION);
				mailTemplate.setCarbonCopyList(this.createExpression(expElem));
			}
			_childElm = Util4Deserializer.child(mailTemplateElem,SUBJECT);
			if (_childElm!=null){
				Element expElem =  Util4Deserializer.child(_childElm,EXPRESSION);
				mailTemplate.setSubject(this.createExpression(expElem));
			}
			
			_childElm = Util4Deserializer.child(mailTemplateElem,EMAIL_BODY);
			if (_childElm!=null){
				Element expElem =  Util4Deserializer.child(_childElm,EXPRESSION);
				mailTemplate.setBody(this.createExpression(expElem));
			}
			
			_childElm = Util4Deserializer.child(mailTemplateElem,BODY_IS_HTML);
			if (_childElm!=null){
				Element expElem =  Util4Deserializer.child(_childElm,EXPRESSION);
				mailTemplate.setBodyIsHtml(this.createExpression(expElem));
			}
						
		}

		//链接信息
		Element connectInfoElem = Util4Deserializer.child(svcElem, CONNECT_INFO);
		String protocol = Util4Deserializer.elementAsString(connectInfoElem, PROTOCOL);
		if (!StringUtils.isEmpty(protocol)){
			mailSendServiceDef.setProtocol(protocol);
		}
		
		String serverUrl = Util4Deserializer.elementAsString(connectInfoElem, SERVER_URL);
		mailSendServiceDef.setSmtpServer(serverUrl);
		
		String port = Util4Deserializer.elementAsString(connectInfoElem, PORT);
		mailSendServiceDef.setSmtpPort(Integer.parseInt(port));
		
		String needAuth =  Util4Deserializer.elementAsString(connectInfoElem, NEED_AUTH);
		mailSendServiceDef.setNeedAuth(Boolean.parseBoolean(needAuth));
		
		String userName = Util4Deserializer.elementAsString(connectInfoElem, USER_NAME);
		mailSendServiceDef.setUserName(userName);
		
		String password = Util4Deserializer.elementAsString(connectInfoElem, PASSWORD);
		mailSendServiceDef.setPassword(password);
		
		String useSSL = Util4Deserializer.elementAsString(connectInfoElem, USE_SSL);
		mailSendServiceDef.setUseSSL(Boolean.parseBoolean(useSSL));
		
		String charset = Util4Deserializer.elementAsString(connectInfoElem, CHARSET);
		mailSendServiceDef.setCharset(charset);
		
		this.loadExtendedAttributes(mailSendServiceDef.getExtendedAttributes(), Util4Deserializer.child(svcElem, EXTENDED_ATTRIBUTES));
		
		return mailSendServiceDef;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.io.service.ServiceParser#serializeService(org.fireflow.model.servicedef.ServiceDef, org.w3c.dom.Element)
	 */
	@Override
	public void serializeService(ServiceDef service, Element parentElement)
			throws SerializerException {
		if (!(service instanceof MailSendServiceDef)){
			return ;
		}
		
		MailSendServiceDef mailSendServiceDef = (MailSendServiceDef)service;
		Document document = parentElement.getOwnerDocument();
		Element svcElem = document.createElementNS(SERVICE_NS_URI,SERVICE_NS_PREFIX+":"+SERVICE_NAME );
		parentElement.appendChild(svcElem);
		
		this.writeCommonServiceAttribute(mailSendServiceDef, svcElem);
		
		//邮件体信息
		Element mailTemplateElement = Util4Serializer.addElement(svcElem, MAIL_TEMPLATE);
		Util4Serializer.addElement(mailTemplateElement, FROM,mailSendServiceDef.getFrom());
		if (mailSendServiceDef.getMailTemplate()!=null){
			MailTemplate template = mailSendServiceDef.getMailTemplate();
			if (template.getMailToList()!=null){
				Element _elem = Util4Serializer.addElement(mailTemplateElement, MAILTO_LIST);
				this.writeExpression(template.getMailToList(), _elem);
			}
			
			if (template.getCarbonCopyList()!=null){
				Element _elem = Util4Serializer.addElement(mailTemplateElement, CARBONCOPY_LIST);
				this.writeExpression(template.getCarbonCopyList(), _elem);
			}
			
			if (template.getSubject()!=null){
				Element _elem = Util4Serializer.addElement(mailTemplateElement, SUBJECT);
				this.writeExpression(template.getSubject(), _elem);
			}
			
			if (template.getBody()!=null){
				Element _elem = Util4Serializer.addElement(mailTemplateElement, EMAIL_BODY);
				this.writeExpression(template.getBody(), _elem);
			}
			
			if (template.getBodyIsHtml()!=null){
				Element _elem = Util4Serializer.addElement(mailTemplateElement, BODY_IS_HTML);
				this.writeExpression(template.getBodyIsHtml(), _elem);
			}
		}

		
		//链接信息
		Element connectInfoElement = Util4Serializer.addElement(svcElem, CONNECT_INFO);
		
		Util4Serializer.addElement(connectInfoElement, PROTOCOL,mailSendServiceDef.getProtocol());
		
		Util4Serializer.addElement(connectInfoElement, SERVER_URL,mailSendServiceDef.getSmtpServer());
		
		Util4Serializer.addElement(connectInfoElement, PORT, Integer.toString(mailSendServiceDef.getSmtpPort()));
		
		Util4Serializer.addElement(connectInfoElement,  NEED_AUTH,	Boolean.toString(mailSendServiceDef.isNeedAuth()));
		
		Util4Serializer.addElement(connectInfoElement, USER_NAME,	mailSendServiceDef.getUserName());
		
		Util4Serializer.addElement(connectInfoElement, PASSWORD, mailSendServiceDef.getPassword());
		
		Util4Serializer.addElement(connectInfoElement,  USE_SSL, Boolean.toString(mailSendServiceDef.isUseSSL()));
		
		Util4Serializer.addElement(connectInfoElement,  CHARSET, mailSendServiceDef.getCharset());
		
		this.writeExtendedAttributes(mailSendServiceDef.getExtendedAttributes(), svcElem);

	}

}
