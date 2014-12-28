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
package org.firesoa.common.jxpath.model.dom;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class W3CDomFactory extends AbstractFactory {
	private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	public boolean createObject(JXPathContext context, Pointer pointer,
			Object parent, String name, int index) {
		String prefix = null;
		String localName = null;
		String namespaceURI = null;
		int thePosition = name.indexOf(":");
		if (thePosition > 0) {
			prefix = name.substring(0, thePosition);
			localName = name.substring(thePosition + 1);
			namespaceURI = context.getNamespaceURI(prefix);
		} else {
			//取缺省命名空间
			localName = name;
		}
		String qualifiedName = localName;
		if(prefix!=null && !prefix.trim().equals("")){
			qualifiedName = prefix+":"+localName;
		}

		addW3CDomElement((Node) parent, index, qualifiedName, namespaceURI);
		
		 
		return true;
	}
	
    private void addW3CDomElement(Node parent, int index, String tag, String namespaceURI) {
    	try{
            Node child = parent.getFirstChild();
            int count = 0;
            while (child != null) {
                if (child.getNodeName().equals(tag)) {
                    count++;
                }
                child = child.getNextSibling();
            }

            // Keep inserting new elements until we have index + 1 of them
            while (count <= index) {
                Document doc = parent.getOwnerDocument();
                Node newElement;
                if (namespaceURI == null) {
                    newElement = doc.createElement(tag);
                } 
                else {
                    newElement = doc.createElementNS(namespaceURI, tag);
                }
           
                parent.appendChild(newElement);
                count++;
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}

    }
	


	public boolean declareVariable(JXPathContext context, String name) {
		return false;
	}

	private static boolean equalStrings(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		s1 = s1 == null ? "" : s1.trim();
		s2 = s2 == null ? "" : s2.trim();
		return s1.equals(s2);
	}
}
