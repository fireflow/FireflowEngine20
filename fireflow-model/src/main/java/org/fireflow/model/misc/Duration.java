/*--

 Copyright (C) 2002-2003 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The names "OBE" and "Open Business Engine" must not be used to
    endorse or promote products derived from this software without prior
    written permission.  For written permission, please contact
    me@anthonyeden.com.

 4. Products derived from this software may not be called "OBE" or
    "Open Business Engine", nor may "OBE" or "Open Business Engine"
    appear in their name, without prior written permission from
    Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on OBE, please see <http://www.openbusinessengine.org/>.

 */
package org.fireflow.model.misc;

import java.io.Serializable;

/**
 * @author Anthony Eden 
 * Updated by nychen2000
 * 时间间隔
 */
@SuppressWarnings("serial")
public class Duration implements Serializable {

    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";
    public static final String WEEK = "WEEK";//    private static final Log log = LogFactory.getLog(Duration.class);
    private int value;
    private String unit;
    private boolean isBusinessTime = true;

    /**
     * 创建时间间隔对象
     * @param value 时间值
     * @param unit 时间单位
     */
    public Duration(int value, String unit) {
        this.value = value;
        this.unit = unit;
    }
    
    /**
     * 获取时间值
     * @return
     */
    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        value = v;
    }

    /**
     * 获取时间单位
     * @return
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置时间单位
     * @param u
     */
    public void setUnit(String u) {
        unit = u;
    }

    /**
     * 获取时间单位，如果时间单位为null，则返回defaultUnit
     * @param defaultUnit
     * @return
     */
    public String getUnit(String defaultUnit) {
        if (unit == null) {
            return defaultUnit;
        } else {
            return unit;
        }
    }

    /**
     * 获取换算成毫秒的时间间隔值
     * @param defaultUnit
     * @return
     */
    public long getDurationInMilliseconds(String defaultUnit) {
        int value = getValue();
        String unit = getUnit(defaultUnit);
        if (value == 0) {
            return value;
        } else {
            long duration = value * toMilliseconds(unit);
            return duration;
        }
    }

    public long toMilliseconds(String unit) {
        if (unit == null) {
            return 0l;
        } else if (unit.equals(SECOND)) {
            return 1 * 1000l;
        } else if (unit.equals(MINUTE)) {
            return 60 * 1000l;
        } else if (unit.equals(HOUR)) {
            return 60 * 60 * 1000l;
        } else if (unit.equals(DAY)) {
            return 24 * 60 * 60 * 1000l;
        } else if (unit.equals(MONTH)) {
            return 30 * 24 * 60 * 60 * 1000l;
        } else if (unit.equals(YEAR)) {
            return 365 * 30 * 24 * 60 * 60 * 1000l;
        } else if (unit.equals(WEEK)) {
            return 7 * 24 * 60 * 60 * 1000l;
        } else {
            return 0l;
        }
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(value);
        if (unit != null) {
            buffer.append(unit);
        }
        return buffer.toString();
    }

    /**
     * 时间间隔是否指工作时间
     * @return
     */
    public boolean isBusinessTime() {
        return isBusinessTime;
    }

    /**
     * 设置时间间隔的属性，isBusinessTime==true表示时间间隔指工作时间
     * @param isBusinessDay
     */
    public void setBusinessTime(boolean isBusinessDay) {
        this.isBusinessTime = isBusinessDay;
    }

}