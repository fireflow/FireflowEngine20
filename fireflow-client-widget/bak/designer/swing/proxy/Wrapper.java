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
package org.fireflow.designer.swing.proxy;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.io.mxCodecRegistry;



/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class Wrapper implements IWrapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9176358838143889154L;
	
	/*
	static{
		mxCodecRegistry.addPackage(Wrapper.class.getPackage().getName());
	}
	*/
	
	
	private String ref = null;
	private String elementType = null;
	private transient boolean dirty = false;
	private Map<String,Object> attributes = null;
	
	public Wrapper(){
		attributes = new HashMap<String,Object>();
	}
	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}
	/**
	 * @param ref the ref to set
	 */
	public void setRef(String id) {
		this.ref = id;
	}
//	/**
//	 * @return the displayName
//	 */
//	public String getDisplayName() {
//		return displayName;
//	}
//	/**
//	 * @param displayName the displayName to set
//	 */
//	public void setDisplayName(String displayName) {
//		this.displayName = displayName;
//	}
	
	public boolean getDirty(){
		return dirty;
	}
	public boolean isDirty(){
		return dirty;
	}
	
	public void setDirty(boolean b){
		this.dirty = b;
	}
	
	public Map<String,Object> getAttributes(){
		return this.attributes;
	}
	
	public void setAttribute(String name,Object value){
		this.attributes.put(name, value);
	}
	
	public void setAttributes(Map<String,Object> arg_attributes){
		this.attributes.clear();
		this.attributes.putAll(arg_attributes);
	}
	
	public Object getAttribute(String name){
		return this.attributes.get(name);
	}
	/**
	 * @return the elementType
	 */
	public String getElementType() {
		return elementType;
	}
	/**
	 * @param elementType the elementType to set
	 */
	public void setElementType(String nodeType) {
		this.elementType = nodeType;
	}
	
	
}
