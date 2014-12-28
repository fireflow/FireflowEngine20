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
package org.fireflow.pvm.kernel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="operationContextNameType")
@XmlEnum(String.class)
public enum OperationContextName {
	NORMAL,
	FAULT,
//	CANCELLATION,//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	COMPENSATION;
	
}
