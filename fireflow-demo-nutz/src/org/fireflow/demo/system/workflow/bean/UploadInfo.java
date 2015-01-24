package org.fireflow.demo.system.workflow.bean;

import java.util.Date;

import org.nutz.mvc.annotation.Param;

public class UploadInfo {
	boolean publishState = true;
	
	@Param(value="validDateFrom",dfmt="yyyy-MM-dd")
	Date validDateFrom = null;
	
	@Param(value="validDateTo",dfmt="yyyy-MM-dd")
	Date validDateTo =  null;
	
	String updateLog = null;
	int version = 0;
	
	public boolean isPublishState() {
		return publishState;
	}
	public void setPublishState(boolean publishState) {
		this.publishState = publishState;
	}
	public Date getValidDateFrom() {
		return validDateFrom;
	}
	public void setValidDateFrom(Date validDateFrom) {
		this.validDateFrom = validDateFrom;
	}
	public Date getValidDateTo() {
		return validDateTo;
	}
	public void setValidDateTo(Date validDateTo) {
		this.validDateTo = validDateTo;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	
}
