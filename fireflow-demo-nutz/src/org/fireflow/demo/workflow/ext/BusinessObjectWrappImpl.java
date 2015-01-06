package org.fireflow.demo.workflow.ext;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.nutz.PersistenceServiceNutzImpl;
import org.fireflow.engine.modules.script.BusinessObjectWrapper;


public class BusinessObjectWrappImpl implements BusinessObjectWrapper {
	String bizClzName = null;
	String bizId = null;
	
	

	public String getBizClzName() {
		return bizClzName;
	}



	public void setBizClzName(String bizClzName) {
		this.bizClzName = bizClzName;
	}



	public String getBizId() {
		return bizId;
	}



	public void setBizId(String bizId) {
		this.bizId = bizId;
	}



	@Override
	public Object resolveBusinessObject(RuntimeContext arg0,
			ProcessInstance arg1, PersistenceService arg2) {
		try{
			String billCode = bizId;
			if (billCode==null || billCode.trim().equals("")){
				billCode = arg1.getBizId();
			}
			
			Class clz = Class.forName(bizClzName);
			
			PersistenceServiceNutzImpl p = (PersistenceServiceNutzImpl)arg2;
			Object obj = p.getDao().fetch(clz, billCode);
			
			return obj;
		}catch(Exception e){
			throw new RuntimeException(e);
		}

	}

}
