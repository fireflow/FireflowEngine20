/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.entity.repository.impl;

import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessRepository;

/**
 * 流程定义对象，
 * 映射到表T_FF_DF_WORKFLOWDEF
 * @author 非也,nychen2000@163.com
 */
public class ProcessRepositoryImpl extends ProcessDescriptorImpl implements ProcessRepository{

    protected transient Object process;

    protected String processContent; //流程定义文件的内容


    public String getProcessContent() {
        return processContent;
    }

    public void setProcessContent(String processContent) {
        this.processContent = processContent;
    }
    
    public void setProcessObject(Object process){
    	this.process = process;
    }
    
    public Object getProcessObject(){
    	return process;
    }

    /**
     * 获取业务流程对象
     * @return
     * @throws RuntimeException
     */
//    public Object getProcess() throws RuntimeException{
//    	return this.process;
//        if (workflowProcess == null) {
//            if (this.processContent != null && !this.processContent.trim().equals("")) {
//
//                ByteArrayInputStream in = null;
//                try {
//                    Dom4JFPDLParser parser = new Dom4JFPDLParser();//采用dom4j来解析xml
//                    in = new ByteArrayInputStream(this.processContent.getBytes("utf-8"));
//                    this.workflowProcess = parser.parse(in);
//
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    throw new RuntimeException(ex.getMessage());
//                } catch (IOException ex) {
//                    Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    throw new RuntimeException(ex.getMessage());
//                } 
//                catch(FPDLParserException ex){
//                    Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    throw new RuntimeException(ex.getMessage());
//                } finally {
//                    try {
//                        in.close();
//                    } catch (IOException ex) {
//                        Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//            }
//        }
//        return workflowProcess;
//    }

    /**
     * @param process
     * @throws RuntimeException
     */
//    public void setProcess(Object process) throws  RuntimeException {
//    	this.process = process;
//        try {
//            this.workflowProcess = process;
//            this.processId = workflowProcess.getId();
//            this.name = workflowProcess.getName();
//            this.displayName = workflowProcess.getDisplayName();
//            this.description = workflowProcess.getDescription();
//
//            Dom4JFPDLSerializer ser = new Dom4JFPDLSerializer();
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//            ser.serialize(workflowProcess, out);
//
//            this.processContent = out.toString("utf-8");
//        } catch (FPDLSerializerException ex) {
//            Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex.toString());
//        } catch (IOException ex) {
//            Logger.getLogger(ProcessRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex.toString());
//        }
//    }
    

    
    /**
     * 转换为ProcessDescriptorImpl对象，主要为了webservice调用参数传递方便
     * @return
     */
    public ProcessDescriptor toProcessDescriptor(){
    	ProcessDescriptorImpl descriptor = new ProcessDescriptorImpl();
    	
    	descriptor.setId(this.getId());//如果Id不为空，表示覆盖；否则表示插入；插入时需要重新计算version字段
    	descriptor.setName(this.getName());
    	descriptor.setDisplayName(this.getDisplayName());
    	descriptor.setProcessId(this.getProcessId());
    	descriptor.setPackageId(this.getPackageId());
    	descriptor.setProcessType(this.getProcessType());
    	descriptor.setVersion(this.getVersion());
    	descriptor.setDescription(this.getDescription());

    	descriptor.setOwnerId(this.getOwnerId());
    	descriptor.setOwnerName(this.getOwnerName());
    	descriptor.setPublishState(this.getPublishState());
    	descriptor.setValidDateFrom(this.getValidDateFrom());
    	descriptor.setValidDateTo(this.getValidDateTo());

    	descriptor.setApprovedTime(this.getApprovedTime());
    	descriptor.setApprover(this.getApprover());
    	descriptor.setHasCallbackService(this.getHasCallbackService());
    	descriptor.setLastEditor(this.getLastEditor());
    	descriptor.setLastUpdateTime(this.getLastUpdateTime());
    	descriptor.setTimerStart(this.getTimerStart());
    	descriptor.setUpdateLog(this.getUpdateLog());
    	return descriptor;
    }
}
