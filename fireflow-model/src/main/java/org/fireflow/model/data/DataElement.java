package org.fireflow.model.data;

import javax.xml.namespace.QName;


/**
 * 数据元素
 * @author 非也
 *
 */
public interface DataElement {

	public String getName();
	public void setName(String name);
	
	public String getDisplayName();
	public void setDisplayName(String displayName);
	
	/**
	 * 数据类型，数据类型必须是一个QName；<br/>
	 * 对于Java类型，写作java:javax.lang.Integer；
	 * XSD数据类型可以写作如, xsd:string; 
	 * @return
	 */
    public QName getDataType();
    
    public void setDataType(QName dataType);
    
    /**
     * 复制
     * @return
     */
    public DataElement copy();//
}
