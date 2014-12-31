package org.fireflow.pdl.fpdl.process.event;

import org.fireflow.model.ModelElement;

public interface EventListenerDef extends ModelElement{
	/**
	 * 业务类别。<br>
	 * 例如："户政业务/户口迁入"
	 * @return
	 */
	//public String getBizCategory();
	
	/**
	 * 获得所引用的EventListener的bean的Id
	 * @return
	 */
	public String getListenerBeanName();
	
	public String getListenerClassName();
	
	public void setListenerBeanName(String beanName);
	
	public void setListenerClassName(String clzName);
}
