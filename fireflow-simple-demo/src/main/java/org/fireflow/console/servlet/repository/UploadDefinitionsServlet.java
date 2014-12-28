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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.demo.fireflow_ext.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.web_client.util.Constants;
import org.fireflow.web_client.util.Utils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class UploadDefinitionsServlet extends HttpServlet {
	public static final String SINGLE_DEF_STEP1 = "SINGLE_DEF_STEP1";
	public static final String SINGLE_DEF_STEP2 = "SINGLE_DEF_STEP2";
	public static final String PROCESS_DEFINITION = "PROCESS_DEFINITION";
	
	
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
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String actionType = req.getParameter(Constants.ACTION_TYPE);
		if (SINGLE_DEF_STEP1.equals(actionType)) {
			uploadSingleDefStep1(req, resp);
		}
		else if (SINGLE_DEF_STEP2.equals(actionType)){
			uploadSingleDefStep2(req,resp);
		}
	}

	protected void uploadSingleDefStep2(final HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//1、首先获得发布参数
		String _publishState = req.getParameter("publishState");
		String _validDateFrom = req.getParameter("validDateFrom");
		String _validDateTo = req.getParameter("validDateTo");
		final String updateLog = req.getParameter("updateLog");
		String _version = req.getParameter("version");
		
		boolean publishStateTmp = false;
		if (_publishState!=null && !_publishState.trim().equals("")){
			try{
				publishStateTmp = Boolean.parseBoolean(_publishState);
			}catch(Exception e){
				publishStateTmp = false;
			}
			
		}
		final boolean publishState = publishStateTmp;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date validDateFromTmp = new Date();
		if (_validDateFrom!=null && !_validDateFrom.trim().equals("")){
			try{
				validDateFromTmp = format.parse(_validDateFrom);
			}catch(Exception e){
				validDateFromTmp = new Date();
			}
		}
		final Date validDateFrom = validDateFromTmp;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2099);
		cal.set(Calendar.MONTH, 12);
		cal.set(Calendar.DATE, 31);
		Date validDateToTmp = cal.getTime();
		if (_validDateTo!=null && !_validDateTo.trim().equals("")){
			try{
				validDateToTmp = format.parse(_validDateTo);
			}catch(Exception e){
				validDateToTmp = cal.getTime();
			}
		}
		final Date validDateTo = validDateToTmp;
		
		int versionTmp = 0;
		if (_version!=null && !_version.trim().equals("")){
			try{
				versionTmp = Integer.parseInt(_version);
			}catch(Exception e){
				versionTmp = 0;
			}
		}
		final int version = versionTmp;
		

		// 2、获得当前用户，建议该用户实现Fireflow的用户接口
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser(req.getSession());

		ProcessDescriptor processDescriptor = (ProcessDescriptor) transactionTemplate
				.execute(new TransactionCallback() {

					public Object doInTransaction(TransactionStatus status) {
						// a、首先创建Fire WorkflowSession
						WorkflowSession session = WorkflowSessionFactory
								.createWorkflowSession(fireContext, currentUser);
						WorkflowStatement stmt = session
								.createWorkflowStatement();

						//b、发布流程到流程库
						ProcessDescriptor processDescriptor = null;
						try {
							WorkflowProcess inStream =(WorkflowProcess) req.getSession().getAttribute(PROCESS_DEFINITION);
							processDescriptor = stmt.uploadProcessObject(
									inStream, version);
							
							((ProcessDescriptorImpl) processDescriptor)
									.setPublishState(publishState);
							((ProcessDescriptorImpl) processDescriptor).setValidDateFrom(validDateFrom);
							((ProcessDescriptorImpl) processDescriptor).setValidDateTo(validDateTo);
							((ProcessDescriptorImpl) processDescriptor).setUpdateLog(updateLog);
							
							stmt.updateProcessDescriptor(processDescriptor);
						} catch (InvalidModelException e) {
							throw new RuntimeException(e);
						}

						return processDescriptor;
					}

				});

		// 4、查询出本次上传的流程，显示在结果页面上
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(
				fireContext, currentUser);
		WorkflowQuery<ProcessRepository> processQuery = session
				.createWorkflowQuery(ProcessRepository.class);
		processQuery
				.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_ID,
						processDescriptor.getProcessId()))
				.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_TYPE,
						processDescriptor.getProcessType()))
				.add(Restrictions.eq(ProcessDescriptorProperty.VERSION,
						processDescriptor.getVersion()));
		ProcessRepository repository = processQuery.unique();// 该查询也可以用如下方式实现：ProcessRepository
																// repository =
																// processQuery.get(processDescriptor.getId());
		req.setAttribute("process_repository", repository);

		// 5、导航到结果页面
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/fireflow_console/repository/upload_definition_result.jsp");
		dispatcher.forward(req, resp);
	}

	/**
	 * 上传单个流程定义文件，步骤1<br/>
	 * 该步骤首先将上传流程定义文件保存在Session中（实际业务系统需要改进一下）；然后打开步骤2页面；待操作员补充完信息后，将流程定义保存到流程库。
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void uploadSingleDefStep1(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		// 1、从request中取得流程定义，将其暂存在session中（实际业务系统需要改进一下）
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> fileItems = null;
		try {
			fileItems = upload.parseRequest(req);

		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		InputStream inStreamTmp = null;// 流程文件
		String fileName = null;

		Iterator<FileItem> i = fileItems.iterator();
		while (i.hasNext()) {
			FileItem item = (FileItem) i.next();
			if (!item.isFormField()) {
				inStreamTmp = item.getInputStream();
				fileName = item.getName();
			} else {
				// 其他字段忽略
				// System.out.println("==="+item.getFieldName());
			}
		}

		// 2、解析该流程定义
		FPDLDeserializer des = new FPDLDeserializer();
		WorkflowProcess process = null;
		try {
			process = des.deserialize(inStreamTmp);
		} catch (DeserializerException e) {
			req.setAttribute(Constants.ERROR_MESSAGE, "解析流程文件时报错，流程文件名是"
					+ fileName);
			req.setAttribute(Constants.ERROR_STACK,
					Utils.exceptionStackToString(e));

			RequestDispatcher dispatcher = req
					.getRequestDispatcher("/common/error_message.jsp");
			dispatcher.forward(req, resp);
			return;
		} catch (InvalidModelException e) {
			req.setAttribute(Constants.ERROR_MESSAGE, "非法的流程定义文件，流程文件名是"
					+ fileName);
			req.setAttribute(Constants.ERROR_STACK,
					Utils.exceptionStackToString(e));

			RequestDispatcher dispatcher = req
					.getRequestDispatcher("/common/error_message");
			dispatcher.forward(req, resp);
			return;
		}
		
		if (process==null){
			req.setAttribute(Constants.ERROR_MESSAGE, "非法的流程定义文件，流程文件名是"
					+ fileName);
			req.setAttribute(Constants.ERROR_STACK,"流程定义文件解析后得到的WorkflowProcess对象为null");

			RequestDispatcher dispatcher = req
					.getRequestDispatcher("/common/error_message");
			dispatcher.forward(req, resp);
			return;
		}
		
		req.getSession().setAttribute(PROCESS_DEFINITION,process);

		// 3、查找同一ID的其他版本的流程
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser(req.getSession());

		WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireContext, currentUser);

		WorkflowQuery<ProcessDescriptor> query = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		List<ProcessDescriptor> existingProcessList = query.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_ID, process.getId()))
			.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_TYPE, FpdlConstants.PROCESS_TYPE_FPDL20))
			.addOrder(Order.asc(ProcessDescriptorProperty.VERSION))
			.list();
		
		
		req.setAttribute("EXISTING_PROCESS_LIST", existingProcessList);
		req.setAttribute(PROCESS_DEFINITION, process);
		
		// 5、导航到step2页面
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("/fireflow_console/repository/upload_definition_step2.jsp");
		dispatcher.forward(req, resp);
	}
}
