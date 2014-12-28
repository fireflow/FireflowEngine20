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
package org.fireflow.engine.modules.script.functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类，
 * 
 * @author 非也
 * @version 2.0
 */
public class DateTimeUtil {
	private static DateTimeUtil dateTimeUtil = null;
	
	private DateTimeUtil(){
		
	}
	public static DateTimeUtil getInstance(){
		if (dateTimeUtil==null){
			dateTimeUtil = new DateTimeUtil();
		}
		return dateTimeUtil;
	}
	
	/**
	 * 返回iniDate后某个时间。
	 * @param initDate 起始时间
	 * @param value 间隔时间值
	 * @param unit 间隔时间单位，y/Y表示年，m/M表示月，d/D表示日，h/H表示时，mi/Mi/MI表示分钟，s表示秒
	 * @return
	 */
	public Date dateAfter(Date initDate,int value, String unit){
		if (initDate==null){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(initDate);
		int timeUnit = Calendar.SECOND;
		if (unit==null || unit.trim().equals("")){
			timeUnit = Calendar.SECOND;
		}
		else if (unit.trim().equalsIgnoreCase("y")){
			timeUnit = Calendar.YEAR;
		}
		else if (unit.trim().equalsIgnoreCase("m")){
			timeUnit = Calendar.MONTH;
		}else if (unit.trim().equalsIgnoreCase("d")){
			timeUnit = Calendar.DATE;
		}
		else if (unit.trim().equalsIgnoreCase("h")){
			timeUnit = Calendar.HOUR;
		}
		else if (unit.trim().equalsIgnoreCase("mi")){
			timeUnit = Calendar.MINUTE;
		}
		else if (unit.trim().equalsIgnoreCase("s")){
			timeUnit = Calendar.SECOND;
		}
		cal.add(timeUnit, value);
		return cal.getTime();
	}
	
	/**
	 * 返回iniDate前的某个时间。
	 * @param initDate 起始时间
	 * @param value 间隔时间值
	 * @param unit 间隔时间单位，y/Y表示年，m/M表示月，d/D表示日，h/H表示时，mi/Mi/MI表示分钟，s表示秒
	 * @return
	 */
	public Date dateBefore(Date initDate,int value, String unit){
		if (initDate==null){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(initDate);
		int timeUnit = Calendar.SECOND;
		if (unit==null || unit.trim().equals("")){
			timeUnit = Calendar.SECOND;
		}
		else if (unit.trim().equalsIgnoreCase("y")){
			timeUnit = Calendar.YEAR;
		}
		else if (unit.trim().equalsIgnoreCase("m")){
			timeUnit = Calendar.MONTH;
		}else if (unit.trim().equalsIgnoreCase("d")){
			timeUnit = Calendar.DATE;
		}
		else if (unit.trim().equalsIgnoreCase("h")){
			timeUnit = Calendar.HOUR;
		}
		else if (unit.trim().equalsIgnoreCase("mi")){
			timeUnit = Calendar.MINUTE;
		}
		else if (unit.trim().equalsIgnoreCase("s")){
			timeUnit = Calendar.SECOND;
		}
		cal.add(timeUnit, value);
		return cal.getTime();
	}
	
	public String format(Date d,String pattern){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(d);
	}
}
