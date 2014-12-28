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
package org.fireflow.pdl.fpdl.diagram.impl;

import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.figure.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CommentShapeImpl extends AbsNodeShapeImpl implements CommentShape {
	public CommentShapeImpl(String id){
		this.id = id;
		
		RectangleImpl rect = new RectangleImpl();
		this.figure = rect;
	}
	
	public void setContent(String comment){
		((RectangleImpl)this.figure).setContent(comment);
	}
	
	public String getContent(){
		return ((RectangleImpl)this.figure).getContent();
	}
	
	public void setTitle(String title){
		this.figure.setTitle(title);
	}
	public String getTitle(){
		return this.figure.getTitle();
	}
}
