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
package org.fireflow.service.file.writer;

import org.fireflow.engine.exception.ServiceInvocationException;

/**
 * @author 非也 nychen2000@163.com
 *
 */
public interface FileWriter {
	public void writeBytesToFile(String fileName,byte[] content) throws ServiceInvocationException;
	public void writeStringToFile(String fileName,String content)throws ServiceInvocationException;
}
