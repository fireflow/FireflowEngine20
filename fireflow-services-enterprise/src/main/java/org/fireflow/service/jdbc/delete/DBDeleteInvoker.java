package org.fireflow.service.jdbc.delete;


import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.w3c.dom.Document;

public class DBDeleteInvoker extends AbsServiceInvoker {

	@Override
	protected Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svcDef,Object activity) throws ServiceInvocationException {
		DBDeleteServiceDef service = (DBDeleteServiceDef)svcDef;
		DBDeleteImpl deleteImpl = new DBDeleteImpl();
		deleteImpl.setDBDeleteService(service);
		return deleteImpl;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName,
			Object[] params) throws ServiceInvocationException {
		Class[] paramTypes = new Class[1];
		paramTypes[0] = Document.class;
		return paramTypes;
	}

}
