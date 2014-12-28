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
package org.fireflow.service.file;

import java.util.Date;

/**
 * 
 * @author 非也 www.firesoa.com
 *
 */
public interface FileObject {
	public static final String FILE_TYPE_BINARY = "BINARY";
	public static final String FILE_TYPE_TEXT = "TEXT";
	/**
	 * 文件名称，不包含路径
	 * @return
	 */
	public String getFileName();
	
	/**
	 * 绝对路径
	 * @return
	 */
	public String getAbsolutePath();
	
	/**
	 * 文件类型
	 * @return
	 */
	public String getFileType();
	
	/**
	 * 返回Text文件的字符集名称
	 * @return
	 */
	public String getCharset();
	
	/**
	 * 最后一次修改时间。该时间是从 Unix epoch time（即 1970-01-01T00:00:00Z ISO 8601）到某一时间点的毫秒数。<br/>
	 * 例如，在Java系统中用(new java.util.Date()).getTime()则可以获得该时间值<br/>
	 * 将.net系统中的某一时间点转换成本系统的计时法，请使用如下方法：<br/>
	 * return Decimal.ToInt64(Decimal.Divide(DateTime.Now.Ticks - new DateTime(1970, 1, 1, 8, 0, 0).Ticks, 10000))<br/>
	 * 或者<br/>
	 * return Decimal.ToInt64(Decimal.Divide(DateTime.UtcNow.Ticks - 621355968000000000, 10000));
	 * @return
	 */
	public long getLastModified();
	
	/**
	 * 文件内容，
	 * @return
	 */
	public byte[] getContent();
	
}
