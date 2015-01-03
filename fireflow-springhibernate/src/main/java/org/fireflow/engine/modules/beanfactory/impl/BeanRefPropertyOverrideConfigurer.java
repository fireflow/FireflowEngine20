/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.engine.modules.beanfactory.impl;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class BeanRefPropertyOverrideConfigurer extends PropertyOverrideConfigurer  {

	
	private static final String BEAN_REFERENCE_PREFIX = "bean-ref:";

	/**
	 * Apply the given property value to the corresponding bean.
	 */
	protected void applyPropertyValue(ConfigurableListableBeanFactory factory,
			String beanName, String property, String value) {
		if (value.startsWith(BEAN_REFERENCE_PREFIX)) {
			String referencedBean = value.substring(BEAN_REFERENCE_PREFIX
					.length());
			applyBeanReferencePropertyValue(factory, beanName, property,
					referencedBean);
		} else {
			super.applyPropertyValue(factory, beanName, property, value);
		}
	}

	private void applyBeanReferencePropertyValue(
			ConfigurableListableBeanFactory factory, String beanName,
			String property, String referencedBean) {
		BeanDefinition bd = factory.getBeanDefinition(beanName);
		while (bd.getOriginatingBeanDefinition() != null) {
			bd = bd.getOriginatingBeanDefinition();
		}
		
		RuntimeBeanReference ref = new RuntimeBeanReference(referencedBean);
		//Object obj = factory.getBean(referencedBean);

		//bd.getPropertyValues().addPropertyValue(property, obj);
		bd.getPropertyValues().addPropertyValue(property, ref);
	}

}
