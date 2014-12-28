package org.fireflow.model.binding.impl;

import org.fireflow.model.binding.Assignment;
import org.fireflow.model.data.Expression;

public class AssignmentImpl implements Assignment {

	private Expression from = null;
	private Expression to = null;
	/**
	 * @return the from
	 */
	public Expression getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(Expression from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public Expression getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(Expression to) {
		this.to = to;
	}

}
