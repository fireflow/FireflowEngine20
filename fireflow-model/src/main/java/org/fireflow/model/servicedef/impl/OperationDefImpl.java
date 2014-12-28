package org.fireflow.model.servicedef.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.OperationDef;


public class OperationDefImpl implements OperationDef{
	String operationName = null;
	List<Input> inputs = new ArrayList<Input>();
	List<Output> outputs = new ArrayList<Output>();
	
	public String getOperationName(){
		return operationName;
	}
	
	public void setOperationName(String nm){
		this.operationName = nm;
	}

	public List<Input> getInputs() {
		return inputs;
	}


	public List<Output> getOutputs() {
		return outputs;
	}
    
}
