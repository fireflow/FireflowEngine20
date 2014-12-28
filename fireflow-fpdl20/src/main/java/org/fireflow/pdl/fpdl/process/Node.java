package org.fireflow.pdl.fpdl.process;

import java.util.List;

import org.fireflow.model.process.WorkflowElement;
import org.fireflow.pdl.fpdl.process.features.Feature;
/**
 * 流程图中的节点
 * @author 非也
 *
 */
public interface Node extends WorkflowElement{
	
	public List<Transition> getEnteringTransitions() ;


	public List<Transition> getLeavingTransitions() ;

	/**
	 * 获得后续节点，后续节点可能是Activity,Router,StartNode或者EndNode
	 * @return
	 */
	public List<Node> getNextNodes();
	
	public List<Activity> getNextActivities();
	/**
	 * 获得Feature，Feature会影响节点的在设计器中的外观和运行时行为
	 * @return
	 */
	public Feature getFeature();
	
	public void setFeature(Feature dec);
}
