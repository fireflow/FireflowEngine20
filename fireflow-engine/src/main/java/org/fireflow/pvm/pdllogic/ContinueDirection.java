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
package org.fireflow.pvm.pdllogic;

import java.util.List;

import org.fireflow.pvm.kernel.PObjectKey;

/**
 * @author 非也
 * @version 2.0
 */
public class ContinueDirection {
	public static final int WAITING_FOR_CLOSE = -1;//继续等待
	public static final int CLOSE_ME = 0;//结束自己
	public static final int RUN_AGAIN = 1;//再次执行execute方法
	public static final int START_NEXT_AND_WAITING_FOR_CLOSE = 3;//启动后续节点,但并不立即关闭自己（在TimerStart作为活动的边事件时有用）
	
	
	private int direction = CLOSE_ME;
	protected List<PObjectKey> nextProcessObjectKeys = null;
	
	public static ContinueDirection waitingForClose(){
		return new ContinueDirection(WAITING_FOR_CLOSE);
	}
	
	public static ContinueDirection closeMe(){
		return new ContinueDirection(CLOSE_ME);
	}
	
	public static ContinueDirection runAgain(){
		return new ContinueDirection(RUN_AGAIN);
	}
	

	public static ContinueDirection startNextAndWaitingForClose(){
		return new ContinueDirection(START_NEXT_AND_WAITING_FOR_CLOSE);
	}
	
	private ContinueDirection(int direct){
		this.direction = direct;
	}
	
	public List<PObjectKey> getNextProcessObjectKeys(){
		return nextProcessObjectKeys;
	}
	
	public void setNextProcessObjectKeys(List<PObjectKey> poKeys){
		nextProcessObjectKeys = poKeys;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
}
