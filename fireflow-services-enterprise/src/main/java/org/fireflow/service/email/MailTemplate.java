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
package org.fireflow.service.email;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.ScriptLanguages;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailTemplate {
	
	protected Expression mailToList = null;
	protected Expression carbonCopyList = null;
	protected Expression subject = null;
	protected Expression body = null;//邮件体模板，用UnifiedJexl编写
	protected Expression bodyIsHtml = null;

	public MailTemplate(){
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
		exp.setBody("false");
		exp.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		this.setBodyIsHtml(exp);
	}

	public Expression getMailToList() {
		return mailToList;
	}

	public void setMailToList(Expression mailToList) {
		this.mailToList = mailToList;
	}

	public Expression getCarbonCopyList() {
		return carbonCopyList;
	}

	public void setCarbonCopyList(Expression carbonCopyList) {
		this.carbonCopyList = carbonCopyList;
	}

	public Expression getSubject() {
		return subject;
	}

	public void setSubject(Expression subject) {
		this.subject = subject;
	}

	public Expression getBody() {
		return body;
	}

	public void setBody(Expression body) {
		this.body = body;
	}

	public Expression getBodyIsHtml() {
		return bodyIsHtml;
	}

	public void setBodyIsHtml(Expression bodyIsHtml) {
		this.bodyIsHtml = bodyIsHtml;
	}
	
	/**
	 * 根据邮件各部分的名称返回相应的expression
	 * @param fieldName
	 * @return
	 */
	public Expression getMailField(String fieldName){
		if (StringUtils.isEmpty(fieldName)) return null;
		if (fieldName.equals(MailMessage.BODY)){
			return this.getBody();
		}
		else if (fieldName.equals(MailMessage.MAILTO_LIST)){
			return this.getMailToList();
		}
		else if (fieldName.equals(MailMessage.CARBONCOPY_LIST)){
			return this.getCarbonCopyList();
		}
		else if (fieldName.equals(MailMessage.SUBJECT)){
			return this.getSubject();
		}
		else if (fieldName.equals(MailMessage.BODY_IS_HTML)){
			return this.getBodyIsHtml();
		}
		return null;
	}
}
