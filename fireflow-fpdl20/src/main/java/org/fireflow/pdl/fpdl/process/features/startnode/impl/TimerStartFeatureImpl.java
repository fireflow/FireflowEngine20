/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.pdl.fpdl.process.features.startnode.impl;

import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;

/**
 * @author 非也
 * @version 2.0
 */
public class TimerStartFeatureImpl implements TimerStartFeature {
	private Expression cronExpression;
	private Expression endTimeExpression;
	private Expression startTimeExpression;
	private Expression repeatCountExpression;
	private Expression repeatIntervalExpression;
	private TimerOperationName timerOprName;
	
	private Boolean cancelAttachedToActivity= false;
	private Activity attachedToActivity = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getCronExpression()
	 */
	public Expression getCronExpression() {
		return cronExpression;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getEndTimeExpression()
	 */
	public Expression getEndTimeExpression() {
		return endTimeExpression;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getRepeatCountExpression()
	 */
	public Expression getRepeatCountExpression() {
		return repeatCountExpression;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getRepeatIntervalExpression()
	 */
	public Expression getRepeatIntervalExpression() {

		return repeatIntervalExpression;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getStartTimeExpression()
	 */
	public Expression getStartTimeExpression() {
		return startTimeExpression;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getTimerOperationName()
	 */
	public TimerOperationName getTimerOperationName() {
		return this.timerOprName;
	}
	
	public void setTimerOperationName(TimerOperationName arg){
		this.timerOprName = arg;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getIfCancelAttachedActivity()
	 */
	public boolean getCancelAttachedToActivity() {
		return this.cancelAttachedToActivity;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#getAttachedActivity()
	 */
	public Activity getAttachedToActivity() {
		return attachedToActivity;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.startnode.TimerStartDecorator#setAttachedActivity(org.fireflow.pdl.fpdl.process.Activity)
	 */
	public void setAttachedToActivity(Activity act) {
		attachedToActivity = act;
	}

	/**
	 * @param cronExpression the cronExpression to set
	 */
	public void setCronExpression(Expression cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @param endTimeExpression the endTimeExpression to set
	 */
	public void setEndTimeExpression(Expression endTimeExpression) {
		this.endTimeExpression = endTimeExpression;
	}

	/**
	 * @param startTimeExpression the startTimeExpression to set
	 */
	public void setStartTimeExpression(Expression startTimeExpression) {
		this.startTimeExpression = startTimeExpression;
	}

	
	/**
	 * @param repeatCountExpression the repeatCountExpression to set
	 */
	public void setRepeatCountExpression(Expression repeatCountExpression) {
		this.repeatCountExpression = repeatCountExpression;
	}

	/**
	 * @param repeatIntervalExpression the repeatIntervalExpression to set
	 */
	public void setRepeatIntervalExpression(Expression repeatIntervalExpression) {
		this.repeatIntervalExpression = repeatIntervalExpression;
	}

	/**
	 * @param cancelAttachedActivity the cancelAttachedActivity to set
	 */
	public void setCancelAttachedToActivity(Boolean cancelAttachedActivity) {
		this.cancelAttachedToActivity = cancelAttachedActivity;
	}

}
