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
@XmlType(name="processInstanceStateType")
@XmlEnum(int.class)
public enum ProcessInstanceState  implements EntityState{
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
	 * 取消处理中
	 */
	@XmlEnumValue("6")
	ABORTING(6),
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
	COMPENSATED(17);

	
	
	
	private int value = 0;
	private ProcessInstanceState(int value){
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
		return this.value;
	}
	
	public int value(){
		return this.value;
	}
	
    public static ProcessInstanceState fromValue(int v) {
        for (ProcessInstanceState c: ProcessInstanceState.values()) {
            if (c.value==v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
	
	public static ProcessInstanceState valueOf(Integer v){
		ProcessInstanceState[] states =  ProcessInstanceState.values();
		for (ProcessInstanceState state : states){
			if (state.getValue()== v){
				return state;
			}
		}
		return null;
	}
}
