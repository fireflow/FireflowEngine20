package org.fireflow.model.data;

/**
 * 输入
 * @author 非也
 *
 */
public interface Input extends DataElement{
	/**
	 * 缺省值
	 * @return
	 */
	public String getDefaultValueAsString();
    
	public void setDefaultValueAsString(String defaultValue);
	
	/**
	 * 数据模式，一般对于应用于日期类型
	 * @return
	 */
	public String getDataPattern();
	public void setDataPattern(String dataPattern);
}
