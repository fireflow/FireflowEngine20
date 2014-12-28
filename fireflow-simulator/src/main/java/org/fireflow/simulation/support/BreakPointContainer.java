/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.simulation.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fireflow.pvm.kernel.Token;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BreakPointContainer {
	
	private final List<BreakPoint> allBreakPoints = Collections.synchronizedList(new ArrayList<BreakPoint>());
	
	public void addBreakPoint(BreakPoint breakPoint){
		if (breakPoint==null 
				|| breakPoint.getElementId()==null 
				|| breakPoint.getProcessId()==null)return ;//不合法的BreakPoint，拒绝加入
		if (isExist(breakPoint))return;
		allBreakPoints.add(breakPoint);
		
		
	}
	
	public List<BreakPoint> getAllBreakPoints(){
		return allBreakPoints;
	}
	
	public void clearAllBreakPoints(){
		allBreakPoints.clear();
	}
	
	public void clearBreakPoint(BreakPoint breakpoint){
		for (BreakPoint pnt : allBreakPoints){
			if (pnt.equals(breakpoint)){
				allBreakPoints.remove(pnt);
				break;
			}
		}
	}
	
	public boolean isExist(BreakPoint breakPoint){
		if (breakPoint==null )return false;
		for (BreakPoint brkPnt : allBreakPoints){
			if (breakPoint.equals(brkPnt))return true;
		}
		return false;
	}
	
	public boolean isExist(Token token){
		for (BreakPoint brkPnt : allBreakPoints){
			if (brkPnt.getProcessId().equals(token.getProcessId())
					&& brkPnt.getElementId().equals(token.getElementId())){
				return true;
			}
		}
		return false;
	}
	
	public void addAllBreanPoints(List<BreakPoint> breakPoints){
		for (BreakPoint breakPoint : breakPoints){
			if (breakPoint==null 
					|| breakPoint.getElementId()==null 
					|| breakPoint.getProcessId()==null)continue ;//不合法的BreakPoint，拒绝加入
			allBreakPoints.add(breakPoint);
		}

	}
}
