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
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.jexl2.Expression;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.firesoa.common.util.ScriptLanguages;
import org.junit.Test;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class XPathTest {
	@Test
	public void testGetValue(){
		String name = "Mr. Feiye";
		Foo f = new Foo();
		f.setName(name);
		
		Map<String,Object> jc = new HashMap<String,Object>();
        jc.put("foo",f);
        jc.put("now",new Date());
        
        
        String expressionStr = "Xpath:getValue('foo/name')";
        ExpressionImpl exp = new ExpressionImpl();
        exp.setBody(expressionStr);
        exp.setLanguage(ScriptLanguages.JEXL.name());
        Object obj = ScriptEngineHelper.evaluateExpression(new RuntimeContext(), exp, jc);

        Assert.assertEquals(name, obj);
        System.out.println("Xpath:getValue('foo/name')="+obj);
        
        String expressionStr1 = "Xpath:getValue('/foo/name')";
        ExpressionImpl exp1 = new ExpressionImpl();
        exp1.setBody(expressionStr1);
        exp1.setLanguage(ScriptLanguages.JEXL.name());
        Object obj1 = ScriptEngineHelper.evaluateExpression(new RuntimeContext(), exp1, jc);

        Assert.assertEquals(name, obj1);
        System.out.println("Xpath:getValue('/foo/name')="+obj1);
        
        String expressionStr2 = "DateUtil:format(now,'yyyy-MM-dd')";
        ExpressionImpl exp2 = new ExpressionImpl();
        exp2.setBody(expressionStr2);
        exp2.setLanguage(ScriptLanguages.JEXL.name());
        Object obj2 = ScriptEngineHelper.evaluateExpression(new RuntimeContext(), exp2, jc);
        System.out.println("DateUtil:format(now,'yyyy-MM-dd')="+obj2);
        
        
        
	}
}
