package org.fireflow.service.jdbc.insert;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;

public interface DBInsert {
	public void doInsert(Document valuesDoc)throws ServiceInvocationException;
}
