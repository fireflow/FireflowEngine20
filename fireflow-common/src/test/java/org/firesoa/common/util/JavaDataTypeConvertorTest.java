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
package org.firesoa.common.util;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class JavaDataTypeConvertorTest {
	public static void main(String[] args)throws ClassNotFoundException{
		Date d = new Date();
		
		Class clz = java.util.Date.class;
		
		Class clz2 = Class.forName("java.util.Date");

		System.out.println(clz.isInstance(d));
		System.out.println(clz2.isInstance(d));
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isTypeValueMatch(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testIsTypeValueMatch() throws ClassNotFoundException{
		//int类型
		boolean b = JavaDataTypeConvertor.isTypeValueMatch("int", 500);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("int", -500);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Integer", 5);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Integer", -5);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Integer", -5.5);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("int", 6l);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("int","5");
		Assert.assertFalse(b);
		
		//short
		short st = 6;
		b = JavaDataTypeConvertor.isTypeValueMatch("short", st);
		Assert.assertTrue(b);
		
		st = -6;
		b = JavaDataTypeConvertor.isTypeValueMatch("short", st);
		Assert.assertTrue(b);
		
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Short", -5.5);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("short", 6l);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("short","5");
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("short",3276700);
		Assert.assertFalse(b);
		
		//Byte
		byte bt = 6;
		b = JavaDataTypeConvertor.isTypeValueMatch("byte", bt);
		Assert.assertTrue(b);
		
		bt = -5;
		b = JavaDataTypeConvertor.isTypeValueMatch("byte", bt);
		Assert.assertTrue(b);
		
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Byte", -5.5);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("byte", 6l);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("byte","5");
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("byte",129);
		Assert.assertFalse(b);
		
		//Long
		b = JavaDataTypeConvertor.isTypeValueMatch("long", 6l);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("long", -6l);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Long", 5l);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Long", -5l);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Long", -5.5);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("long", 6d);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("long","5");
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("long",129);
		Assert.assertFalse(b);
		
		//Float
		b = JavaDataTypeConvertor.isTypeValueMatch("float", 6f);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("float", -6f);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Float", 5.1f);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Float", -5.1f);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Float", -5.5f);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("float", 6d);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("float","5");
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("float",129);
		Assert.assertFalse(b);
		
		//Double
		b = JavaDataTypeConvertor.isTypeValueMatch("double", 6d);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("double", -6d);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Double", 5.1d);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Double", -5.1d);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Double", -5.5d);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("double", 6);
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("double","5");
		Assert.assertFalse(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("double",129);
		Assert.assertFalse(b);
		
		//boolean
		b = JavaDataTypeConvertor.isTypeValueMatch("boolean", true);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Boolean", false);
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Boolean", "false");
		Assert.assertFalse(b);
		
		//chart
		b = JavaDataTypeConvertor.isTypeValueMatch("char", 'a');
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Character", 'b');
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.lang.Character", "false");
		Assert.assertFalse(b);
		
		//Date
		b = JavaDataTypeConvertor.isTypeValueMatch("java.util.Date", new Date());
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isTypeValueMatch("java.util.Date", "abc");
		Assert.assertFalse(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isPrimaryDataType(java.lang.String)}.
	 */
	@Test
	public void testIsPrimaryDataType() {
		boolean b = JavaDataTypeConvertor.isPrimaryDataType("int");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Integer");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("short");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Short");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("byte");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Byte");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("float");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Float");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("double");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Double");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("long");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Long");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("boolean");
		Assert.assertTrue(b);
		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.Boolean");
		Assert.assertTrue(b);
		

		b = JavaDataTypeConvertor.isPrimaryDataType("java.lang.String");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isPrimaryDataType("java.util.Date");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isPrimaryObject(java.lang.Object)}.
	 */
	@Test
	public void testIsPrimaryObject() {
		boolean b = JavaDataTypeConvertor.isPrimaryObject(5);
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isChar(java.lang.String)}.
	 */
	@Test
	public void testIsChar() {
		boolean b = JavaDataTypeConvertor.isChar("char");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isChar("java.lang.Character");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isBoolean(java.lang.String)}.
	 */
	@Test
	public void testIsBoolean() {
		boolean b = JavaDataTypeConvertor.isBoolean("boolean");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isBoolean("java.lang.Boolean");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isDate(java.lang.String)}.
	 */
	@Test
	public void testIsDate() {
		boolean b = JavaDataTypeConvertor.isDate("java.util.Date");
		Assert.assertTrue(b);

	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isString(java.lang.String)}.
	 */
	@Test
	public void testIsString() {
		boolean b = JavaDataTypeConvertor.isString("java.lang.String");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isShort(java.lang.String)}.
	 */
	@Test
	public void testIsShort() {
		boolean b = JavaDataTypeConvertor.isShort("short");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isShort("java.lang.Short");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isLong(java.lang.String)}.
	 */
	@Test
	public void testIsLong() {
		boolean b = JavaDataTypeConvertor.isLong("long");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isLong("java.lang.Long");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isInt(java.lang.String)}.
	 */
	@Test
	public void testIsInt() {
		boolean b = JavaDataTypeConvertor.isInt("int");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isInt("java.lang.Integer");
		Assert.assertTrue(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isByte(java.lang.String)}.
	 */
	@Test
	public void testIsByte() {
		boolean b = JavaDataTypeConvertor.isByte("byte");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isByte("java.lang.Byte");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isByte("java.lang.Integer");
		Assert.assertFalse(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isFloat(java.lang.String)}.
	 */
	@Test
	public void testIsFloat() {
		boolean b = JavaDataTypeConvertor.isFloat("float");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isFloat("java.lang.Float");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isFloat("java.lang.Integer");
		Assert.assertFalse(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#isDouble(java.lang.String)}.
	 */
	@Test
	public void testIsDouble() {
		boolean b = JavaDataTypeConvertor.isDouble("double");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isDouble("java.lang.Double");
		Assert.assertTrue(b);
		
		b = JavaDataTypeConvertor.isDouble("java.lang.Integer");
		Assert.assertFalse(b);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToJavaObject(javax.xml.namespace.QName, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConvertToJavaObject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#dataTypeConvert(javax.xml.namespace.QName, java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testDataTypeConvert() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToByte(java.lang.Object)}.
	 */
	@Test
	public void testConvertToByte() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToInt(java.lang.Object)}.
	 */
	@Test
	public void testConvertToInt() {
		int i = JavaDataTypeConvertor.convertToInt(5);
		Assert.assertEquals(5, i);
		i = JavaDataTypeConvertor.convertToInt(-5);
		Assert.assertEquals(-5, i);
		
		i = JavaDataTypeConvertor.convertToInt("5");
		Assert.assertEquals(5, i);
		
		i = JavaDataTypeConvertor.convertToInt("-5");
		Assert.assertEquals(-5, i);
		
		i = JavaDataTypeConvertor.convertToInt(5.6f);
		Assert.assertEquals(5, i);
		
		i = JavaDataTypeConvertor.convertToInt(-5.6f);
		Assert.assertEquals(-5, i);
		
		i = JavaDataTypeConvertor.convertToInt(5.6d);
		Assert.assertEquals(5, i);
		
		i = JavaDataTypeConvertor.convertToInt(-5.6d);
		Assert.assertEquals(-5, i);
		
		i = JavaDataTypeConvertor.convertToInt(5l);
		Assert.assertEquals(5, i);
		
		i = JavaDataTypeConvertor.convertToInt(-5l);
		Assert.assertEquals(-5, i);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToFloat(java.lang.Object)}.
	 */
	@Test
	public void testConvertToFloat() {
		Float i = JavaDataTypeConvertor.convertToFloat(5f);
		Assert.assertEquals(new Float(5f), i);
		i = JavaDataTypeConvertor.convertToFloat(-5f);
		Assert.assertEquals(new Float(-5f), i);
		
		i = JavaDataTypeConvertor.convertToFloat("5.5");
		Assert.assertEquals(new Float(5.5), i);
		
		i = JavaDataTypeConvertor.convertToFloat("-5.5");
		Assert.assertEquals(new Float(-5.5), i);
		
		i = JavaDataTypeConvertor.convertToFloat(5.6f);
		Assert.assertEquals(new Float(5.6), i);
		
		i = JavaDataTypeConvertor.convertToFloat(-5.6f);
		Assert.assertEquals(new Float(-5.6), i);
		
		i = JavaDataTypeConvertor.convertToFloat(5.6d);
		Assert.assertEquals(new Float(5.6), i);
		
		i = JavaDataTypeConvertor.convertToFloat(-5.6d);
		Assert.assertEquals(new Float(-5.6), i);
		
		i = JavaDataTypeConvertor.convertToFloat(5l);
		Assert.assertEquals(new Float(5), i);
		
		i = JavaDataTypeConvertor.convertToFloat(-5l);
		Assert.assertEquals(new Float(-5), i);
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToDouble(java.lang.Object)}.
	 */
	@Test
	public void testConvertToDouble() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToLong(java.lang.Object)}.
	 */
	@Test
	public void testConvertToLong() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToShort(java.lang.Object)}.
	 */
	@Test
	public void testConvertToShort() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToBoolean(java.lang.Object)}.
	 */
	@Test
	public void testConvertToBoolean() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.firesoa.common.util.JavaDataTypeConvertor#convertToChar(java.lang.Object)}.
	 */
	@Test
	public void testConvertToChar() {
		fail("Not yet implemented");
	}

}
