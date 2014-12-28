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
package org.fireflow.engine.entity.config;

import org.fireflow.engine.entity.WorkflowEntity;
import org.nutz.dao.entity.annotation.Table;

/**
 * FireWorkflow引擎设置参数
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */


public interface FireflowConfig extends WorkflowEntity {
	public static final String ROOT_CATEGORY_ID = "ALL_CONFIG_CATEGORIES";
	public static final String ROOT_CATEGORY_VALUE = "ALL CONFIG CATEGORIES";
	public static final String ROOT_CATEGORY_NAME = "所有的配置参数";//TODO 待国际化
	
	//下面是内置的参数类别的Id
	public static final String CATEGORY_BIZ_TYPE = "BIZ_TYPE";//业务类别
	public static final String CONFIG_ALL_BIZ = "ALL_BIZ";//所有业务
	
	
	
	/**
	 * 内部Id
	 */
	public String getId();
	
	/**
	 * 参数Id
	 * @return
	 */
	public String getConfigId();
	
	/**
	 * 参数名称
	 * @return
	 */
	public String getConfigName();
	
	/**
	 * 参数值
	 * @return
	 */
	public String getConfigValue();
	
	public String getDescription();
	
	/**
	 * 参数的类别Id，即字典类别
	 * @return
	 */
	public String getCategoryId();

	/**
	 * 父id
	 * @return
	 */
	public String getParentConfigId();
	
}
