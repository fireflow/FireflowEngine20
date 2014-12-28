/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.clientwidget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BrowserUtils {
	public enum BrowserType {
		IE10,IE9,IE8,IE7,IE6,Firefox,Safari,Chrome,Opera,Camino,Gecko
	}

	/**判断是否是IE*/
	public static boolean isIE(HttpServletRequest request) {
		return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 ? true : false;
	}

	/**
	 * 获取IE版本
	 * 
	 * @param request
	 * @return
	 */
	public static Double getIEversion(HttpServletRequest request) {
		Double version = 0.0;
		if (getBrowserType(request, "msie 10.0")) {
			version = 10.0;
		}
		if (getBrowserType(request, "msie 9.0")) {
			version = 9.0;
		}
		if (getBrowserType(request, "msie 8.0")) {
			version = 8.0;
		}
		if (getBrowserType(request, "msie 7.0")) {
			version = 7.0;
		}
		if (getBrowserType(request, "msie 6.0")) {
			version = 6.0;
		}
		return version;
	}

	/**
	 * 获取浏览器类型
	 * 
	 * @param request
	 * @return
	 */
	public static BrowserType getBrowserType(HttpServletRequest request) {
		BrowserType browserType = null;
		if (getBrowserType(request, "msie 10.0")) {
			browserType = BrowserType.IE9;
		}
		if (getBrowserType(request, "msie 9.0")) {
			browserType = BrowserType.IE9;
		}
		if (getBrowserType(request, "msie 8.0")) {
			browserType = BrowserType.IE8;
		}
		if (getBrowserType(request, "msie 7.0")) {
			browserType = BrowserType.IE7;
		}
		if (getBrowserType(request, "msie 6.0")) {
			browserType = BrowserType.IE6;
		}
		if (getBrowserType(request, "Firefox")) {
			browserType = BrowserType.Firefox;
		}
		if (getBrowserType(request, "Safari")) {
			browserType = BrowserType.Safari;
		}
		if (getBrowserType(request, "Chrome")) {
			browserType = BrowserType.Chrome;
		}
		if (getBrowserType(request, "Opera")) {
			browserType = BrowserType.Opera;
		}
		if (getBrowserType(request, "Camino")) {
			browserType = BrowserType.Camino;
		}
		return browserType;
	}

	private static boolean getBrowserType(HttpServletRequest request, String brosertype) {
		return request.getHeader("USER-AGENT").toLowerCase().indexOf(brosertype.toLowerCase()) > 0 ? true : false;
	}
	private final static String IE10 = "MSIE 10.0";
	private final static String IE9 = "MSIE 9.0";
	private final static String IE8 = "MSIE 8.0";
	private final static String IE7 = "MSIE 7.0";
	private final static String IE6 = "MSIE 6.0";
	private final static String MAXTHON = "Maxthon";
	private final static String QQ = "QQBrowser";
	private final static String GREEN = "GreenBrowser";
	private final static String SE360 = "360SE";
	private final static String FIREFOX = "Firefox";
	private final static String OPERA = "Opera";
	private final static String CHROME = "Chrome";
	private final static String SAFARI = "Safari";
	private final static String OTHER = "其它";

	public static String checkBrowse(HttpServletRequest request) {
		String userAgent=request.getHeader("USER-AGENT");
		if (regex(OPERA, userAgent))
			return OPERA;
		if (regex(CHROME, userAgent))
			return CHROME;
		if (regex(FIREFOX, userAgent))
			return FIREFOX;
		if (regex(SAFARI, userAgent))
			return SAFARI;
		if (regex(SE360, userAgent))
			return SE360;
		if (regex(GREEN, userAgent))
			return GREEN;
		if (regex(QQ, userAgent))
			return QQ;
		if (regex(MAXTHON, userAgent))
			return MAXTHON;
		if (regex(IE10, userAgent))
			return IE10;
		if (regex(IE9, userAgent))
			return IE9;
		if (regex(IE8, userAgent))
			return IE8;
		if (regex(IE7, userAgent))
			return IE7;
		if (regex(IE6, userAgent))
			return IE6;
		return OTHER;
	}

	private static boolean regex(String regex, String str) {
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		return m.find();
	}

}
