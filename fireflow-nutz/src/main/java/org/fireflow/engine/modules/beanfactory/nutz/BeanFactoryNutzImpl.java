package org.fireflow.engine.modules.beanfactory.nutz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.nutz.ioc.Ioc;

public class BeanFactoryNutzImpl  extends AbsEngineModule implements BeanFactory {
	Log log = LogFactory.getLog(BeanFactoryNutzImpl.class);
	
	private Ioc ioc = null;

	public Object getBean(String beanName) {
		return ioc.get(null, beanName);
	}

    public Object createBean(String javaClassName){
		Class clz = null;
		try {
			clz = Class.forName(javaClassName);
			return clz.newInstance();
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException", e);
		} catch (InstantiationException e) {
			log.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
		}
		
		return null;
    }

	public Ioc getIoc() {
		return ioc;
	}

	public void setIoc(Ioc ioc) {
		this.ioc = ioc;
		
	}
    
    
}
