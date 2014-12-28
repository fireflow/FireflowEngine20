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
package org.fireflow.model.servicedef;

import java.util.Map;

import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Types;
/**
 * 
 * @author 非也
 * @version 2.0
 */
public interface ServiceDef extends ModelElement{
	/**
	 * 业务类别
	 * @return
	 */
	public String getBizCategory();
	
	
	/**
	 * 服务版本号
	 * @return
	 */
	public String getVersion();
	
	
	/**
	 * 
	 */
	public String getTargetNamespaceUri();
	
	/**
	 * 返回类型定义
	 */
	public XmlSchemaCollection getXmlSchemaCollection();

	
	/**
	 * 代表该服务的接口
	 * @return
	 */
	public InterfaceDef getInterface();
	
	/**
	 * 类型定义
	 * @return
	 */
	public Types getTypeDeclarations();
	/**
	 * 
	 * @return
	 */
	public String getInvokerBeanName();

	
	public String getInvokerClassName();

	public String getParserClassName();
	
}
