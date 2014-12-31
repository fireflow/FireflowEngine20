/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.calendar;

import java.util.Date;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.model.misc.Duration;


/**
 * 日历服务。一般情况下，业务系统继承 {@link org.fireflow.engine.modules.calendar.impl.CalendarServiceDefaultImpl CalendarServiceDefaultImpl}，
 * 覆盖isBusinessDay,getBusinessTime,getAverageWorkingHours三个方法即可。
 * 
 * 
 * @author 非也，nychen2000@163.com
 */
public interface CalendarService extends RuntimeContextAware,EngineModule{
	public static final String DAY_FORMAT = "yyyy-MM-dd";
	public static final String HOUR_FORMAT = "HH:mm"; //24时制

    /**
     * 在CalendarService的缺省实现 {@link org.fireflow.engine.modules.calendar.impl.CalendarServiceDefaultImpl CalendarServiceDefaultImpl}，周六周日都是非工作日，周一至周五为工作日。
     * 实际应用中，可以在数据库中建立一张非工作日表，将周末以及法定节假日录入其中，
     * 然后在该方法中读该表的数据来判断工作日和非工作日。
     * @param d
     * @return
     */
    public boolean isBusinessDay(Date d);
    
    /**
     * 返回某一天的工作时间。<br>     * 例如8:30-12:00 & 13:30-17:30 表示早上8点半到中午12点和下午1点半到5点半<<br>    * 例如9:00-12:00 表示这一天只有上午半天班。<b<br>   * 例如8:30-12:00 & 13:30-17:30 & 19:30-22:00， 表示这一天除了上午、下午之外还有晚班，晚班从19:30-22:00<br<br><br> * 
     * 注意:工作时间的格式必须符合"HH:mm"；如果某一天是休息日，直接返回null。
     * @param date
     * @return 返回参数所示日期的工作时间，如果这一天是休息日，直接返回null。
     */
    public String getBusinessTime(Date date);
    
    /**
     * 
     * 每天的工作时长，该返回值主要用于函数{@link #dateAfter(Date, Duration) dateAfter(Date, Duration)}的计算。<br><<br><br> 
     * 例如：如果{@link #getBusinessTime getBusinessTime(argDate)}的返回值是"8:30-12:00 & 13:30-17:30"，则每天的工作时间长度是7个半小时，
     * 那么本方法的返回值应该是7.5。因此本方法的返回值要和{@link #getBusinessTime(argDate) getBusinessTime(argDate)}要相匹配。<br><br<br><br>     * 如果每天工作时间不相同（例如周一至周五上全天班，周六上半天班），且对于流程超时设置不严格的情况下，可以返回-1。如果返回-1，
     * 则CalendarServiceDefaultImpl.dateAfter(Date fromDate, Duration duration)在计算时，采用简易的办法获得结果。
     * 
     * @return
     */
    public float getAverageWorkingHours();
    
    /**
     * 获得fromDate后相隔duration的某个日期。<br>
    <br>1：如果fromDate是2013-01-01 00:00:00，<strong>4天</strong>后的日期时间是2013-01-05 00:00:00。<br>
     <br>数duration的值如下:duration.value=4,duration.unit=DAY,duration.isBusinessTime=false;<br><br>
  <br><br> * 示例2：如果fromDate是2013-01-01 00:00:00， <strong>4个工作日</strong>后的日期时间是2013-01-08 00:00:00。
     * (假设周六周日是休息日，周一至周五是工作日)<br>
     * 此<br>ration的值如下:duration.value=4,duration.unit=DAY,<strong>duration.isBusinessTime=true;</strong><br><br>
     <br><br>在缺省实现缺省实现 {@link org.fireflow.engine.modules.calendar.impl.CalendarServiceDefaultImpl CalendarServiceDefaultImpl}中只区分工作日/自然日，工作时/自然时；
     * 其他都按照自然时间间隔计算。 这种实现方法已经满足绝大多数业务需求。
     * @param duration
     * @return
     */
    public Date dateAfter(Date fromDate, Duration duration);

    /**
     * 获得系统当前时间
     * @return
     */
    public Date getSysDate();
}
