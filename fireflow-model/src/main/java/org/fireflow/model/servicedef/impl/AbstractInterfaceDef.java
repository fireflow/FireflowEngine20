package org.fireflow.model.servicedef.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;

public abstract class AbstractInterfaceDef implements InterfaceDef {
//	protected String namespaceUri = null;//通过ServiceDef.TargetNamespaceUri表达，2012-03-01
	protected String name = null;
	
//	public String getNamespaceUri(){
//		return namespaceUri;
//	}
//	
//	public void setNamespaceUri(String uri){
//		this.namespaceUri = uri;
//	}
	protected boolean resolved = false;

	
	public boolean isResolved() {
		return resolved;
	}
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	protected List<OperationDef> operationsList = new ArrayList<OperationDef>();
	
	public OperationDef getOperation(String opName) {
		for (OperationDef op : operationsList){
			if (op.getOperationName()!=null && op.getOperationName().equals(opName)){
				return op;
			}
		}
		return null;
	}
	

//	public void setOperation(Operation operation) {
//		operationsList.add(operation);
//
//	}

	public List<OperationDef> getOperations() {
		
		return operationsList;
	}


	public List<OperationDef> getOperations(String opName) {
		List<OperationDef> tmp = new ArrayList<OperationDef>();
		for (OperationDef op : operationsList){
			if (op.getOperationName().equals(opName)){
				tmp.add(op);
			}
		}
		return tmp;
	}

	public String toString(){
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Interface ").append(this.getName()).append("[\n");
		
		List<OperationDef> oprationList = this.getOperations();
		for (OperationDef op : oprationList){
			sbuf.append("\t");
			
			List<Output> outputList = op.getOutputs();
			for (int i=0;i<outputList.size();i++){
				Output output = outputList.get(i);
				sbuf.append(output.getDataType());
				if (i<outputList.size()-1){
					sbuf.append(",");
				}else{
					sbuf.append("  ");
				}
			}
			
			sbuf.append(op.getOperationName()).append("(");
			List<Input> inputList = op.getInputs();
			for (int i=0;i<inputList.size();i++){
				Input input = inputList.get(i);
				sbuf.append(input.getDataType());
				sbuf.append("  ").append(input.getName());
				if (i<inputList.size()-1){
					sbuf.append(",\n\t\t");
				}
			}
			sbuf.append(")\n");
		}
		sbuf.append("]");
		return sbuf.toString();
	}
}
