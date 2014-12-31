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
package org.fireflow.engine.entity.runtime.impl;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.runtime.ProcessInstance;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="processInstanceHistory")
@XmlType(name="processInstanceHistoryType")
@XmlAccessorType(XmlAccessType.FIELD)

//@Table("T_FF_HIS_PROCESS_INSTANCE")
public class ProcessInstanceHistory extends AbsProcessInstance implements
		ProcessInstance, Serializable {


}
