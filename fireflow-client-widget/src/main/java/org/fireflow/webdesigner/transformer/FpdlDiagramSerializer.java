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
package org.fireflow.webdesigner.transformer;

import org.fireflow.model.io.SerializerException;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.w3c.dom.Document;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface FpdlDiagramSerializer {
	public Document serializeDiagramToDoc(WorkflowProcess workflowProcess,
			String subProcessName) throws SerializerException ;
	public String serializeDiagramToStr(WorkflowProcess workflowProcess,
			String subProcessName,String encoding,boolean omitXmlDeclaration);
	public void setResourcePathPrefix(String s);
}
