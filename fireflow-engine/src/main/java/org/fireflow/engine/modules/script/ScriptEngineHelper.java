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
package org.fireflow.engine.modules.script;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.xml.namespace.QName;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.UnifiedJEXL;
import org.apache.commons.jxpath.JXPathContext;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.invocation.Message;
import org.fireflow.engine.modules.script.functions.DateTimeUtil;
import org.fireflow.engine.modules.script.functions.JexlContext4Fireflow;
import org.fireflow.engine.modules.script.functions.XPath;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.data.Expression;
import org.firesoa.common.jxpath.model.dom.W3CDomFactory;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConvertor;
import org.firesoa.common.util.ScriptLanguages;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ScriptEngineHelper {
//	private static final Dom4JFactory dom4JFactory = new Dom4JFactory();
	private static final W3CDomFactory w3cDomFactory = new W3CDomFactory();
//	static{
//		JXPathContextReferenceImpl.addNodePointerFactory(new Dom4JPointerFactory());
//	}
	
	private static final Map<String,Object> jexlFunctionsMap = new HashMap<String,Object>();
	private static final JexlEngine jexlEngine = new JexlEngine();
	static{
		//TODO 所有的function应该写在一个xml注册文件中，便于扩展
		jexlFunctionsMap.put("DateUtil", DateTimeUtil.getInstance());
		jexlFunctionsMap.put("Xpath", XPath.class);
		jexlEngine.setFunctions(jexlFunctionsMap);
	}
	
	
	//所有的表达式解析，均通过该方法。
	public static Object evaluateExpression(RuntimeContext rtCtx,Expression fireflowExpression,Map<String,Object> contextObjects){
		if (fireflowExpression==null) return null;
		if (fireflowExpression.getNamespaceMap()!=null && fireflowExpression.getNamespaceMap().size()>0){
			contextObjects.put(XPath.NAMESPACE_PREFIX_URI_MAP, fireflowExpression.getNamespaceMap());
		}
		if (ScriptLanguages.JEXL.name().equalsIgnoreCase(fireflowExpression.getLanguage())){
			return evaluateJexlExpression(fireflowExpression,contextObjects);
		}else if (ScriptLanguages.UNIFIEDJEXL.name().equalsIgnoreCase(fireflowExpression.getLanguage())){
			return evaluateUnifiedJexlExpression(fireflowExpression,contextObjects);
		}else if (ScriptLanguages.XPATH.name().equalsIgnoreCase(fireflowExpression.getLanguage())){
			return evaluateXpathExpression(fireflowExpression,contextObjects);
		}else{
			return evaluateJSR233Expression(rtCtx,fireflowExpression,contextObjects);
		}
	}
	
	private static Object evaluateJSR233Expression(RuntimeContext rtCtx,Expression fireflowExpression,Map<String,Object> contextObjects){
		ScriptEngine scriptEngine = rtCtx.getScriptEngine(fireflowExpression.getLanguage());
		
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings engineScope = scriptContext
				.getBindings(ScriptContext.ENGINE_SCOPE);
		engineScope.putAll(contextObjects);
		try {
			Object result = scriptEngine.eval(fireflowExpression.getBody(), scriptContext);
			return result;
		} catch (ScriptException e) {
			throw new RuntimeException("Can NOT evaluate the expression. ",e);
		}
	}
	
	private static Object evaluateJexlExpression(Expression fireflowExpression,Map<String,Object> contextObjects){
		JexlContext4Fireflow context = new JexlContext4Fireflow();
		context.setAllContextObject(contextObjects);
		
		org.apache.commons.jexl2.Expression jexlExp = jexlEngine.createExpression(fireflowExpression.getBody());
		return jexlExp.evaluate(context);
	}
	
	private static Object evaluateUnifiedJexlExpression(Expression fireflowExpression,Map<String,Object> contextObjects){
		JexlContext4Fireflow context = new JexlContext4Fireflow();
		context.setAllContextObject(contextObjects);
		UnifiedJEXL ujexl = new UnifiedJEXL(jexlEngine);

		UnifiedJEXL.Expression jexlExp = ujexl.parse(fireflowExpression.getBody());
		Object result = jexlExp.evaluate(context);
		return result==null?null:result.toString();
	}
	
	private static Object evaluateXpathExpression(Expression fireflowExpression,Map<String,Object> contextObjects){
		Map<String,String> namespacePrefixUriMap = fireflowExpression.getNamespaceMap();

		JXPathContext jxpathContext = JXPathContext.newContext(contextObjects);
		jxpathContext.setFactory(w3cDomFactory );
		if (namespacePrefixUriMap!=null){
			Iterator<String> prefixIterator = namespacePrefixUriMap.keySet().iterator();
			while(prefixIterator.hasNext()){
				String prefix = prefixIterator.next();
				String nsUri = namespacePrefixUriMap.get(prefix);
				jxpathContext.registerNamespace(prefix, nsUri);
			}
		}
		
		//仅对W3C DOM进行了判断
		//TODO 需要时情况增加对dom4j document值以及jdom document值的判断
		Object obj = null;
		Object _node = jxpathContext.selectSingleNode(fireflowExpression.getBody());
		if (_node instanceof org.w3c.dom.Node){
			if (_node instanceof org.w3c.dom.Document){
				obj = _node;
			}else{
				obj = ((org.w3c.dom.Node)_node).getTextContent();
			}
		}else{
			obj = _node;
		}
		return obj;
	}
	
	
	/**
	 * 构建脚本引擎的Context,
	 * 
	 * @param session
	 * @param processInstance
	 * @param activityInstance
	 * @return
	 */
	public static Map<String, Object> fulfillScriptContext(
			WorkflowSession session, RuntimeContext runtimeContext,
			ProcessInstance processInstance, ActivityInstance activityInstance) {
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		
		Map<String, Object> engineScope = new HashMap<String, Object>();

		engineScope.put(ScriptContextVariableNames.CURRENT_PROCESS_INSTANCE,
				processInstance);
		engineScope.put(ScriptContextVariableNames.CURRENT_ACTIVITY_INSTANCE,
				activityInstance);
		Map<String, Object> varValues = processInstance
				.getVariableValues(session);
		engineScope
				.put(ScriptContextVariableNames.PROCESS_VARIABLES, varValues);

		if (activityInstance != null) {
			Map<String, Object> varValues2 = activityInstance
					.getVariableValues(session);
			engineScope.put(ScriptContextVariableNames.ACTIVITY_VARIABLES,
					varValues2);
		}

		engineScope.put(ScriptContextVariableNames.SESSION_ATTRIBUTES,
				localSession.getAllAttributes());
		
		engineScope.put(ScriptContextVariableNames.WORKFLOW_SESSION, session);
		
		engineScope.put(ScriptContextVariableNames.RUNTIME_CONTEXT, runtimeContext);
		
		return engineScope;
	}

	public static Map<String, Object> resolveAssignments(
			RuntimeContext runtimeContext, List<Assignment> assignments,
			Map<String, Object> contextVars) throws ScriptException {

		if (assignments == null || assignments.size() == 0) {
			return null;
		}

		Map<String, Object> jxpathRoot = new HashMap<String, Object>();
		if (contextVars.get(ScriptContextVariableNames.INPUTS)!=null){
			jxpathRoot.put(ScriptContextVariableNames.INPUTS,
					contextVars.get(ScriptContextVariableNames.INPUTS));
		}else{
			jxpathRoot.put(ScriptContextVariableNames.INPUTS,
					new HashMap<String, Object>());
		}
		
		if (contextVars.get(ScriptContextVariableNames.OUTPUTS)!=null){
			jxpathRoot.put(ScriptContextVariableNames.OUTPUTS, contextVars.get(ScriptContextVariableNames.OUTPUTS));
		}
		else{
			jxpathRoot.put(ScriptContextVariableNames.OUTPUTS,
					new HashMap<String, Object>());
		}
		
		jxpathRoot.put(ScriptContextVariableNames.PROCESS_VARIABLES,
				new HashMap<String, Object>());
		jxpathRoot.put(ScriptContextVariableNames.ACTIVITY_VARIABLES,
				new HashMap<String, Object>());
		jxpathRoot.put(ScriptContextVariableNames.SESSION_ATTRIBUTES,
				new HashMap<String, Object>());

		for (Assignment assignment : assignments) {
			Expression fromExpression = assignment.getFrom();

			Object obj = evaluateExpression(runtimeContext,fromExpression,contextVars);

			if (fromExpression.getDataType() != null && obj!=null) {
				try {
					obj = JavaDataTypeConvertor.dataTypeConvert(
							fromExpression.getDataType(), obj, null);
				} catch (ClassCastException e) {
					throw new ScriptException(e);
				} catch (ClassNotFoundException e) {
					throw new ScriptException(e);
				}
			}

			// TODO 所有的To 表达式都用JXpath处理器处理，不使用脚本 引擎
			Expression toExpression = assignment.getTo();
			
			QName dataType = toExpression.getDataType();
			if (dataType != null
					&& dataType.getNamespaceURI().equals(
							NameSpaces.JAVA.getUri()) && obj!=null) {// 进行类型验证和转换
				try {
					obj = JavaDataTypeConvertor.dataTypeConvert(dataType, obj, null);
				} catch (ClassCastException e) {
					throw new ScriptException(e);
				} catch (ClassNotFoundException e) {
					throw new ScriptException(e);
				}
			} else {
				// XSD数据类型待处理
				//TODO (临时性代码)将date转为string 
				if (obj instanceof java.util.Date){
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					obj = df.format((java.util.Date)obj);
				}
			}
			

			JXPathContext jxpathContext = JXPathContext.newContext(jxpathRoot);
			jxpathContext.setFactory(w3cDomFactory );
			
			Map<String,String> nsMap = toExpression.getNamespaceMap();
			Iterator<String> prefixIterator = nsMap.keySet().iterator();
			while(prefixIterator.hasNext()){
				String prefix = prefixIterator.next();
				String nsUri = nsMap.get(prefix);
				jxpathContext.registerNamespace(prefix, nsUri);
			}

			//赋值处理
			jxpathContext.createPathAndSetValue(toExpression.getBody(), obj);
		}

		return jxpathRoot;
	}

	/**
	 * 解析输入参数的值
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "restriction", "unchecked" })
	public static Map<String, Object> resolveInputParameters(
			RuntimeContext runtimeContext, List<Assignment> inputAssignments,
			Map<String, Object> contextVars) throws ScriptException {

		Map<String, Object> result = resolveAssignments(
				runtimeContext, inputAssignments, contextVars);
		if (result != null) {
			Map tmp = (Map) result.get(ScriptContextVariableNames.INPUTS);
			return tmp;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
	public static void assignOutputToVariable(WorkflowSession session,
			RuntimeContext runtimeContext, ProcessInstance processInstance,
			ActivityInstance activityInstance,
			List<Assignment> assignmentsList, Map<String, Object> contextVars)
			throws ScriptException {

		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		
		if (assignmentsList == null || assignmentsList.size()==0)
			return;

		Map<String, Object> result = resolveAssignments(
				runtimeContext, assignmentsList, contextVars);
		//System.out.println("===解析后的Assignment is "+result);
		if (result==null)return;

		Map<String,Object> procInstVars = (Map<String, Object>)result.get(ScriptContextVariableNames.PROCESS_VARIABLES);
		if (procInstVars!=null && procInstVars.size()>0){
			Iterator<String> keys = procInstVars.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				try {
					Object value = procInstVars.get(key);
					if (value instanceof Message){
						processInstance.setVariableValue(session, key, ((Message)value).getPayload(),((Message)value).getHeaders());
					}else{
						processInstance.setVariableValue(session, key, value);
					}
					
				} catch (InvalidOperationException e) {
					throw new ScriptException(e);
				}
			}
		}
		
		Map<String,Object> actInstVars = (Map<String, Object>)result.get(ScriptContextVariableNames.ACTIVITY_VARIABLES);
		if (actInstVars!=null && actInstVars.size()>0){
			Iterator<String> keys = actInstVars.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				try {
					Object value = actInstVars.get(key);
					if (value instanceof Message){
						activityInstance.setVariableValue(session, key, ((Message)value).getPayload(),((Message)value).getHeaders());
					}else{
						activityInstance.setVariableValue(session, key, value);
					}
					
				} catch (InvalidOperationException e) {
					throw new ScriptException(e);
				}
			}
		}
		
		Map<String,Object> sessionAttrs = (Map<String, Object>)result.get(ScriptContextVariableNames.SESSION_ATTRIBUTES);
		if (sessionAttrs!=null && sessionAttrs.size()>0){
			Iterator<String> keys = sessionAttrs.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				localSession.setAttribute(key, sessionAttrs.get(key)); 
			}
		}

	}



}
