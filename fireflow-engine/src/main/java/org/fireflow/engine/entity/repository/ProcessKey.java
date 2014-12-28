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
package org.fireflow.engine.entity.repository;

import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessKey {
	String processId = null;
	Integer version = null;
	String processType = null;
	public static ProcessKey valueOf(PObjectKey poKey){
		assert(poKey!=null);
		return new ProcessKey(poKey.getProcessId(),poKey.getVersion(),poKey.getProcessType());
	}
	
	public static ProcessKey valueOf(Token token){
		assert(token!=null);
		return new ProcessKey(token.getProcessId(),token.getVersion(),token.getProcessType());
	}
	
	public static ProcessKey valueOf(ProcessInstance processInstance){
		assert(processInstance!=null);
		return new ProcessKey(processInstance.getProcessId(),processInstance.getVersion(),processInstance.getProcessType());
	}
	public ProcessKey(String processId,Integer v,String processType){
		this.processId = processId;
		this.version = v;
		this.processType = processType;
	}
	
	
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode());
		result = prime * result
				+ ((processType == null) ? 0 : processType.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessKey other = (ProcessKey) obj;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (processType == null) {
			if (other.processType != null)
				return false;
		} else if (!processType.equals(other.processType))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	
}
