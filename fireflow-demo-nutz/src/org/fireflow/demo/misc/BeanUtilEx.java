package org.fireflow.demo.misc;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanUtilEx extends BeanUtils {
	 private static Map cache = new HashMap();  
	  private static Log logger = LogFactory.getFactory().getInstance(BeanUtilEx.class);  
	  
	  private BeanUtilEx() {  
	  }  
	  
	  static {  
	    //注册sql.date的转换器，即允许BeanUtils.copyProperties时的源目标的sql类型的值允许为空  
	    ConvertUtils.register(new DateConvertor(), java.util.Date.class);  
	    //ConvertUtils.register(new SqlTimestampConverter(), java.sql.Timestamp.class);  
	    //注册util.date的转换器，即允许BeanUtils.copyProperties时的源目标的util类型的值允许为空  
	    ConvertUtils.register(new DateConvertor(), java.util.Date.class);  
	  }  
	  
	  public static void copyProperties(Object target, Object source) throws  
	      InvocationTargetException, IllegalAccessException {  
	    //update bu zhuzf at 2004-9-29  
	    //支持对日期copy  
	  
	    org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);  
	  
	  }  
	  

}
class DateConvertor implements Converter {
	private static String dateFormatStr = "yyyy/MM/dd";
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormatStr);
	
	private static String dateLongFormatStr = dateFormatStr+" HH:mm:ss";
	private static SimpleDateFormat dateTimeLongFormat = new SimpleDateFormat(dateLongFormatStr);

	public Object convert(Class arg0, Object arg1) {

		return arg1;

	}
	
	public static String formatDateTime(Object obj) {
		if (obj != null)
			return dateTimeFormat.format(obj);
		else
			return "";
	}
	
	public static String formatLongDateTime(Object obj) {
		if (obj != null)
			return dateTimeLongFormat.format(obj);
		else
			return "";
	}

}

