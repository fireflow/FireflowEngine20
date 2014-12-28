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

import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchCompensationFeature;

/**
 * @author 非也
 * @version 2.0
 */
public class CatchCompensationFeatureImpl implements
		CatchCompensationFeature {

	String compensationCode = CATCH_ALL_COMPENSATION;
	Activity referencedActivity = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.process.decorator.startnode.CatchCompensationDecorator#getActivityRef()
	 */
	public Activity getAttachedToActivity() {
		return referencedActivity;
	}
	public void setAttachedToActivity(Activity act){
		this.referencedActivity = act;
	}

	public void setCompensationCode(String compensationCode){
		this.compensationCode = compensationCode;
	}
	public String getCompensationCode(){
		return compensationCode;
	}
}
