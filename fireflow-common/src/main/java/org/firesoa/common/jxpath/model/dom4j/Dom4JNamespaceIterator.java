package org.firesoa.common.jxpath.model.dom4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;



public class Dom4JNamespaceIterator implements NodeIterator {
    private NodePointer parent;
    private List namespaces;
    private Set prefixes;
    private int position = 0;
    
    public Dom4JNamespaceIterator(NodePointer parent) {
        this.parent = parent;
        Object node = parent.getNode();
        if (node instanceof Document) {
            node = ((Document) node).getRootElement();
        }
        if (node instanceof Element) {
            namespaces = new ArrayList();
            prefixes = new HashSet();
            collectNamespaces((Element) node);
        }
    }

	public int getPosition() {
        return position;
	}


	public boolean setPosition(int position) {
        if (namespaces == null) {
            return false;
        }
        this.position = position;
        return position >= 1 && position <= namespaces.size();
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
        Namespace ns = (Namespace) namespaces.get(index);
        return new Dom4JNamespacePointer(parent, ns.getPrefix(), ns.getURI());
	}
    private void collectNamespaces(Element element) {
        Namespace ns = element.getNamespace();
        if (ns != null && !prefixes.contains(ns.getPrefix())) {
            namespaces.add(ns);
            prefixes.add(ns.getPrefix());
        }
        List others = element.additionalNamespaces();
        for (int i = 0; i < others.size(); i++) {
            ns = (Namespace) others.get(i);
            if (ns != null && !prefixes.contains(ns.getPrefix())) {
                namespaces.add(ns);
                prefixes.add(ns.getPrefix());
            }
        }
        Object elementParent = element.getParent();
        if (elementParent instanceof Element) {
            collectNamespaces((Element) elementParent);
        }
    }
}
