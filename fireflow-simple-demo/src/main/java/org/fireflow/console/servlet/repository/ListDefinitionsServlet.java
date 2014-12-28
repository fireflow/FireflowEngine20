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
package org.fireflow.console.servlet.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.query.Order;
import org.fireflow.console.misc.TreeNode;
import org.fireflow.demo.fireflow_ext.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.config.FireflowConfig;
import org.fireflow.engine.entity.config.impl.FireflowConfigImpl;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ListDefinitionsServlet extends HttpServlet{
	protected WebApplicationContext  springCtx  = null;
	protected RuntimeContext fireContext  = null;
	protected TransactionTemplate transactionTemplate  = null;
    public void init() throws ServletException {
		//准备相关参数及相关spring bean
    	springCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	fireContext = (RuntimeContext)springCtx.getBean(RuntimeContext.Fireflow_Runtime_Context_Name);
    	transactionTemplate = (TransactionTemplate)springCtx.getBean("demoTransactionTemplate");
    	
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
    	this.doPost(req, resp);
    }
    
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String actionType = req.getParameter("workflowActionType");
		if (actionType!=null && actionType.equals("LOAD_DEFINITION")){
			loadDefinition(req,resp);
		}else{
			loadAllDefinitions(req,resp);
		}
	}
	
	protected void loadDefinition(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String descriptorId = req.getParameter("processDescriptorId");
		//1、查找流程定义
		//获得当前用户，建议该用户实现Fireflow的用户接口
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser(req.getSession());
		
    	WorkflowSession workflowSession = WorkflowSessionFactory.createWorkflowSession(fireContext, currentUser);

    	WorkflowQuery<ProcessRepository> q = workflowSession.createWorkflowQuery(ProcessRepository.class);
    	ProcessRepository repository = q.get(descriptorId);
    	
    	req.setAttribute("process_repository",repository);
    	//5、导航到结果页面
    	RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_console/repository/view_definition.jsp");
    	dispatcher.forward(req, resp);
	}
	
	protected void loadAllDefinitions(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
 		//1、获得当前用户，建议该用户实现Fireflow的用户接口
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser(req.getSession());
		
		WorkflowSession workflowSession = WorkflowSessionFactory.createWorkflowSession(fireContext, currentUser);

		//2、查询所有的ProcessDescriptor
		WorkflowQuery<ProcessDescriptor> processDescriptorQuery = workflowSession.createWorkflowQuery(ProcessDescriptor.class);
		processDescriptorQuery.addOrder(Order.asc(ProcessDescriptorProperty.PACKAGE_ID));
		
		List<ProcessDescriptor> processDescriptorList = processDescriptorQuery.list();
		if (processDescriptorList==null){
			processDescriptorList = new ArrayList<ProcessDescriptor>();
		}
		
		//3、组装成流程定义树		
		TreeNode treeRoot = buildProcessDecriptTree( processDescriptorList);

		//保存到request attribute
		req.setAttribute("defsTree", treeRoot);
		
		
    	//5、导航到结果页面
    	RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_console/repository/list_definitions.jsp");
    	dispatcher.forward(req, resp);
	}
	
	protected TreeNode buildProcessDecriptTree(List<ProcessDescriptor> allProcessDescriptors){
		TreeNode rootNode = new TreeNode();
		rootNode.setTreeId("0");
		rootNode.setName("所有流程");
		rootNode.setDescription("所有流程");

		if (allProcessDescriptors==null || allProcessDescriptors.size()==0){
			return rootNode;
		}
		

		//查找流程，建立叶子节点
		for (ProcessDescriptor descriptor : allProcessDescriptors){
			String pkgId = descriptor.getPackageId();
			if (pkgId==null || pkgId.trim().equals("")){
				TreeNode treeNode4ProcessDescriptor = new TreeNode();
				treeNode4ProcessDescriptor.setTreeId(descriptor.getProcessId()+"_v"+descriptor.getVersion());
				treeNode4ProcessDescriptor.setName(descriptor.getDisplayName()+".v"+descriptor.getVersion()+".xml");
				treeNode4ProcessDescriptor.setDescription(descriptor.getDescription());
				treeNode4ProcessDescriptor.setLeaf(true);
				treeNode4ProcessDescriptor.setActionUrl("/servlet/ListDefinitionsServlet?workflowActionType=LOAD_DEFINITION&processDescriptorId="+descriptor.getId());	
				rootNode.addChild(treeNode4ProcessDescriptor);
			}else{
				TreeNode parentTreeNode = insertPackageIntoTree(rootNode,pkgId);
				
				TreeNode treeNode4ProcessDescriptor = new TreeNode();
				treeNode4ProcessDescriptor.setTreeId(descriptor.getProcessId()+"_v"+descriptor.getVersion());
				treeNode4ProcessDescriptor.setName(descriptor.getDisplayName()+".v"+descriptor.getVersion()+".xml");
				treeNode4ProcessDescriptor.setDescription(descriptor.getDescription());
				treeNode4ProcessDescriptor.setLeaf(true);
				treeNode4ProcessDescriptor.setActionUrl("/servlet/ListDefinitionsServlet?workflowActionType=LOAD_DEFINITION&processDescriptorId="+descriptor.getId());	
				parentTreeNode.addChild(treeNode4ProcessDescriptor);
			}

		}
		return rootNode;
	}
	
	private TreeNode insertPackageIntoTree(TreeNode rootTreeNode,String pakcageId){
		StringTokenizer tokenizer = new StringTokenizer(pakcageId,".");

		TreeNode parentTreeNode = rootTreeNode;
		while(tokenizer.hasMoreTokens()){
			String pkgFragment = tokenizer.nextToken();
			
			TreeNode existNode = findExistTreeNode(parentTreeNode,pkgFragment);
			
			if (existNode==null){
				//创建一个字节点
				existNode = new TreeNode();
				existNode.setTreeId(pkgFragment);
				existNode.setName(pkgFragment);
				existNode.setLeaf(false);
				existNode.setDescription(pkgFragment);
				
				parentTreeNode.addChild(existNode);
			}
			
			parentTreeNode = existNode;
		}
		return parentTreeNode;
	}
	private TreeNode findExistTreeNode(TreeNode treeNode,String fragment){
		List<TreeNode> children = treeNode.getChildren();
		if (treeNode.getTreeId().equals(fragment)){
			return treeNode;
		}else if (children!=null && children.size()>0){
			for (TreeNode tnd : children){
				TreeNode tmp = findExistTreeNode(tnd,fragment);
				if (tmp!=null){
					return tmp;
				}
			}
			return null;
		}else{
			return null;
		}
	}
	
}
