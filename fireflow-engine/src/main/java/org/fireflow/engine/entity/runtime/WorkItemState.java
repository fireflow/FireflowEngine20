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
package org.fireflow.engine.entity.runtime;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.EntityState;

/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="workItemStateType")
@XmlEnum(int.class)
public enum WorkItemState  implements EntityState{
	/**
	 * 活的状态
	 */
	@XmlEnumValue("-1")
	ALIVE(-1),
	
	/**
	 * 初始化状态
	 */
	@XmlEnumValue("0")
	INITIALIZED(0),

	/**
	 * 运行状态
	 */
	@XmlEnumValue("1")
	RUNNING(1),
	
	
	/**
	 * 错误处理中
	 */
	@XmlEnumValue("5")
	FAULTING(5),
	
	/**
	 * 补偿操作中
	 */
	@XmlEnumValue("7")
	COMPENSATING(7),
	
	/**
	 * 活动属性和非活动属性的分界线
	 */
	@XmlEnumValue("10")
	DELIMITER(10),
	
	/**
	 * 已经结束
	 */
	@XmlEnumValue("11")
	COMPLETED(11),
	
	/**
	 * 因错误而终止
	 */
	@XmlEnumValue("15")
	FAULTED(15),
	
	/**
	 * 被取消
	 */
	@XmlEnumValue("16")
	ABORTED(16),
	/**
	 * 被补偿
	 */
	@XmlEnumValue("17")
	COMPENSATED(17),
	

	
	/**
	 * 退签收，用于WorkItem
	 */
	@XmlEnumValue("31")
	DISCLAIMED(31),
	
	/**
	 * 拒收
	 */
	@XmlEnumValue("33")
	REJECTED(33),
	
	/**
	 * 被委派，被加签
	 */
	@XmlEnumValue("35")
	REASSIGNED(35),
	
	
	/**
	 * 表示该工作项是抄送、知会性质的
	 */
	@XmlEnumValue("99")
	READONLY(99)
	
	
	;
	
	
	private int value = 0;
	private WorkItemState(int value){
		this.value = value;
	}
	
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
	public int getValue(){
		return value;
	}
	
	public int value(){
		return this.value;
	}
    public static WorkItemState fromValue(int v) {
        for (WorkItemState c: WorkItemState.values()) {
            if (c.value==v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
	public static WorkItemState valueOf(Integer v){
		WorkItemState[] states =  WorkItemState.values();
		for (WorkItemState state : states){
			if (state.getValue()== v){
				return state;
			}
		}
		return null;
	}		
}
