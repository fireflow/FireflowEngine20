/**
 * Copyright 2007-2011 非也
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
package org.firesoa.common.schema;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;


/**
 * 维护java类型与XSD类型，java.sql.Types类型与XSD类型之间的映射
 * @author 非也 nychen2000@163.com
 *
 */
public class DataMapper {
    public static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
    
	public static final Map<Integer,String> SQLTYPES_2_XSD_LOCALNAME_MAP = new HashMap<Integer,String>();
	public static final Map<Integer,QName> SQLTYPES_2_XSD_QNAME_MAP = new HashMap<Integer,QName>();

	public static final Map<String, String> XSD_2_JAVA_MAP;
	public static final Map<String, Class<?>> HOLDER_TYPES_MAP;
	static{
		//初始化java.sql.Types类型与XSD类型之间的映射
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.CHAR , "string");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.VARCHAR, "string");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.LONGVARCHAR, "string");
		//SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.NCHAR , "string");
		//SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.NVARCHAR, "string");
		//SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.LONGNVARCHAR, "string");
		
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.NUMERIC, "decimal");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.DECIMAL, "decimal");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.BOOLEAN , "boolean");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.BIT , "boolean");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.TINYINT , "byte");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.SMALLINT , "short");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.INTEGER , "int");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.BIGINT , "long");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.REAL , "float");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.FLOAT , "double");		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.DOUBLE , "double");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.BINARY , "base64Binary");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.VARBINARY , "base64Binary");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.LONGVARBINARY , "base64Binary");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.BLOB , "base64Binary");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.CLOB , "base64Binary");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.DATE , "date");
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.TIME , "time");
		
		SQLTYPES_2_XSD_LOCALNAME_MAP.put(java.sql.Types.TIMESTAMP , "dateTime");
		
	}
	
	
	static {
		QName qstring = new QName (URI_2001_SCHEMA_XSD, "string");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.CHAR ,qstring);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.VARCHAR, qstring);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.LONGVARCHAR, qstring);
		//SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.NCHAR , qstring);
		//SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.NVARCHAR, qstring);
		//SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.LONGNVARCHAR, qstring);
		
		QName qdecimal = new QName(URI_2001_SCHEMA_XSD,"decimal");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.NUMERIC, qdecimal);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.DECIMAL, qdecimal);
		
		QName qboolean = new QName(URI_2001_SCHEMA_XSD,"boolean");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.BOOLEAN , qboolean);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.BIT , qboolean);
		
		QName qbyte = new QName(URI_2001_SCHEMA_XSD,"byte");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.TINYINT , qbyte);
		
		QName qshort = new QName(URI_2001_SCHEMA_XSD,"short");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.SMALLINT , qshort);
		
		QName qint = new QName(URI_2001_SCHEMA_XSD,"int");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.INTEGER , qint);
		
		QName qlong = new QName(URI_2001_SCHEMA_XSD,"qlong");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.BIGINT , qlong);
		
		QName qfloat = new QName(URI_2001_SCHEMA_XSD,"float");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.REAL , qfloat);
		
		QName qdouble = new QName(URI_2001_SCHEMA_XSD,"double");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.FLOAT , qdouble);		
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.DOUBLE , qdouble);
		
		QName qbase64Binary =  new QName(URI_2001_SCHEMA_XSD,"base64Binary");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.BINARY , qbase64Binary);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.VARBINARY ,qbase64Binary);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.LONGVARBINARY , qbase64Binary);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.BLOB , qbase64Binary);
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.CLOB , qbase64Binary);
		
		QName qdate = new QName(URI_2001_SCHEMA_XSD,"date");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.DATE , qdate);
		
		QName qtime = new QName(URI_2001_SCHEMA_XSD,"time");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.TIME , qtime);
		
		QName qdateTime = new QName(URI_2001_SCHEMA_XSD,"dateTime");
		SQLTYPES_2_XSD_QNAME_MAP.put(java.sql.Types.TIMESTAMP , qdateTime);
	}
	
    static {
        XSD_2_JAVA_MAP = new HashMap<String, String>();        
        XSD_2_JAVA_MAP.put("string", "java.lang.String");
        XSD_2_JAVA_MAP.put("integer", "java.math.BigInteger");
        XSD_2_JAVA_MAP.put("int", "int");
        XSD_2_JAVA_MAP.put("long", "long");
        XSD_2_JAVA_MAP.put("short", "short");
        XSD_2_JAVA_MAP.put("decimal", "java.math.BigDecimal");
        XSD_2_JAVA_MAP.put("float", "float");
        XSD_2_JAVA_MAP.put("double", "double");
        XSD_2_JAVA_MAP.put("boolean", "boolean");
        XSD_2_JAVA_MAP.put("byte", "byte");
        XSD_2_JAVA_MAP.put("QName", "javax.xml.namespace.QName");
        XSD_2_JAVA_MAP.put("dateTime", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("base64Binary", "byte[]");
        XSD_2_JAVA_MAP.put("hexBinary", "byte[]");
        XSD_2_JAVA_MAP.put("unsignedInt", "long");
        XSD_2_JAVA_MAP.put("unsignedShort", "short");
        XSD_2_JAVA_MAP.put("unsignedByte", "byte");
        XSD_2_JAVA_MAP.put("time", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("date", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("gYear", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("gYearMonth", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("gMonth", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("gMonthDay", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("gDay", "javax.xml.datatype.XMLGregorianCalendar");
        XSD_2_JAVA_MAP.put("duration", "javax.xml.datatype.Duration");
        XSD_2_JAVA_MAP.put("NOTATION", "javax.xml.namespace.QName");
        XSD_2_JAVA_MAP.put("string", "java.lang.String");
        
        HOLDER_TYPES_MAP = new HashMap<String, Class<?>>();
        HOLDER_TYPES_MAP.put("int", java.lang.Integer.class);
        HOLDER_TYPES_MAP.put("long", java.lang.Long.class);
        HOLDER_TYPES_MAP.put("short", java.lang.Short.class);
        HOLDER_TYPES_MAP.put("float", java.lang.Float.class);
        HOLDER_TYPES_MAP.put("double", java.lang.Double.class);
        HOLDER_TYPES_MAP.put("boolean", java.lang.Boolean.class);
        HOLDER_TYPES_MAP.put("byte", java.lang.Byte.class);
    }
}
