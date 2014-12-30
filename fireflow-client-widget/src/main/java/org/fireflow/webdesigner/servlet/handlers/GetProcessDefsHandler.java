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
package org.fireflow.webdesigner.servlet.handlers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.clientwidget.BrowserUtils;
import org.fireflow.clientwidget.servlet.ActionHandler;
import org.fireflow.clientwidget.tag.ClientWidgetBase;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.firesoa.common.util.Utils;
//import org.springframework.context.ApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class GetProcessDefsHandler implements ActionHandler {

	/* (non-Javadoc)
	 * @see org.fireflow.clientwidget.servlet.ActionHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String processId = req.getParameter("processId");
		String version = req.getParameter("processVersion");
		String processType = req.getParameter("processType");
		int v = 1;
		if (version!=null && !version.trim().equals("")){
			v = Integer.parseInt(version);
		}
		if (processType==null || processType.trim().equals("")){
			processType = FpdlConstants.PROCESS_TYPE_FPDL20;
		}
		RuntimeContext runtimeContext = RuntimeContext.getInstance();

		
		FireWorkflowSystem user = FireWorkflowSystem.getInstance();
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, user);
		WorkflowStatement stmt = session.createWorkflowStatement();

		String processXml = stmt.getWorkflowProcessXml(new ProcessKey(processId,v,processType));
		
		if (processXml!=null && !processXml.trim().equals("")){
			if (BrowserUtils.isIE(req)){//IE直接输出原始的xml，因为ie可以打开xml
				outputRawXml(processXml,resp);
			}else{//其他浏览器输出html包装的xml，
				this.outputXmlWithinHtml(processXml, resp, req);
			}
			

		}else{
			throw new ServletException("流程库没有找到匹配的流程定义，process key is [processId="+processId+",version="+version+",processType="+processType+"]");
		}

	}
	
	protected void outputXmlWithinHtml(String processXml,HttpServletResponse resp,HttpServletRequest req)
	throws ServletException, IOException {
		HttpServletRequest request = req;
		String contextPath = request.getContextPath();
		if (contextPath==null)contextPath = "";
		if (contextPath.endsWith("/") ){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}

		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE html>\n")
			.append("<html>\n")
			.append("<head>\n")
			.append("<script src=\"")
			.append(contextPath)
			.append(ClientWidgetBase.FIREFLOW_RESOURCE_SERVLET)
			.append("/org/fireflow/webdesigner/resources/google-code-prettify/prettify.js\"></script>\n")
			.append("<link rel=\"stylesheet\" href=\"")
			.append(contextPath)
			.append(ClientWidgetBase.FIREFLOW_RESOURCE_SERVLET)
			.append("/org/fireflow/webdesigner/resources/google-code-prettify/prettify.css\"/>\n")
			.append("<style>\n")
			.append("body { margin: 0; padding: 0;font-size:12px }\n")
			.append("pre { margin: 0 }\n")
			.append("</style>\n")
			.append("</head>\n")
			.append("<body onload=\"prettyPrint();\">\n")
			.append("<pre class=\"prettyprint lang-xml\">");
		
		String tmp = StringEscapeUtils.escapeXml(processXml);
		sbuf.append(tmp);
		
		sbuf.append("</pre>\n")
			.append("</body>\n")
			.append("</html>");
		
		
		// 设置contentType
		String encoding = Utils.findXmlCharset(processXml);
		resp.setContentType("text/html");
		resp.setCharacterEncoding(encoding);

		// 告诉浏览器不要缓存
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setIntHeader("Expires", -1);

		
					
					
		byte[] byteArr = sbuf.toString().getBytes(encoding);
		
		OutputStream outStream = resp.getOutputStream();
		outStream.write(byteArr);
	}

	
	protected void outputRawXml(String processXml,HttpServletResponse resp)
	throws ServletException, IOException {
		// 设置contentType
		String encoding = Utils.findXmlCharset(processXml);
//		resp.setContentType("text/xml； charset="+encoding);
		resp.setContentType("text/xml");
		resp.setCharacterEncoding(encoding);

		// 告诉浏览器不要缓存
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setIntHeader("Expires", -1);

		
		byte[] byteArr = processXml.getBytes(encoding);
		
		OutputStream outStream = resp.getOutputStream();
		outStream.write(byteArr);
	}

}
