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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.service.email.MailMessage;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class MailSenderImpl implements MailSender {
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
	private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; 
	
	MailSendServiceDef mailServiceDef = null;
	
	Date sentDate = null;
	
	
	public MailSendServiceDef getMailSentServiceDef() {
		return mailServiceDef;
	}

	public void setMailSentServiceDef(MailSendServiceDef mailServiceDef) {
		this.mailServiceDef = mailServiceDef;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.service.email.send.MailSender#sendEMail(java.lang.String, java.util.List, java.util.List, java.lang.String, java.lang.String)
	 */
	public void sendEMail(String mailToList,
			String carbonCopyList, String subject, String body,Boolean bodyIsHtml)
			throws ServiceInvocationException {	
		MailMessage mailMessage = new MailMessage();
		
		mailMessage.setFrom(mailServiceDef.getFrom());
		mailMessage.setMailToList(string2StringList(mailToList));
		mailMessage.setCarbonCopyList(string2StringList(carbonCopyList));
		mailMessage.setSubject(subject);
		mailMessage.setBody(body);
		mailMessage.setBodyIsHtml(bodyIsHtml);
		this.checkMailMessage(mailMessage);
		this.sendEMail( mailMessage);
	}
	
	private List<String> string2StringList(String addresses){
		List<String> list = new ArrayList<String>();
		if (StringUtils.isEmpty(addresses)) return list;
		StringTokenizer tokenizer = new StringTokenizer(addresses,";");
		while (tokenizer.hasMoreTokens()){
			list.add(tokenizer.nextToken());
		}
		return list;
	}
	
	

	/* (non-Javadoc)
	 * @see org.fireflow.service.email.send.MailSender#sendEMail(java.lang.String, java.util.List, java.util.List, java.lang.String, org.fireflow.service.email.send.MailEntity)
	 */
	public void sendEMail(MailMessage mailMessage)
			throws ServiceInvocationException {

		
		//1、创建Session
		Properties javaMailProperties = new Properties();
		
		javaMailProperties.put("mail.transport.protocol", mailServiceDef.getProtocol());
		javaMailProperties.put("mail.smtp.host", mailServiceDef.getSmtpServer());
		javaMailProperties.put("mail.smtp.auth", mailServiceDef.isNeedAuth()?"true":"false");
		if (mailServiceDef.isUseSSL()){
			javaMailProperties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			javaMailProperties.setProperty("mail.smtp.socketFactory.fallback", "false"); 
			javaMailProperties.setProperty("mail.smtp.socketFactory.port",Integer.toString(mailServiceDef.getSmtpPort())); 
		}
		
		Session mailSession = Session.getInstance(javaMailProperties);
		
		//2、创建MimeMessage
		MimeMessage mimeMsg = null;
		try {
			mimeMsg = createMimeMessage(mailSession,mailMessage);
			
			//3、发送邮件
			Transport transport = mailSession.getTransport();
			transport.connect(mailServiceDef.getUserName(), mailServiceDef.getPassword());
			
			transport.sendMessage(mimeMsg,mimeMsg.getAllRecipients());
		} catch (AddressException e) {
			throw new ServiceInvocationException(e);
		} catch (MessagingException e) {
			throw new ServiceInvocationException(e);
		}
	}
	
	/**
	 * TODO 需要加强校验
	 * @param mailSession
	 * @param mailMessage
	 * @return
	 * @throws MessagingException 
	 * @throws AddressException 
	 * @throws ServiceInvocationException
	 */
	private MimeMessage createMimeMessage(Session mailSession,MailMessage mailMessage) throws AddressException, MessagingException{
		MimeMessage mimeMsg = new MimeMessage(mailSession);
		
		//1、set from
		//Assert.notNull(mailMessage.getFrom(),"From address must not be null");
		mimeMsg.setFrom(new InternetAddress(mailMessage.getFrom()));
		
		//2、set mailto
		List<String> mailToList = mailMessage.getMailToList();
		InternetAddress[] addressList = new InternetAddress[mailToList.size()];
		for (int i=0;i<mailToList.size();i++){
			String mailTo = mailToList.get(i);
			addressList[i] = new InternetAddress(mailTo);
		}
		mimeMsg.setRecipients(Message.RecipientType.TO, addressList);
		
		//3、set cc
		List<String> ccList = mailMessage.getCarbonCopyList();
		if (ccList!=null && ccList.size()>0){
			addressList = new InternetAddress[ccList.size()];
			for (int i=0;i<ccList.size();i++){
				String mailTo = ccList.get(i);
				addressList[i] = new InternetAddress(mailTo);
			}
			mimeMsg.setRecipients(Message.RecipientType.CC, addressList);
		}

		
		//4、set subject
		mimeMsg.setSubject(mailMessage.getSubject(),mailServiceDef.getCharset());
		
		//5、set sentDate
		if (this.sentDate!=null){
			mimeMsg.setSentDate(sentDate);
		}
		
		//6、set email body
		Multipart multiPart = new MimeMultipart();
		MimeBodyPart bp = new MimeBodyPart();
		if (mailMessage.getBodyIsHtml())
			bp.setContent(mailMessage.getBody(), CONTENT_TYPE_HTML + CONTENT_TYPE_CHARSET_SUFFIX +mailServiceDef.getCharset());
		else
			bp.setText(mailMessage.getBody(),mailServiceDef.getCharset());		
		
		multiPart.addBodyPart(bp);
		mimeMsg.setContent(multiPart);
		
		//7、set attachment
		//TODO 待处理
		
		return mimeMsg;
	}

	private void checkMailMessage(MailMessage mailMessage) throws ServiceInvocationException{
		if (mailMessage.getFrom()==null || mailMessage.getFrom().trim().equals("")){
			throw new ServiceInvocationException("The email from address can not be empty.");
		}
		if (mailMessage.getMailToList()==null || mailMessage.getMailToList().size()==0){
			throw new ServiceInvocationException("The email recipient address can not be empty.");
		}
		
		
	}
}
