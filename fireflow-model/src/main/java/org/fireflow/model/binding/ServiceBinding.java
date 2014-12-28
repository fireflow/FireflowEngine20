package org.fireflow.model.binding;

import java.util.List;

import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;


/**
 * 
 * 	<ServiceBinding>
 * 		<!--
 *      <service/>
 *      -->  
 * 		<ref service="Approve_XX" operation="">
 * 		  <IOMapping>
 * 			<InputAssignments>
 * 				<InputAssignment from="an_expression" to="the_input_name">
 * 			<InputAssignments>
 * 			<OutputAssignments>
 * 				<OutputAssignment from="an_expression" to="the_process_property_name">
 * 			<OutputAssignments> 
 * 		  </IOMapping>
 *      </ref>
 * 		<PropOverrides>
 * 			<PropOverride propGroupName="" propName="" value="">
 * 		</PropOverrides>
 * 	</ServiceBinding>
 * 
 * @author 非也
 * @version 2.0
 */
public interface ServiceBinding {	
	public String getServiceId();
	public void setServiceId(String serviceId);
	

	public String getOperationName();
	public void setOperationName(String opName);
	
//	public OperationDef getOperation();
//	public void setOperation(OperationDef op);
//	
//	public ServiceDef getService();
//
//	public void setService(ServiceDef svc);

	//1、输入参数的赋值方式之一：Assignment
	public List<Assignment> getInputAssignments();
	public void setInputAssignments(List<Assignment> assignments);	
	public List<Assignment> getOutputAssignments();
	public void setOutputAssignments(List<Assignment> assignments);
	
	
	//2、输入参数的赋值方式之二：实现ServiceInputOutputHandler接口
	public String getServiceInputOutputHandler();
	public void setServiceInputOutputHandler(String handlerClassName);
	
	//3、输入输出赋值方式之三：Transformation xml转换？
	
//	public List<PropOverride> getPropOverrides();
//	
//	public void setPropOverrides(List<PropOverride> propOverrides);
}
