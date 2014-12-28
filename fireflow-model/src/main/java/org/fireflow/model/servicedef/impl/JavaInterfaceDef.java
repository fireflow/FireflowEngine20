package org.fireflow.model.servicedef.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebResult;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.PrioritizedParameterNameDiscoverer;


public class JavaInterfaceDef extends AbstractInterfaceDef implements InterfaceDef {
	public static final String OUTPUT_NAME_PREFIX = "output_of_";
	private static final Log log = LogFactory.getLog(JavaInterfaceDef.class);
	
	/**
	 * 被排除的java方法，这些方法不作为服务接口对外暴露。
	 */
	public static final String[] EXCLUDED_METHODES = new String[]{"wait","equals","toString","hashCode","getClass","notify","notifyAll"};

	
	protected String interfaceClassName = null;
	protected Map<String,Map<Integer,QName>> parameterTypeMap = new HashMap<String,Map<Integer,QName>>();

	public JavaInterfaceDef(){
//		this.namespaceUri = NameSpaces.JAVA.getUri();
	}
	/**
	 * 设置参数类型映射，其中paramPoistion表示参数位置，输入参数从0开始；输出参数位置值为-1
	 * @param paramPoistion
	 * @param paramQName
	 */
	public void putParameterTypeMap(String methodName,Integer paramPoistion,QName paramQName){
		Map<Integer,QName> methodParamMap = this.parameterTypeMap.get(methodName);
		if (methodParamMap==null){
			methodParamMap = new HashMap<Integer,QName>();
			this.parameterTypeMap.put(methodName, methodParamMap);
		}
		methodParamMap.put(paramPoistion, paramQName);
	}
	
	public String getInterfaceClassName() {
		return interfaceClassName;
	}

	public void setInterfaceClassName(String interfaceClass)  {
		this.interfaceClassName = interfaceClass;
		this.name = interfaceClass;
		
		this.operationsList.clear();
//		this.namespaceUri = NameSpaces.JAVA.getUri();
		resolved = false;
	}
	

	
	
	/**
	 * 解析接口类，构建Operations列表
	 * @throws ClassNotFoundException 
	 */
	public static List<OperationDef> resolveInterfaceClass(String className)  {
		List<OperationDef> operations = new ArrayList<OperationDef>();
		
		PrioritizedParameterNameDiscoverer discover = PrioritizedParameterNameDiscoverer.DEFAULT;
		
		Class clz = null;
		try {
			clz=Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException (e);
		}
		Method[] methods = clz.getMethods();
		
		for (Method m : methods){
			if (isExcluded(m)){
				continue;
			}
			Class[] types = m.getParameterTypes();
			
			String[] paramNames = discover.getParameterNames(m);
			
			OperationDefImpl op = new OperationDefImpl();
			op.setOperationName(m.getName());
			Map<Integer,QName> methodParamMap = null;//TODO 待解决。this.parameterTypeMap.get(m.getName());
			for (int i=0;i<types.length;i++){
				Class t = types[i];
				String paramName = paramNames[i];
				
				InputImpl input = new InputImpl();
				if (methodParamMap==null || methodParamMap.get(i)==null){
					input.setDataType(new QName(NameSpaces.JAVA.getUri(),t.getName()));

				}else{
					input.setDataType(methodParamMap.get(i));
				}
					

				input.setName(paramName);
				
				input.setDisplayName(paramName);
				
				op.getInputs().add(input);
			}
			
			if(!void.class.equals(m.getReturnType())){
				OutputImpl output = new OutputImpl();
				WebResult webResult = m.getAnnotation(WebResult.class);
				String outputNameFromWebResult = null;
				if (webResult!=null){
					outputNameFromWebResult = webResult.name();
				}
				
				String outputName = OUTPUT_NAME_PREFIX+m.getName();
				if (!StringUtils.isEmpty(outputNameFromWebResult)){
					outputName = outputNameFromWebResult;
				}
				
				output.setName(outputName);
				if (methodParamMap==null || methodParamMap.get(-1)==null){
					output.setDataType(new QName(NameSpaces.JAVA.getUri(),m.getReturnType().getName()));
				}else{
					output.setDataType(methodParamMap.get(-1));
				}
				
				op.getOutputs().add(output);
			}

			
			operations.add(op);
		}
		return operations;
	}
	
	private static boolean isExcluded(Method m){
		String name = m.getName();
		for (String s : EXCLUDED_METHODES){
			if (s.equals(name)){
				return true;
			}
		}
		return false;
	}
	
}
