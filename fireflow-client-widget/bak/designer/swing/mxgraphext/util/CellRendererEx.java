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
package org.fireflow.designer.swing.mxgraphext.util;

import java.awt.Color;

import org.fireflow.designer.swing.mxgraphext.canvas.SvgCanvasEx;
import org.fireflow.designer.swing.mxgraphext.canvas.VmlCanvasEx;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CellRendererEx {
	public static Document createSvgDocument(mxGraph graph, Object[] cells,
			double scale, Color background, mxRectangle clip,
			final String svgId,
			final int viewPortWidth,final int viewPortHeight,
			final String fireflowResourceServlet,
			final String contextPath)
	{
		SvgCanvasEx canvas = (SvgCanvasEx) mxCellRenderer.drawCells(graph, cells, scale, clip,
				new CanvasFactory()
				{
					public mxICanvas createCanvas(int width, int height)
					{
						return new SvgCanvasEx(createSvgDocument(svgId,viewPortWidth,viewPortHeight,width,
								height,fireflowResourceServlet,contextPath),fireflowResourceServlet,contextPath);
					}

				});

		return canvas.getDocument();
	}

	
	public static Document createVmlDocument(final mxGraph graph, Object[] cells,
			double scale, Color background, mxRectangle clip,
			final String vmlId,
			final String clientWidgetServletPath,
			final String contextPath)
	{
		VmlCanvasEx canvas = (VmlCanvasEx) mxCellRenderer.drawCells(graph, cells, scale, clip,
				new CanvasFactory()
				{
					public mxICanvas createCanvas(int width, int height)
					{
						return new VmlCanvasEx(createVmlDocument(),width,height,
								clientWidgetServletPath,contextPath,vmlId);
					}

				});

		return canvas.getDocument();
	}
	
	private static Document createVmlDocument(){

		Document document = mxDomUtils.createDocument();


		return document;
	}
	
	private static Document createSvgDocument(String elementId ,
			int width, int height,int viewBoxWidth,int viewBoxHeight,
			String argFireflowResourceServlet,
			String argContextPath)
	{
		String contextPath = argContextPath;
		String fireflowResourceServlet = argFireflowResourceServlet;
		if (contextPath==null)contextPath = "";
		if (fireflowResourceServlet==null) fireflowResourceServlet="";
		if (contextPath.endsWith("/") && fireflowResourceServlet.startsWith("/")){
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		
		Document document = mxDomUtils.createDocument();
		Element root = document.createElement("svg");

		String w = String.valueOf(width);
		String h = String.valueOf(height);

		root.setAttribute("id", elementId);
		root.setAttribute("width", w);
		root.setAttribute("height", h);
		//root.setAttribute("viewBox", "0 0 " + viewBoxWidth + " " + viewBoxHeight);
		//root.setAttribute("preserveAspectRatio","xMinYMin slice");
		root.setAttribute("version", "1.1");
		root.setAttribute("xmlns", mxConstants.NS_SVG);
		root.setAttribute("xmlns:xlink", mxConstants.NS_XLINK);
		root.setAttribute("onload", "fireflowSvgInit('"+elementId+"');");
		root.setAttribute("onunload", "fireflowSvgDestroy('"+elementId+"')");

		document.appendChild(root);
		
		//构造javascript
		Element scriptElm = document.createElement("script");
		scriptElm.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", 
				contextPath+fireflowResourceServlet+"/org/fireflow/clientwidget/resources/jquery-ui-1.10.3.custom/js/jquery-1.10.2.min.js");
		root.appendChild(scriptElm);
		
		scriptElm = document.createElement("script");
		scriptElm.setAttribute("type","application/ecmascript");
		root.appendChild(scriptElm);
		String data = "$ff=$;";
		CDATASection cdata = document.createCDATASection(data);
		scriptElm.appendChild(cdata);
		
		scriptElm = document.createElement("script");
		scriptElm.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", 
				contextPath+fireflowResourceServlet+"/org/fireflow/clientwidget/resources/svg/FireflowSvgControl.js");
		root.appendChild(scriptElm);
		
		//构造id=viewport的g节点
		Element g = document.createElement("g");
		g.setAttribute("id", "viewport");
		root.appendChild(g);

		return document;
	}
}
