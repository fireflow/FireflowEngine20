package org.fireflow.model.binding.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.InputAssignment;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.PropOverride;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;

public class ServiceBindingImpl implements ServiceBinding{
	protected String serviceId = null;
//	protected ServiceDef service = null;
	protected String operationName = null;
//	protected OperationDef operation = null;
	protected List<Assignment> inputAssignments = new ArrayList<Assignment>();
	protected List<Assignment> outputAssignments = new ArrayList<Assignment>();
	protected List<PropOverride> propOverrides = new ArrayList<PropOverride>();


//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.binding.ServiceRef#getService()
//	 */
//	public ServiceDef getService() {
//		return service;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.model.process.binding.ServiceRef#setService(org.fireflow.model.service.impl.ServiceImpl)
//	 */
//	public void setService(ServiceDef svc) {
//		service = svc;
//		
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#getInputAssignments()
	 */
	public List<Assignment> getInputAssignments() {
		return inputAssignments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#getOutputAssignments()
	 */
	public List<Assignment> getOutputAssignments() {
		return outputAssignments;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#getPropOverrides()
//	 */
//	public List<PropOverride> getPropOverrides() {
//		return propOverrides;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#getServiceId()
	 */
	public String getServiceId() {
		return serviceId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#setInputAssignments(java.util.List)
	 */
	public void setInputAssignments(List<Assignment> assignments) {
		inputAssignments = assignments;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#setOutputAssignments(java.util.List)
	 */
	public void setOutputAssignments(List<Assignment> assignments) {
		outputAssignments = assignments;
		
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#setPropOverrides(java.util.List)
//	 */
//	public void setPropOverrides(List<PropOverride> propOverrides) {
//		this.propOverrides = propOverrides;
//		
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.ServiceBinding#setServiceId(java.lang.String)
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;		
	}
	
	public String getOperationName(){
		return this.operationName;
	}
	public void setOperationName(String opName){
		this.operationName = opName;
	}
	
//	public OperationDef getOperation(){
//		return this.operation;
//	}
//	public void setOperation(OperationDef op){
//		this.operation = op;
//	}

	public String getServiceInputOutputHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setServiceInputOutputHandler(String handlerClassName) {
		// TODO Auto-generated method stub
		
	}
}
