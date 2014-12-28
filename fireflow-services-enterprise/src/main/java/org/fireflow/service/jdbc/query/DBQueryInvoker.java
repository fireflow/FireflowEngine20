package org.fireflow.service.jdbc.query;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.w3c.dom.Document;

public class DBQueryInvoker extends AbsServiceInvoker implements
		ServiceInvoker {

	public String getServiceType() {

		return null;
	}

	@Override
	protected Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svc,Object activity) throws ServiceInvocationException {
		DBQueryServiceDef dbQueryService = (DBQueryServiceDef)svc;
		
		DBQueryImpl dbQueryImpl = new DBQueryImpl();
		
		dbQueryImpl.setDBQueryService(dbQueryService);
		
		return dbQueryImpl;
	}
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName,
			Object[] params) throws ServiceInvocationException {
		Class[] paramTypes = new Class[1];
		paramTypes[0] = Document.class;
		return paramTypes;
	}
}
