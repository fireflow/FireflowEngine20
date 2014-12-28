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
package org.fireflow.server.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="properties")
@XmlType(name="propertiesType")
@XmlAccessorType(XmlAccessType.FIELD) 
public class PropertiesConvertor {
	@XmlElement(name="entry")
    private List<PropertiesEntry> entries = new ArrayList<PropertiesConvertor.PropertiesEntry>();
	
	@XmlTransient
	private Properties properties = new Properties();
    
    public void addEntry(PropertiesEntry entry) {
        entries.add(entry);
    }

    public List<PropertiesEntry> getEntries() {
        return entries;
    }

    public void putAll(Properties properties){
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()){
			String key = (String)keys.next();
			String value = properties.getProperty(key);
			PropertiesEntry entry = new PropertiesEntry(key,value);
			entries.add(entry);
		}
    }
    
    public Properties getProperties(){
    	properties.clear();
		for (PropertiesEntry entry : entries){
			properties.put(entry.getKey(), entry.getValue());
		}
		return properties;
    }
    
    //结构依赖于需要Conver的Map，比如这里Map<String, Person>
    @XmlType(name="")
    @XmlAccessorType(XmlAccessType.FIELD) 
    public static class PropertiesEntry {
    	@XmlAttribute(name="key")
        private String key;
    	
    	@XmlValue
        private String value;

        public PropertiesEntry() {

        }

        public PropertiesEntry(String key, String v) {
            this.key = key;
            this.value = v;
        }


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
