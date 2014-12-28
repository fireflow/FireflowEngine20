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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailMessage {
	public static final String MAILTO_LIST = "mailToList";
	public static final String CARBONCOPY_LIST = "carbonCopyList";
	public static final String SUBJECT = "subject";
	public static final String BODY = "body";
	public static final String BODY_IS_HTML = "bodyIsHtml";
	public static final String FROM = "from";
	
	String from = null;
	List<String> mailToList;
	List<String> carbonCopyList;
	String subject = null;
	String body = null;
	boolean bodyIsHtml = false;
	Map<String,Object> attachment = new HashMap<String,Object>();
	public String getBody() {
		return body;
	}
	public void setBody(String content) {
		this.body = content;
	}
	public boolean getBodyIsHtml() {
		return bodyIsHtml;
	}
	public void setBodyIsHtml(boolean isHtml) {
		this.bodyIsHtml = isHtml;
	}
	public Map<String, Object> getAttachment() {
		return attachment;
	}
	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<String> getMailToList() {
		return mailToList;
	}
	public void setMailToList(List<String> mailToList) {
		this.mailToList = mailToList;
	}
	public List<String> getCarbonCopyList() {
		return carbonCopyList;
	}
	public void setCarbonCopyList(List<String> carbonCopyList) {
		this.carbonCopyList = carbonCopyList;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
