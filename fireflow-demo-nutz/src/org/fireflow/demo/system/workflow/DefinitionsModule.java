package org.fireflow.demo.system.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.misc.Message;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.misc.ZTreeNode;
import org.fireflow.demo.system.workflow.bean.UploadInfo;
import org.fireflow.demo.workflow.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

@At("/module/system/workflow/DefinitionsModule")
@IocBean(fields={"fireflowRuntimeContext"})
public class DefinitionsModule {
	private RuntimeContext fireflowRuntimeContext = null;
	
	

	public RuntimeContext getFireflowRuntimeContext() {
		return fireflowRuntimeContext;
	}

	public void setFireflowRuntimeContext(RuntimeContext fireflowRuntimeContext) {
		this.fireflowRuntimeContext = fireflowRuntimeContext;
	}

	@At
	@Ok("jsp:/template/system/workflow/repository/upload_definition_step1.jsp")
	public void initUpload(HttpServletRequest req){
		
	}
	
	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp_definitions" })
	@Ok("jsp:/template/system/workflow/repository/upload_definition_step2.jsp")
	public void uploadDefinitionsStep1(HttpServletRequest req,@Param("process_definition_file") File f){
		InputStream inStreamTmp = null;// 流程文件
		

		
		// 2、解析该流程定义
		FPDLDeserializer des = new FPDLDeserializer();
		WorkflowProcess process = null;
		try {
			inStreamTmp = new FileInputStream(f);

			process = des.deserialize(inStreamTmp);
		} catch (DeserializerException e) {
			Message msg = new Message(false,"解析流程文件时报错，流程文件名是"
					+ f.getName());
			msg.setStack(Utils.exceptionStackToString(e));
			req.setAttribute(MainModule.MESSAGE_OBJECT,msg );

			throw new RuntimeException(e);
		} catch (InvalidModelException e) {
			Message msg = new Message(false,"非法的流程定义文件，流程文件名是"
					+ f.getName());
			msg.setStack(Utils.exceptionStackToString(e));
			req.setAttribute(MainModule.MESSAGE_OBJECT,msg );

			throw new RuntimeException(e);
		} catch(FileNotFoundException e){
			throw new RuntimeException(e);
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
		
		if (process==null){
			Message msg = new Message(false,"流程定义文件解析后得到的WorkflowProcess对象为null，文件名是："
					+ f.getName());
			
			throw new RuntimeException("流程定义文件解析后得到的WorkflowProcess对象为null，文件名是："
					+ f.getName());
		}
		
		req.getSession().setAttribute(MainModule.BIZ_OBJECT_IN_SESSION_KEY,process);

		// 3、查找同一ID的其他版本的流程
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();

		WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);

		WorkflowQuery<ProcessDescriptor> query = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		List<ProcessDescriptor> existingProcessList = query.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_ID, process.getId()))
			.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_TYPE, FpdlConstants.PROCESS_TYPE_FPDL20))
			.addOrder(Order.asc(ProcessDescriptorProperty.VERSION))
			.list();
		
		
		req.setAttribute("EXISTING_PROCESS_LIST", existingProcessList);
		req.setAttribute("PROCESS_DEFINITION", process);
	}
	
	@At
	@Ok("jsp:/template/system/workflow/repository/upload_definition_result.jsp")
	public void uploadDefinitionsStep2(final HttpServletRequest req,@Param("..") final UploadInfo uploadInfo){
		final org.fireflow.engine.modules.ousystem.User currentUser =
			WorkflowUtil.getCurrentWorkflowUser();

		WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		final WorkflowStatement statement = fireSession.createWorkflowStatement();
		
		// b、发布流程到流程库
		Trans.exec(new Atom() {
			public void run() {
				ProcessDescriptor processDescriptor = null;
				try {
					WorkflowProcess workflowProcess = (WorkflowProcess) req
							.getSession().getAttribute(
									MainModule.BIZ_OBJECT_IN_SESSION_KEY);
					processDescriptor = statement.uploadProcessObject(workflowProcess,
							uploadInfo.getVersion());

					((ProcessDescriptorImpl) processDescriptor)
							.setPublishState(uploadInfo.isPublishState());
					((ProcessDescriptorImpl) processDescriptor)
							.setValidDateFrom(uploadInfo.getValidDateFrom());
					((ProcessDescriptorImpl) processDescriptor)
							.setValidDateTo(uploadInfo.getValidDateTo());
					((ProcessDescriptorImpl) processDescriptor)
							.setUpdateLog(uploadInfo.getUpdateLog());

					statement.updateProcessDescriptor(processDescriptor);
				} catch (InvalidModelException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		//将session中的对象删除
		req
		.getSession().removeAttribute(
				MainModule.BIZ_OBJECT_IN_SESSION_KEY);
	}
	
	
	@At
	@Ok("jsp:/template/system/workflow/repository/list_definitions.jsp")
	public Map<String,Object> gotoListAllProcess(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		ZTreeNode rootNode = getAllProcessDescriptorAsZTreeNode();
		
		String rootNodeAsJson = Json.toJson(rootNode);
		
		result.put("rootNodeAsJson", rootNodeAsJson);
		return result;
	}
	
	@At
	@Ok("json")
	public ZTreeNode getAllProcessDescriptorAsZTreeNode(){

		final org.fireflow.engine.modules.ousystem.User currentUser =
				WorkflowUtil.getCurrentWorkflowUser();

		WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		WorkflowQuery<ProcessDescriptor> procDefQuery = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		procDefQuery.addOrder(Order.asc(ProcessDescriptorProperty.PACKAGE_ID))
					.addOrder(Order.asc(ProcessDescriptorProperty.NAME))
					.addOrder(Order.asc(ProcessDescriptorProperty.VERSION));
		
		List<ProcessDescriptor> allProcesses = procDefQuery.list();
		
		ZTreeNode processTreeRoot = new ZTreeNode();
		processTreeRoot.setId("0");
		processTreeRoot.setName("流程列表");
		processTreeRoot.setOpen(true);
		
		List<ZTreeNode> packageList = processTreeRoot.getChildren();
		
		for (ProcessDescriptor descriptor : allProcesses){
			String pkgId = descriptor.getPackageId();
			if (pkgId ==null ){
				pkgId = "";
			}
			ZTreeNode packageNode = null;
			for (ZTreeNode node : packageList){
				if (pkgId.equals(node.getId())){
					packageNode = node;
					break;
				}
			}
			
			if (packageNode==null){
				packageNode = new ZTreeNode();
				packageNode.setParent(true);
				packageNode.setParentId("0");
				packageNode.setId(pkgId.trim());
				packageNode.setName(pkgId.trim());
				packageNode.setOpen(true);

				packageList.add(packageNode);
			}
			
			List<ZTreeNode> processIdNodeList = packageNode.getChildren();
			
			ZTreeNode processIdNode = null;
			String processId = descriptor.getProcessId();
			
			for (ZTreeNode node : processIdNodeList){
				if (processId .equals(node.getId())){
					processIdNode = node;
					break;
				}
			}
			
			if (processIdNode==null){
				processIdNode = new ZTreeNode();
				processIdNode.setParent(true);
				processIdNode.setParentId(pkgId);
				processIdNode.setId(processId);
				processIdNode.setName(processId);
				processIdNode.setOpen(true);
				
				processIdNodeList.add(processIdNode);
			}
			
			ZTreeNode descriptorNode = new ZTreeNode();
			descriptorNode.setParent(false);
			descriptorNode.setParentId(processId);
			
			String name = descriptor.getDisplayName();
			if (name==null || name.trim().equals("")){
				name = descriptor.getName();
			}
			descriptorNode.setId(descriptor.getName()+descriptor.getVersion());
			descriptorNode.setName(name+".v"+descriptor.getVersion()+".xml");
			descriptorNode.setUrl(Mvcs.getReq().getContextPath()+"/module/system/workflow/DefinitionsModule/viewDefinition?processId="+descriptor.getProcessId()+"&version="+descriptor.getVersion());
			descriptorNode.setTarget("Win_for_definition_detail");
			
			processIdNode.getChildren().add(descriptorNode);

		}
		
		return processTreeRoot;
	}
	
	@At
	@Ok("jsp:/template/system/workflow/repository/definition_info.jsp")
	public Map<String,Object> viewDefinition(@Param("processId") String processId,@Param("version")int version){
		Map<String,Object> result = new HashMap<String,Object>();
		final org.fireflow.engine.modules.ousystem.User currentUser =
				WorkflowUtil.getCurrentWorkflowUser();

		WorkflowSession fireSession = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext, currentUser);
		WorkflowQuery<ProcessDescriptor> procDefQuery = fireSession.createWorkflowQuery(ProcessDescriptor.class);
		procDefQuery.add(Restrictions.eq(ProcessDescriptorProperty.PROCESS_ID, processId)).add(Restrictions.eq(ProcessDescriptorProperty.VERSION, version));
		
		ProcessDescriptor descriptor = procDefQuery.unique();
		result.put("processDescriptor", descriptor);
		return result;
	}
			
}
