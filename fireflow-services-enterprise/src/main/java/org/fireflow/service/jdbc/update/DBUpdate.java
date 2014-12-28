package org.fireflow.service.jdbc.update;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;

public interface DBUpdate {
	public void doUpdate(Document updateDoc)throws ServiceInvocationException; 
}
