package org.firesoa.common.jxpath.model.dom4j;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.dom4j.Attribute;


public class Dom4JAttributePointer extends NodePointer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5491204762084246036L;
	private Attribute attr;
    
    public Dom4JAttributePointer(NodePointer parent,Attribute attr){
    	super(parent);
    	this.attr = attr;
    }
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
    public QName getName() {
        return new QName(
            Dom4JNodePointer.getPrefix(attr),
            Dom4JNodePointer.getLocalName(attr));
    }
	@Override
	public Object getBaseValue() {
		return attr;
	}

	@Override
	public Object getImmediateNode() {
		return attr;
	}

	@Override
	public void setValue(Object value) {
        attr.setValue((String) TypeUtils.convert(value, String.class));

		
	}

	@Override
	public int compareChildNodePointers(NodePointer pointer1,
			NodePointer pointer2) {
		// TODO Auto-generated method stub
		return 0;
	}
    public void remove() {
        attr.getParent().remove(attr);
    }
    public boolean isActual() {
        return true;
    }
    
    public int hashCode() {
        return System.identityHashCode(attr);
    }
    
    public Object getValue() {
        return attr.getValue();
    }
    
    public String getNamespaceURI() {
        String uri = attr.getNamespaceURI();
        if (uri != null && uri.equals("")) {
            uri = null;
        }
        return uri;
    }
    
    public boolean equals(Object object) {
        return object == this || object instanceof Dom4JAttributePointer
                && ((Dom4JAttributePointer) object).attr == attr;
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
        buffer.append('@');
        buffer.append(getName());
        return buffer.toString();
    }

}
