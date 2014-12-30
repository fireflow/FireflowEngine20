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

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.clientwidget.BrowserUtils;
import org.fireflow.clientwidget.servlet.ActionHandler;
import org.fireflow.clientwidget.tag.ClientWidgetBase;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.webdesigner.transformer.FpdlDiagramSerializer;
import org.fireflow.webdesigner.transformer.FpdlDiagramSerializerSvgImpl;
import org.fireflow.webdesigner.transformer.FpdlDiagramSerializerVmlImpl;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class GetProcessDiagramHandler implements ActionHandler {

	/* (non-Javadoc)
	 * @see org.fireflow.clientwidget.servlet.ActionHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String processId = req.getParameter("processId");
		String version = req.getParameter("processVersion");
		String processType = req.getParameter("processType");
		String subProcessName = req.getParameter("subProcessName");

//		String svgId = req.getParameter("svgVmlId");
		
		
		if (subProcessName==null || subProcessName.trim().equals("")){
			subProcessName = WorkflowProcess.MAIN_PROCESS_NAME;
		}
		int v = 1;
		if (version!=null && !version.trim().equals("")){
			try{
				v = Integer.parseInt(version);
			}catch(Exception e){
				
			}
			
		}
		if (processType==null || processType.trim().equals("")){
			processType = FpdlConstants.PROCESS_TYPE_FPDL20;
		}
		
		RuntimeContext runtimeContext = RuntimeContext.getInstance();		
		FireWorkflowSystem user = FireWorkflowSystem.getInstance();
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, user);
		WorkflowStatement stmt = session.createWorkflowStatement();
		WorkflowProcess process = null;
		try{
			process = (WorkflowProcess)stmt.getWorkflowProcess(new ProcessKey(processId,v,FpdlConstants.PROCESS_TYPE_FPDL20));
		}catch(InvalidModelException e){
			throw new ServletException(e);
		}
		
		String encoding = resp.getCharacterEncoding();
		if (encoding==null || encoding.trim().endsWith("")){
			encoding="UTF-8";
			resp.setCharacterEncoding(encoding);
		}
		
		boolean isIE = BrowserUtils.isIE(req);
		double ieVersion = BrowserUtils.getIEversion(req);
		
		if (isIE && ieVersion<9){
			outputVml(process,subProcessName,encoding,req,resp);
		}else{
			outputSvg(process,subProcessName,encoding,req,resp);
		}
	}
	
	private void outputSvg(WorkflowProcess process,String subProcessName,
			String encoding,HttpServletRequest req,HttpServletResponse resp)
	throws ServletException, IOException {
		/*
		String svg = FpdlExporter.exportToSVGDiagram(process, subProcessName,
				svgId,width,height,//给一个足够大的高和宽
				ClientWidgetBase.FIREFLOW_RESOURCE_SERVLET,
				req.getContextPath(),encoding);
		*/
		
		/* */
		String contextPath = req.getContextPath();
		if (contextPath==null)contextPath="";
		String resourceServletPath = ClientWidgetBase.FIREFLOW_RESOURCE_SERVLET;
		
		if (resourceServletPath==null) resourceServletPath="";
		if (contextPath.endsWith("/") && resourceServletPath.startsWith("/")){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		FpdlDiagramSerializer ser = new FpdlDiagramSerializerSvgImpl();
		ser.setResourcePathPrefix(contextPath+resourceServletPath);
		String svg = ser.serializeDiagramToStr(process, subProcessName, encoding, false);
		
		
		resp.setContentType("image/svg+xml");
		
		// 告诉浏览器不要缓存
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setIntHeader("Expires", -1);
		
		byte[] byteArr = svg.getBytes(encoding);
		
		OutputStream outStream = resp.getOutputStream();
		outStream.write(byteArr);
	}
	
	private void outputVml(WorkflowProcess process,String subProcessName,
			String encoding,HttpServletRequest req,HttpServletResponse resp)
	throws ServletException, IOException {
		String contextPath = req.getContextPath();
		if (contextPath==null)contextPath="";
		String resourceServletPath = ClientWidgetBase.FIREFLOW_RESOURCE_SERVLET;
		
		if (resourceServletPath==null) resourceServletPath="";
		if (contextPath.endsWith("/") && resourceServletPath.startsWith("/")){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		
		String subProcessId = process.getId()+WorkflowProcess.ID_SEPARATOR+subProcessName;
		Diagram diagram = process.getDiagramBySubProcessId(subProcessId);
		String vmlId = (diagram==null?"":diagram.getId());
		
		StringBuffer vmlBuf = new StringBuffer();
		vmlBuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n")
			.append("<html xmlns:v=\"urn:schemas-microsoft-com:vml\">\n")
			.append("<head>\n");
		
		vmlBuf.append("<script src=\"")
				.append(contextPath).append(resourceServletPath)
				.append("/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/js/jquery-1.10.2.min.js\"></script>\n");
		
		vmlBuf.append("<script language=\"JavaScript\">\n")
			.append("var $ff = jQuery.noConflict(true);\n")
			.append("</script>\n");
		
		vmlBuf.append("<script src=\"")
			.append(contextPath).append(resourceServletPath)
			.append("/org/fireflow/webdesigner/resources/vml/FireflowVmlControl.js\"></script>\n");

		vmlBuf.append("<STYLE>\n")
			.append("v\\:Rect,v\\:RoundRect,v\\:imagedata,v\\:image ,v\\:oval  { BEHAVIOR: url(#default#VML) ;display:inline-block; *display:inline; *zoom:1 }\n")
			.append("v\\:Shape,v\\:shapetype,v\\:group ,v\\:background ,v\\:path,v\\:formulas ,v\\:f,v\\:handles ,v\\:fill, v\\:stroke,v\\:shadow,v\\:TextBox,v\\:textpath,v\\:line ,v\\:polyline ,v\\:curve ,v\\:arc  { BEHAVIOR: url(#default#VML)}\n")
			.append("</STYLE>\n");	
		
		vmlBuf.append("</head>\n");
		vmlBuf.append("<body onload=\"fireflowDiagramInit('").append(vmlId).append("')\">\n");
				
		/* 
		String xml = FpdlExporter.exportToVMLDiagram(process, subProcessName,
				svgId,
				resourceServletPath,
				contextPath,encoding);
		*/
		/* */
		FpdlDiagramSerializer ser = new FpdlDiagramSerializerVmlImpl();
		ser.setResourcePathPrefix(contextPath+resourceServletPath);
		String xml = ser.serializeDiagramToStr(process, subProcessName, encoding,true);
		
		
		vmlBuf.append(xml);
		
		vmlBuf.append("\n</body>\n</html>");
		
		resp.setContentType("text/html");
		
		// 告诉浏览器不要缓存
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setIntHeader("Expires", -1);
		
		
		byte[] byteArr = vmlBuf.toString().getBytes(encoding);
		
		OutputStream outStream = resp.getOutputStream();
		outStream.write(byteArr);
	}

}
