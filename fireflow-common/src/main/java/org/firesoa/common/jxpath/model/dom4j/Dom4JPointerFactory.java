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
package org.firesoa.common.jxpath.model.dom4j;

import java.util.Locale;

import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.dom4j.Node;


/**
 * @author 非也 nychen2000@163.com
 *
 */
public class Dom4JPointerFactory implements NodePointerFactory {

	
	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#getOrder()
	 */
	public int getOrder() {
		// TODO Auto-generated method stub
		return 150;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.QName, java.lang.Object, java.util.Locale)
	 */
	public NodePointer createNodePointer(QName name, Object bean,
			Locale locale) {
        if (bean instanceof Node) {
            return new Dom4JNodePointer((Node)bean, locale);
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.model.NodePointer, org.apache.commons.jxpath.ri.QName, java.lang.Object)
	 */
	public NodePointer createNodePointer(NodePointer parent, QName name,
			Object bean) {
        if (bean instanceof Node) {
            return new Dom4JNodePointer(parent,(Node)bean);
        }
        return null;
	}

}
