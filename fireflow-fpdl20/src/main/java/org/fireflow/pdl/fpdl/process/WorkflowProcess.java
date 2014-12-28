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
package org.fireflow.pdl.fpdl.process;

import java.util.List;

import org.fireflow.model.ModelElement;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.diagram.Diagram;


/**
 * 业务过程。<br/>
 * 这是Fire workflow 模型的最顶层元素，一个业务过程可以包含多个工作流程，也可以调用外部的业务过程。
 * <br/>
 * @author 非也,nychen2000@163.com
 * 
 */
public interface WorkflowProcess extends ModelElement{
	
	public static final String MAIN_PROCESS_NAME="main";
	
	/**
	 * 返回业务过程的运行时间，业务过程的duration等于main_flow的duration
	 * @return
	 */
	public Duration getDuration();
	
	
	/**
	 * 返回该流程所属的包，该字段用于流程管理目的，可将流程按照包的树状结构进行显示；
	 * 
	 * @return
	 */
	public String getPackageId();
	
	public void setPackageId(String packageId);
	
	
	/**
	 * 流程定义文件在classpath中的位置
	 * 该属性放在WorkflowProcess中不合理，2012-04-29
	 * 2014-01-07,该属性用packageId替代了。
	 * @return
	 */
//	public String getClasspathUri();
//	
//	public void setClasspathUri(String classPathUri);
	
	/**
	 * 根据WorkflowElmentId查找对应的Workflow Element; 
	 * Workflow Element可以是subprocess,StartNode,Activity,EndNode,Router,Transition。
	 * @param workflowElementRef
	 * @return
	 */
	public WorkflowElement findWorkflowElementById(String workflowElementId);
	
	/**
	 * 找到指定元素的后继Activity列表
	 * @param elementId
	 * @return
	 */
	public List<Activity> findNextActivities(String elementId);
	
	
	/**
	 * 返回主流程的流程Id
	 * @return
	 */
	public SubProcess getMainSubProcess();
	
	/**
	 * 根据subprocessId 返回subprocess
	 * @param subProcessId
	 * @return
	 */
	public SubProcess getLocalSubProcess(String subProcessId);
	
	
	/**
	 * 向WorkflowProcess中增加一个subprocess
	 * @param subProcess
	 */
	public void addSubProcess(SubProcess subProcess);
	
	/**
	 * 删除子过程
	 * @param subProcessId
	 */
	public void deleteSubProcess(String subProcessId);
	
	/**
	 * 获得流程所有的subprocesss，包括引入的外部流程的main_flow。<br/>
	 * 注意：暂不启用对WorkflowProcess的引用，因为容易发生循环引用，导致死锁。故注释掉该方法。
	 * @return
	 */
	//public List<SubProcess> getsubprocesses();
	
	/**
	 * 获得本WorkflowProcess内部定义的所有subprocess
	 * @return
	 */
	public List<SubProcess> getLocalSubProcesses();
	

	
	/**
	 * 获得所有的Service，包括import进来的
	 * @return
	 */
	public List<ServiceDef> getServices();
	
	/**
	 * 获得在本WorkflowProcess定义的所有Service。
	 * 2014-01-07 resource 以及service不单独作为资源文件存在，注释掉该方法
	 * @return
	 */
//	public List<ServiceDef> getLocalServices();
	
	public ServiceDef getService(String serviceId);
	
	/**
	 * 定义一个局部Service到本业务流程
	 * @param svc
	 */
	public void addService(ServiceDef svc);
	
	public void deleteService(ServiceDef svc);
	
	/**
	 * 获得该流程所有的资源定义，包括import进来的。
	 * @return
	 */
	public List<ResourceDef> getResources();
	
	/**
	 * 2014-01-07 resource 以及service不单独作为资源文件存在，注释掉该方法
	 * @param resourceId
	 * @return
	 */
//	public List<ResourceDef> getLocalResources();
	
	public ResourceDef getResource(String resourceId);
	/**
	 * 定义一个局部的resource到本业务流程
	 * @param resource
	 */
	public void addResource(ResourceDef resource);

	public void deleteResource(ResourceDef resource);
	
	/**
	 * 根据location值获得Import对象
	 * @param location
	 * @return
	 */
	public Import getImportByLocation(String location);
	

	/**
	 * 获得workflowprocess import 列表
	 * @return
	 */
	public List<Import> getImports();
	
	
	public void addImport(Import processImport);
	
	public void deleteImport(Import impt);
	
	/**
	 * 流程的namespace
	 * 2012-02-26，该属性放在service定义里面
	 * @return
	 */
//	public String getTargetNamespace();
	
	public List<Diagram> getDiagrams();
	
	public void deleteDiagram(String subprocessId);
	public void addDiagram(Diagram diagram);
	public Diagram getDiagramBySubProcessId(String subProcessId);
}
