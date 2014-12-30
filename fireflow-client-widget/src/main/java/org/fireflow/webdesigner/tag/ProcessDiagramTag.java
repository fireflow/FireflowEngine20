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
package org.fireflow.webdesigner.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.clientwidget.BrowserUtils;
import org.fireflow.clientwidget.servlet.Constants;
import org.fireflow.clientwidget.tag.AbsClientWidget;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
//import org.springframework.context.ApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ProcessDiagramTag extends AbsClientWidget {
	private static final Log log = LogFactory.getLog(ProcessDiagramTag.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1144781754347835744L;

	private static final int TOOLBAR_HEIGHT = 26;
	
	
	private static String toolbar_js_fragment = null;
	private static String toolbar_tag_fragment = null;
	static{
		InputStream in = ProcessDiagramTag.class.getResourceAsStream("ProcessDiagramTag_ToolBar_JS_Fragment.js");
		if (in!=null){
			java.io.InputStreamReader inReader = new InputStreamReader(in);
			java.io.BufferedReader bufReader = new BufferedReader(inReader);
			
			StringBuffer tmpBuf = new StringBuffer();
			try{
				String line = null;
				while((line= bufReader.readLine())!=null){
					tmpBuf.append(line).append(" \n ");
				}
				
				
				toolbar_js_fragment = tmpBuf.toString();	
			}catch(IOException e){
				e.printStackTrace();
			}

		}
		in = ProcessDiagramTag.class.getResourceAsStream("ProcessDiagramTag_ToolBar_Tag_Fragment.txt");
		if (in!=null){
			java.io.InputStreamReader inReader = new InputStreamReader(in);
			java.io.BufferedReader bufReader = new BufferedReader(inReader);
			
			StringBuffer tmpBuf = new StringBuffer();
			try{
				String line = null;
				while((line= bufReader.readLine())!=null){
					tmpBuf.append(line).append(" \n ");
				}
				
				
				toolbar_tag_fragment = tmpBuf.toString();	
			}catch(IOException e){
				e.printStackTrace();
			}

		}
	}

	
	
	protected String processId = null;
	protected String subProcessName = null;
	protected int version = 1;
	protected RuntimeContext runtimeContext = null;
	protected String rtCtxBeanName = null;
	protected String clientWidgetServletPath = Fireflow_ClientWidget_Servlet_Path;
	//
	protected int borderWidth = 1;
	protected String borderColor = "#336699";
	protected String width = "600";
	protected int height = 300;
	protected String style = null;
	protected boolean showSubProcessSelector = true;
	
	//***********不需要通过外部进行设置的属性****************//
	protected WorkflowProcess workflowProcess = null;
	protected String svgVmlId = null;//svg或者 vml根元素的Id
	protected String svgVmlWrapperDivId = null;
	protected String toolBarId = null;
	protected String zoomInId = null;
	protected String zoomOutId = null;
	protected String zoomFitId = null;
	protected String moveLeftId = null;
	protected String moveRightId = null;
	protected String moveUpId = null;
	protected String moveDownId = null;
	protected String sourceXmlId = null;
	

	/**
	 * 采用固定值，故注释掉
	 * @return the clientWidgetServletPath
	 */
//	public String getClientWidgetServletPath() {
//		return clientWidgetServletPath;
//	}




	/**
	 * @param clientWidgetServletPath the clientWidgetServletPath to set
	 */
//	public void setClientWidgetServletPath(String clientWidgetServletPath) {
//		this.clientWidgetServletPath = clientWidgetServletPath;
//	}




	/**
	 * 采用固定值，故注释掉
	 * @return the rtCtxBeanName
	 */
//	public String getRtCtxBeanName() {
//		return rtCtxBeanName;
//	}




	/**
	 * @param rtCtxBeanName the rtCtxBeanName to set
	 */
//	public void setRtCtxBeanName(String rtCtxBeanName) {
//		this.rtCtxBeanName = rtCtxBeanName;
//	}

	public WorkflowProcess getWorkflowProcess(){
		return this.workflowProcess;
	}


	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}




	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}




	/**
	 * @return the subProcessName
	 */
	public String getSubProcessName() {
		return subProcessName;
	}




	/**
	 * @param subProcessName the subProcessName to set
	 */
	public void setSubProcessName(String subProcessNm) {
		this.subProcessName = subProcessNm;
	}




	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}




	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}




	/**
	 * @return the fireflowRuntimeContext
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}




	/**
	 * @param fireflowRuntimeContext the fireflowRuntimeContext to set
	 */
	public void setRuntimeContext(RuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;
	}




	/**
	 * @return the borderWidth
	 */
	public int getBorderWidth() {
		return borderWidth;
	}




	/**
	 * @param borderWidth the borderWidth to set
	 */
	public void setBorderWidth(int border) {
		this.borderWidth = border;
	}




	/**
	 * @return the borderColor
	 */
	public String getBorderColor() {
		return borderColor;
	}




	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}




	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}




	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}




	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}




	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}




	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}




	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}





	//初始化
	protected void init(boolean isIE,double ieVersion){
		//初始化version,subProcessName
		if (subProcessName==null || subProcessName.trim().equals("")){
			subProcessName = WorkflowProcess.MAIN_PROCESS_NAME;
		}
		
		//1、如果runtimeContext没有显式设置，则取缺省值
		if (runtimeContext==null){
			if (this.rtCtxBeanName==null || rtCtxBeanName.trim().equals("")){
				rtCtxBeanName = RuntimeContext.Fireflow_Runtime_Context_Name;
			}

			runtimeContext = RuntimeContext.getInstance();

		}
		
		this.workflowProcess = loadWorkflowProcess(runtimeContext,this.processId,this.version);
		
		//初始化id
		if (id==null || id.trim().equals("")){
			id="div_"+System.currentTimeMillis();//系统时间作为id，所以，一般要求id属性为必录
		}
		
		if (isIE && ieVersion<9){
			svgVmlWrapperDivId = id+"_vml_wrapper_div";
			
		}else{
			svgVmlWrapperDivId = id+"_svg_wrapper_div";
		}
		
		
		toolBarId = id+"_toolbar";
		zoomInId = id+"_zoom_in";
		zoomOutId = id+"_zoom_out";
		zoomFitId = id+"_zoom_fit";
		moveLeftId = id+"_move_left";
		moveRightId = id+"_move_right";
		moveUpId = id+"_move_up";
		moveDownId = id+"_move_down";
		sourceXmlId = id+"_source_xml";
		
		if (this.workflowProcess!=null){
			Diagram diagram = this.workflowProcess.getDiagramBySubProcessId(this.workflowProcess.getId()+WorkflowProcess.ID_SEPARATOR+subProcessName);
			if (diagram==null){
				//TODO 显示没有流程图形信息
			}else{
				svgVmlId = diagram.getId();//subProcessName;//id+"_vml_root";
			}
		}
	}
	
	private WorkflowProcess loadWorkflowProcess(RuntimeContext argCtx,String argProcId , int argVersion){
		FireWorkflowSystem user = FireWorkflowSystem.getInstance();
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(argCtx, user);
		WorkflowStatement stmt = session.createWorkflowStatement();
		WorkflowProcess process = null;
		try{
			process = (WorkflowProcess)stmt.getWorkflowProcess(new ProcessKey(argProcId,argVersion,FpdlConstants.PROCESS_TYPE_FPDL20));
		}catch(InvalidModelException e){
			log.error("读取流程定义发生异常。", e);
		}
		return process;
	}


	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
		boolean isIE = BrowserUtils.isIE(request);
		double ieVersion = BrowserUtils.getIEversion(request);
		
		init(isIE,ieVersion);//初始化属性
		
		StringBuffer sbuf = new StringBuffer();



		//1、首先include js,css资源，以及ie6,7,8的一些特殊设置
		sbuf.append(buildIncludeResources(isIE,ieVersion));
		
		//2、构造container div
		sbuf.append(buildContainerDiv());
		
		//sbuf.append("<table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr height=\"24px\"><td>");
		//3、构造toolbar 
		sbuf.append(this.buildToolBarDiv());
		
		//sbuf.append("</td></tr>").append("<tr  height=\"100%\"><td>");
		
		
		//4、构造iframe
		if (isIE && ieVersion<9){
			sbuf.append(this.buildSvgVmlIframe(false));
		}else{
			sbuf.append(this.buildSvgVmlIframe(true));
		}
		
		//4、在svg（vml）外面包一层div
//		sbuf.append("<div  id=\"")
//			.append(this.svgVmlWrapperDivId).append("\" style=\"width:100%;height:100%\">\n");
//		sbuf.append("<div").append("\" style=\"width:100%;height:100%\">\n");
	
		//5、构造svg或者vml	

		/*
		String diagram = "";
		if (isIE){
			if (ieVersion<9){
				diagram = buildVMLDiagram();
			}else{
				diagram = buildSVGDiagram();
			}
			
		}else{
			diagram = buildSVGDiagram();
		}
		
		sbuf.append(diagram);

		*/
		
//		sbuf.append("\n</div>\n");//svg wrapper div
		
//		sbuf.append("\n</td></tr></table>\n");
		
		sbuf.append("</div>\n");//container div
		/*
		if (isIE && ieVersion<9){
			sbuf.append("<script>\n")
				.append("fireflowDiagramInit(\"").append(this.svgVmlId).append("\");\n")
				.append("</script>\n");
		}
		*/
		try{
			pageContext.getOut().write(sbuf.toString());  
		}catch(IOException e){
			throw new JspException(e);
		}
		 
		
		return SKIP_BODY;
	}
	
	protected String buildContainerDiv(){
		StringBuffer sbuf = new StringBuffer();
		StringBuffer divStyle = new StringBuffer();
		divStyle.append("width:").append(width).append(";")
				.append("height:").append(height).append("px;")
				.append("border:").append(borderWidth).append("px ")
					.append(" solid ").append(borderColor).append(";");
		
		if (style!=null){
			String tmp = style.toLowerCase();
			boolean useDefaultOverflow = true;
			boolean useDefaultBackground = true;
			
			if (tmp.indexOf("overflow:")>=0){
				useDefaultOverflow = false;
			}
			
			if (tmp.indexOf("background-color:")>=0){
				useDefaultBackground = false;
			}
			
			divStyle.append(style);
			
			if (useDefaultOverflow){
				
				if (!divStyle.toString().endsWith(";")){
					divStyle.append(";");
				}
				divStyle.append("overflow:hidden;");
			}
			
			if (useDefaultBackground){
				if (!divStyle.toString().endsWith(";")){
					divStyle.append(";");
				}
				divStyle.append("background-color:#FFFFFF");
			}

		}else{
			//默认自动加上滚动条,白色背景
			divStyle.append("overflow:hidden;background-color:#FFFFFF");
		}
//		

		
		sbuf.append("<div ");
		if (id!=null && !id.trim().equals("")){
			sbuf.append("id=\"").append(id).append("\"");
		}
		sbuf.append("style=\"").append(divStyle).append("\"")
			.append(">\n");
		
		return sbuf.toString();
	}
	
	/**
	 * 标签所需要引用的js,css资源都在此处处理
	 * @return
	 */
	protected String buildIncludeResources(boolean isIE,double ieVersion){
		StringBuffer resourceBuf = new StringBuffer();
		
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
		String contextPath = request.getContextPath();
		if (contextPath==null)contextPath = "";
		if (contextPath.endsWith("/") ){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		
		//1、首先处理JQuery UI js资源
		Boolean isJQueryIncluded = (Boolean)this.pageContext.getAttribute(HAS_JQUERY_JS_INCLUDED);
		if (isJQueryIncluded==null){
			isJQueryIncluded = false;
		}
		if (this.useFireflowJQueryUI ){
			if (!isJQueryIncluded){
				//引入fireflow自带的jquery资源
				resourceBuf.append("<script src=\"").append(contextPath)
					.append(this.FIREFLOW_RESOURCE_SERVLET).append("/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/js/jquery-1.10.2.min.js")
					.append("\"></script>\n");
				
				resourceBuf.append("<script src=\"").append(contextPath)
				.append(this.FIREFLOW_RESOURCE_SERVLET).append("/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js")
				.append("\"></script>\n");
				
				//重新命名jquery和$避免冲突
				resourceBuf.append("<script language=\"JavaScript\">\n")
						.append("var $ff = jQuery.noConflict(true);\n")
						.append("</script>\n");
				
				this.pageContext.setAttribute(HAS_JQUERY_JS_INCLUDED,true);
			}

		}else{
			if (!isJQueryIncluded){
				//重新命名jquery和$避免冲突
				resourceBuf.append("<script language=\"JavaScript\">\n")
						.append("var $ff = $;\n")
						.append("</script>\n");
				
				this.pageContext.setAttribute(HAS_JQUERY_JS_INCLUDED,true);
			}
		}

		//2、处理jquery ui theme资源
		Boolean themeIncluded = (Boolean)this.pageContext.getAttribute(HAS_JQUERY_CSS_INCLUDED);
		if (themeIncluded==null) themeIncluded = false;
		if (this.useFireflowJQueryTheme!=null && 
				!this.useFireflowJQueryTheme.trim().equals("")){
			if (!themeIncluded){
				resourceBuf.append("<link rel=\"stylesheet\" href=\"")
				.append(contextPath).append(this.FIREFLOW_RESOURCE_SERVLET)
				.append("/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/css/")
				.append(useFireflowJQueryTheme).append("/jquery-ui-1.10.3.custom.min.css\"/>\n");

				this.pageContext.setAttribute(HAS_JQUERY_CSS_INCLUDED,true);
			}
			
		}
		
		//3、引入svg control脚本
		/* svg通过iframe引入，因此不需要在此处引入
		Boolean svgControlJsIncluded = (Boolean )this.pageContext.getAttribute(HAS_SVG_VML_CONTROL_JS_INCLUDED);
		if (svgControlJsIncluded==null || !svgControlJsIncluded){
			if (isIE && ieVersion<9){
				//IE6-ie8的vml controller
				resourceBuf.append("<script src=\"").append(contextPath)
				.append(this.FIREFLOW_RESOURCE_SERVLET).append("/org/fireflow/clientwidget/resources/vml/FireflowVmlControl.js")
				.append("\"></script>\n");
			}else{
				resourceBuf.append("<script src=\"").append(contextPath)
				.append(this.FIREFLOW_RESOURCE_SERVLET).append("/org/fireflow/clientwidget/resources/svg/FireflowSvgControl.js")
				.append("\"></script>\n");
				
			}

			this.pageContext.setAttribute(HAS_SVG_VML_CONTROL_JS_INCLUDED,Boolean.TRUE);
		}
		*/
		//4、引入toolbar js
		resourceBuf.append("\n");
		resourceBuf.append(buildToolBarJs());
		resourceBuf.append("\n");
		
		//最后 IE特殊处理
		/*
		if (isIE){
			Boolean isIncluded = (Boolean)this.pageContext.getAttribute(HAS_VML_SETTINGS_INCLUDED);
			if (isIncluded==null) isIncluded=Boolean.FALSE;
			if (ieVersion<9 && !isIncluded){//IE9以下采用VML，IE9及以上版本采用SVG
				resourceBuf.append("<!-- Include the VML behavior -->\n");
				//resourceBuf.append("<?import namespace=\"v\" implementation=\"#default#VML\" ?>");

				resourceBuf.append("<!-- Declare the VML namespace -->\n");
				resourceBuf.append("<xml:namespace ns=\"urn:schemas-microsoft-com:vml\" prefix=\"v\" />\n");
				resourceBuf.append("<xml:namespace ns=\"urn:schemas-microsoft-com:office:office\" prefix=\"o\" />\n");
				resourceBuf.append("<STYLE>\n");
				resourceBuf.append("v\\:Rect,v\\:RoundRect,v\\:imagedata,v\\:image ,v\\:oval  { BEHAVIOR: url(#default#VML) ;display:inline-block; *display:inline; *zoom:1 }\n");
				resourceBuf.append("v\\:Shape,v\\:shapetype,v\\:group ,v\\:background ,v\\:path,v\\:formulas ,v\\:f,v\\:handles ,v\\:fill, v\\:stroke,v\\:shadow,v\\:TextBox,v\\:textpath,v\\:line ,v\\:polyline ,v\\:curve ,v\\:arc  { BEHAVIOR: url(#default#VML)}\n");
				resourceBuf.append("</STYLE>\n");
				this.pageContext.setAttribute(HAS_VML_SETTINGS_INCLUDED,Boolean.TRUE);
			}
		}
		*/
		//

		return resourceBuf.toString();
	}
	
	protected String buildToolBarJs(){

		String tmp = new String(toolbar_js_fragment);
		
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
		String contextPath = request.getContextPath();
		if (contextPath==null)contextPath = "";
		if (contextPath.endsWith("/") ){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		
		String[] search = new String[]{"DIAGRAM_ID","CONTEXT_PATH",
				"PROCESS_ID","PROCESS_VERSION","PROCESS_TYPE","SVG_VML_WRAPPER_ID","SUB_PROCESS_NAME","SVG_VML_ID"};
		String[] replacement = new String[]{id,contextPath,
				this.processId,Integer.toString(this.version),FpdlConstants.PROCESS_TYPE_FPDL20,this.svgVmlWrapperDivId,this.subProcessName,this.svgVmlId};
		String js = StringUtils.replaceEach(tmp,search,replacement);
//		System.out.println("=========subProcessName is ==========================="+this.subProcessName);
//		System.out.println("=========original js is ===========================");
//		System.out.println(tmp);
//		System.out.println("=========new js is ===========================");
//		System.out.println(js);
		return js;
	}
	protected String buildToolBarDiv(){

		String tmp = new String(toolbar_tag_fragment);
		
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
		String contextPath = request.getContextPath();
		if (contextPath==null)contextPath = "";
		if (contextPath.endsWith("/") ){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		
		String[] search = new String[]{"DIAGRAM_ID","CONTEXT_PATH",
				"PROCESS_ID","PROCESS_VERSION","PROCESS_TYPE","SVG_VML_ID"};
		String[] replacement = new String[]{id,contextPath,
				this.processId,Integer.toString(this.version),FpdlConstants.PROCESS_TYPE_FPDL20,this.svgVmlId
				};
		String toolhtml = StringUtils.replaceEach(tmp,search,replacement);
		
		//追加subProcessSelector
		StringBuffer buf = new StringBuffer(toolhtml);
		buf.append("\n").append("<select id=\"").append(this.id).append("_subprocess_selector\" ")
			.append(" title=\"选择子流程\"  class=\"ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only\"")
			.append(" style=\"width:160px;text-align:left");
		
		if (showSubProcessSelector){
			buf.append("\">");
		}else{
			buf.append(";display:none\">\n");
		}
		
		//根据subProcess构造option
		WorkflowProcess process =  getWorkflowProcess();
		List<SubProcess> subProcessList = process==null?null:process.getLocalSubProcesses();
		StringBuffer optionsBuf = new StringBuffer();
		StringBuffer mainSubProcBuf = new StringBuffer();
		if (subProcessList!=null){
			for (SubProcess subProcess : subProcessList){
				String displayName = subProcess.getDisplayName();
				String name = subProcess.getName();
				if (displayName==null || displayName.trim().equals("")){
					displayName = name;
				}
				if (WorkflowProcess.MAIN_PROCESS_NAME.equals(name)){
					mainSubProcBuf.append("<option value=\"").append(name).append("\"")
					.append(">").append(displayName).append("</option>\n");
				}else{
					optionsBuf.append("<option value=\"").append(name).append("\"")
					.append(">").append(displayName).append("</option>\n");
				}
			}
		}
		buf.append(mainSubProcBuf).append(optionsBuf);
		buf.append("</select>\n");
		
		buf.append("</div>\n");
		
//		System.out.println("=========original is ===========================");
//		System.out.println(tmp);
//		System.out.println("=========new is ===========================");
//		System.out.println(toolhtml);
		return buf.toString();
	}
	
	protected String buildSvgVmlIframe(boolean isSvg){
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
		String contextPath = request.getContextPath();
		if (contextPath==null)contextPath = "";
		if (this.clientWidgetServletPath==null) this.clientWidgetServletPath="";
		if (contextPath.endsWith("/") && this.clientWidgetServletPath.startsWith("/")){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		StringBuffer sbuf = new StringBuffer();
		String diagramWidth = this.width;//this.width-(this.borderWidth+1)*2;
		int diagramHeight = this.height-(this.borderWidth+1)*2-TOOLBAR_HEIGHT;
		sbuf.append("<iframe id=\"").append(this.svgVmlWrapperDivId)
			.append("\" width=\"").append("100%")//.append(diagramWidth)
			.append("\" height=\"").append(diagramHeight)
			.append("px\"");
			
		sbuf.append(" src=\"").append(contextPath).append(clientWidgetServletPath)
		.append("?").append(Constants.ACTION_TYPE).append("=").append(Constants.GET_PROCESS_DIAGRAM)
		.append("&processId=").append(this.processId)
		.append("&processVersion=").append(this.version)
		.append("&processType=").append(FpdlConstants.PROCESS_TYPE_FPDL20)
		.append("&subProcessName=").append(this.subProcessName)
		.append("\"");
		
		sbuf.append("></iframe>");
		return sbuf.toString();
	}

//	protected String buildSVGDiagram(){	
//		//打开model
//		WorkflowProcess process = getWorkflowProcess();
//		
//		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
//		HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
//		String encoding = response.getCharacterEncoding();
//		if (encoding==null || encoding.trim().equals("")){
//			encoding = "UTF-8";
//		}
//		String contextPath = request.getContextPath();
//		
//		return FpdlExporter.exportToSVGDiagram(process, subProcessName,
//				this.svgVmlId,this.width-(this.borderWidth+1)*2,this.height-(this.borderWidth+1)*2-TOOLBAR_HEIGHT,
//				FIREFLOW_RESOURCE_SERVLET,contextPath,encoding);
//	}
//	
//	protected String buildVMLDiagram(){
//		//打开model
//		WorkflowProcess process = getWorkflowProcess();
//		
//		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
//		HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
//		String encoding = response.getCharacterEncoding();
//		if (encoding==null || encoding.trim().equals("")){
//			encoding = "UTF-8";
//		}
//		String contextPath = request.getContextPath();
//
//		return FpdlExporter.exportToVMLDiagram(process, subProcessName,this.svgVmlId,FIREFLOW_RESOURCE_SERVLET,contextPath,encoding);
//	}
	
	public static void main(String[] args){
		String[] search = new String[]{"DIAGRAM_ID","CONTEXT_PATH",
				"PROCESS_ID","PROCESS_VERSION","PROCESS_TYPE"};
		String[] replacement = new String[]{"processdiagram1","/demo",
				"process111","1",FpdlConstants.PROCESS_TYPE_FPDL20
				};
		
		String tmp = "    	}).click(function(event){"+
    		"//查询流程源代码"+
    		"$ff.get(\"/CONTEXT_PAT-H/FireflowClientWidgetServlet\","+
    					"{workflowActionType:\"GET_PROCESS_DEFS\","+
    					 "processId:\"PROCESS_ID\","+
    					 "processVersion:\"PROCESS_VERSION\","+
    					 "processType:\"PROCESS_TYPE\"})  DIAGRAM_ID";
    
		String toolhtml = StringUtils.replaceEach(tmp,search,replacement);
		
		System.out.println(toolhtml);
	}
}
