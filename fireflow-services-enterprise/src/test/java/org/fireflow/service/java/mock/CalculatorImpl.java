/**
 * Copyright 2007-2011 非也
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
package org.fireflow.service.java.mock;


/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public class CalculatorImpl implements Calculator {

	/* (non-Javadoc)
	 * @see org.firesoa.service.modules.java.Calculator#calculate(org.firesoa.service.modules.java.MathsAction, org.firesoa.service.modules.java.Parameter)
	 */
	public Result calculate(MathsAction action, Operand param,int flag)
			throws Exception {
		Result result = new Result();
		result.setMsg("The result is ");
		if (action.equals(MathsAction.ADD)){
			result.setResult(param.getX()+param.getY());
			
		}else if (action.equals(MathsAction.MULTIPLY)){
			result.setResult(param.getX()*param.getY());
		}else if (action.equals(MathsAction.SUBTRACT)){
			result.setResult(param.getX()-param.getY());
		}
		else if (action.equals(MathsAction.SUBTRACT)){
			if (param.getY()==0){
				throw new Exception("Y can NOT be 0!");
			}else{
				result.setResult(param.getX()/param.getY());
			}
		}
		return result;
	}

}
