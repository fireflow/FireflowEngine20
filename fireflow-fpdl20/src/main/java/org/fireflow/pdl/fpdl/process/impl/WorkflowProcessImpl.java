/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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
package org.fireflow.pdl.fpdl.process.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.diagram.impl.DiagramImpl;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.Import;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.firesoa.common.util.Utils;

@SuppressWarnings("serial")
public class WorkflowProcessImpl extends AbstractModelElement implements
		WorkflowProcess {
	
	// ********************************************************************
	// ********************************************************************
//	private String targetNamespace = null;//2012-02-26 该属性在service中定义
	private String packageId = null;
//	private String classpathUri = null;//该属性放在WorkflowProcess中不合理，已经被packageId代替，2014-01-07
	
	private Map<String,ServiceDef> localServices = new HashMap<String,ServiceDef>();
	private Map<String,ResourceDef> localResources = new HashMap<String,ResourceDef>();
	private Map<String,SubProcess> localFlowsMap = new HashMap<String,SubProcess>();
	
	//private List<Import<ResourceDef>> importsForResource = new ArrayList<Import<ResourceDef>>();//2014-01-07 resource 以及service不单独作为资源文件存在，注释掉
	//private List<Import<ServiceDef>> importsForService = new ArrayList<Import<ServiceDef>>();//2014-01-07 resource 以及service不单独作为资源文件存在，注释掉
	private List<Import> importsForProcess = new ArrayList<Import>();
	
	/**
	 * Diagram表，key是subprocess的id
	 */
	private Map<String,Diagram> diagramsMap = new HashMap<String,Diagram>();
	
	// ********************************************************************
	// ********************************************************************
	/**
	 * 构造函数
	 * 
	 * @param id
	 * @param name
	 * @param pkg
	 */
	public WorkflowProcessImpl(String name,String displayName) {
		super(null, name,displayName);
		//构造一个缺省的main flow
		SubProcessImpl flow = new SubProcessImpl(this,WorkflowProcess.MAIN_PROCESS_NAME,WorkflowProcess.MAIN_PROCESS_NAME);
		localFlowsMap.put(flow.getId(), flow);
	}
	
	public SubProcess getMainSubProcess(){
		String mainFlowId = this.getName()+"."+WorkflowProcess.MAIN_PROCESS_NAME;
		return this.localFlowsMap.get(mainFlowId);
	}
	
	public SubProcess getLocalSubProcess(String flowId){
		return this.localFlowsMap.get(flowId);
	}
	
	public void addSubProcess(SubProcess flow){
		this.localFlowsMap.put(flow.getId(), flow);
	}
	
	public void deleteDiagram(String subprocessId){
		this.diagramsMap.remove(subprocessId);
	}
	public void deleteSubProcess(String subProcessId){
		SubProcess subProcess = this.localFlowsMap.get(subProcessId);
		//1、首先删除SubProcess和对应的diagram
		this.localFlowsMap.remove(subProcessId);
		deleteDiagram(subProcessId);
		
		//如果是main subprocess，则创建一个空的
		if (WorkflowProcess.MAIN_PROCESS_NAME.equals(subProcess.getName())){
			SubProcessImpl newSubProcess = new SubProcessImpl(this,WorkflowProcess.MAIN_PROCESS_NAME);
			this.addSubProcess(newSubProcess);
			
			String diagramId = Utils.newUUID();
			Diagram subflowDiagram = new DiagramImpl(diagramId,newSubProcess);
			this.addDiagram(subflowDiagram);
		}
	}

	public List<Activity> findNextActivities(String elementId){
		WorkflowElement element = this.findWorkflowElementById(elementId);
		if (element==null)return null;
		else if (element instanceof Transition){
			return ((Transition)element).getNextActivities();
		}else if (element instanceof Node){
			return ((Node)element).getNextActivities();
		}
		else {
			return null;
		}
	}
	
	public WorkflowElement findWorkflowElementById(String workflowElementId){
		SubProcess subflow = this.getLocalSubProcess(workflowElementId);
		if (subflow!=null)return subflow;
		
		List<SubProcess> subflowList = this.getLocalSubProcesses();
		for (SubProcess tmpSubflow : subflowList){
			WorkflowElement we = tmpSubflow.findWFElementById(workflowElementId);
			if (we!=null) return we;
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#getDuration()
	 */
	public Duration getDuration() {
		SubProcess mainflow = this.getMainSubProcess();
		if (mainflow!=null){
			return mainflow.getDuration();
		}
		return null;
	}

	
	public String getPackageId(){
		return packageId;
	}
	
	public void setPackageId(String pkgId){
		this.packageId = pkgId;
	}	

//	public String getClasspathUri(){
//		return this.classpathUri;
//	}
//	
//	public void setClasspathUri(String classPathUri){
//		this.classpathUri = classPathUri;
//	}
	
	public void addService(ServiceDef svc){
		this.localServices.put(svc.getId(),svc);
	}
	public void deleteService(ServiceDef svc){
		this.localServices.remove(svc.getId());
	}
	
	public void addResource(ResourceDef resource){
		this.localResources.put(resource.getId(),resource);
	}
	
	public void deleteResource(ResourceDef resource){
		this.localResources.remove(resource.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl.process.WorkflowProcess#getResource(java.lang
	 * .String)
	 */
	public ResourceDef getResource(String resourceId) {
		if (resourceId == null || resourceId.trim().equals(""))
			return null;
		List<ResourceDef> allResource = this.getResources();
		for (ResourceDef resource : allResource) {
			if (resourceId.equals(resource.getId())) {
				return resource;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#getResources()
	 */
	public List<ResourceDef> getResources() {
		//getLocalResources被废除，该方法返回的即是localResources。2014-01-07
		PrivateList<ResourceDef> privateList = new PrivateList<ResourceDef>();
		privateList.privateAddAll(this.localResources.values());
		return privateList;
		/*
		PrivateList<ResourceDef> privateList = new PrivateList<ResourceDef>();
		privateList.privateAddAll(this.localResources.values());
		// 将import进来的service也加入到列表中
		for (Import<ResourceDef> processImport : this.importsForResource) {
			List<ResourceDef> content = (List<ResourceDef>) processImport
					.getContents();
			privateList.privateAddAll(content);
		}
		return privateList;
		*/
	}
	
	/**
	 * 2014-01-07 resource 以及service不单独作为资源文件存在，注释掉该方法
	 * @return
	 */
	/*
	public List<ResourceDef> getLocalResources(){
		PrivateList<ResourceDef> privateList = new PrivateList<ResourceDef>();
		privateList.privateAddAll(this.localResources.values());
		return privateList;
	}
	*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl.process.WorkflowProcess#getService(java.lang.
	 * String)
	 */
	public ServiceDef getService(String serviceId) {
		if (serviceId == null || serviceId.trim().equals(""))
			return null;
		List<ServiceDef> allServices = this.getServices();
		for (ServiceDef service : allServices) {
			if (serviceId.equals(service.getId())) {
				return service;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#getServices()
	 */
	public List<ServiceDef> getServices() {
		PrivateList<ServiceDef> privateList = new PrivateList<ServiceDef>();
		privateList.privateAddAll(localServices.values());
		return privateList;
		/*
		PrivateList<ServiceDef> privateList = new PrivateList<ServiceDef>();
		privateList.privateAddAll(localServices.values());
		// 将import进来的service也加入到列表中
		for (Import<ServiceDef> processImport : this.importsForService) {
			List<ServiceDef> content = (List<ServiceDef>) processImport.getContents();
			privateList.privateAddAll(content);
		}
		return privateList;
		*/
	}
	
	/*
	public List<ServiceDef> getLocalServices(){
		PrivateList<ServiceDef> privateList = new PrivateList<ServiceDef>();
		privateList.privateAddAll(localServices.values());
		return privateList;
	}
	*/


	@SuppressWarnings("unchecked")
	public Import getImportByLocation(String location) {
		if (location == null || location.trim().equals(""))
			return null;
		/*
		for (Import processImport : importsForResource) {
			if (processImport.getLocation().equals(location)) {
				return processImport;
			}
		}
		
		for (Import processImport : this.importsForService){
			if (processImport.getLocation().equals(location)){
				return processImport;
			}
		}
		*/
		
		for (Import processImport : this.importsForProcess){
			if (processImport.getLocation().equals(location)){
				return processImport;
			}
		}
		return null;
	}

	/*
	public List<Import<ServiceDef>> getImportsForService() {
		return this.importsForService;
	}

	public List<Import<ResourceDef>> getImportsForResource() {
		return this.importsForResource;
	}
	*/
	
	public List<Import> getImports(){
		return this.importsForProcess;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#getSubflows()
	 */
//	public List<SubProcess> getSubflows() {
//		PrivateList<SubProcess> privateList = new PrivateList<SubProcess>();
//		privateList.privateAddAll(this.localFlowsMap.values());
//		
//		//将import进来的流程加入到Subflow List
//		for (Import<WorkflowProcess> import4Process : this.importsForProcess){
//			List<WorkflowProcess> processList = import4Process.getContents();
//			
//		
//			for (WorkflowProcess process: processList){
//				privateList.privateAdd(process.getMainflow());
//			}
//		}
//
//		return privateList;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#getLocalSubflows()
	 */
	public List<SubProcess> getLocalSubProcesses() {
		PrivateList<SubProcess> privateList = new PrivateList<SubProcess>();
		privateList.privateAddAll(this.localFlowsMap.values());
		return privateList;
	}
	
	//2012-02-26 该属性在servicedef中定义
//	public String getTargetNamespace(){
//		if (StringUtils.isEmpty(targetNamespace)){
//			this.targetNamespace = "http://www.fireflow.org/project_powered_by_fireworkflow/tns/"+this.getName();
//		}
//		return targetNamespace;
//	}
//	
//	public void setTargetNamespace(String tns){
//		this.targetNamespace = tns;
//	}

	
	public void addDiagram(Diagram diagram){
		if (diagram==null)return;
		ModelElement wfElmRef = diagram.getWorkflowElementRef();
		if (wfElmRef!=null){
			this.diagramsMap.put(wfElmRef.getId(), diagram);
		}
		
	}
	
	public List<Diagram> getDiagrams(){
		PrivateList<Diagram> privateList = new PrivateList<Diagram>();
		privateList.privateAddAll( diagramsMap.values());
		return privateList;
	}
	
	public Diagram getDiagramBySubProcessId(String subflowId){
		return this.diagramsMap.get(subflowId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#addServiceImport(org.fireflow.pdl.fpdl.process.Import)
	 */
//	public void addServiceImport(Import<ServiceDef> svcImport) {
//		this.importsForService.add(svcImport);
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#addResourceImport(org.fireflow.pdl.fpdl.process.Import)
	 */
//	public void addResourceImport(Import<ResourceDef> rscImport) {
//		this.importsForResource.add(rscImport);
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.WorkflowProcess#addProcessImport(org.fireflow.pdl.fpdl.process.Import)
	 */
	public void addImport(Import processImport) {
		this.importsForProcess.add(processImport);
	}
	
	public void deleteImport(Import impt){
		this.importsForProcess.remove(impt);
	}
}

class PrivateList<T> extends ArrayList<T> {
	public boolean add(T element) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	protected void privateAddAll(Collection<? extends T> arg0) {
		super.addAll(arg0);
	}
	
	protected boolean privateAdd(T arg0){
		return super.add(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#clear()
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Can not clear this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public T remove(int arg0) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#removeRange(int, int)
	 */
	@Override
	protected void removeRange(int arg0, int arg1) {
		throw new UnsupportedOperationException(
				"Can not remove element from this List.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	@Override
	public T set(int arg0, T arg1) {
		throw new UnsupportedOperationException(
				"Can not add element  to this List.");
	}

}
