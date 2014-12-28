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
package org.fireflow.designer.swing.mxgraphext.canvas;

import java.util.Map;
import java.util.StringTokenizer;

import org.fireflow.clientwidget.servlet.Constants;
import org.fireflow.designer.swing.mxgraphext.shape.CommentShape;
import org.fireflow.designer.swing.proxy.Wrapper;
import org.fireflow.pdl.fpdl.io.FPDLNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class SvgCanvasEx extends mxSvgCanvas {
	private String clientWidgetServletPath = null;
	private String contextPath = null;
	private Node viewportNode = null;

	public SvgCanvasEx(Document document){
		super(document);
	}
	/**
	 * @param document
	 */
	public SvgCanvasEx(Document document,String clientWidgetServletPath,String contextPath) {
		super(document);
		this.clientWidgetServletPath = clientWidgetServletPath;
		this.contextPath = contextPath;
		
		if (this.contextPath==null)this.contextPath = "";
		if (this.clientWidgetServletPath==null) this.clientWidgetServletPath="";
		if (this.contextPath.endsWith("/") && this.clientWidgetServletPath.startsWith("/")){
			this.contextPath = this.contextPath.substring(0,contextPath.length()-1);
		}
		
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("g");
		int l = nodeList.getLength();
		if (nodeList!=null && nodeList.getLength()>0){
			for (int i=0;i<l;i++){
				Node gNode = nodeList.item(i);
				NamedNodeMap map = gNode.getAttributes();
				if (map!=null){
					Node idNode = map.getNamedItem("id");
					if(idNode!=null && "viewport".equals(idNode.getNodeValue())){
						viewportNode = gNode;
						break;
					}
				}
			}
		}
	}
	
	public void appendSvgElement(Element node)
	{
		if (viewportNode!=null){
			viewportNode.appendChild(node);
		}
		else if (document != null)
		{
			Element root = document.getDocumentElement();

			root.appendChild(node);
		}
	}

	
	
	protected Element createImageElement(double x, double y, double w,
			double h, String src, boolean aspect, boolean flipH, boolean flipV,
			boolean embedded)
	{
		Element elem = null;

		if (embedded)
		{
			elem = document.createElement("use");

			Element img = getEmbeddedImageElement(src);
			elem.setAttributeNS(mxConstants.NS_XLINK, "xlink:href",
					"#" + img.getAttribute("id"));
		}
		else
		{
			elem = document.createElement("image");
			String imgUri = contextPath+this.clientWidgetServletPath+src;
			elem.setAttributeNS(mxConstants.NS_XLINK, "xlink:href", imgUri);
		}

		elem.setAttribute("x", String.valueOf(x));
		elem.setAttribute("y", String.valueOf(y));
		elem.setAttribute("width", String.valueOf(w));
		elem.setAttribute("height", String.valueOf(h));

		// FIXME: SVG element must be used for reference to image with
		// aspect but for images with no aspect this does not work.
		if (aspect)
		{
			elem.setAttribute("preserveAspectRatio", "xMidYMid");
		}
		else
		{
			elem.setAttribute("preserveAspectRatio", "none");
		}

		double sx = 1;
		double sy = 1;
		double dx = 0;
		double dy = 0;

		if (flipH)
		{
			sx *= -1;
			dx = -w - 2 * x;
		}

		if (flipV)
		{
			sy *= -1;
			dy = -h - 2 * y;
		}

		String transform = "";

		if (sx != 1 || sy != 1)
		{
			transform += "scale(" + sx + " " + sy + ") ";
		}

		if (dx != 0 || dy != 0)
		{
			transform += "translate(" + dx + " " + dy + ") ";
		}

		if (transform.length() > 0)
		{
			elem.setAttribute("transform", transform);
		}

		return elem;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mxgraph.canvas.mxSvgCanvas#drawShape(int, int, int, int,
	 * java.util.Map)
	 */
	@Override
	public Element drawShape(int x, int y, int w, int h,
			Map<String, Object> style) {
		String fillColor = mxUtils.getString(style,
				mxConstants.STYLE_FILLCOLOR, "none");
		String gradientColor = mxUtils.getString(style,
				mxConstants.STYLE_GRADIENTCOLOR, "none");
		String strokeColor = mxUtils.getString(style,
				mxConstants.STYLE_STROKECOLOR, "none");
		float strokeWidth = (float) (mxUtils.getFloat(style,
				mxConstants.STYLE_STROKEWIDTH, 1) * scale);
		float opacity = mxUtils.getFloat(style, mxConstants.STYLE_OPACITY, 100);

		// Draws the shape
		String shape = mxUtils.getString(style, mxConstants.STYLE_SHAPE, "");
		Element elem = null;
		Element background = null;

		if (shape.equals(CommentShape.SHAPE_FIREFLOW_COMMENT)) {
			elem = document.createElement("g");
			background = document.createElement("rect");
			background.setAttribute("x", String.valueOf(x + strokeWidth));// strokeWidth
			background.setAttribute("y", String.valueOf(y + strokeWidth));
			background.setAttribute("width",
					String.valueOf(w - strokeWidth * 2));
			background.setAttribute("height",
					String.valueOf(h - strokeWidth * 2));
			background.setAttribute("stroke", "#ffffff");
			background.setAttribute("stroke-opacity", "0");
			background.setAttribute("stroke-width", "1");
			elem.appendChild(background);

			Element foreground = document.createElement("path");

			String d = "M " + (x + w / 3) + " " + y + " L " + x + " " + y
					+ " L " + x + " " + (y + h) + " L " + (x + w / 3) + " "
					+ (y + h);

			foreground.setAttribute("d", d);
			foreground.setAttribute("fill", "none");
			foreground.setAttribute("stroke", strokeColor);
			foreground
					.setAttribute("stroke-width", String.valueOf(strokeWidth));
			elem.appendChild(foreground);

			double rotation = mxUtils.getDouble(style,
					mxConstants.STYLE_ROTATION);
			int cx = x + w / 2;
			int cy = y + h / 2;

			Element bg = background;


			if (!bg.getNodeName().equalsIgnoreCase("use")
					&& !bg.getNodeName().equalsIgnoreCase("image")) {
				if (!fillColor.equalsIgnoreCase("none")
						&& !gradientColor.equalsIgnoreCase("none")) {
					String direction = mxUtils.getString(style,
							mxConstants.STYLE_GRADIENT_DIRECTION);
					Element gradient = getGradientElement(fillColor,
							gradientColor, direction);

					if (gradient != null) {
						bg.setAttribute("fill",
								"url(#" + gradient.getAttribute("id") + ")");
					}
				} else {
					bg.setAttribute("fill", fillColor);
				}

				bg.setAttribute("stroke", strokeColor);
				bg.setAttribute("stroke-width", String.valueOf(strokeWidth));

				// Adds the shadow element
				Element shadowElement = null;

				if (mxUtils.isTrue(style, mxConstants.STYLE_SHADOW, false)
						&& !fillColor.equals("none")) {
					shadowElement = (Element) bg.cloneNode(true);

					shadowElement.setAttribute("transform",
							mxConstants.SVG_SHADOWTRANSFORM);
					shadowElement.setAttribute("fill",
							mxConstants.W3C_SHADOWCOLOR);
					shadowElement.setAttribute("stroke",
							mxConstants.W3C_SHADOWCOLOR);
					shadowElement.setAttribute("stroke-width",
							String.valueOf(strokeWidth));

					if (rotation != 0) {
						shadowElement.setAttribute("transform", "rotate("
								+ rotation + "," + cx + "," + cy + ") "
								+ mxConstants.SVG_SHADOWTRANSFORM);
					}

					if (opacity != 100) {
						String value = String.valueOf(opacity / 100);
						shadowElement.setAttribute("fill-opacity", value);
						shadowElement.setAttribute("stroke-opacity", value);
					}

					appendSvgElement(shadowElement);
				}
			}

			if (rotation != 0) {
				elem.setAttribute("transform", elem.getAttribute("transform")
						+ " rotate(" + rotation + "," + cx + "," + cy + ")");

			}

			if (opacity != 100) {
				String value = String.valueOf(opacity / 100);
				elem.setAttribute("fill-opacity", value);
				elem.setAttribute("stroke-opacity", value);
			}

			if (mxUtils.isTrue(style, mxConstants.STYLE_DASHED)) {
				String pattern = mxUtils.getString(style,
						mxConstants.STYLE_DASH_PATTERN, "3, 3");
				elem.setAttribute("stroke-dasharray", pattern);
			}

			appendSvgElement(elem);

			return elem;
		} else {
			return super.drawShape(x, y, w, h, style);
		}

	}

	/* (non-Javadoc)
	 * @see com.mxgraph.canvas.mxSvgCanvas#drawLabel(java.lang.String, com.mxgraph.view.mxCellState, boolean)
	 */
	@Override
	public Object drawLabel(String label, mxCellState state, boolean html) {
		Map<String,Object> style = state.getStyle();
		
		String shape = mxUtils.getString(style, mxConstants.STYLE_SHAPE, "");
		

		if (shape.equals(CommentShape.SHAPE_FIREFLOW_COMMENT)) {
			mxRectangle bounds = state.getPerimeterBounds();
				//state.getLabelBounds();

			mxCell cell = (mxCell)state.getCell();
			Wrapper wrapper = (Wrapper)cell.getValue();
			String originalLabel = (String)wrapper.getAttribute(FPDLNames.DESCRIPTION);
			
			if (drawLabels && bounds != null)
			{
				int x = (int) bounds.getX() + translate.x;
				int y = (int) bounds.getY() + translate.y;
				int w = (int) bounds.getWidth();
				int h = (int) bounds.getHeight();
				
				state.getCell();
				
				return drawCommentText(originalLabel,label,x,y,w,h,style);
			}

			return null;
		}else{
			return super.drawLabel(label, state, html);
		}
		
	}

	public Object drawCommentText(String originalText,String text, int x, int y, int w, int h,
			Map<String, Object> style)
	{
		Element result = null;
		String fontColor = mxUtils.getString(style,
				mxConstants.STYLE_FONTCOLOR, "black");
		String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
				//mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);
		int fontSize = (int) (mxUtils.getInt(style, mxConstants.STYLE_FONTSIZE,
				mxConstants.DEFAULT_FONTSIZE) * scale);
		
		int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = ((fontStyle & mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD) ? "bold"
				: "normal";
		String uline = ((fontStyle & mxConstants.FONT_UNDERLINE) == mxConstants.FONT_UNDERLINE) ? "underline"
				: "none";
		
		if (text != null && text.length() > 0)
		{
			// Applies the opacity
			float opacity = mxUtils.getFloat(style,
					mxConstants.STYLE_TEXT_OPACITY, 100);



			String transform = null;

			if (!mxUtils.isTrue(style, mxConstants.STYLE_HORIZONTAL, true))
			{
				double cx = x + w / 2;
				double cy = y + h / 2;
				transform = "rotate(270 " + cx + " " + cy + ")";
			}


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
			textArea.setAttribute("font-decoration", uline);
			if ((fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC)
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
			
			textArea.appendChild(document.createTextNode(originalText));

			
			//2、创建foreignObject 
			Element foreignObject = document.createElement("foreignObject");
			foreignObject.setAttribute("x", Integer.toString(x+CommentShape.COMMENT_FIGURE_INSETS));
			foreignObject.setAttribute("y", Integer.toString(y+CommentShape.COMMENT_FIGURE_INSETS));
			foreignObject.setAttribute("width", Integer.toString(w-CommentShape.COMMENT_FIGURE_INSETS*2));
			foreignObject.setAttribute("height", Integer.toString(h-CommentShape.COMMENT_FIGURE_INSETS*2));
			foreignObject.setAttribute("requiredFeatures", "http://www.w3.org/TR/SVG11/feature#Extensibility");
			switchElm.appendChild(foreignObject);
			
			
			StringBuffer styleBuf = new StringBuffer();
			styleBuf.append("font-weight:").append(weight).append(";")
				.append("font-size:").append(String.valueOf(fontSize)).append("px;")
				.append("font-family:").append(fontFamily).append(";")
				.append("color:").append(fontColor).append(";");
			if ((fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC)
			{
				styleBuf.append("font-style:").append( "italic").append(";");
			}
			
			StringTokenizer tokenizer = new StringTokenizer(originalText,"\n");
			while (tokenizer.hasMoreTokens()){
				String tmpStr = tokenizer.nextToken();
				Element pElem = document.createElement("p");
				pElem.setAttribute("style", styleBuf.toString());
				pElem.setAttribute("xmlns","http://www.w3.org/1999/xhtml");

				pElem.appendChild(document.createTextNode(tmpStr));
				foreignObject.appendChild(pElem);
			}

			
			//4、构造text节点
			Element textElm = null;
			textElm = document.createElement("text");
			textElm.setAttribute("font-weight", weight);
			textElm.setAttribute("font-decoration", uline);
			if ((fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC)
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
					+ (h - lines.length * (fontSize + mxConstants.LINESPACING))
					/ 2 - 2;

			String align = mxUtils.getString(style, mxConstants.STYLE_ALIGN,
					mxConstants.ALIGN_CENTER);
			String anchor = "start";

			if (align.equals(mxConstants.ALIGN_RIGHT))
			{
				anchor = "end";
				x += w - mxConstants.LABEL_INSET * scale;
			}
			else if (align.equals(mxConstants.ALIGN_CENTER))
			{
				anchor = "middle";
				x += w / 2;
			}
			else
			{
				x += mxConstants.LABEL_INSET * scale;
			}

			textElm.setAttribute("text-anchor", anchor);

			for (int i = 0; i < lines.length; i++)
			{
				Element tspan = document.createElement("tspan");

				tspan.setAttribute("x", String.valueOf(x));
				tspan.setAttribute("y", String.valueOf(y));

				tspan.appendChild(document.createTextNode(lines[i]));
				textElm.appendChild(tspan);

				y += fontSize + mxConstants.LINESPACING;
			}

			if (transform != null)
			{
				textElm.setAttribute("transform", transform);
			}
			
			switchElm.appendChild(textElm);

			appendSvgElement(switchElm);//为什么要append在根节点呢？
		}

		return result;
	}
	

}
