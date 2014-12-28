package org.fireflow.service.webservice;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;

public interface WebServiceCaller {

	/**
	 * 调用web service。其中messagePlayload列表中的每个Document对象代表Web Service Message中的一个part。
	 * 
	 * @param messagePlayload
	 * @return
	 * @throws ServiceInvocationException
	 */
//	public List<Document> callWebService(List<Document> messagePlayload) throws ServiceInvocationException;

	public Document callWebService(Document messagePlayload) throws ServiceInvocationException;
}
