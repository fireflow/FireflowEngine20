/**
 * Copyright 2007-2011 非也
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
package org.firesoa.common.schema;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * 为了提高系统效率，避免每次重新生成PojoSchema，通过该注册类缓存已经生成的PojoSchema。
 * 
 * @author 非也 www.firesoa.com
 * 
 * 
 */
public class PojoSchemaRegistry {
	protected static Map<Class, PojoSchema> pojoSchemaCache = new HashMap<Class, PojoSchema>();

	public static PojoSchema getPojoSchema(Class pojoClass) throws Exception{
		PojoSchema pojoSchema = pojoSchemaCache.get(pojoClass);
		if (pojoSchema==null){
			pojoSchema = JAXBUtil.generatePojoSchema(pojoClass);
			pojoSchemaCache.put(pojoClass, pojoSchema);
		}
		return pojoSchema;
	}


}
