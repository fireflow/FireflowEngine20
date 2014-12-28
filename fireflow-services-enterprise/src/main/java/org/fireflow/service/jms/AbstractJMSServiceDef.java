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
package org.fireflow.service.jms;

import org.fireflow.model.servicedef.impl.AbstractServiceDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class AbstractJMSServiceDef extends AbstractServiceDef {
	private String jndiInitialContextFactory = null;//JNDI 初始化工厂
	private String jndiAddress = null;//JNDI查询地址
	private String connectionFactoryJndiName = null;//连接工厂的jndi路径
	private String destinationJndiName = null;//destination的jndi路径
	
	
}
