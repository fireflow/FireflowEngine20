package org.fireflow.model.data.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.data.DataElement;
import org.fireflow.model.data.Output;

public class OutputImpl extends AbsDataElement implements Output{

    
    /**
     * 初始值
     */
    private String valueAsString;
    
    /**
     * 数据格式
     */
    private String dataPattern;

	public String getValueAsString() {
		return valueAsString;
	}

	public void setValueAsString(String avlueAsString) {
		this.valueAsString = avlueAsString;
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}
    public DataElement copy(){
    	OutputImpl obj = new OutputImpl();
    	obj.setDataPattern(this.getDataPattern());
    	obj.setDataType(this.getDataType());
    	obj.setDisplayName(this.getDisplayName());
    	obj.setName(this.getName());
    	obj.setValueAsString(this.getValueAsString());
    	return obj;
    }
}
