package org.fireflow.engine.invocation;

import java.util.Properties;


public interface Message<T> {
	public Properties getHeaders();
	public T getPayload();	
}
