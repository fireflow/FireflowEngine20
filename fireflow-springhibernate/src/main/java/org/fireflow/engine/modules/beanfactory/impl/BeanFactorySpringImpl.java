/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.beanfactory.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
/**
 * 用Spring 的IOC容器作为Fire Workflow 的BeanFactory
 * @author 非也，nychen2000@163.com
 */
public class BeanFactorySpringImpl extends AbsEngineModule implements BeanFactory,BeanFactoryAware {
	Log log = LogFactory.getLog(BeanFactorySpringImpl.class);
	
	org.springframework.beans.factory.BeanFactory springBeanFactory = null;


    /**
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
		if (beanName == null) {
			return null;
		}

		Object bean = springBeanFactory.getBean(beanName);
		return bean;
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

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(org.springframework.beans.factory.BeanFactory arg0) throws BeansException {
        springBeanFactory = arg0;
    }
}
