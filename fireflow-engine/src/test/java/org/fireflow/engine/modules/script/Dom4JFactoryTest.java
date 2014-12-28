package org.fireflow.engine.modules.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.firesoa.common.jxpath.model.dom4j.Dom4JFactory;
import org.firesoa.common.jxpath.model.dom4j.Dom4JPointerFactory;

public class Dom4JFactoryTest {
	public static void main(String[] args) {
		Map<String, Map<String, Object>> jxpathRoot = new HashMap<String, Map<String, Object>>();
		jxpathRoot.put(ScriptContextVariableNames.INPUTS,
				new HashMap<String, Object>());
		jxpathRoot.put(ScriptContextVariableNames.PROCESS_VARIABLES,
				new HashMap<String, Object>());
		jxpathRoot.put(ScriptContextVariableNames.ACTIVITY_VARIABLES,
				new HashMap<String, Object>());
		jxpathRoot.put(ScriptContextVariableNames.SESSION_ATTRIBUTES,
				new HashMap<String, Object>());

		JXPathContextReferenceImpl
				.addNodePointerFactory(new Dom4JPointerFactory());
		JXPathContext jxpathContext = JXPathContext.newContext(jxpathRoot);
		jxpathContext.setFactory(new TempDom4JFactory());//初始化构造函数
		jxpathContext.registerNamespace("ns1", "http://test/");

		
		jxpathContext.createPathAndSetValue("/inputs/mapValue", "value123");
		System.out.println(jxpathRoot.get("inputs"));
		
		jxpathContext.createPathAndSetValue("/inputs/dom4jDoc/where_fields/id",
				"123");
		
		System.out.println(jxpathRoot.get("inputs"));
		Document dom4jDoc = (Document)jxpathRoot.get("inputs").get("dom4jDoc");
		System.out.println(dom4jDoc.asXML());
		
		jxpathContext.createPathAndSetValue("/inputs/dom4jDoc/where_fields/id",
		"234");
		
		jxpathContext.createPathAndSetValue("/inputs/dom4jDoc/where_fields/state",
		"789");
		
		System.out.println(dom4jDoc.asXML());
	}
}

class TempDom4JFactory extends Dom4JFactory {
	public boolean createObject(JXPathContext context, Pointer pointer,
			Object parent, String name, int index) {
		if (parent instanceof Map){
			//TODO 剩下的问题是如何初始化Document
			DocumentFactory dom4jFactory = DocumentFactory.getInstance();
			Document dom4jDoc = dom4jFactory.createDocument();
			((Map)parent).put(name, dom4jDoc);
			return true;
		}else{
			return super.createObject(context, pointer, parent, name, index);
		}
	}
}
