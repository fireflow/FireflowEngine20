package org.fireflow.model.data.impl;

import javax.xml.namespace.QName;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.DataElement;
import org.fireflow.model.data.Property;
import org.firesoa.common.schema.NameSpaces;


public class PropertyImpl extends AbstractModelElement implements Property{

    
    /**
	 * 
	 */
	private static final long serialVersionUID = -644114332126268399L;

	/**
     * 数据类型，数据类型必须是一个合法的java类名，如 java.lang.String，java.lang.Integer等。
     */
    private QName dataType;
    
    /**
     * 初始值
     */
    private String initialValue;
    
    /**
     * 数据格式
     */
    private String dataPattern;
    
    public PropertyImpl(ModelElement parentElement,String name){
    	super(parentElement,name);
    	this.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String",NameSpaces.JAVA.getPrefix()));
    }

    public PropertyImpl() {
    	
        this.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String",NameSpaces.JAVA.getPrefix()));
        
    }

//    public PropertyImpl(WorkflowProcessImpl workflowProcess, String name, String dataType) {
//        super(workflowProcess, name);
//        setDataType(dataType);
//    }

    /**
     * 返回流程变量的数据类型
     * @return 数据类型
     */
    public QName getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型，
     * @param dataType
     */
    public void setDataType(QName dataType) {
        this.dataType = dataType;
    }


    public String getInitialValueAsString() {
        return initialValue;
    }


    public void setInitialValueAsString(String initialValue) {
        this.initialValue = initialValue;
    }


    public String getDataPattern() {
        return dataPattern;
    }


    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
    public DataElement copy(){
    	PropertyImpl obj = new PropertyImpl();
    	obj.setDataPattern(this.getDataPattern());
    	obj.setDataType(this.getDataType());
    	obj.setDescription(this.getDescription());
    	obj.setDisplayName(this.getDisplayName());
    	obj.setInitialValueAsString(this.getInitialValueAsString());
    	obj.setName(this.getName());
    	obj.setParent(this.getParent());
    	return obj;
    }
}
