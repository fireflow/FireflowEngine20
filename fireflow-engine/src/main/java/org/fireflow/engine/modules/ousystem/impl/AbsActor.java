/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.modules.ousystem.impl;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.modules.ousystem.Actor;
import org.fireflow.server.support.PropertiesXmlAdapter;


/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absActorType",propOrder={"id","name","properties"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({UserImpl.class,RoleImpl.class,GroupImpl.class,DepartmentImpl.class})
public abstract class AbsActor implements Actor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5454575926553679668L;

	@XmlElement(name="properties")
	@XmlJavaTypeAdapter(value=PropertiesXmlAdapter.class)
	private Properties properties = new Properties(); 
	
	@XmlElement(name="id")
	private String id = null;
	
	@XmlElement(name="name")
	private String name = null;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty(String key){
		return properties==null?null:(String)properties.get(key);
	}
	
	public Properties getProperties(){
		return properties;
	}

	public void setProperties(Properties properties){
		this.properties.putAll( properties);
	}
	
	public void setProperty(String key,String value){
		this.properties.put(key, value);
	}
}