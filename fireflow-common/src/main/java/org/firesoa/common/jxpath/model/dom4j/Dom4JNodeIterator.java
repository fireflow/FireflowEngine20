package org.firesoa.common.jxpath.model.dom4j;

import java.util.Collections;
import java.util.List;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class Dom4JNodeIterator implements NodeIterator {
    private NodePointer parent;
    private NodeTest nodeTest;

    private boolean reverse;
    private int position = 0;
    private int index = 0;
    private List children;
    private Object child;
    
    public Dom4JNodeIterator(
            NodePointer parent, NodeTest nodeTest,
            boolean reverse, NodePointer startWith) {
        this.parent = parent;
        if (startWith != null) {
            this.child = startWith.getNode();
        }
        // TBD: optimize me for different node tests
        Object node = parent.getNode();
        if (node instanceof Document) {
            this.children = ((Document) node).content();
        }
        else if (node instanceof Element) {
            this.children = ((Element) node).content();
        }
        else {
            this.children = Collections.EMPTY_LIST;
        }
        this.nodeTest = nodeTest;
        this.reverse = reverse;
    }    

	public int getPosition() {
        return position;
	}


	public boolean setPosition(int position) {
        while (this.position < position) {
            if (!next()) {
                return false;
            }
        }
        while (this.position > position) {
            if (!previous()) {
                return false;
            }
        }
        return true;
	}


	public NodePointer getNodePointer() {
        if (child == null) {
            if (!setPosition(1)) {
                return null;
            }
            position = 0;
        }

        return new Dom4JNodePointer(parent,(Node) child);
	}
    /**
     * This is actually never invoked during the normal evaluation
     * of xpaths - an iterator is always going forward, never backwards.
     * So, this is implemented only for completeness and perhaps for
     * those who use these iterators outside of XPath evaluation.
     * @return boolean
     */
    private boolean previous() {
        position--;
        if (!reverse) {
            while (--index >= 0) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        else {
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterate to next pointer.
     * @return whether valid
     */
    private boolean next() {
        position++;
        if (!reverse) {
            if (position == 1) {
                index = 0;
                if (child != null) {
                    index = children.indexOf(child) + 1;
                }
            }
            else {
                index++;
            }
            for (; index < children.size(); index++) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
        else {
            if (position == 1) {
                index = children.size() - 1;
                if (child != null) {
                    index = children.indexOf(child) - 1;
                }
            }
            else {
                index--;
            }
            for (; index >= 0; index--) {
                child = children.get(index);
                if (testChild()) {
                    return true;
                }
            }
            return false;
        }
    }
    private boolean testChild() {
        return Dom4JNodePointer.testNode(parent, child, nodeTest);
    }
}
