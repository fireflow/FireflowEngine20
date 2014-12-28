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
package org.fireflow.engine.modules.script.functions;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jxpath.JXPathContext;
import org.fireflow.engine.modules.script.ScriptEngineHelper;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class XPath {
//	private static Map<String,String> namespacePrefixUriMap = null;
	public static final String NAMESPACE_PREFIX_URI_MAP = "org.fireflow.contants.NAMESPACE_PREFIX_URI_MAP";
	private JexlContext4Fireflow context = null;
	public XPath(JexlContext ctx){
		this.context = (JexlContext4Fireflow)ctx;
	}

	

	
	public Object getValue(String xpath){
		Map<String,Object> contextObjects = this.context.getAllContextObject();
		Map<String,String> namespacePrefixUriMap = null;
		if (contextObjects.containsKey(NAMESPACE_PREFIX_URI_MAP)){
			namespacePrefixUriMap = (Map<String,String>)contextObjects.remove(NAMESPACE_PREFIX_URI_MAP);
		}
		JXPathContext jxpathContext = JXPathContext.newContext(contextObjects);

		if (namespacePrefixUriMap!=null){
			Iterator<String> prefixIterator = namespacePrefixUriMap.keySet().iterator();
			while(prefixIterator.hasNext()){
				String prefix = prefixIterator.next();
				String nsUri = namespacePrefixUriMap.get(prefix);
				jxpathContext.registerNamespace(prefix, nsUri);
			}
		}
		Object result = jxpathContext.getValue(xpath);
		return result;
	}
	
//	public void setValue(Object obj,String xpath){
//		
//	}
	

}

