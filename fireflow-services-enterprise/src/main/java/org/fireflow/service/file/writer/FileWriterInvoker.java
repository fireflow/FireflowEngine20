package org.fireflow.service.file.writer;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;

public class FileWriterInvoker extends AbsServiceInvoker {

	public String getServiceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svc,Object activity) throws ServiceInvocationException {
		FileWriteServiceDef service = (FileWriteServiceDef)svc;
		FileWriterImpl writer = new FileWriterImpl();
		writer.setFileWriteService(service);
		return writer;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName, Object[] params) {
		if (methodName.equals("writeBytesToFile")){
			Class[] paramTypes = new Class[]{String.class,byte[].class};
			return paramTypes;
		}else if (methodName.equals("writeStringToFile")){
			Class[] paramTypes = new Class[]{String.class,String.class};
			return paramTypes;
		}
		
		return null;
	}

	
}
