package org.fireflow.service.webservice;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.w3c.dom.Document;


public class WebServiceCallerInvoker extends AbsServiceInvoker {

	public String getServiceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svc,Object activity) throws ServiceInvocationException {
		WebServiceDef ws = (WebServiceDef)svc;
		
		WebServiceCallerImpl callerImpl = new WebServiceCallerImpl();
		callerImpl.setWebService(ws);
		
		return callerImpl;
	}
	
	protected String getOperationName(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding)throws ServiceInvocationException{
		return "callWebService";
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
