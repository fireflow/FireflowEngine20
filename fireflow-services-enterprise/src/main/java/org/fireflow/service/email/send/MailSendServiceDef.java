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
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.email.AbstractMailServiceDef;
import org.fireflow.service.email.MailTemplate;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailSendServiceDef extends AbstractMailServiceDef {
	protected String from = null;

	protected MailTemplate mailTemplate = null;
	
	public MailSendServiceDef(){
		this.invokerClassName = MailSenderInvoker.class.getName();
		this.parserClassName = MailSendServiceParser.class.getName();

		JavaInterfaceDef _interfaceDef = new JavaInterfaceDef();
		_interfaceDef.setInterfaceClassName(MailSender.class.getName());
		this.setInterface(_interfaceDef);
	}

	public String getFrom() {
		if (StringUtils.isEmpty(from)){
			return this.getUserName();
		}
		return from;
	}

	public void setFrom(String fromAddress) {
		this.from = fromAddress;
	}

	public MailTemplate getMailTemplate() {
		return mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}
	
	
}
