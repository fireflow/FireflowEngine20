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

import java.util.List;

import javax.jws.WebParam;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.service.email.MailMessage;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public interface MailSender {

	/**
	 * 
	 * @param mailToList 分号分隔的邮箱列表
	 * @param carbonCopyList 分号分隔的邮箱列表
	 * @param subject
	 * @param body
	 * @param bodyIsHtml
	 * @throws ServiceInvocationException
	 */
	public void sendEMail(@WebParam(name=MailMessage.MAILTO_LIST) String mailToList,
						  @WebParam(name=MailMessage.CARBONCOPY_LIST)String carbonCopyList,
						  @WebParam(name=MailMessage.SUBJECT) String subject, 
						  @WebParam(name=MailMessage.BODY) String body,
						  @WebParam(name=MailMessage.BODY_IS_HTML) Boolean bodyIsHtml)
			throws ServiceInvocationException;
	
	//该方法不需要，2012-03-11
//	public void sendEMail(@WebParam(name="mailMessage") MailMessage mailMessage)
//	throws ServiceInvocationException;
}
