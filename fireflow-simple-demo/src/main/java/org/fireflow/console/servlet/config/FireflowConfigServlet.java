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
package org.fireflow.console.servlet.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.config.FireflowConfig;
import org.fireflow.engine.entity.config.FireflowConfigProperty;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FireflowConfigServlet extends HttpServlet{
	private static final String LIST_ALL_CONFIGS = "LIST_ALL_CONFIGS";
	private static final String LOAD_CONFIGS_OF_CATEGORY = "LOAD_CONFIGS_OF_CATEGORY";
	
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
    {
    	this.doPost(req, resp);
    }
    
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    	WebApplicationContext  ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	final RuntimeContext fireContext = (RuntimeContext)ctx.getBean("fireflowRuntimeContext");
		//获得当前用户，建议该用户实现Fireflow的用户接口
    	final org.fireflow.engine.modules.ousystem.User currentUser = org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem.getInstance();
    	WorkflowSession workflowSession = WorkflowSessionFactory.createWorkflowSession(fireContext, currentUser);
		
    	String actionType = req.getParameter("actionType");
		if (LIST_ALL_CONFIGS.equals(actionType)){
			List<FireflowConfig> all_categories = loadConfigsOfCurrentCategory(req,workflowSession,fireContext,FireflowConfig.ROOT_CATEGORY_ID);
			req.setAttribute("ALL_CATEGORIES", all_categories);
			if (all_categories!=null && all_categories.size()>0){
				FireflowConfig config = all_categories.get(0);
				String currentCategoryId = config.getConfigId();
				req.setAttribute("currentCategory", config);
				List<FireflowConfig> configsOfCurrentCategory = loadConfigsOfCurrentCategory(req,workflowSession,fireContext,currentCategoryId);
				req.setAttribute("configsOfCurrentCategory", configsOfCurrentCategory);
			}
		}
		else if (LOAD_CONFIGS_OF_CATEGORY.equals(actionType)){
			List<FireflowConfig> all_categories = loadConfigsOfCurrentCategory(req,workflowSession,fireContext,FireflowConfig.ROOT_CATEGORY_ID);
			req.setAttribute("ALL_CATEGORIES", all_categories);
			String currentCategoryId = req.getParameter("currentCategoryId");
			for (FireflowConfig config : all_categories){
				if (config.getConfigId().equals(currentCategoryId)){

					req.setAttribute("currentCategory", config);
				}
			}
			if (currentCategoryId!=null && !currentCategoryId.trim().equals("")){
				List<FireflowConfig> configsOfCurrentCategory = loadConfigsOfCurrentCategory(req,workflowSession,fireContext,currentCategoryId);
				req.setAttribute("configsOfCurrentCategory", configsOfCurrentCategory);
			}

		}
    	//导航到结果页面
    	RequestDispatcher dispatcher = req.getRequestDispatcher("/fireflow_console/config/fireflow_config.jsp");
    	dispatcher.forward(req, resp);
	}

	
	protected List<FireflowConfig> loadConfigsOfCurrentCategory(HttpServletRequest req,WorkflowSession workflowSession ,RuntimeContext fireContext,String categoryId){
    	//PersistenceService persistenceService = fireContext.getDefaultEngineModule(PersistenceService.class);

		WorkflowQuery query = workflowSession.createWorkflowQuery(FireflowConfig.class);
		query.add(Restrictions.eq(FireflowConfigProperty.CATEGORY_ID, categoryId));
		List<FireflowConfig> all_categories = query.list();
		
		return all_categories ;
	}
}
