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
package org.fireflow.webdesigner.transformer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.model.ModelElement;
import org.fireflow.model.io.SerializerException;
import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.AssociationShape;
import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.ConnectorShape;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.EndNodeShape;
import org.fireflow.pdl.fpdl.diagram.GroupShape;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl.diagram.NodeShape;
import org.fireflow.pdl.fpdl.diagram.PoolShape;
import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.RouterShape;
import org.fireflow.pdl.fpdl.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl.diagram.TransitionShape;
import org.fireflow.pdl.fpdl.diagram.figure.Figure;
import org.fireflow.pdl.fpdl.diagram.figure.Line;
import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.LabelImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;
import org.fireflow.pdl.fpdl.io.FPDLNames;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FpdlDiagramSerializerSvgImpl extends AbstractFpdlDiagramSerializer {
	private Log log = LogFactory.getLog(FpdlDiagramSerializerSvgImpl.class);
	private static final String BASIC_CIRCLE_MARKER_ID = "BASIC_CIRCLE_MARKER";
	private static final String BASIC_BLOCK_ARROW_MARKER_ID  = "BASIC_BLOCK_ARROW_MARKER";
	private static final String BASIC_BLOCK_ARROW_MARKER2_ID  = "BASIC_BLOCK_ARROW_MARKER2";//空心
	
	
	private Element defsElement = null;
	private Map<String,Element> circleMarkerMap = new HashMap<String,Element>();
	private Map<String,Element> blockArrowMarkerMap = new HashMap<String,Element>();
	private Map<String,Element> blockArrowMarker2Map = new HashMap<String,Element>();

	/* (non-Javadoc)
	 * @see org.fireflow.clientwidget.transformer.FpdlDiagramSerializer#serializeDiagramToDoc(org.fireflow.pdl.fpdl.process.WorkflowProcess, java.lang.String)
	 */
	public Document serializeDiagramToDoc(WorkflowProcess workflowProcess,
			String subProcessName) throws SerializerException {
		_init(workflowProcess,subProcessName);
		
		//构造根节点
		Element root = document.createElement("svg");	
		root.setAttribute("id", diagram.getId());
		if (diagram.getWorkflowElementRef()!=null){
			root.setAttribute(FPDLNames.REF,diagram.getWorkflowElementRef().getId());
		}
		
		root.setAttribute("version", "1.1");
		root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		root.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
		root.setAttribute("onload", "fireflowSvgInit('"+diagram.getId()+"');");
		root.setAttribute("onunload", "fireflowSvgDestroy('"+diagram.getId()+"')");
		
		document.appendChild(root);
		
		//构造javascript
		Element scriptElm = document.createElement("script");
		scriptElm.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", 
				this.resourcePathPrefix+"/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/js/jquery-1.10.2.min.js");
		root.appendChild(scriptElm);
		
		scriptElm = document.createElement("script");
		scriptElm.setAttribute("type","application/ecmascript");
		root.appendChild(scriptElm);
		String data = "$ff=$;";
		CDATASection cdata = document.createCDATASection(data);
		scriptElm.appendChild(cdata);
		
		scriptElm = document.createElement("script");
		scriptElm.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", 
				this.resourcePathPrefix+"/org/fireflow/webdesigner/resources/svg/FireflowSvgControl.js");
		root.appendChild(scriptElm);
		
		//构造defs节点
		defsElement = document.createElement("defs");
		root.appendChild(defsElement);
		
		//构造markers
		//圆形
		Element circleMk = document.createElement("marker");
		circleMk.setAttribute("id", BASIC_CIRCLE_MARKER_ID);
		circleMk.setAttribute("markerWidth", "8");
		circleMk.setAttribute("markerHeight", "8");
		circleMk.setAttribute("refX", "4");
		circleMk.setAttribute("refY", "4");
		circleMk.setAttribute("markerUnits","userSpaceOnUse");
		defsElement.appendChild(circleMk);
		
		Element circleTmp = document.createElement("circle");
		circleTmp.setAttribute("cx", "4");
		circleTmp.setAttribute("cy", "4");
		circleTmp.setAttribute("r", "4");
		circleTmp.setAttribute("style", "stroke:#000000;fill:none;stroke-dasharray:2 0;");
		circleMk.appendChild(circleTmp);
		
		circleMarkerMap.put("#000000", circleMk);
		
		//实心的Block形态的三角形
		Element blockArrowMk = document.createElement("marker");
		blockArrowMk.setAttribute("id", BASIC_BLOCK_ARROW_MARKER_ID);
		blockArrowMk.setAttribute("markerWidth", "13");
		blockArrowMk.setAttribute("markerHeight", "12");
		blockArrowMk.setAttribute("refX", "11");
		blockArrowMk.setAttribute("refY", "6");
		blockArrowMk.setAttribute("orient","auto");
		blockArrowMk.setAttribute("markerUnits","userSpaceOnUse");
		defsElement.appendChild(blockArrowMk);
		
		Element blockArrow = document.createElement("path");
		blockArrow.setAttribute("d", "M2,2 L2,10 L11,6 L2,2");
		blockArrow.setAttribute("style", "stroke:none;fill:#000000;");
		blockArrowMk.appendChild(blockArrow);
		
		blockArrowMarkerMap.put("#000000", blockArrowMk);
		
		//空心的的Block形态的三角形
		Element blockArrowMk2 = document.createElement("marker");
		blockArrowMk2.setAttribute("id", BASIC_BLOCK_ARROW_MARKER2_ID);
		blockArrowMk2.setAttribute("markerWidth", "13");
		blockArrowMk2.setAttribute("markerHeight", "12");
		blockArrowMk2.setAttribute("refX", "11");
		blockArrowMk2.setAttribute("refY", "6");
		blockArrowMk2.setAttribute("orient","auto");
		blockArrowMk2.setAttribute("markerUnits","userSpaceOnUse");
		defsElement.appendChild(blockArrowMk2);
		
		Element blockArrow2 = document.createElement("path");
		blockArrow2.setAttribute("d", "M2,2 L2,10 L11,6 L2,2");
		blockArrow2.setAttribute("style", "stroke:#000000;fill:none;stroke-dasharray:2,0;");
		blockArrowMk2.appendChild(blockArrow2);
		
		blockArrowMarker2Map.put("#000000", blockArrowMk2);
		
		
		//构造id=viewport的g节点
		Element g = document.createElement("g");
		g.setAttribute("id", "viewport");
		root.appendChild(g);
		
		
		//流程节点
		List<ProcessNodeShape> processNodeShapeList = diagram
				.getProcessNodeShapes();
		if (processNodeShapeList!=null && processNodeShapeList.size()>0){
			for (ProcessNodeShape nodeShape : processNodeShapeList) {
				Element cell = this.transformProcessNodeShape2Svg(nodeShape,true);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}
		
		//注释
		List<CommentShape> commentShapeList = diagram.getComments();
		if (commentShapeList!=null && commentShapeList.size()>0){
			for (CommentShape commentShape : commentShapeList){
				Element cell = transformCommentShape2Svg(commentShape,true);
				if (cell!=null){
					g.appendChild(cell);
				}

			}
		}


		//Group
		List<GroupShape> groupShapeList = diagram.getGroups();
		if (groupShapeList!=null && groupShapeList.size()>0){
			for (GroupShape groupShape : groupShapeList){
				Element cell = transformGroupShape2Svg(groupShape,true);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}

		//Pool
		List<PoolShape> poolShapeList = diagram.getPools();
		if (poolShapeList!=null && poolShapeList.size()>0){
			for (PoolShape poolShape:poolShapeList){
				Element cell = this.transformPool2Svg(poolShape, true);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}
		
		//
		//transition
		List<TransitionShape> transitionShapeList = diagram.getTransitions();
		if (transitionShapeList!=null && transitionShapeList.size()>0){
			for (TransitionShape transitionShape : transitionShapeList){
				Element cell = transformConnectorShape2Svg(transitionShape);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}
		
		//association
		List<AssociationShape> associationShapeList = diagram.getAssociations();
		if(associationShapeList!=null && associationShapeList.size()>0){
			for (AssociationShape associationShape : associationShapeList){
				Element cell = transformConnectorShape2Svg(associationShape);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}
//		
//		//messageflow
		List<MessageFlowShape> messageFlowShapeList = diagram.getMessageFlows();
		if (messageFlowShapeList!=null && messageFlowShapeList.size()>0){
			for (MessageFlowShape messageFlowShape : messageFlowShapeList){
				Element cell = this.transformConnectorShape2Svg(messageFlowShape);
				if (cell!=null){
					g.appendChild(cell);
				}
			}
		}
		
		//根据图形长宽，设置root属性
		int width = this.rightDownX-this.leftTopX;
		int height = this.rightDownY - this.leftTopY;
		root.setAttribute("width", Integer.toString(width));
		root.setAttribute("height", Integer.toString(height));
		
		/*
		int origX = 0;
		int origY = 0;
		if (this.leftTopX<0){
			origX = this.leftTopX;
		}
		if (this.leftTopY<0){
			origY = this.leftTopY;
		}
		root.setAttribute("coordorigin", origX+","+origY);
		 */

		return document;
	}

	private Element transformProcessNodeShape2Svg(ProcessNodeShape nodeShape,boolean isTopLevelElem){
		Element cell = null;
		if (nodeShape instanceof StartNodeShape){
			cell = this.transformStartNodeShape2Svg((StartNodeShape)nodeShape,isTopLevelElem,true);
		}
		
		else if (nodeShape instanceof EndNodeShape){
			cell = this.transformEndNodeShape2Svg((EndNodeShape)nodeShape,isTopLevelElem);
		}
		else if (nodeShape instanceof RouterShape){
			cell = this.transformRouterShape2Svg((RouterShape)nodeShape,isTopLevelElem);
		}
		else if (nodeShape instanceof ActivityShape){
			cell = this.transformActivityShape2Svg((ActivityShape)nodeShape,true);
		}
		//ModelElement wfElm = nodeShape.getWorkflowElementRef();

		return cell;
	}
	
	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformActivityShape2Svg(ActivityShape nodeShape,boolean isTopLevelElem){
		Activity activity = (Activity)nodeShape.getWorkflowElementRef();
		String imgUri = _getActivityImgUri(activity);	
		
		//1、构造viewport
		Bounds newportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle)nodeShape.getFigure();

		Element nodeGroup = document.createElement("g");
		
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.ACTIVITY);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		String wfElmId = wfElmRef==null?"":wfElmRef.getId();
		String wfElmName = wfElmRef==null?"":wfElmRef.getName();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmId);
		}
		
		
		//2、Activity边框		
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth()-thick;
		int newHeight = rectBounds.getHeight()-thick;
		
		Element rect = document.createElement("rect");
		rect.setAttribute("x",Integer.toString(newportBounds.getX()+thick/2));
		rect.setAttribute("y", Integer.toString(newportBounds.getY()+thick/2));
		rect.setAttribute("width", Integer.toString(newWidth));
		rect.setAttribute("height", Integer.toString(newHeight));
		rect.setAttribute("rx", Integer.toString(rectBounds.getCornerRadius()));
		rect.setAttribute("ry", Integer.toString(rectBounds.getCornerRadius()));
		nodeGroup.appendChild(rect);
		
		
		StringBuffer rectStyle = (new StringBuffer());
		
		//2.1 画笔
		if (figure.getBounds()!=null){			
			rectStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			String color = rectBounds.getColor();
			rectStyle.append("stroke:").append((color==null || color.trim().equals(""))?"#000000":color).append(";");
			String lineType = rectBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = rectBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					if (Bounds.LINETYPE_DOTTED.equals(lineType)){
						dashArray = DEFAULT_DOT_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHED.equals(lineType)){
						dashArray = DEFAULT_DASH_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
						dashArray = DEFAULT_DOTDASH_DASHARRAY;
					}
				}
				rectStyle.append("stroke-dasharray: ").append(dashArray).append(";");
			}
		}
		
		//2.2填充

		if (figure.getFulfilStyle()!=null){
			//首先往defsElement中插入一个grandient
			String gradientElmId = wfElmName+"_fill_pattern";
			Element gradientElm = document.createElement("linearGradient");
			gradientElm.setAttribute("id", gradientElmId);
			gradientElm.setAttribute("spreadMethod", "pad");
			
			String color1 = figure.getFulfilStyle().getColor1();
			Element stop1 = document.createElement("stop");
			stop1.setAttribute("offset", "0%");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("stop-opacity", "1");
			
			
			String color2 = figure.getFulfilStyle().getColor2();
			Element stop2 = document.createElement("stop");
			stop2.setAttribute("offset", "100%");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("stop-opacity", "1");
			
			
			String gradient = figure.getFulfilStyle().getGradientStyle();
	
			
			if (gradient==null || gradient.trim().equals("") || FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)){
				gradientElm.appendChild(stop2);
			}else{
				gradientElm.appendChild(stop1);
				gradientElm.appendChild(stop2);
				
				
				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradient)){
					gradientElm.setAttribute("x1", "100%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}else{
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}
			}
			
			this.defsElement.appendChild(gradientElm);
			
			rectStyle.append("fill:url(#").append(gradientElmId).append(");");
		}else{
			rectStyle.append("fill:none;");
		}
		
		rect.setAttribute("style", rectStyle.toString());
		
		
		//2.3 activity的名称
		String displayName = activity.getDisplayName();
		if (displayName == null || displayName.trim().equals("")) {
			displayName = activity.getName();
		}
		int padding = 6;
		int spacing = 5;
		
		//构造textbox
		int textBoxPaddingY = (SVC_LOGO_RADIUS * 2 + padding+spacing );
		int textBoxPaddingX = padding ;
		Label contentLabel = null;
		int fontSize = DEFAULT_FONT_SIZE;
		if (figure.getContentLabel()!=null){
			contentLabel = figure.getContentLabel().copy();
			fontSize = contentLabel.getFontSize();
		}else{
			contentLabel = new LabelImpl();
			contentLabel.setFontSize(fontSize);
		}
		contentLabel.setText(displayName);

		Element textRect = this.drawActivityText(nodeShape, FPDLNames.ACTIVITY,
				newportBounds.getX()+textBoxPaddingX, newportBounds.getY()+textBoxPaddingY,
				rectBounds.getWidth()-textBoxPaddingX*2, rectBounds.getHeight()-textBoxPaddingY*2, contentLabel);
		
		if (textRect!=null){
			nodeGroup.appendChild(textRect);
		}
		
		
		
		//图形logo
		if (imgUri!=null){
			Element nodeImg = document.createElement("image");
			nodeImg.setAttribute("x",Integer.toString( newportBounds.getX()+padding+thick));
			nodeImg.setAttribute("y",Integer.toString( newportBounds.getY()+padding+thick));
			nodeImg.setAttribute("width",Integer.toString( SVC_LOGO_RADIUS*2));
			nodeImg.setAttribute("height",Integer.toString( SVC_LOGO_RADIUS*2));
			nodeImg.setAttribute("xlink:href", this.resourcePathPrefix+imgUri);
			nodeGroup.appendChild(nodeImg);       
		}
		
		//构造attatched event node

		List<DiagramElement> children = nodeShape.getChildren();
		if (children!=null && children.size()>0){
			//1、首先构造坐标系
			Element attachedEvtSvg = document.createElement("svg");
			attachedEvtSvg.setAttribute("x", Integer.toString(rectBounds.getX()));
			attachedEvtSvg.setAttribute("y", Integer.toString(rectBounds.getY()+rectBounds.getHeight()-IMG_RADIUS));
			attachedEvtSvg.setAttribute("width", Integer.toString(rectBounds.getWidth()));
			attachedEvtSvg.setAttribute("height", Integer.toString(IMG_RADIUS*2+DEFAULT_FONT_SIZE+IconTextGap));
			
			nodeGroup.appendChild(attachedEvtSvg);
			
			for (DiagramElement child : children){
				Element elm = this.transformStartNodeShape2Svg((StartNodeShape)child, false,false);
				attachedEvtSvg.appendChild(elm);
			}
		}


		if (isTopLevelElem){
			_refreshDiagramSize(newportBounds);			
		}
		
		return nodeGroup;
	}		
	
	private Element drawActivityText(DiagramElement diagramElement,String elementType,
			int x, int y, int w, int h,
			Label label)
	{
		Element result = null;
		
		String text = label.getText();
		
		String fontColor = label.getFontColor();
//		String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
				//mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);
		
		int fontSize = label.getFontSize();
		
		String fireFontStyle = label.getFontStyle();
		
		boolean isItalic = false;
		if (Label.FONT_STYLE_ITALIC.equals(fireFontStyle) ||
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			isItalic = true;
		}
//		int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = "normal";
		if (Label.FONT_STYLE_BOLD.equals(fireFontStyle) || 
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			weight = "bold";
		}

		String uline = "none";
		
		if (text != null && text.length() > 0)
		{
			Element foreignObject = document.createElement("foreignObject");
			foreignObject.setAttribute("x", Integer.toString(x));
			foreignObject.setAttribute("y", Integer.toString(y));
			foreignObject.setAttribute("width", Integer.toString(w));
			foreignObject.setAttribute("height", Integer.toString(h));
			foreignObject.setAttribute("requiredFeatures", "http://www.w3.org/TR/SVG11/feature#Extensibility");
			
			
			Element textDiv = document.createElement("div");
			foreignObject.appendChild(textDiv);			
			
			textDiv.setAttribute("xmlns","http://www.w3.org/1999/xhtml");
			textDiv.setAttribute("onmouseover", "this.style.cursor='pointer';");
			textDiv.setAttribute("onmouseout","this.style.cursor='default';");
			
			StringBuffer styleBufP = new StringBuffer();
			
			//
			styleBufP.append("width:").append(w).append("px;")
					.append("height:").append(h).append("px;")
					.append("text-align:center;word-wrap:break-word;");
			
			//行高 等于 fontSize+3像素
			styleBufP.append("line-height:100%;");
			
			styleBufP.append("font-weight:").append(weight).append(";")
				.append("font-size:").append(String.valueOf(fontSize)).append("px;")
				.append("color:").append(fontColor).append(";")
				.append("text-decoration:none;");
			if (isItalic)
			{
				styleBufP.append("font-style:").append( "italic").append(";");
			}
			textDiv.setAttribute("style", styleBufP.toString());
			

			//构造click handler，diagramId
			StringBuffer clickHandler = new StringBuffer();
			clickHandler.append("on_element_click(")
					.append("'").append(diagramElement.getId()).append("',");
			
			ModelElement wfElement = diagramElement.getWorkflowElementRef();
			
			//构造click handler wfElement Id
			if (wfElement!=null){
				clickHandler.append("'").append(wfElement.getId()).append("',");
			}else{
				clickHandler.append("'',");
			}
			//构造click handler --element type
			clickHandler.append("'").append(elementType).append("',");
			
			//构造click handler --process id 
			clickHandler.append("'").append(workflowProcess.getId()).append("',");
			
			//构造click handler --subprocess name
			clickHandler.append("'").append(this.subProcessName).append("');");
					
			textDiv.setAttribute("onclick", clickHandler.toString()); 
			
			textDiv.appendChild(document.createTextNode(text));
			
			result = foreignObject;
		}
		return result;
	}
	
	private Element drawActivityText_bak(DiagramElement diagramElement,String elementType,
			int x, int y, int w, int h,
			Label label)
	{
		Element result = null;
		
		String text = label.getText();
		
		String fontColor = label.getFontColor();
		String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
				//mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);
		
		int fontSize = label.getFontSize();
		
		String fireFontStyle = label.getFontStyle();
		
		boolean isItalic = false;
		if (Label.FONT_STYLE_ITALIC.equals(fireFontStyle) ||
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			isItalic = true;
		}
//		int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = "normal";
		if (Label.FONT_STYLE_BOLD.equals(fireFontStyle) || 
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			weight = "bold";
		}

		String uline = "none";
		
		if (text != null && text.length() > 0)
		{
			// Applies the opacity
			float opacity = 100;



			String transform = null;

//			if (!mxUtils.isTrue(style, mxConstants.STYLE_HORIZONTAL, true))
//			{
//				double cx = x + w / 2;
//				double cy = y + h / 2;
//				transform = "rotate(270 " + cx + " " + cy + ")";
//			}


			//0、构造switch元素
			Element switchElm = document.createElement("switch");
			result = switchElm;
			
			
			//1、首先svg1.2的textarea元素
			Element g = document.createElement("g");
			g.setAttribute("requiredFeatures", "http://www.w3.org/Graphics/SVG/feature/1.2/#TextFlow");
			switchElm.appendChild(g);
			
			Element textArea = document.createElement("textArea");
			textArea.setAttribute("width", Integer.toString(w));
			textArea.setAttribute("height", Integer.toString(h));
			textArea.setAttribute("font-weight", weight);
			textArea.setAttribute("text-decoration", uline);
			if (isItalic)
			{
				textArea.setAttribute("font-style", "italic");
			}
			textArea.setAttribute("font-size", String.valueOf(fontSize));
			textArea.setAttribute("font-family", fontFamily);
			textArea.setAttribute("fill", fontColor);
			if (opacity != 100)
			{
				String value = String.valueOf(opacity / 100);
				textArea.setAttribute("fill-opacity", value);
				textArea.setAttribute("stroke-opacity", value);
			}
			g.appendChild(textArea);
			
			textArea.appendChild(document.createTextNode(text));

			
			//2、创建foreignObject 
			Element foreignObject = document.createElement("foreignObject");
			foreignObject.setAttribute("x", Integer.toString(x));
			foreignObject.setAttribute("y", Integer.toString(y));
			foreignObject.setAttribute("width", Integer.toString(w));
			foreignObject.setAttribute("height", Integer.toString(h));
			foreignObject.setAttribute("requiredFeatures", "http://www.w3.org/TR/SVG11/feature#Extensibility");
			switchElm.appendChild(foreignObject);
			
			
			StringBuffer styleBuf = new StringBuffer();
			styleBuf.append("font-weight:").append(weight).append(";")
				.append("font-size:").append(String.valueOf(fontSize)).append("px;")
				.append("font-family:").append(fontFamily).append(";")
				.append("color:").append(fontColor).append(";");
			if (isItalic)
			{
				styleBuf.append("font-style:").append( "italic").append(";");
			}
			Element pElem = document.createElement("p");
			pElem.setAttribute("style", styleBuf.toString());
			pElem.setAttribute("xmlns","http://www.w3.org/1999/xhtml");

			pElem.appendChild(document.createTextNode(text));
			foreignObject.appendChild(pElem);

			
			//4、构造text节点
			Element textElm = null;
			textElm = document.createElement("text");
			textElm.setAttribute("font-weight", weight);
			textElm.setAttribute("font-decoration", uline);
			if (isItalic)
			{
				textElm.setAttribute("font-style", "italic");
			}

			textElm.setAttribute("font-size", String.valueOf(fontSize));
			textElm.setAttribute("font-family", fontFamily);
			textElm.setAttribute("fill", fontColor);

			if (opacity != 100)
			{
				String value = String.valueOf(opacity / 100);
				textElm.setAttribute("fill-opacity", value);
				textElm.setAttribute("stroke-opacity", value);
			}

			String[] lines = text.split("\n");
			y += fontSize
					+ (h - lines.length * (fontSize ))
					/ 2 - 2;

			String align = "center";
			String anchor = "start";

			if (align.equals("right"))
			{
				anchor = "end";
				x += w - 0;//mxConstants.LABEL_INSET ;
			}
			else if (align.equals("center"))
			{
				anchor = "middle";
				x += w / 2;
			}
			else
			{
				x += 0;//mxConstants.LABEL_INSET ;
			}

			textElm.setAttribute("text-anchor", anchor);

			for (int i = 0; i < lines.length; i++)
			{
				Element tspan = document.createElement("tspan");

				tspan.setAttribute("x", String.valueOf(x));
				tspan.setAttribute("y", String.valueOf(y));

				tspan.appendChild(document.createTextNode(lines[i]));
				textElm.appendChild(tspan);

				y += fontSize + 0;// mxConstants.LINESPACING;
			}

			if (transform != null)
			{
				textElm.setAttribute("transform", transform);
			}
			
			switchElm.appendChild(textElm);

			//appendSvgElement(switchElm);//为什么要append在根节点呢？
		}

		return result;
	}

	
	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformRouterShape2Svg(RouterShape nodeShape,boolean isTopLevelElem){
		String imgUri = _getSynchronizerNodeImgUri(nodeShape);
		
		Router node = (Router)nodeShape.getWorkflowElementRef();
		
		//计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(nodeShape);
		Figure figure = nodeShape.getFigure();

		
		Element nodeGroup = document.createElement("g");

		//id 等属性
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.ROUTER);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}

		Element nodeImg = document.createElement("image");

		nodeImg.setAttribute("width", Integer.toString(IMG_RADIUS*2));
		nodeImg.setAttribute("height", Integer.toString(IMG_RADIUS*2));
		
		nodeImg.setAttribute("x", Integer.toString(bounds.getX()+(bounds.getWidth()/2-IMG_RADIUS)));
		nodeImg.setAttribute("y", Integer.toString(bounds.getY()));
		
		nodeImg.setAttribute("xlink:href", this.resourcePathPrefix+imgUri);
		nodeGroup.appendChild(nodeImg);
		
		
		String displayName = node.getDisplayName();
		if(displayName!=null && !displayName.trim().equals("")){
			Label titleLabel = null;
			int fontSize = DEFAULT_FONT_SIZE;
			if (figure.getTitleLabel()!=null){
				titleLabel = figure.getTitleLabel().copy();
				fontSize = titleLabel.getFontSize();
			}else{
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(fontSize);
			}
			titleLabel.setText(displayName);
			
			Element textRect = this.buildTextBox(nodeShape,FPDLNames.ROUTER,
					bounds.getX(), bounds.getY()+(IMG_RADIUS*2+IconTextGap),
					bounds.getWidth(), (int)(fontSize), titleLabel, "middle");
			
			nodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem){
			_refreshDiagramSize(bounds);			
		}
		
		return nodeGroup;
	}	
	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformEndNodeShape2Svg(EndNodeShape nodeShape,boolean isTopLevelElem){
		String imgUri = _getSynchronizerNodeImgUri(nodeShape);
		
		EndNode node = (EndNode)nodeShape.getWorkflowElementRef();
		
		//计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(nodeShape);
		Figure figure = nodeShape.getFigure();
		
		Element nodeGroup = document.createElement("g");

		
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.END_NODE);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		
		Element nodeImg = document.createElement("image");

		nodeImg.setAttribute("width", Integer.toString(IMG_RADIUS*2));
		nodeImg.setAttribute("height", Integer.toString(IMG_RADIUS*2));
		
		nodeImg.setAttribute("x", Integer.toString(bounds.getX()+(bounds.getWidth()/2-IMG_RADIUS)));
		nodeImg.setAttribute("y", Integer.toString(bounds.getY()));
		
		nodeImg.setAttribute("xlink:href", this.resourcePathPrefix+imgUri);
		nodeGroup.appendChild(nodeImg);
		
		
		String displayName = node.getDisplayName();
		if(displayName!=null && !displayName.trim().equals("")){
			Label titleLabel = null;
			int fontSize = DEFAULT_FONT_SIZE;
			if (figure.getTitleLabel()!=null){
				titleLabel = figure.getTitleLabel().copy();
				fontSize = titleLabel.getFontSize();
			}else{
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(fontSize);
			}
			titleLabel.setText(displayName);
			
			Element textRect = this.buildTextBox(nodeShape,FPDLNames.END_NODE,
					bounds.getX(), bounds.getY()+(IMG_RADIUS*2+IconTextGap),
					bounds.getWidth(), (int)(fontSize), titleLabel, "middle");
			
			nodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem){
			_refreshDiagramSize(bounds);			
		}
		
		return nodeGroup;
	}
	
	/**
	 * 
	 * @param startNodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformStartNodeShape2Svg(StartNodeShape startNodeShape,boolean isTopLevelElem,boolean showLabel){
		String imgUri = _getSynchronizerNodeImgUri(startNodeShape);
		
		StartNode startNode = (StartNode)startNodeShape.getWorkflowElementRef();
		
		//计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(startNodeShape);
		Figure figure = startNodeShape.getFigure();

		
		Element startNodeGroup = document.createElement("g");

		
		//id 等属性
		startNodeGroup.setAttribute(FPDLNames.ID, startNodeShape.getId());
		startNodeGroup.setAttribute(TYPE, FPDLNames.START_NODE);
		ModelElement wfElmRef = startNodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			startNodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}

		
		
		Element startNodeImg = document.createElement("image");
		
		startNodeImg.setAttribute("width", Integer.toString(IMG_RADIUS*2));
		startNodeImg.setAttribute("height", Integer.toString(IMG_RADIUS*2));
		
		startNodeImg.setAttribute("x", Integer.toString(bounds.getX()+(bounds.getWidth()/2-IMG_RADIUS)));
		startNodeImg.setAttribute("y", Integer.toString(bounds.getY()));

		startNodeImg.setAttribute("xlink:href", this.resourcePathPrefix+imgUri);
		startNodeGroup.appendChild(startNodeImg);
		

		if (showLabel){
			String displayName = startNode.getDisplayName();
			if(displayName!=null && !displayName.trim().equals("")){
				Label titleLabel = null;
				int fontSize = DEFAULT_FONT_SIZE;
				if (figure.getTitleLabel()!=null){
					titleLabel = figure.getTitleLabel().copy();
					fontSize = titleLabel.getFontSize();
				}else{
					titleLabel = new LabelImpl();
					titleLabel.setFontSize(fontSize);
				}
				titleLabel.setText(displayName);
				
				Element textRect = this.buildTextBox(startNodeShape,FPDLNames.START_NODE,
						bounds.getX(), bounds.getY()+(IMG_RADIUS*2+IconTextGap),
						bounds.getWidth(), (int)(fontSize), titleLabel,"middle");
				
				startNodeGroup.appendChild(textRect);
			}	
		}


		if (isTopLevelElem){
			_refreshDiagramSize(bounds);			
		}
		
		return startNodeGroup;
	}
	
	private Element buildTextBox(DiagramElement diagramElement,String elementType,int left,int top,int width,int height,
			Label label,String textAlign){
		return buildTextBox(diagramElement,elementType,left,top,width,height,label,textAlign,"middle",true);
	}
	private Element buildTextBox(DiagramElement diagramElement,String elementType,int left,int top,int width,int height,
			Label label,String textAlign,String valign,boolean ishorizonal){

		int fontSize = label.getFontSize();
		String fontColor = label.getFontColor();
		String fontStyle = label.getFontStyle();
		
		if (fontStyle==null || fontStyle.trim().equals("")){
			fontStyle = Label.FONT_STYLE_NORMAL;
		}
		
		Element textBox = document.createElement("text");
		
		if (ishorizonal){//横向文字坐标
			if("middle".equals(textAlign)){
				textBox.setAttribute("x", Integer.toString(left+width/2));
			}else if ("start".equals(textAlign)){
				textBox.setAttribute("x", Integer.toString(left));
			}else if ("end".equals(textAlign)){
				textBox.setAttribute("x", Integer.toString(left+width));
			}else{
				textBox.setAttribute("x", Integer.toString(left));
			}
			textBox.setAttribute("y", Integer.toString(top+height));
		}else{
			//纵向文字坐标，TODO 仅处理middle的情况
			String x = Integer.toString(left+fontSize);
			String y = Integer.toString(top+height/2);
			textBox.setAttribute("x", x);
			textBox.setAttribute("y", y);
			
			//旋转90度
			textBox.setAttribute("transform", "rotate(270 "+x+","+y+")");
		}

		StringBuffer textStyle = new StringBuffer();
		
		if (!ishorizonal){
			//firefox 不支持writting-mode
			//textStyle.append("writing-mode: tb;");
		}
		
		textStyle.append("text-anchor:").append(textAlign).append(";")
				.append("font-size:").append(fontSize).append("px;")
//				.append("letter-spacing:").append("1px;")
				.append("fill:").append(fontColor).append(";")
				.append("stroke:none;");

		
		if (fontStyle.equals(Label.FONT_STYLE_BOLD)){
			textStyle.append("font-weight:").append("bold;");
		}else if (fontStyle.equals(Label.FONT_STYLE_ITALIC)){
			textStyle.append("font-style:").append("italic;");
		}else if (fontStyle.equals(Label.FONT_STYLE_ITALIC_BOLD)){
			textStyle.append("font-weight:").append("bold;");
			textStyle.append("font-style:").append("italic;");
		}

		
		textBox.setAttribute("style", textStyle.toString());
		
		textBox.setAttribute("onmouseover", "this.style.cursor='pointer';");
		textBox.setAttribute("onmouseout", "this.style.cursor='default';");



		//构造click handler，diagramId
		StringBuffer clickHandler = new StringBuffer();
		clickHandler.append("on_element_click(")
				.append("'").append(diagramElement.getId()).append("',");
		
		ModelElement wfElement = diagramElement.getWorkflowElementRef();
		
		//构造click handler wfElement Id
		if (wfElement!=null){
			clickHandler.append("'").append(wfElement.getId()).append("',");
		}else{
			clickHandler.append("'',");
		}
		//构造click handler --element type
		clickHandler.append("'").append(elementType).append("',");
		
		//构造click handler --process id 
		clickHandler.append("'").append(workflowProcess.getId()).append("',");
		
		//构造click handler --subprocess name
		clickHandler.append("'").append(this.subProcessName).append("');");

		textBox.setAttribute("onclick", clickHandler.toString()); 
		
		Text textNode = document.createTextNode(label.getText());
		textBox.appendChild(textNode);

		return textBox;
	}
	
	private Element transformGroupShape2Svg(GroupShape nodeShape, boolean isTopLevelElem){
		
		//1、构造viewport
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle)nodeShape.getFigure();

		Element nodeGroup = document.createElement("g");
		

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.GROUP);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		//2、Group边框		
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth()-thick;
		int newHeight = rectBounds.getHeight()-thick;
		
		Element rect = document.createElement("rect");
		rect.setAttribute("x",Integer.toString(viewportBounds.getX()+thick/2));
		rect.setAttribute("y", Integer.toString(viewportBounds.getY()+thick/2));
		rect.setAttribute("width", Integer.toString(newWidth));
		rect.setAttribute("height", Integer.toString(newHeight));
		rect.setAttribute("rx", Integer.toString(rectBounds.getCornerRadius()));
		rect.setAttribute("ry", Integer.toString(rectBounds.getCornerRadius()));
		nodeGroup.appendChild(rect);
		
		
		StringBuffer rectStyle = (new StringBuffer());
		
		//2.1 画笔
		if (figure.getBounds()!=null){			
			rectStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			String color = rectBounds.getColor();
			rectStyle.append("stroke:").append((color==null || color.trim().equals(""))?"#000000":color).append(";");
			String lineType = rectBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = rectBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					dashArray = DEFAULT_GROUP_DASHARRAY;
				}

				rectStyle.append("stroke-dasharray: ").append(dashArray).append(";");
			}
		}
		
		//2.2填充

		if (figure.getFulfilStyle()!=null){
			//首先往defsElement中插入一个grandient
			String gradientElmId = nodeShape.getId()+"_fill_pattern";
			Element gradientElm = document.createElement("linearGradient");
			gradientElm.setAttribute("id", gradientElmId);
			gradientElm.setAttribute("spreadMethod", "pad");
			
			String color1 = figure.getFulfilStyle().getColor1();
			Element stop1 = document.createElement("stop");
			stop1.setAttribute("offset", "0%");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("stop-opacity", "1");
			
			
			String color2 = figure.getFulfilStyle().getColor2();
			Element stop2 = document.createElement("stop");
			stop2.setAttribute("offset", "100%");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("stop-opacity", "1");
			
			
			String gradient = figure.getFulfilStyle().getGradientStyle();
	
			
			if (gradient==null || gradient.trim().equals("") || FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)){
				gradientElm.appendChild(stop2);
			}else{
				gradientElm.appendChild(stop1);
				gradientElm.appendChild(stop2);
				
				
				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradient)){
					gradientElm.setAttribute("x1", "100%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}else{
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}
			}
			
			this.defsElement.appendChild(gradientElm);
			
			rectStyle.append("fill:url(#").append(gradientElmId).append(");");
		}else{
			rectStyle.append("fill:none;");
		}
		
		rect.setAttribute("style", rectStyle.toString());
		

		//2.3 label的名称
		String title = figure.getTitle();
		if (title!=null && !title.trim().equals("")){			
			//构造textbox
			int paddingLeft = thick;
			Label titleLabel = figure.getTitleLabel();
			int fontSize = titleLabel.getFontSize();
			
			int width = rectBounds.getWidth()-paddingLeft*2;//一般而言，width不可能小于2倍thick
			if (width<0){
				width = 0;
			}
			
			int textRectHeight = (int)(fontSize+2);//设置一定的余量
			
			int check = GROUP_TITLE_HEIGHT-textRectHeight-thick;
			int paddingTop = thick;
			int height = textRectHeight;
			
			if (check>0){
				paddingTop = thick+(int)(check/2);
				
			}else{
				height = GROUP_TITLE_HEIGHT-thick;
			}
			
			Element textRect = this.buildTextBox(nodeShape,FPDLNames.GROUP,
					viewportBounds.getX()+paddingLeft,viewportBounds.getY()+ paddingTop,
					width, height,
					titleLabel,"middle");
			
			nodeGroup.appendChild(textRect);
		}

		


		if (isTopLevelElem){
			_refreshDiagramSize(viewportBounds);			
		}
		
		//3、构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children!=null && children.size()>0){
			//构造一个新的view port，该viewport应该和group边框匹配
			Element childrenGroup = document.createElement("svg");
			childrenGroup.setAttribute("x", Integer.toString(viewportBounds.getX()+thick));
			childrenGroup.setAttribute("y", Integer.toString(viewportBounds.getY()+GROUP_TITLE_HEIGHT));
			childrenGroup.setAttribute("width", Integer.toString(rectBounds.getWidth()-thick*2));
			childrenGroup.setAttribute("height", Integer.toString(rectBounds.getHeight()-GROUP_TITLE_HEIGHT-thick));
			
			for (DiagramElement diagramElm : children){
				if (diagramElm instanceof ProcessNodeShape){
					Element elm = this.transformProcessNodeShape2Svg((ProcessNodeShape)diagramElm, false);
					if (elm!=null){
						childrenGroup.appendChild(elm);
					}
				}
//				else if (diagramElm instanceof CommentShape){
//					Element elm = this.transformCommentShape2Vml((CommentShape)diagramElm, false);
//					if (elm!=null){
//						childrenGroup.appendChild(elm);
//					}
//				}
			}
			
			nodeGroup.appendChild(childrenGroup);
		}
		
		return nodeGroup;
	}
	
	private Element drawCommentText(DiagramElement diagramElement,String elementType,
			int x, int y, int w, int h,
			Label label)
	{
		Element result = null;
		
		String text = label.getText();
		
		String fontColor = label.getFontColor();
//		String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
				//mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);
		
		int fontSize = label.getFontSize();
		
		String fireFontStyle = label.getFontStyle();
		
		boolean isItalic = false;
		if (Label.FONT_STYLE_ITALIC.equals(fireFontStyle) ||
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			isItalic = true;
		}
//		int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = "normal";
		if (Label.FONT_STYLE_BOLD.equals(fireFontStyle) || 
				Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)){
			weight = "bold";
		}

		String uline = "none";
		
		if (text != null && text.length() > 0)
		{
			Element foreignObject = document.createElement("foreignObject");
			foreignObject.setAttribute("x", Integer.toString(x));
			foreignObject.setAttribute("y", Integer.toString(y));
			foreignObject.setAttribute("width", Integer.toString(w));
			foreignObject.setAttribute("height", Integer.toString(h));
			foreignObject.setAttribute("requiredFeatures", "http://www.w3.org/TR/SVG11/feature#Extensibility");
			
			
			Element textDiv = document.createElement("div");
			textDiv.setAttribute("xmlns","http://www.w3.org/1999/xhtml");
			foreignObject.appendChild(textDiv);			
			
			StringBuffer styleBuf = new StringBuffer();
			//
			styleBuf.append("width:").append(w).append("px;")
					.append("height:").append(h).append("px;")
					.append("word-wrap:break-word;");
			
			//行高 等于 fontSize+3像素
			styleBuf.append("line-height:100%;");
			styleBuf.append("font-weight:").append(weight).append(";")
				.append("font-size:").append(String.valueOf(fontSize)).append("px;")
				.append("color:").append(fontColor).append(";");
			if (isItalic)
			{
				styleBuf.append("font-style:").append( "italic").append(";");
			}

			textDiv.setAttribute("style", styleBuf.toString());
			
			StringTokenizer tokenizer = new StringTokenizer(text,"\n",true);
			while (tokenizer.hasMoreTokens()){
				String tmpStr = tokenizer.nextToken();
				if (tmpStr.equals("\n")){
					Element brElm = document.createElement("br");
					textDiv.appendChild(brElm);
				}else{
					Element pElem = document.createElement("span");

					pElem.appendChild(document.createTextNode(tmpStr));
					textDiv.appendChild(pElem);
				}
			}
			
			result = foreignObject;
		}
		return result;
	}
	
	private Element transformCommentShape2Svg(CommentShape commentShape,boolean isTopLevelElm){
		//1、构造view port
		Bounds viewportBounds = _getViewPortBounds(commentShape);
		Rectangle figure = (Rectangle)commentShape.getFigure();

		Element nodeGroup = document.createElement("g");
		
		nodeGroup.setAttribute(FPDLNames.ID, commentShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.COMMENT);
		ModelElement wfElmRef = commentShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		//2、Comment边框		
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth()-thick;
		int newHeight = rectBounds.getHeight()-thick;
		
		
		Element polyline = document.createElement("path");
		StringBuffer pointList = new StringBuffer();
		pointList.append("M ").append(rectBounds.getX()+(int)(rectBounds.getWidth()/3)).append(" ").append(rectBounds.getY()).append(" ")
				.append("L ").append(rectBounds.getX()).append(" ").append(rectBounds.getY()).append(" ")
				.append("L ").append(rectBounds.getX()).append(" ").append(rectBounds.getY()+rectBounds.getHeight()).append(" ")
				.append("L ").append(rectBounds.getX()+(int)(rectBounds.getWidth()/3)).append(" ").append(rectBounds.getY()+rectBounds.getHeight());
		
		polyline.setAttribute("d", pointList.toString());
		polyline.setAttribute("fill", "none");
		nodeGroup.appendChild(polyline);

		
		//2.1 画笔
		StringBuffer polylineStyle = (new StringBuffer());
		if (figure.getBounds()!=null){			
			polylineStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			String color = rectBounds.getColor();
			polylineStyle.append("stroke:").append((color==null || color.trim().equals(""))?"#000000":color).append(";");
			String lineType = rectBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = rectBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					if (Bounds.LINETYPE_DOTTED.equals(lineType)){
						dashArray = DEFAULT_DOT_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHED.equals(lineType)){
						dashArray = DEFAULT_DASH_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
						dashArray = DEFAULT_DOTDASH_DASHARRAY;
					}
				}
				polylineStyle.append("stroke-dasharray: ").append(dashArray).append(";");
			}
		}
		polyline.setAttribute("style", polylineStyle.toString());
		
		
		
		//2.2 内容区域填充		
		Element contentBackground = document.createElement("rect");
		contentBackground.setAttribute("x", Integer.toString(rectBounds.getX()+thick+1));
		contentBackground.setAttribute("y", Integer.toString(rectBounds.getY()+thick+1));
		contentBackground.setAttribute("width", Integer.toString(newWidth-2));
		contentBackground.setAttribute("height", Integer.toString(newHeight-2));
		
		nodeGroup.appendChild(contentBackground);
		
		StringBuffer contentBackgroundStyle = (new StringBuffer());
		contentBackgroundStyle.append("stroke:none;");
		
		if (figure.getFulfilStyle()!=null){
			//首先往defsElement中插入一个grandient
			String gradientElmId = commentShape.getId()+"_fill_pattern";
			Element gradientElm = document.createElement("linearGradient");
			gradientElm.setAttribute("id", gradientElmId);
			gradientElm.setAttribute("spreadMethod", "pad");
			
			String color1 = figure.getFulfilStyle().getColor1();
			Element stop1 = document.createElement("stop");
			stop1.setAttribute("offset", "0%");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("stop-opacity", "1");
			
			
			String color2 = figure.getFulfilStyle().getColor2();
			Element stop2 = document.createElement("stop");
			stop2.setAttribute("offset", "100%");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("stop-opacity", "1");
			
			
			String gradient = figure.getFulfilStyle().getGradientStyle();
	
			
			if (gradient==null || gradient.trim().equals("") || FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)){
				gradientElm.appendChild(stop2);
			}else{
				gradientElm.appendChild(stop1);
				gradientElm.appendChild(stop2);
				
				
				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradient)){
					gradientElm.setAttribute("x1", "100%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}else{
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}
			}
			
			this.defsElement.appendChild(gradientElm);
			
			contentBackgroundStyle.append("fill:url(#").append(gradientElmId).append(");");
		}else{
			contentBackgroundStyle.append("fill:none;");
		}
		
		contentBackground.setAttribute("style", contentBackgroundStyle.toString());
		
		//comment的内容
		String contentTxt = figure.getContent();
		if (contentTxt!=null && !contentTxt.trim().equals("")){
			//构造textbox
			int padding = 4;
			int spacing = 0;
			int textBoxPadding = (padding+thick);
			Label contentLabel = figure.getContentLabel().copy();

			Element textRect = this.drawCommentText(commentShape,FPDLNames.COMMENT,
					viewportBounds.getX()+textBoxPadding, viewportBounds.getY()+textBoxPadding,
					newWidth-textBoxPadding*2, newHeight -textBoxPadding*2,
					contentLabel);
			
			nodeGroup.appendChild(textRect);
		}


		if (isTopLevelElm){
			_refreshDiagramSize(viewportBounds);			
		}
		
		return nodeGroup;
	}	
	
	private Element transformPool2Svg(PoolShape nodeShape, boolean isTopLevelElem){		
		//1、构造viewport
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle)nodeShape.getFigure();

		Element nodeGroup = document.createElement("g");

		
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.POOL);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		//2、构造pool边框		
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth()-thick;
		int newHeight = rectBounds.getHeight()-thick;
		Element rect = document.createElement("rect");
		rect.setAttribute("x",Integer.toString( rectBounds.getX()+thick/2));
		rect.setAttribute("y",Integer.toString( rectBounds.getY()+thick/2));
		rect.setAttribute("width", Integer.toString(newWidth));
		rect.setAttribute("height", Integer.toString(newHeight));
		
		nodeGroup.appendChild(rect);
		
		Element line = document.createElement("line");
		if (Diagram.VERTICAL.equals(diagram.getDirection())){
			line.setAttribute("x1", Integer.toString(rectBounds.getX()+thick));
			line.setAttribute("y1", Integer.toString(rectBounds.getY()+POOL_LANE_TITLE_HEIGHT));

			line.setAttribute("x2", Integer.toString(rectBounds.getX()+(thick+newWidth)));
			line.setAttribute("y2", Integer.toString(rectBounds.getY()+POOL_LANE_TITLE_HEIGHT));
		}else{
			line.setAttribute("x1", Integer.toString(rectBounds.getX()+POOL_LANE_TITLE_HEIGHT));
			line.setAttribute("y1", Integer.toString(rectBounds.getY()+thick));

			line.setAttribute("x2", Integer.toString(rectBounds.getX()+POOL_LANE_TITLE_HEIGHT));
			line.setAttribute("y2", Integer.toString(rectBounds.getY()+(thick+newHeight)));
		}		
		nodeGroup.appendChild(line);
		
		//2.1画笔
		StringBuffer rectStyle = (new StringBuffer());
		
		//2.1 画笔
		if (figure.getBounds()!=null){			
			rectStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			String color = rectBounds.getColor();
			rectStyle.append("stroke:").append((color==null || color.trim().equals(""))?"#000000":color).append(";");
			String lineType = rectBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = rectBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					if (Bounds.LINETYPE_DOTTED.equals(lineType)){
						dashArray = DEFAULT_DOT_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHED.equals(lineType)){
						dashArray = DEFAULT_DASH_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
						dashArray = DEFAULT_DOTDASH_DASHARRAY;
					}
				}
				rectStyle.append("stroke-dasharray: ").append(dashArray).append(";");
			}
		}
		line.setAttribute("style", rectStyle.toString());
		
		//2.2填充
		if (figure.getFulfilStyle()!=null){
			//首先往defsElement中插入一个grandient
			String gradientElmId = nodeShape.getId()+"_fill_pattern";
			Element gradientElm = document.createElement("linearGradient");
			gradientElm.setAttribute("id", gradientElmId);
			gradientElm.setAttribute("spreadMethod", "pad");
			
			String color1 = figure.getFulfilStyle().getColor1();
			Element stop1 = document.createElement("stop");
			stop1.setAttribute("offset", "0%");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("stop-opacity", "1");
			
			
			String color2 = figure.getFulfilStyle().getColor2();
			Element stop2 = document.createElement("stop");
			stop2.setAttribute("offset", "100%");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("stop-opacity", "1");
			
			
			String gradient = figure.getFulfilStyle().getGradientStyle();
	
			
			if (gradient==null || gradient.trim().equals("") || FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)){
				gradientElm.appendChild(stop2);
			}else{
				gradientElm.appendChild(stop1);
				gradientElm.appendChild(stop2);
				
				
				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradient)){
					gradientElm.setAttribute("x1", "100%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}else{
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}
			}
			
			this.defsElement.appendChild(gradientElm);
			
			rectStyle.append("fill:url(#").append(gradientElmId).append(");");
		}else{
			rectStyle.append("fill:none;");
		}
		
		rect.setAttribute("style", rectStyle.toString());
		
		//2.3 label的名称
		String title = figure.getTitle();
		if (title!=null && !title.trim().equals("")){			
			//构造textbox
			int paddingLeft = ( thick);
			Label titleLabel = figure.getTitleLabel().copy();
			int fontSize = titleLabel.getFontSize();;

			int textRectHeight = (int)(fontSize+2);//设置一定的余量
			
			int check = POOL_LANE_TITLE_HEIGHT-textRectHeight-thick;
			int paddingTop = thick;
			
			if (check>0){
				paddingTop = thick+(int)(check/2);
				
			}else{
				textRectHeight = POOL_LANE_TITLE_HEIGHT-thick;
			}
			//纵向泳道
			if (Diagram.VERTICAL.equals(diagram.getDirection())){
				int width = rectBounds.getWidth()-paddingLeft*2;//一般而言，width不可能小于2倍thick
				if (width<0){
					width = 0;
				}
				Element textRect = this.buildTextBox(nodeShape,FPDLNames.POOL,
						rectBounds.getX()+paddingLeft, rectBounds.getY()+paddingTop,
						width, textRectHeight,
						titleLabel,"middle");
				nodeGroup.appendChild(textRect);
			}
			else{//横向泳道
				int height = rectBounds.getHeight()-paddingLeft*2;
				Element textRect = this.buildTextBox(nodeShape,FPDLNames.POOL,
						rectBounds.getX()+paddingTop,rectBounds.getY()+paddingLeft, 
						textRectHeight, 
						height,
						titleLabel, "middle","middle",false);
				nodeGroup.appendChild(textRect);
			}
		}

		if (isTopLevelElem){
			_refreshDiagramSize(viewportBounds);			
		}
		
		//3、构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children!=null && children.size()>0){
			//构造一个新的坐标系统
			Element childrenGroup = document.createElement("svg");
//			StringBuffer tmpStyle = new StringBuffer();
			if (Diagram.VERTICAL.equals(diagram.getDirection())){
				childrenGroup.setAttribute("x", Integer.toString(rectBounds.getX()+thick));
				childrenGroup.setAttribute("y", Integer.toString(rectBounds.getY()+POOL_LANE_TITLE_HEIGHT));
				childrenGroup.setAttribute("width", Integer.toString(newWidth));
				childrenGroup.setAttribute("height", Integer.toString(rectBounds.getHeight()-GROUP_TITLE_HEIGHT-thick));
			}else{
				childrenGroup.setAttribute("x", Integer.toString(rectBounds.getX()+POOL_LANE_TITLE_HEIGHT));
				childrenGroup.setAttribute("y", Integer.toString(rectBounds.getY()+thick));
				childrenGroup.setAttribute("width", Integer.toString(rectBounds.getWidth()-POOL_LANE_TITLE_HEIGHT-thick));
				childrenGroup.setAttribute("height", Integer.toString(newHeight));
		
//				tmpStyle.append("position:absolute;")
//				.append("left:").append().append("px;")
//				.append("top:").append().append("px;")
//				.append("width:").append().append("px;")
//				.append("height:").append(newHeight).append("px");
			}

			
//			if (Diagram.VERTICAL.equals(diagram.getDirection())){
//				StringBuffer coordSizeBufTmp = new StringBuffer();
//				coordSizeBufTmp.append(newWidth).append(",").append(rectBounds.getHeight()-GROUP_TITLE_HEIGHT-thick);
//				childrenGroup.setAttribute("coordsize", coordSizeBufTmp.toString());
//			}else{
//				StringBuffer coordSizeBufTmp = new StringBuffer();
//				coordSizeBufTmp.append(viewportBounds.getWidth()-POOL_LANE_TITLE_HEIGHT-thick).append(",").append(newHeight);
//				childrenGroup.setAttribute("coordsize", coordSizeBufTmp.toString());
//			}
			int offset = 0;
			int laneCount = children.size();
			for (int index = 0;index<laneCount;index++){
				DiagramElement diagramElm = children.get(index);
				if (diagramElm instanceof LaneShape){
					LaneShape laneShape = (LaneShape)diagramElm;
					Element elm = this.transformLane2Svg(laneShape, offset,index,laneCount);
					if (elm!=null){
						childrenGroup.appendChild(elm);
					}
					
					Rectangle rectTmp = (Rectangle)laneShape.getFigure();
					Bounds tmpBounds = rectTmp.getBounds();
					
					if (Diagram.VERTICAL.equals(diagram.getDirection())){
						offset = offset+tmpBounds.getWidth();
					}else{
						offset = offset+tmpBounds.getHeight();
					}
				}
			}
			
			nodeGroup.appendChild(childrenGroup);
		}
		
		return nodeGroup;
	}
	private Element transformLane2Svg(LaneShape nodeShape,int offset,int laneIndex,int laneCount){
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle)nodeShape.getFigure();

		Element nodeGroup = document.createElement("g");
		
		int x = 0;
		int y = 0;
		if (Diagram.VERTICAL.equals(diagram.getDirection())) {
			x = offset;
			y = 0;
		} else {
			x = 0;
			y = offset;
		}
	
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.LANE);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth()-thick*2;
		int newHeight = rectBounds.getHeight()-thick*2;
		String title = figure.getTitle();
		
		//1、填充
		Element rect = document.createElement("rect");
		rect.setAttribute("width", Integer.toString(newWidth));
		rect.setAttribute("height", Integer.toString(newHeight));
		rect.setAttribute("x", Integer.toString(x+thick));
		rect.setAttribute("y", Integer.toString(y+thick));
		
		nodeGroup.appendChild(rect);
		
		StringBuffer fillStyle = new StringBuffer();
		fillStyle.append("stroke:none;");

		if (figure.getFulfilStyle()!=null){
			//首先往defsElement中插入一个grandient
			String gradientElmId = nodeShape.getId()+"_fill_pattern";
			Element gradientElm = document.createElement("linearGradient");
			gradientElm.setAttribute("id", gradientElmId);
			gradientElm.setAttribute("spreadMethod", "pad");
			
			String color1 = figure.getFulfilStyle().getColor1();
			Element stop1 = document.createElement("stop");
			stop1.setAttribute("offset", "0%");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("stop-opacity", "1");
			
			
			String color2 = figure.getFulfilStyle().getColor2();
			Element stop2 = document.createElement("stop");
			stop2.setAttribute("offset", "100%");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("stop-opacity", "1");
			
			
			String gradient = figure.getFulfilStyle().getGradientStyle();
	
			
			if (gradient==null || gradient.trim().equals("") || FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)){
				gradientElm.appendChild(stop2);
			}else{
				gradientElm.appendChild(stop1);
				gradientElm.appendChild(stop2);
				
				
				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "100%");
				}
				else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradient)){
					gradientElm.setAttribute("x1", "100%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "0%");
					gradientElm.setAttribute("y2", "100%");
				}else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradient)){
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}else{
					gradientElm.setAttribute("x1", "0%");
					gradientElm.setAttribute("y1", "0%");
					gradientElm.setAttribute("x2", "100%");
					gradientElm.setAttribute("y2", "0%");//从左到右
				}
			}
			
			this.defsElement.appendChild(gradientElm);
			
			fillStyle.append("fill:url(#").append(gradientElmId).append(");");
		}else{
			fillStyle.append("fill:none;");
		}
		rect.setAttribute("style", fillStyle.toString());
		
		//2、边框 
		StringBuffer strokeStyle = (new StringBuffer());
		
		//2.1 画笔
		if (figure.getBounds()!=null){			
			strokeStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			String color = rectBounds.getColor();
			strokeStyle.append("stroke:").append((color==null || color.trim().equals(""))?"#000000":color).append(";");
			String lineType = rectBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = rectBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					if (Bounds.LINETYPE_DOTTED.equals(lineType)){
						dashArray = DEFAULT_DOT_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHED.equals(lineType)){
						dashArray = DEFAULT_DASH_DASHARRAY;
					}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
						dashArray = DEFAULT_DOTDASH_DASHARRAY;
					}
				}
				strokeStyle.append("stroke-dasharray: ").append(dashArray).append(";");
			}
		}
		// lane边框
		if (Diagram.VERTICAL.equals(diagram.getDirection())){
		
			//横线2
			if (title!=null && !title.trim().equals("")){
				Element line = document.createElement("line");
				
				line.setAttribute("x1", Integer.toString(x+thick));
				line.setAttribute("y1", Integer.toString(y+POOL_LANE_TITLE_HEIGHT));
//				String from = thick + "," + POOL_LANE_TITLE_HEIGHT;
//				line.setAttribute("from", from);

				line.setAttribute("x2", Integer.toString(x+(thick+newWidth)));
				line.setAttribute("y2", Integer.toString(y+POOL_LANE_TITLE_HEIGHT));
//				String to = (thick+newWidth) + ","  + POOL_LANE_TITLE_HEIGHT;
//				line.setAttribute("to", to);
				if (strokeStyle!=null){
					line.setAttribute("style", strokeStyle.toString());
				}
				nodeGroup.appendChild(line);
			}

			
			//竖线
			if (laneIndex > 0) {
				Element line = document.createElement("line");
				line.setAttribute("x1", Integer.toString(x+thick));
				line.setAttribute("y1", Integer.toString(y+thick));
				
//				String from = thick + "," + thick;
//				line.setAttribute("from", from);
				line.setAttribute("x2", Integer.toString(x+(thick+newHeight)));
				line.setAttribute("y2", Integer.toString(y+thick));
				
//				String to = (thick+newHeight) + "," + (thick );
//				line.setAttribute("to", to);
				if (strokeStyle!=null){
					line.setAttribute("style", strokeStyle.toString());
				}
				nodeGroup.appendChild(line);
			}
			
		}else{
			//竖线1
			
			//竖线2
			if (title!=null && !title.trim().equals("")){
				Element line = document.createElement("line");
				line.setAttribute("x1", Integer.toString(x+POOL_LANE_TITLE_HEIGHT));
				line.setAttribute("y1", Integer.toString(y+thick));
//				String from = POOL_LANE_TITLE_HEIGHT + "," + thick;
//				line.setAttribute("from", from);

				line.setAttribute("x2", Integer.toString(x+POOL_LANE_TITLE_HEIGHT));
				line.setAttribute("y2", Integer.toString(y+(thick + newHeight)));
//				String to = POOL_LANE_TITLE_HEIGHT + "," + (thick + newHeight);
//				line.setAttribute("to", to);
				if (strokeStyle!=null){
					line.setAttribute("style", strokeStyle.toString());
				}
				nodeGroup.appendChild(line);
			}

			
			//横线
			if (laneIndex > 0) {
				Element line = document.createElement("line");
				line.setAttribute("x1", Integer.toString(x+thick));
				line.setAttribute("y1", Integer.toString(y+thick));
//				String from = thick + "," + thick;
//				line.setAttribute("from", from);

				line.setAttribute("x2", Integer.toString(x+(thick+newWidth)));
				line.setAttribute("y2", Integer.toString(y+thick));
//				String to = (thick+newWidth) + "," + (thick );
//				line.setAttribute("to", to);
				if (strokeStyle!=null){
					line.setAttribute("style", strokeStyle.toString());
				}
				nodeGroup.appendChild(line);
			}
			
		}


		
		//3、label的名称		
		if (title!=null && !title.trim().equals("")){
			int paddingLeft = ( thick);
			Label titleLabel = figure.getTitleLabel().copy();
			int fontSize = titleLabel.getFontSize();

			
			int textRectHeight = (int)(fontSize+2);//设置一定的余量
			
			int check = POOL_LANE_TITLE_HEIGHT-textRectHeight-thick;
			int paddingTop = thick;
			
			if (check>0){
				paddingTop = thick+(int)(check/2);
				
			}else{
				textRectHeight = POOL_LANE_TITLE_HEIGHT-thick;
			}
			//纵向泳道
			if (Diagram.VERTICAL.equals(diagram.getDirection())){
				int width = rectBounds.getWidth()-paddingLeft*2;//一般而言，width不可能小于2倍thick
				if (width<0){
					width = 0;
				}
				Element textRect = this.buildTextBox(nodeShape,FPDLNames.LANE,x+paddingLeft, y+paddingTop,
						width, textRectHeight,
						titleLabel, "middle");
				nodeGroup.appendChild(textRect);
			}
			else{//横向泳道
				int height = rectBounds.getHeight()-paddingLeft*2;
				Element textRect = this.buildTextBox(nodeShape,FPDLNames.LANE,x+paddingTop,y+paddingLeft, 
						textRectHeight, 
						height,
						titleLabel, "middle","middle",false);
				nodeGroup.appendChild(textRect);
			}
		}
		
		//4、构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children!=null && children.size()>0){

			//构造一个新的坐标系统
			Element childrenGroup = document.createElement("svg");
			if (Diagram.VERTICAL.equals(diagram.getDirection())){
				childrenGroup.setAttribute("x", Integer.toString(x+thick));
				childrenGroup.setAttribute("y", Integer.toString(y+POOL_LANE_TITLE_HEIGHT));
				childrenGroup.setAttribute("width", Integer.toString(newWidth));
				childrenGroup.setAttribute("height", Integer.toString(rectBounds.getHeight()-GROUP_TITLE_HEIGHT-thick));
			}else{
				childrenGroup.setAttribute("x", Integer.toString(x+POOL_LANE_TITLE_HEIGHT));
				childrenGroup.setAttribute("y", Integer.toString(y+thick));
				childrenGroup.setAttribute("width", Integer.toString(rectBounds.getWidth()-POOL_LANE_TITLE_HEIGHT-thick));
				childrenGroup.setAttribute("height", Integer.toString(newHeight));
			}

			for (DiagramElement diagramElm : children){
				if (diagramElm instanceof ProcessNodeShape){
					Element elm = this.transformProcessNodeShape2Svg((ProcessNodeShape)diagramElm, false);
					if (elm!=null){
						childrenGroup.appendChild(elm);
					}
				}
				else if (diagramElm instanceof CommentShape){
					Element elm = this.transformCommentShape2Svg((CommentShape)diagramElm, false);
					if (elm!=null){
						childrenGroup.appendChild(elm);
					}
				}
				else if (diagram instanceof GroupShape){
					Element elm = this.transformGroupShape2Svg((GroupShape)diagram, false);
					if (elm!=null){
						childrenGroup.appendChild(elm);
					}
				}
			}
			
			nodeGroup.appendChild(childrenGroup);

		}
		
		return nodeGroup;
	}
	
	private Element transformConnectorShape2Svg(ConnectorShape connectorShape){
		NodeShape fromNodeShape = connectorShape.getFromNode();
		NodeShape toNodeShape = connectorShape.getToNode();
		ModelElement wfElm = connectorShape.getWorkflowElementRef();
		String wfElmId = wfElm==null?"null":wfElm.getId();
		if (fromNodeShape==null || toNodeShape==null){			
			log.warn("TransitionShape[Id="+connectorShape.getId()+
					",WfElemId="+wfElmId+"]的 from_node或者to_node为空，无法绘制该连接线");
			return null;
		}
		
		Bounds absoluteFromNodeBounds = this._getAbsoluteBounds(fromNodeShape);
		Bounds absoluteToNodeBounds = this._getAbsoluteBounds(toNodeShape);
		
		if (absoluteFromNodeBounds==null || absoluteToNodeBounds==null){			
			log.warn("TransitionShape[Id="+connectorShape.getId()+
					",WfElemId="+wfElmId+"]的 from_node或者to_node的bounds为空，无法绘制该连接线");
			return null;
		}
		
		Line fireLine = (Line)connectorShape.getFigure();
		List<Point> linePoints = fireLine.getPoints();
		
		
		Point fromRefPoint = null;//from_anchor的参考点
		Point toRefPoint = null;//to_anchor的参考点
		if (linePoints==null || linePoints.size()==0){
			fromRefPoint = new Point();
			fromRefPoint.setX(absoluteToNodeBounds.getX()+absoluteToNodeBounds.getWidth()/2);
			fromRefPoint.setY(absoluteToNodeBounds.getY()+absoluteToNodeBounds.getHeight()/2);

		}else{
			fromRefPoint = linePoints.get(0);
		}
		if (linePoints==null || linePoints.size()==0){
			toRefPoint = new Point();
			toRefPoint.setX(absoluteFromNodeBounds.getX()+absoluteFromNodeBounds.getWidth()/2);
			toRefPoint.setY(absoluteFromNodeBounds.getY()+absoluteFromNodeBounds.getHeight()/2);
			
		}else{
			toRefPoint = linePoints.get(linePoints.size()-1);
		}		
		
		//计算from_anchor
		Point fromAnchor = null;
		fromAnchor = _calculateAnchor(fromNodeShape,absoluteFromNodeBounds,fromRefPoint);
		
		
		//计算to_anchor		
		Point toAnchor = null;
		toAnchor = _calculateAnchor(toNodeShape,absoluteToNodeBounds,toRefPoint);
		
		Element groupElement = document.createElement("g");
		String elementType = null;
		groupElement.setAttribute(FPDLNames.ID, connectorShape.getId());
		if (connectorShape instanceof TransitionShape){
			elementType = FPDLNames.TRANSITION;
		}
		else if (connectorShape instanceof MessageFlowShape){
			elementType = FPDLNames.MESSAGEFLOW;
		}
		else if (connectorShape instanceof AssociationShape){
			elementType = FPDLNames.ASSOCIATION;
		}
		groupElement.setAttribute(TYPE, elementType);
		
		ModelElement wfElmRef = connectorShape.getWorkflowElementRef();
		if (wfElmRef!=null){
			groupElement.setAttribute(FPDLNames.REF, wfElmRef==null?"":wfElmRef.getId());
		}
		
		//polyline
		Element polylineElm = document.createElement("polyline");
		groupElement.appendChild(polylineElm);
		
		polylineElm.setAttribute("points", _makePointsSeq(fromAnchor.copy(),toAnchor.copy(),linePoints));
		

		
		//2.1 画笔
		StringBuffer lineStyle = (new StringBuffer());
		Bounds lineBounds = fireLine.getBounds();
		String color = "#000000";
		if (lineBounds!=null){		
			int thick = lineBounds.getThick();
			lineStyle.append("stroke-width:").append(Integer.toString(thick<0?1:thick)).append("px;");
			color = lineBounds.getColor();
			color = (color==null || color.trim().equals(""))?"#000000":color;
			lineStyle.append("stroke:").append(color).append(";");
			String lineType = lineBounds.getLineType();
			if (Bounds.LINETYPE_DOTTED.equals(lineType)
					|| Bounds.LINETYPE_DASHED.equals(lineType)
					|| Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
				String dashArray = lineBounds.getDashArray();
				if (dashArray==null || dashArray.trim().equals("")){
					if (connectorShape instanceof AssociationShape){
						dashArray = DEFAULT_ASSOC_DASHARRAY;
					}else if (connectorShape instanceof MessageFlowShape){
						dashArray = DEFAULT_MESSAGEFLOW_DASHARRAY;
					}
					else{
						if (Bounds.LINETYPE_DOTTED.equals(lineType)){
							dashArray = DEFAULT_DOT_DASHARRAY;
						}else if (Bounds.LINETYPE_DASHED.equals(lineType)){
							
							dashArray = DEFAULT_DASH_DASHARRAY;
						}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
							dashArray = DEFAULT_DOTDASH_DASHARRAY;
						}
					}

				}
				lineStyle.append("stroke-dasharray: ").append(lineBounds.getDashArray()).append(";");
			}
		}
		
		//箭头尾
		if (connectorShape instanceof TransitionShape){
			Element mkElm = this.blockArrowMarkerMap.get(color);
			String mkId = BASIC_BLOCK_ARROW_MARKER_ID;
			if (mkElm!=null){
				mkId = mkElm.getAttribute("id");
			}else{
				//创建新的marker
				Element mkElmOriginal = this.blockArrowMarkerMap.get("#000000");
				if (mkElmOriginal!=null){
					Element newElm = (Element)mkElmOriginal.cloneNode(true);
					
					mkId = connectorShape.getId()+"_end_marker";
					newElm.setAttribute("id", mkId);
					Element pathElm = (Element)newElm.getFirstChild();
					pathElm.setAttribute("style", "stroke:none;fill:"+color+";");
					
					blockArrowMarkerMap.put(color, newElm);
					this.defsElement.appendChild(newElm);
				}
			}
			polylineElm.setAttribute("marker-end", "url(#"+mkId+")");
		}
		else if (connectorShape instanceof MessageFlowShape){
			Element mkElm = this.circleMarkerMap.get(color);
			String mkId = BASIC_CIRCLE_MARKER_ID;
			if (mkElm!=null){
				mkId = mkElm.getAttribute("id");
			}else{
				//创建新的marker
				Element mkElmOriginal = this.circleMarkerMap.get("#000000");
				if (mkElmOriginal!=null){
					Element newElm = (Element)mkElmOriginal.cloneNode(true);
					
					mkId = connectorShape.getId()+"_start_marker";
					newElm.setAttribute("id", mkId);
					Element pathElm = (Element)newElm.getFirstChild();
					pathElm.setAttribute("style", "stroke:"+color+";fill:none;stroke-dasharray:2,0;");
					
					circleMarkerMap.put(color, newElm);
					this.defsElement.appendChild(newElm);
				}
			}
			polylineElm.setAttribute("marker-start", "url(#"+mkId+")");
			
			

			mkElm = this.blockArrowMarker2Map.get(color);
			mkId = BASIC_BLOCK_ARROW_MARKER2_ID;
			if (mkElm!=null){
				mkId = mkElm.getAttribute("id");
			}else{
				//创建新的marker
				Element mkElmOriginal = this.blockArrowMarker2Map.get("#000000");
				if (mkElmOriginal!=null){
					Element newElm = (Element)mkElmOriginal.cloneNode(true);
					
					mkId = connectorShape.getId()+"_end_marker";
					newElm.setAttribute("id", mkId);
					Element pathElm = (Element)newElm.getFirstChild();
					pathElm.setAttribute("style", "stroke:"+color+";fill:none;stroke-dasharray:2,0;");
					
					blockArrowMarker2Map.put(color, newElm);
					this.defsElement.appendChild(newElm);
				}
			}
			polylineElm.setAttribute("marker-end", "url(#"+mkId+")");

		}
		
		//填充
		lineStyle.append("fill:none;");
		
		polylineElm.setAttribute("style", lineStyle.toString());

		
		
		//构造标题
		Label lb = fireLine.getTitleLabel();
		String displayName = null;
		if (wfElm!=null){
			displayName = wfElm.getDisplayName();
		}else{
			displayName = fireLine.getTitle();
		}
		if (displayName!=null 
				&& !displayName.trim().equals("")){
			Point labelRelativePos = fireLine.getLabelPosition();
			//labelAbsPos是label的中心点
			Point labelAbsPos = _getLabelAbsolutePosition(fromAnchor.copy(),toAnchor.copy(),linePoints,labelRelativePos);
			Label titleLabel = null; 
			if (lb==null){
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(DEFAULT_FONT_SIZE);
			}else{
				titleLabel = lb.copy();
			}
			titleLabel.setText(displayName);
			Dimension dimension = _calculateFontSize(titleLabel.getText(),titleLabel.getFontSize());
			
			labelAbsPos.setX(labelAbsPos.getX()-dimension.width/2);
			labelAbsPos.setY(labelAbsPos.getY()-dimension.height/2);
			
			Element textRect = this.buildTextBox(connectorShape,elementType,labelAbsPos.getX(), labelAbsPos.getY(),
					dimension.width, dimension.height, titleLabel,"middle");
			
			groupElement.appendChild(textRect);
			
			//重设画布大小
			Bounds boundsTmp = new BoundsImpl();
			boundsTmp.setX(labelAbsPos.getX());
			boundsTmp.setY(labelAbsPos.getY());
			boundsTmp.setWidth(dimension.width);
			boundsTmp.setHeight(dimension.height);
			
			this._refreshDiagramSize(boundsTmp);
			
		}

		
		
		//重设画布大小
		List<Point> tmpList = new ArrayList<Point>();
		tmpList.addAll(linePoints);
		tmpList.add(fromAnchor);
		tmpList.add(toAnchor);
		_refreshDiagramSize(tmpList);
		
		return groupElement;
	}
}
