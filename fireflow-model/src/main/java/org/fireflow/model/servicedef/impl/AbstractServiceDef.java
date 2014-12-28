package org.fireflow.model.servicedef.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.data.Types;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;


public abstract class AbstractServiceDef extends AbstractModelElement implements ServiceDef{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4397356923004911343L;
	
	protected String invokerBeanName = null;
	protected String invokerClassName = null;
	protected String parserClassName = null;
	protected String bizCategory = null;
	protected String version = "1";
	protected String targetNamespaceUri = null;
	protected XmlSchemaCollection schemaCollection = null;
	protected InterfaceDef _interface = null;
	protected Types typeDeclarations = null;
	
	public AbstractServiceDef(){
	}
	
	public Types getTypeDeclarations(){
		return typeDeclarations;
	}
	
	public void setTypeDeclarations(Types types){
		this.typeDeclarations = types;
	}
	
	
    /**
     * Service的业务类别，便于分类管理。格式如下：<br>
     * 受理科业务/车管受理/新车登记
     * @return
     */
    public String getBizCategory(){
    	return bizCategory;
    }
    
    /**
     * 设置服务的业务类别
     * @param category
     */
    public void setBizCategory(String category){
    	this.bizCategory = category;
    }
    
    

	/**
	 * @return the executorName
	 */
	public String getInvokerBeanName() {
		return invokerBeanName;
	}
	
	/**
	 * @param executorName the executorName to set
	 */
	public void setInvokerBeanName(String executorName) {
		this.invokerBeanName = executorName;
	}

	public String getVersion() {
		
		return version;
	}
	public void setVersion(String v) {
		version = v;
		
	}
	public InterfaceDef getInterface() {
		return _interface;
	}
	public void setInterface(InterfaceDef _interface) {
		this._interface = _interface;
		
	}


	

	
	public String getInvokerClassName() {
		
		return this.invokerClassName;
	}
	
	public void setInvokerClassName(String invokerClassName){
		this.invokerClassName = invokerClassName;
	}
	public String getTargetNamespaceUri(){
		return this.targetNamespaceUri;
	}
	
	public void setTargetNamespaceUri(String nsUri){
		this.targetNamespaceUri = nsUri;
	}
	public XmlSchemaCollection getXmlSchemaCollection() {
		return schemaCollection;
	}
	
	public void setXmlSchemaCollection(XmlSchemaCollection schemaCollection){
		this.schemaCollection = schemaCollection;
	}
	public String getParserClassName() {
		return parserClassName;
	}
	public void setParserClassName(String parserClassName) {
		this.parserClassName = parserClassName;
	}
	
	public void afterPropertiesSet()throws Exception{
		//TODO 将typeDeclarations中的schema读取到xmlchemacollections中
		if (this.typeDeclarations!=null){
			//TODO 待完成
		}
	}
	
	public String toString(){
		return "ServiceDef[name="+this.getName()+"]";
	}
}