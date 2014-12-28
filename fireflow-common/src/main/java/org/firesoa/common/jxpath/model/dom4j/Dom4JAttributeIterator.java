package org.firesoa.common.jxpath.model.dom4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;


public class Dom4JAttributeIterator implements NodeIterator {
    private NodePointer parent;
    private QName name;
    private List attributes;
    private int position = 0;
    
	public Dom4JAttributeIterator(NodePointer parent, QName name){
        this.parent = parent;
        this.name = name;
        attributes = new ArrayList();
        Node node = (Node) parent.getNode();
        
        String prefix = name.getPrefix();
        Namespace ns = null;
        if (prefix != null) {
            if (prefix.equals("xml")) {
                ns = Namespace.XML_NAMESPACE;
            }
            else {
                String uri = parent.getNamespaceResolver().getNamespaceURI(prefix);
                if (uri != null) {
                    ns = Namespace.get(prefix, uri);
                }
                if (ns == null) {
                    // TBD: no attributes
                    attributes = Collections.EMPTY_LIST;
                    return;
                }
            }
        }
        else {
            ns = Namespace.NO_NAMESPACE;
        }
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String lname = name.getName();
            if (!lname.equals("*")) {
                org.dom4j.QName attributeName = new org.dom4j.QName(name.getName(),ns);

            	Attribute attr = ((Element)node).attribute(attributeName);
                if (attr != null) {
                    attributes.add(attr);
                }
            }
            else {
                attributes = new ArrayList();
                List allAttributes = ((Element)node).attributes();
                for (int i = 0; i < allAttributes.size(); i++) {
                    Attribute attr = (Attribute) allAttributes.get(i);
                    if (ns == Namespace.NO_NAMESPACE
                            || attr.getNamespace().equals(ns)) {
                        attributes.add(attr);
                    }
                }
            }
        }
	}
	


	public int getPosition() {
        return position;
	}


	public boolean setPosition(int position) {
        if (attributes == null) {
            return false;
        }
        this.position = position;
        return position >= 1 && position <= attributes.size();
	}


	public NodePointer getNodePointer() {
        if (position == 0) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }
        int index = position - 1;
        if (index < 0) {
            index = 0;
        }
        return new Dom4JAttributePointer(
            parent,
            (Attribute) attributes.get(index));
	}


}
