package org.firesoa.common.jxpath.model.dom4j;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
//import org.apache.commons.jxpath.ri.model.jdom.JDOMNamespacePointer;


public class Dom4JNamespacePointer extends NodePointer {
    private String prefix;
    private String namespaceURI;
    
    public Dom4JNamespacePointer(NodePointer parent, String prefix) {
        super(parent);
        this.prefix = prefix;
    }
    public Dom4JNamespacePointer(
            NodePointer parent,
            String prefix,
            String namespaceURI) {
        super(parent);
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }
    public String asPath() {
        StringBuffer buffer = new StringBuffer();
        if (parent != null) {
            buffer.append(parent.asPath());
            if (buffer.length() == 0
                || buffer.charAt(buffer.length() - 1) != '/') {
                buffer.append('/');
            }
        }
        buffer.append("namespace::");
        buffer.append(prefix);
        return buffer.toString();
    }

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isCollection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public QName getName() {
        return new QName(prefix);
	}

	@Override
	public Object getBaseValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getImmediateNode() {
		return getNamespaceURI();
	}
    public String getNamespaceURI() {
        if (namespaceURI == null) {
            namespaceURI = parent.getNamespaceURI(prefix);
        }
        return namespaceURI;
    }
	@Override
	public void setValue(Object value) {
        throw new UnsupportedOperationException("Cannot modify a namespace");
	}

	@Override
	public int compareChildNodePointers(NodePointer pointer1,
			NodePointer pointer2) {
		// TODO Auto-generated method stub
		return 0;
	}
    public boolean equals(Object object) {
        return object == this || object instanceof Dom4JNamespacePointer && prefix.equals(((Dom4JNamespacePointer) object).prefix);
    }
    
    public int hashCode() {
        return prefix.hashCode();
    }
}
