package org.fireflow.pdl.fpdl.process;

import org.fireflow.model.process.WorkflowElement;


public interface Connector extends WorkflowElement{
    /**
     * 边所连接的源节点
     * @return
     */
    public Node getFromNode() ;

    public void setFromNode(Node fromNode);

    /**
     * 边所连接的目标节点
     * @return
     */
    public Node getToNode();

    public void setToNode(Node toNode) ;
}
