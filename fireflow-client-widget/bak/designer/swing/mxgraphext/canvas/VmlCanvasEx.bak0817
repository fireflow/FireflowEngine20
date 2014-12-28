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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.fireflow.clientwidget.servlet.Constants;
import org.fireflow.clientwidget.tag.ClientWidgetBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mxgraph.canvas.mxVmlCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class VmlCanvasEx extends mxVmlCanvas {
	private int width = 100;
	private int height = 100;
	private String clientWidgetServletPath = ClientWidgetBase.Fireflow_ClientWidget_Servlet_Path;
	private String contextPath = "";
//	/**
//	 * 
//	 */
//	public VmlCanvasEx() {
//		super();
//		// TODO Auto-generated constructor stub
//	}

	/**
	 * @param document
	 */
	public VmlCanvasEx(Document document,int width,int height,String clientWidgetServletPath,String contextPath,String vmlId) {
		super(document);
		this.width = width;
		this.height = height;
		this.clientWidgetServletPath = clientWidgetServletPath;
		this.contextPath = contextPath;
		
		if (this.contextPath==null)this.contextPath = "";
		if (this.clientWidgetServletPath==null) this.clientWidgetServletPath="";
		if (this.contextPath.endsWith("/") && this.clientWidgetServletPath.startsWith("/")){
			this.contextPath = this.contextPath.substring(0,contextPath.length()-1);
		}
		
		//构造document根元素
		Element root = document.createElement("v:group");
		root.setAttribute("id", vmlId);
		document.appendChild(root);

		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("position:relative;")
			.append("width:").append(width).append("px;")
			.append("height:").append(height).append("px;");
		root.setAttribute("style", styleBuf.toString());
		
		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(width).append(",").append(height);
		root.setAttribute("coordsize", coordSizeBuf.toString());
		
	}
	
	public void appendVmlElement(Element node)
	{
		if (document != null)
		{
//			Node body = document.getDocumentElement().getFirstChild()
//					.getNextSibling();
			Node body = document.getDocumentElement();
			if (body != null)
			{
				body.appendChild(node);
			}
		}

	}

	public Element drawShape(int x, int y, int w, int h,
			Map<String, Object> style)
	{
		String fillColor = mxUtils
				.getString(style, mxConstants.STYLE_FILLCOLOR);
		String strokeColor = mxUtils.getString(style,
				mxConstants.STYLE_STROKECOLOR);
		float strokeWidth = (float) (mxUtils.getFloat(style,
				mxConstants.STYLE_STROKEWIDTH, 1) * scale);

		// Draws the shape
		String shape = mxUtils.getString(style, mxConstants.STYLE_SHAPE);
		Element elem = null;

		if (shape.equals(mxConstants.SHAPE_IMAGE))
		{
			String img = getImageForStyle(style);

			String imgUri = contextPath+this.clientWidgetServletPath+img;
			if (img != null)
			{
				elem = document.createElement("v:image");
				elem.setAttribute("src", imgUri);
			}
		}
		else if (shape.equals(mxConstants.SHAPE_LINE))
		{
			String direction = mxUtils.getString(style,
					mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
			String points = null;

			if (direction.equals(mxConstants.DIRECTION_EAST)
					|| direction.equals(mxConstants.DIRECTION_WEST))
			{
				int mid = Math.round(h / 2);
				points = "m 0 " + mid + " l " + w + " " + mid;
			}
			else
			{
				int mid = Math.round(w / 2);
				points = "m " + mid + " 0 L " + mid + " " + h;
			}

			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);
			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_ELLIPSE))
		{
			elem = document.createElement("v:oval");
		}
		else if (shape.equals(mxConstants.SHAPE_DOUBLE_ELLIPSE))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);
			int inset = (int) ((3 + strokeWidth) * scale);

			String points = "ar 0 0 " + w + " " + h + " 0 " + (h / 2) + " "
					+ (w / 2) + " " + (h / 2) + " e ar " + inset + " " + inset
					+ " " + (w - inset) + " " + (h - inset) + " 0 " + (h / 2)
					+ " " + (w / 2) + " " + (h / 2);

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_RHOMBUS))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			String points = "m " + (w / 2) + " 0 l " + w + " " + (h / 2)
					+ " l " + (w / 2) + " " + h + " l 0 " + (h / 2);

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_TRIANGLE))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			String direction = mxUtils.getString(style,
					mxConstants.STYLE_DIRECTION, "");
			String points = null;

			if (direction.equals(mxConstants.DIRECTION_NORTH))
			{
				points = "m 0 " + h + " l " + (w / 2) + " 0 " + " l " + w + " "
						+ h;
			}
			else if (direction.equals(mxConstants.DIRECTION_SOUTH))
			{
				points = "m 0 0 l " + (w / 2) + " " + h + " l " + w + " 0";
			}
			else if (direction.equals(mxConstants.DIRECTION_WEST))
			{
				points = "m " + w + " 0 l " + w + " " + (h / 2) + " l " + w
						+ " " + h;
			}
			else
			// east
			{
				points = "m 0 0 l " + w + " " + (h / 2) + " l 0 " + h;
			}

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_HEXAGON))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			String direction = mxUtils.getString(style,
					mxConstants.STYLE_DIRECTION, "");
			String points = null;

			if (direction.equals(mxConstants.DIRECTION_NORTH)
					|| direction.equals(mxConstants.DIRECTION_SOUTH))
			{
				points = "m " + (int) (0.5 * w) + " 0 l " + w + " "
						+ (int) (0.25 * h) + " l " + w + " " + (int) (0.75 * h)
						+ " l " + (int) (0.5 * w) + " " + h + " l 0 "
						+ (int) (0.75 * h) + " l 0 " + (int) (0.25 * h);
			}
			else
			{
				points = "m " + (int) (0.25 * w) + " 0 l " + (int) (0.75 * w)
						+ " 0 l " + w + " " + (int) (0.5 * h) + " l "
						+ (int) (0.75 * w) + " " + h + " l " + (int) (0.25 * w)
						+ " " + h + " l 0 " + (int) (0.5 * h);
			}

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_CLOUD))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			String points = "m " + (int) (0.25 * w) + " " + (int) (0.25 * h)
					+ " c " + (int) (0.05 * w) + " " + (int) (0.25 * h) + " 0 "
					+ (int) (0.5 * h) + " " + (int) (0.16 * w) + " "
					+ (int) (0.55 * h) + " c 0 " + (int) (0.66 * h) + " "
					+ (int) (0.18 * w) + " " + (int) (0.9 * h) + " "
					+ (int) (0.31 * w) + " " + (int) (0.8 * h) + " c "
					+ (int) (0.4 * w) + " " + (h) + " " + (int) (0.7 * w) + " "
					+ (h) + " " + (int) (0.8 * w) + " " + (int) (0.8 * h)
					+ " c " + (w) + " " + (int) (0.8 * h) + " " + (w) + " "
					+ (int) (0.6 * h) + " " + (int) (0.875 * w) + " "
					+ (int) (0.5 * h) + " c " + (w) + " " + (int) (0.3 * h)
					+ " " + (int) (0.8 * w) + " " + (int) (0.1 * h) + " "
					+ (int) (0.625 * w) + " " + (int) (0.2 * h) + " c "
					+ (int) (0.5 * w) + " " + (int) (0.05 * h) + " "
					+ (int) (0.3 * w) + " " + (int) (0.05 * h) + " "
					+ (int) (0.25 * w) + " " + (int) (0.25 * h);

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_ACTOR))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			double width3 = w / 3;
			String points = "m 0 " + (h) + " C 0 " + (3 * h / 5) + " 0 "
					+ (2 * h / 5) + " " + (w / 2) + " " + (2 * h / 5) + " c "
					+ (int) (w / 2 - width3) + " " + (2 * h / 5) + " "
					+ (int) (w / 2 - width3) + " 0 " + (w / 2) + " 0 c "
					+ (int) (w / 2 + width3) + " 0 " + (int) (w / 2 + width3)
					+ " " + (2 * h / 5) + " " + (w / 2) + " " + (2 * h / 5)
					+ " c " + (w) + " " + (2 * h / 5) + " " + (w) + " "
					+ (3 * h / 5) + " " + (w) + " " + (h);

			elem.setAttribute("path", points + " x e");
		}
		else if (shape.equals(mxConstants.SHAPE_CYLINDER))
		{
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			double dy = Math.min(40, Math.floor(h / 5));
			String points = "m 0 " + (int) (dy) + " C 0 " + (int) (dy / 3)
					+ " " + (w) + " " + (int) (dy / 3) + " " + (w) + " "
					+ (int) (dy) + " L " + (w) + " " + (int) (h - dy) + " C "
					+ (w) + " " + (int) (h + dy / 3) + " 0 "
					+ (int) (h + dy / 3) + " 0 " + (int) (h - dy) + " x e"
					+ " m 0 " + (int) (dy) + " C 0 " + (int) (2 * dy) + " "
					+ (w) + " " + (int) (2 * dy) + " " + (w) + " " + (int) (dy);

			elem.setAttribute("path", points + " e");
		}
		/*
		else if (shape.equals(CommentShape.SHAPE_FIREFLOW_COMMENT))
		{
			
			elem = document.createElement("v:shape");
			elem.setAttribute("coordsize", w + " " + h);

			String points = "m " + (w / 3) + " 0 l 0 0" +
				" l " + (0) + " " + h + " l "+(w/3) +" " +(h );

			elem.setAttribute("path", points + " x e");
			
			
		}
		*/
		else
		{
			if (mxUtils.isTrue(style, mxConstants.STYLE_ROUNDED, false))
			{
				elem = document.createElement("v:roundrect");
				elem.setAttribute("arcsize",
						(mxConstants.RECTANGLE_ROUNDING_FACTOR * 100) + "%");
			}
			else
			{
				elem = document.createElement("v:rect");
			}
		}

		String s = "position:absolute;left:" + String.valueOf(x) + "px;top:"
				+ String.valueOf(y) + "px;width:" + String.valueOf(w)
				+ "px;height:" + String.valueOf(h) + "px;";

		// Applies rotation
		double rotation = mxUtils.getDouble(style, mxConstants.STYLE_ROTATION);

		if (rotation != 0)
		{
			s += "rotation:" + rotation + ";";
		}

		elem.setAttribute("style", s);

		// Adds the shadow element
		if (mxUtils.isTrue(style, mxConstants.STYLE_SHADOW, false)
				&& fillColor != null)
		{
			Element shadow = document.createElement("v:shadow");
			shadow.setAttribute("on", "true");
			shadow.setAttribute("color", mxConstants.W3C_SHADOWCOLOR);
			elem.appendChild(shadow);
		}

		float opacity = mxUtils.getFloat(style, mxConstants.STYLE_OPACITY, 100);

		// Applies opacity to fill
		if (fillColor != null)
		{
			Element fill = document.createElement("v:fill");
			fill.setAttribute("color", fillColor);

			if (opacity != 100)
			{
				fill.setAttribute("opacity", String.valueOf(opacity / 100));
			}

			elem.appendChild(fill);
		}
		else
		{
			elem.setAttribute("filled", "false");
		}

		// Applies opacity to stroke
		if (strokeColor != null)
		{
			elem.setAttribute("strokecolor", strokeColor);
			Element stroke = document.createElement("v:stroke");

			if (opacity != 100)
			{
				stroke.setAttribute("opacity", String.valueOf(opacity / 100));
			}

			elem.appendChild(stroke);
		}
		else
		{
			elem.setAttribute("stroked", "false");
		}

		elem.setAttribute("strokeweight", String.valueOf(strokeWidth) + "px");
		appendVmlElement(elem);

		return elem;
	}
	
	public Element drawText(String text, int x, int y, int w, int h,
			Map<String, Object> style)
	{
		if(text==null || text.trim().equals("")){
			return null;
		}
		Element table = mxUtils.createTable(document, text, x, y, w, h, scale,
				style);
		appendVmlElement(table);

		return table;
	}
}
