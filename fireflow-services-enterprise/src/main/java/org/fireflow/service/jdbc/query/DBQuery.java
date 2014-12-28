package org.fireflow.service.jdbc.query;


import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;

public interface DBQuery{
	/**
	 * 查询条件以xml方式输入，查询结果以xml方式输出。
	 * 
	 * @param condition
	 * @return
	 * @throws ServiceInvocationException
	 */
	
	public Document doQuery(Document condition)throws ServiceInvocationException; 
}
