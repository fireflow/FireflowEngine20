package org.fireflow.pdl.bpel.structure;

import org.fireflow.model.data.Expression;
import org.fireflow.pdl.bpel.BpelActivity;


public class Child {
	
	/**
	 * 条件表达式
	 */
	Expression conditionExpression = null;
	
	/**
	 * bpel activity
	 */
	BpelActivity childBpelActivity = null;
	
	public Child(Expression conditionExpression,BpelActivity activity){
		this.conditionExpression = conditionExpression;
		this.childBpelActivity = activity;
	}
	
	public Expression getConditionExpression() {
		return conditionExpression;
	}
	public void setConditionExpression(Expression conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	public BpelActivity getChildBpelActivity() {
		return childBpelActivity;
	}
	public void setChildBpelActivity(BpelActivity childBpelActivity) {
		this.childBpelActivity = childBpelActivity;
	}
	
	
}
