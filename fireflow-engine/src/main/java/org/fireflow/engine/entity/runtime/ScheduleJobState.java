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
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlType(name="scheduleJobStateType")
@XmlEnum(int.class)
public enum ScheduleJobState  implements EntityState{
	/**
	 * 运行状态
	 */
	@XmlEnumValue("1")
	RUNNING(1),
	
	/**
	 * 调度器被暂停，但是仍然有效
	 * TODO 什么场景用到呢?
	 */
	@XmlEnumValue("3")
	SUSPENDED(3),
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
	 * 已经失效的，
	 */
	@XmlEnumValue("98")
	OUT_OF_DATE(98);
	
	private int value = 0;
	private ScheduleJobState(int value){
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
	
	public static ScheduleJobState valueOf(Integer v){
		ScheduleJobState[] states =  ScheduleJobState.values();
		for (ScheduleJobState state : states){
			if (state.getValue()== v){
				return state;
			}
		}
		return null;
	}	
	
	public int value(){
		return this.value;
	}
    public static ScheduleJobState fromValue(int v) {
        for (ScheduleJobState c: ScheduleJobState.values()) {
            if (c.value==v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
}
