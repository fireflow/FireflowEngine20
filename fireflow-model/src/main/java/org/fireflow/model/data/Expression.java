package org.fireflow.model.data;

import java.util.Map;

/**
 * 表达式，
 * @author 非也
 *
 */
public interface Expression extends DataElement{
	public static final String EXPRESSION_DISPLAY_NAME_TIP = "请编辑该表达式...";
	/**
	 * 表达式的语言，目前支持 org.fireflow.engine.modules.script.ScriptLanguages.JEXL;
	 * org.fireflow.engine.modules.script.ScriptLanguages.XPATH;
	 * org.fireflow.engine.modules.script.ScriptLanguages.UNIFIEDJEXL
	 * @return
	 */
	public String getLanguage();
	
	/**
	 * 表达式体
	 * @return
	 */
	public String getBody();
	
	/**
	 * 返回namespace prefix和 namespace uri组成的映射表。<br/>
	 * 对于xpath类型的表达式需要用到该属性，例如，如下xpath表达式，
	 * 必须声明namespace，否则xpatch处理器无法处理。<br/>
	 * /ns0:foo/ns1:bar
	 * 
	 * @return
	 */
	public Map<String,String> getNamespaceMap();
	
}
