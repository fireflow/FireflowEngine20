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
package org.fireflow.engine.invocation;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public enum TimerOperationName {
	TRIGGERED_ONLY_ONCE("org.fireflow.constants.timmer.TRIGGERED_ONLY_ONCE"),
	TRIGGERED_BY_REPEAT_COUNT("org.fireflow.constants.timmer.TRIGGERED_BY_REPEAT_COUNT"),
	TRIGGERED_BY_STARTTIME_ENDTIME("org.fireflow.constants.timmer.TRIGGERED_BY_STARTTIME_ENDTIME"),
	TRIGGERED_BY_CRON("org.fireflow.constants.timmer.TRIGGERED_BY_CRON");
	
	String value = null;
	private TimerOperationName(String v){
		value = v;
	}
	
	public String getValue(){
		return value;
	}
	
	public static TimerOperationName fromValue(String v){
		TimerOperationName[] values =  TimerOperationName.values();
		for (TimerOperationName tmp : values){
			if (tmp.getValue().equals(v)){
				return tmp;
			}
		}
		return null;
	}	
}
