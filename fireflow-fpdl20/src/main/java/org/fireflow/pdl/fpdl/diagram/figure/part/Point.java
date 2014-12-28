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
package org.fireflow.pdl.fpdl.diagram.figure.part;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class Point {
	int x = 0;
	int y = 0;

	public Point() {

	}

	public Point(int xx, int yy) {
		x = xx;
		y = yy;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Point))
			return false;
		Point p2 = (Point) obj;
		if ((p2.x == x) && (p2.y == y))
			return true;
		return false;
	}

	public int hashCode() {
		int result = x;
		return result * 31 + y;
	}
	
	public String toString(){
		String s = "("+x+","+y+")";
		return s;
	}

	public static Point fromString(String s) {
		if (StringUtils.isEmpty(s))
			return null;
		if (!s.startsWith("(")){
			return null;
		}

		int index1 = s.indexOf(",");
		String x = s.substring(1, index1);

		String y = s.substring(index1 + 1, s.length() - 1);

		return new Point(Integer.parseInt(x), Integer.parseInt(y));
	}
	
	public Point copy(){
		return new Point(this.x,this.y);
	}
}
