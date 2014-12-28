package org.fireflow.service.jdbc.delete;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;

public interface DBDelete {
	public void doDelete(Document where)throws ServiceInvocationException; 
}
