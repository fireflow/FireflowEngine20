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
package org.fireflow.demo.misc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.fireflow.demo.security.bean.User;
import org.nutz.dao.Cnd;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class Utils {
	private static final String algorithmName = "SHA-256";  
	private static final int hashIterations = 2;
	
	public static  String exceptionStackToString(Throwable e){
		if (e==null)return"";
		java.io.ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(out);
		
		e.printStackTrace(stream);
		return out.toString();
	}
	
	public static User getCurrentUser(){
		return (org.fireflow.demo.security.bean.User)SecurityUtils.getSubject().getPrincipal();
	}
	
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request,String name){
	    Map<String,Cookie> cookieMap = ReadCookieMap(request);
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    }   
	}
	 
	 
	 
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){  
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	            cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}	
	
	/**
	 * 将用户的密码初始化为123456，同时设置相应的salt
	 * @param u 用户对象，
	 * @param mustChangePwd 初始化密码后，是否强制该用户修改密码
	 * @return 新生成的密码的明文
	 */
	public static String initUserPassword(User u,boolean mustChangePwd){

		String username = u.getLoginName();  
		String password = "123456";  
		String salt1 = username;  
		String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();  

		SimpleHash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);  
		String encodedPassword = hash.toHex();  
		
		u.setPwd(encodedPassword);
		u.setSalt(salt2);
		u.setMustChangePwd(mustChangePwd);
		
		return password;
		
	}
	
	/**
	 * 修改密码，传入用户对象，将新的密码明文转换成密文。
	 * @param u 用户对象
	 * @param newPwdPlainTxt 新密码的明文
	 * @return
	 */
	public static String encryptNewPassword(User u,String newPwdPlainTxt){
		String username = u.getLoginName();  
		String password = newPwdPlainTxt;  
		String salt1 = username;  
		String salt2 = u.getSalt();  
 

		SimpleHash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);  
		String encodedPassword = hash.toHex();  
		
		return encodedPassword;
	}
	
	/**
	 * 根据jtable的排序字符串组装Nutz的排序条件
	 * @param cnd
	 * @param orderByStr
	 */
	public static void makeJTableOrderBy(Cnd cnd,String orderByStr){
		if (orderByStr==null || orderByStr.equals(""))return;
		StringTokenizer tokenizer = new StringTokenizer(orderByStr,",");
		while(tokenizer.hasMoreTokens()){
			String orderItem = tokenizer.nextToken();
			int idx = orderItem.indexOf(" ");
			if (idx<0){
				continue;
			}
			String propertyName=orderItem.substring(0,idx);
			String sx = orderItem.substring(idx+1);
			
			if (sx!=null) sx = sx.trim();
			if("ASC".equalsIgnoreCase(sx)){
				cnd.asc(propertyName);
			}
			if ("DESC".equalsIgnoreCase(sx)){
				cnd.desc(propertyName);
			}
		}
	}
	
	/**
	 * 数字补零，返回一个字符串（比如num=12345,maxDigit=2,minDigits=3那么处理后返回345）。
	 * @param num 要补零的数字
	 * @param maxDigits 要返回的最大位数
	 * @param minDigits 要返回的最小位数（这两个以数值最大返回）
	 * @return
	 */
	public static String numberFormat(long num,int maxDigits,int minDigits){
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(maxDigits);
        //设置最小整数位数    
        nf.setMinimumIntegerDigits(minDigits);
		return nf.format(num);
	}
	
	/**
	 * 字符串补零，返回一个字符串（比如string=123,maxLength=4;那么处理后返回0123）。
	 * 如果字符串大于maxLength，直接返回，不再补零
	 * @param string 要补零的字符串
	 * @param maxLength 要返回的最大位数
	 * @return
	 */
	public static String stringFormatZero(String string,int maxLength){
		if(string.length() < maxLength){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i < (maxLength-string.length()); i++){
				sb.append("0");
			}
			sb.append(string);
			string = sb.toString();
		}
		return string;
	}
	
	
	public static void main(String[] args){
		
			System.out.println(Utils.stringFormatZero("", 6));
	}
	
	public static String convertArrayToString(String[] arr){
		if (arr==null)return "";
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<arr.length;i++){
			String s = arr[i];
			buf.append(s);
			if (i<arr.length-1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	
	public static String[] convertStringToArray(String s ){
		if (s==null)return new String[]{};
		StringTokenizer tokenizer = new StringTokenizer(s,",");
		List<String> arraylist = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()){
			String tmp = tokenizer.nextToken();
			arraylist.add(tmp);
		}
		return (String[])arraylist.toArray(new String[arraylist.size()]);
	}
	
	public static String changeExcelDateFormat(String excelDateStr,String pattern){
		String tempTime =  excelDateStr;

		long tempTimeLong = Long.valueOf(tempTime).intValue();  //将数字转化成long型 

		long ss = (tempTimeLong - 70 * 365 - 17 - 2) * 24 * 3600 * 1000;  //excel的那个数字是距离1900年的天数 
		                                                                          //java 是距离1970年天数，但是明明期间只有17个闰年 
		                                                                          //我尝试的结果要减19才能正确，原因不明 

		Date dss = new Date(ss);                              //用这个数字初始化一个Date 

		SimpleDateFormat formatter = new SimpleDateFormat(pattern); 

		String sss = formatter.format(dss);
		return sss;
	}

}
