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
package org.fireflow.pvm.kernel;

/**
 * ProcessObject的唯一键。由processId,version,workflowElementId唯一确定
 * @author 非也
 * @version 2.0
 */
public class PObjectKey {
	private String processId;
	private int version;
	private String workflowElementId;
	private String processType = null;//流程类别，FPDL,XPDL,BPMN,BPEL等。
	
	
	/**
	 * @param processId
	 * @param version
	 * @param workflowElementId
	 */
	public PObjectKey(String processId, int version,String processType,
			String workflowElementId) {
		super();
		this.processId = processId;
		this.version = version;
		this.processType = processType;
		this.workflowElementId = workflowElementId;
	}
	
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getWorkflowElementId() {
		return workflowElementId;
	}
	public void setWorkflowElementId(String workflowElementId) {
		this.workflowElementId = workflowElementId;
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
		result = prime * result + version;
		result = prime
				* result
				+ ((workflowElementId == null) ? 0 : workflowElementId
						.hashCode());
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
		PObjectKey other = (PObjectKey) obj;
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
		if (version != other.version)
			return false;
		if (workflowElementId == null) {
			if (other.workflowElementId != null)
				return false;
		} else if (!workflowElementId.equals(other.workflowElementId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessObjectKey [processId=" + processId + ", processType="
				+ processType + ", version=" + version + ", workflowElementId="
				+ workflowElementId + "]";
	}
	
	
	
}
