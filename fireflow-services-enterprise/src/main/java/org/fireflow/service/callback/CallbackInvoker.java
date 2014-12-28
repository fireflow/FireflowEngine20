/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.service.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class CallbackInvoker  implements ServiceInvoker {
	private static Log log = LogFactory.getLog(CallbackInvoker.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#invoke(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding, java.lang.Object)
	 */
	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity)
			throws ServiceInvocationException {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#determineActivityCloseStrategy(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object)
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,
			ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding) {
		return ServiceInvoker.CLOSE_ACTIVITY;
	}


}
