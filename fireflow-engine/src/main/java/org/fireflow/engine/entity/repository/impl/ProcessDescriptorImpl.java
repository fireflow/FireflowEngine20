package org.fireflow.engine.entity.repository.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessRepository;

/**
 * TODO 如何体现“流程族”的概念
 * 
 * 流程定义相关信息对象
 * @author 非也
 *
 */
@XmlRootElement(name="processDescriptor")
@XmlType(name="processDescriptorType",propOrder={"processId","processType",
		"version","packageId","isTimerStart","hasCallbackService"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessDescriptorImpl extends AbsRepositoryDescriptorImpl implements ProcessDescriptor{
	//TODO 如何体现“流程族”的概念
	@XmlElement(name="processId")
    protected String processId;//流程id
	
	@XmlElement(name="processType")
    protected String processType = null;//定义文件的语言类型，fpdl,xpdl,bepl...
    
	@XmlElement(name="version")
	protected Integer version;//版本号
    
    @XmlElement(name="packageId")
    protected String packageId = null;//业务类别
    
	@XmlElement(name="isTimerStart")
    protected Boolean isTimerStart = Boolean.FALSE;//是否是定时启动的流程
    
	@XmlElement(name="hasCallbackService")
    protected Boolean hasCallbackService = Boolean.FALSE;//是否有回调接口，即是否要发布Webservice
    
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

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ProcessRepositoryDescriptor#getProcessType()
	 */
	public String getProcessType() {
		return processType;
	}
	
	public void setProcessType(String processType){
		this.processType = processType;
	}



    public Boolean getTimerStart(){
    	return this.isTimerStart;
    }
    
    public void setTimerStart(Boolean b){
    	this.isTimerStart = b;
    }
    
    public Boolean getHasCallbackService(){
    	return this.hasCallbackService;
    }
    
    public void setHasCallbackService(Boolean b){
    	this.hasCallbackService = b;
    }
    
	public String getPackageId(){
		return this.packageId;
	}
	
	public void setPackageId(String pkgId){
		this.packageId = pkgId;
	}

    
    public ProcessRepository toProcessRepository(){
    	ProcessRepositoryImpl repository = new ProcessRepositoryImpl();
    	repository.setId(this.getId());//如果Id不为空，表示覆盖；否则表示插入；插入时需要重新计算version字段
    	repository.setPackageId(this.getPackageId());
    	repository.setName(this.getName());
    	repository.setDisplayName(this.getDisplayName());
    	repository.setProcessId(this.getProcessId());
    	repository.setProcessType(this.getProcessType());
    	repository.setVersion(this.getVersion());
    	repository.setDescription(this.getDescription());

    	repository.setOwnerId(this.getOwnerId());
    	repository.setOwnerName(this.getOwnerName());
    	repository.setPublishState(this.getPublishState());
    	repository.setValidDateFrom(this.getValidDateFrom());
    	repository.setValidDateTo(this.getValidDateTo());
    	repository.setApprovedTime(this.getApprovedTime());
    	repository.setApprover(this.getApprover());

    	repository.setHasCallbackService(this.getHasCallbackService());
    	repository.setLastEditor(this.getLastEditor());
    	repository.setLastUpdateTime(this.getLastUpdateTime());
    	repository.setTimerStart(this.getTimerStart());
    	repository.setUpdateLog(this.getUpdateLog());
    	
    	return repository;
    }
}
