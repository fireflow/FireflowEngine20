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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.exception.EngineException;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="map")
@XmlType(name="mapType")
@XmlAccessorType(XmlAccessType.FIELD) 
public class MapConvertor {
	@XmlTransient
	public static final String MAP_TYPE_VARIABLE = "VAR_MAP";
	@XmlTransient
	public static final String MAP_TYPE_ASSIGNMENT_HANDLER = "ASSIGNMENT_HANDLER_MAP";
	
	@XmlElement(name="entry")
    private List<AbsMapEntry> mapEntries = new ArrayList<AbsMapEntry>();
    
    public void addEntry(AbsMapEntry entry) {
        mapEntries.add(entry);
    }

    public List<AbsMapEntry> getEntries() {
        return mapEntries;
    }
    
    public Map getMap(){
    	Map<String,Object> map = new HashMap<String,Object>();
    	for (AbsMapEntry entry : mapEntries){
    		map.put(entry.getKey(), entry.getValue());
    	}
    	return map;
    }
    public void putAll(Map originalMap,String mapType){
    	if (originalMap==null)return;
		Iterator entries = originalMap.entrySet().iterator();
		if (entries!=null){
			while (entries.hasNext()){
				Map.Entry entry = (Map.Entry)entries.next();
				Object value = entry.getValue();
				String key = (String)entry.getKey();
				if (MAP_TYPE_VARIABLE.equals(mapType)){
					VariableMapEntry mapEntry = new VariableMapEntry();
					mapEntry.setKey(key);
					mapEntry.setValue(value);
					mapEntries.add(mapEntry);
				}else if (MAP_TYPE_ASSIGNMENT_HANDLER.equals(mapType)){
					AssignmentHandlerMapEntry mapEntry = new AssignmentHandlerMapEntry();
					mapEntry.setKey(key);
					mapEntry.setValue(value);
					mapEntries.add(mapEntry);
				}else{
					throw new EngineException("引擎不支持该MapType类型，接口参数传入的mapType="+
							mapType+
							"；请使用MapConvertor.MAP_TYPE_VARIABLE或者MapConvertor.MAP_TYPE_ASSIGNMENT_HANDLER");
				}

			}
		}
    }
}
