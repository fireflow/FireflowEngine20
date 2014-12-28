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
package org.fireflow.pdl.fpdl.enginemodules.processlanguage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaSerializer.XmlSchemaSerializerException;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.WebservicePublishException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.environment.Environment;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.processlanguage.AbsProcessLanguageManager;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.io.FPDLSerializer;
import org.fireflow.pdl.fpdl.io.ImportLoader;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.WebserviceStartFeature;
/* 暂时屏蔽该功能
import org.fireflow.service.callback.CallbackService;
import org.fireflow.service.callback.ProcessServiceProvider;
*/
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.schema.WSDLConstants;
import org.w3c.dom.Document;

import com.ibm.wsdl.extensions.schema.SchemaImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;

/**
 * TODO 等fire2.0 流程xsd出来后，此处需要用FPDLSerializer和FPDLDeserializer改造。
 * @author 非也
 * @version 2.0
 */
public class FpdlManager  extends AbsProcessLanguageManager implements
		ProcessLanguageManager {

	Map<QName,Boolean> servicePublishRegistry = new HashMap<QName,Boolean>();

	
	public String getProcessEntryId(String workflowProcessId, int version,String processType){
		return workflowProcessId+"."+WorkflowProcess.MAIN_PROCESS_NAME;
	}
	
    public ServiceBinding getServiceBinding(Object argActivity)throws InvalidModelException{
    	Activity activity = (Activity)argActivity;
    	if (activity==null){
    		return null;
    	}else{
    		return activity.getServiceBinding();
    	}
    }
    
    public ResourceBinding getResourceBinding(Object argActivity)throws InvalidModelException{

    	Activity activity = (Activity)argActivity;
    	if (activity==null){
    		return null;
    	}else{
    		return activity.getResourceBinding();
    	}
    }
    
    public Object findActivity(ProcessKey processKey,String subflowId, String activityId)throws InvalidModelException{
    	WorkflowProcess process = (WorkflowProcess)this.getWorkflowProcess(processKey);
    	if (process==null) return null;
    	SubProcess subflow = process.getLocalSubProcess(subflowId);
    	Activity activity = subflow.getActivity(activityId);
    	return activity;
    }
    
    public Object findSubProcess(ProcessKey processKey,String subProcessId)throws InvalidModelException{
    	WorkflowProcess process = (WorkflowProcess)this.getWorkflowProcess(processKey);
    	if (process==null) return null;
    	SubProcess subflow = process.getLocalSubProcess(subProcessId);
    	return subflow;
    }


	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getWorkflowProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	protected Object getWorkflowProcess(ProcessKey processKey) throws InvalidModelException{
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(processKey);
		return repository.getProcessObject();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#deserializeXml2Process(java.lang.String)
	 */
	public Object deserializeXml2Process(InputStream inStream) throws InvalidModelException{
		if (inStream==null) return null;
		FPDLDeserializer parser = new FPDLDeserializer();
		
		//2012-02-02
		InnerImportLoader importLoader = new InnerImportLoader();
		parser.setImportLoader(importLoader);
		
		try {
			WorkflowProcess process;
			process = parser.deserialize(inStream);
			
			return process;
		} catch(IOException e){
			throw new InvalidModelException(e);
		} catch (DeserializerException e) {
			throw new InvalidModelException(e);
		}

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#serializeProcess2Xml(java.lang.Object)
	 */
	public String serializeProcess2Xml(Object process) throws InvalidModelException{
		
		WorkflowProcess wfProcess = (WorkflowProcess)process;
		FPDLSerializer ser = new FPDLSerializer();
		try {
			return ser.serializeToXmlString(wfProcess);
		} catch (IOException e) {
			
			throw new InvalidModelException(e);
		} catch (SerializerException e) {
			throw new InvalidModelException(e);
		}
	}

	public ProcessDescriptor generateProcessDescriptor(Object process){
		WorkflowProcess wfProcess = (WorkflowProcess)process;
		ProcessDescriptorImpl descriptor = new ProcessDescriptorImpl();

		descriptor.setProcessId(wfProcess.getId());
		descriptor.setPackageId(wfProcess.getPackageId());
		descriptor.setVersion(0);
		descriptor.setTimerStart(isTimerStart(wfProcess));
		descriptor.setHasCallbackService(hasCallbackService(wfProcess));
		descriptor.setProcessType(FpdlConstants.PROCESS_TYPE_FPDL20);
		descriptor.setName(wfProcess.getName());
		String displayName = wfProcess.getDisplayName();
		descriptor.setDisplayName((displayName==null || displayName.trim().equals(""))?wfProcess.getName():displayName);
		descriptor.setDescription(wfProcess.getDescription());
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class,FpdlConstants.PROCESS_TYPE_FPDL20);
		descriptor.setPublishState(false);//发布状态false;
		
		descriptor.setValidDateFrom(calendarService.getSysDate());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2099);
		cal.set(Calendar.MONTH, 12);
		cal.set(Calendar.DATE, 31);
		
		descriptor.setValidDateTo(cal.getTime());

			
		return descriptor;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#serializeProcess2ProcessRepository(java.lang.Object)
	 */
	public ProcessRepository serializeProcess2ProcessRepository(Object process) throws InvalidModelException{
		ProcessDescriptor descriptor = 	generateProcessDescriptor(process);
		ProcessRepositoryImpl repository = (ProcessRepositoryImpl)descriptor.toProcessRepository();

		repository.setProcessObject(process);
		repository.setProcessContent(this.serializeProcess2Xml(process));	

		return repository;
	}
	
	private boolean hasCallbackService(WorkflowProcess workflowProcess){
		/* 暂时屏蔽call back service
		List<SubProcess> subflowList = workflowProcess.getLocalSubProcesses();
		if (subflowList == null || subflowList.size() == 0)
			return false;
		for (SubProcess subflow : subflowList) {
			// 首先检查Activity是否绑定了Callback service
			List<Activity> activityList = subflow.getActivities();
			if (activityList != null) {
				for (Activity activity : activityList) {
					ServiceBinding svcBinding = activity.getServiceBinding();
					if (svcBinding != null) {
						ServiceDef svcDef = workflowProcess.getService(svcBinding.getServiceId());
						if (svcDef != null && svcDef instanceof CallbackService) {
							return true;
						}
					}
				}
			}
			// 然后检查Main subflow 的 StartNode是否绑定了CallbackService
			if (WorkflowProcess.MAIN_PROCESS_NAME.equals(subflow.getName())){
				List<StartNode> startNodeList = subflow.getStartNodes();
				if (startNodeList != null) {
					for (StartNode startNode : startNodeList) {
						Feature ft = startNode.getFeature();
						if (ft != null && ft instanceof WebserviceStartFeature) {
							WebserviceStartFeature wsFt = (WebserviceStartFeature) ft;
							ServiceBinding svcBinding = wsFt.getServiceBinding();
							if (svcBinding != null) {
								ServiceDef svcDef = workflowProcess.getService(svcBinding.getServiceId());
								if (svcDef != null && svcDef instanceof CallbackService) {
									return true;
								}
							}
						}
					}
				}
			}

		}*/
		return false;
	}
	
	private boolean isTimerStart(WorkflowProcess wfProcess){
		SubProcess subflow = wfProcess.getMainSubProcess();
		if (subflow==null) return false;
		StartNode startNode = (StartNode)subflow.getEntry();
		if (startNode==null) return false;
		
		Feature startNodeFeature = startNode.getFeature();
		if (startNodeFeature!=null && startNodeFeature instanceof TimerStartFeature){
			return true;
		}
		return false;
	}
	
	private class InnerImportLoader implements ImportLoader{

		/* (non-Javadoc)
		 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadResources(java.lang.String)
		 */
//		public List<ResourceDef> loadResources(String resourceLocation)
//				throws DeserializerException, IOException {
//			PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
//			ResourcePersister resourcePersister = persistenceService.getResourcePersister();
//			
//			ResourceRepository repository = resourcePersister.findResourceRepositoryByFileName(resourceLocation);
//			return repository.getResources();
//		}

		/* (non-Javadoc)
		 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadServices(java.lang.String)
		 */
//		public List<ServiceDef> loadServices(String serviceLocation)
//				throws DeserializerException, IOException {
//			PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
//			ServicePersister servicePersister = persistenceService.getServicePersister();
//			
//			ServiceRepository repository = servicePersister.findServiceRepositoryByFileName(serviceLocation);
//			return repository.getServices();
//		}

		/* (non-Javadoc)
		 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadProcess(java.lang.String)
		 */
		public WorkflowProcess loadProcess(String processLocation)
				throws InvalidModelException, DeserializerException,
				IOException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProperty(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String, java.lang.String)
	 */
	public Property getProperty(Object workflowElement,
			String propertyName) {
    	if (workflowElement instanceof SubProcess){
    		return ((SubProcess)workflowElement).getProperty(propertyName);
    	}else if (workflowElement instanceof Activity){
    		return ((Activity)workflowElement).getProperty(propertyName);
    	}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getServiceDef(org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, java.lang.String)
	 */
	public ServiceDef getServiceDef(ActivityInstance activityInstance,
			Object activity, String serviceId) {
		Activity theActivity = (Activity)activity;
		SubProcess subflow = (SubProcess)theActivity.getParent();
		WorkflowProcess process = (WorkflowProcess)subflow.getParent();
		return process.getService(serviceId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getResourceDef(org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, java.lang.String)
	 */
	public ResourceDef getResourceDef(ActivityInstance activityInstance,
			Object activity, String resourceId) {
		SubProcess subProcess = (SubProcess)((Activity)activity).getParent();
		WorkflowProcess process = (WorkflowProcess)subProcess.getParent();
		return process.getResource(resourceId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#publishAllProcessServices()
	 */
	public void publishAllProcessServices() throws WebservicePublishException {
		// 1、查询所有的WorkflowProcess找到其中的Callback定义
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(
				ctx, FireWorkflowSystem.getInstance());
		WorkflowQuery<ProcessDescriptor> processDescQuery = session
				.createWorkflowQuery(ProcessDescriptor.class);
		// 未发布的也应该查询出来，因为已经运行的实例，即便流程未发布，也要响应webservice请求。
		processDescQuery.add(Restrictions.eq(
				ProcessDescriptorProperty.HAS_CALLBACK_SERVICE, Boolean.TRUE));

		List<ProcessDescriptor> descriptorList = processDescQuery.list();
		if (descriptorList == null && descriptorList.size() == 0)
			return;

		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();

		// 2、针对每个流程的Callback，发布Webservice
		for (ProcessDescriptor descriptor : descriptorList) {
			try {
				ProcessRepository repository = processPersister
						.findProcessRepositoryByProcessKey(new ProcessKey(
								descriptor.getProcessId(), descriptor
										.getVersion(), descriptor
										.getProcessType()));

				publishCallbackService((WorkflowProcess) repository
						.getProcessObject());
			} catch (InvalidModelException ex) {
				// TODO 记录操作日志
				ex.printStackTrace();
			}
		}
	}
	
	protected void publishCallbackService(WorkflowProcess workflowProcess)throws WebservicePublishException {
		/* 暂时屏蔽该功能
		List<SubProcess> subflowList = workflowProcess.getLocalSubProcesses();

		if (subflowList == null || subflowList.size() == 0)
			return;
		for (SubProcess subflow : subflowList) {
			// 首先检查Activity是否绑定了Callback service
			List<Activity> activityList = subflow.getActivities();
			if (activityList != null) {
				for (Activity activity : activityList) {
					ServiceBinding svcBinding = activity.getServiceBinding();
					if (svcBinding != null) {
						ServiceDef svcDef = workflowProcess.getService(svcBinding.getServiceId());
						if (svcDef != null && svcDef instanceof CallbackService) {
							publishCallbackService(workflowProcess, subflow,
									activity, (CallbackService) svcDef);
						}
					}
				}
			}
			// 然后检查main subflow 的StartNode是否绑定了CallbackService
			if (WorkflowProcess.MAIN_PROCESS_NAME.equals(subflow.getName())) {
				List<StartNode> startNodeList = subflow.getStartNodes();
				if (startNodeList != null) {
					for (StartNode startNode : startNodeList) {
						Feature ft = startNode.getFeature();
						if (ft != null && ft instanceof WebserviceStartFeature) {
							WebserviceStartFeature wsFt = (WebserviceStartFeature) ft;
							ServiceBinding svcBinding = wsFt.getServiceBinding();
							if (svcBinding != null) {
								ServiceDef svcDef = workflowProcess.getService(svcBinding.getServiceId());
								if (svcDef != null && svcDef instanceof CallbackService) {
									publishCallbackService(workflowProcess, subflow,
											startNode, (CallbackService) svcDef);
								}
							}
						}
					}
				}
			}
		}
		*/
	}
	/* 暂时屏蔽 该功能
	protected void publishCallbackService(WorkflowProcess workflowProcess,
			SubProcess subflow, Node node, CallbackService callbackService) throws WebservicePublishException{

		CommonInterfaceDef commonInterfaceDef = (CommonInterfaceDef) callbackService
				.getInterface();
		String serviceName = callbackService.getName()+"_"+callbackService.getVersion();
		
		QName serviceQName = new QName(callbackService.getTargetNamespaceUri(),
				serviceName);
		
		if (servicePublishRegistry.get(serviceQName)!=null && 
				servicePublishRegistry.get(serviceQName)){
			return;//已经发布，不必重复发布；
		}
		
		QName portQName = new QName(callbackService.getTargetNamespaceUri(),
				commonInterfaceDef.getName()+"_Port");//

		// 1、构造address，wsdlfilename
		// TODO 如何组织address，使之与已有的Server context适应？
		Environment evn = ctx
				.getDefaultEngineModule(Environment.class);
		String contextPath = evn.getWebserviceContextPath();
		if (!contextPath.startsWith("/")){
			contextPath = "/"+contextPath;
		}
		String address = "http://"+evn.getWebserviceIP()+":"
					+Integer.toString(evn.getWebservicePort())
					+contextPath;
		// 2、构造wsdl
		List<Source> wsdls = null;
		try {
			wsdls = this
					.buildWSDL(callbackService, commonInterfaceDef,address,evn.getWorkDir(),serviceQName,portQName);
		} catch (FileNotFoundException e) {
			throw new WebservicePublishException(e);
		} catch (WSDLException e) {
			throw new WebservicePublishException(e);
		}catch(IOException e){
			throw new WebservicePublishException(e);
		}

		// 3、指定service name 和port name
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Endpoint.WSDL_SERVICE, serviceQName);
		properties.put(Endpoint.WSDL_PORT, portQName);


		// 4、构建Implementor
		ProcessServiceProvider implementor = new ProcessServiceProvider();
		implementor.setWorkflowRuntimeContext(ctx);
		implementor.setProcessId(workflowProcess.getId());
		implementor.setProcessType(FpdlConstants.PROCESS_TYPE_FPDL20);
		if (node instanceof Activity){
			Activity activity = (Activity)node;
			ServiceBinding binding = activity.getServiceBinding();
			implementor.setServiceBinding(binding);
		}else if (node instanceof StartNode){
			implementor.setStartNewProcess(true);
			WebserviceStartFeature wsFt = (WebserviceStartFeature)((StartNode)node).getFeature();
			ServiceBinding svcBinding = wsFt.getServiceBinding();
			implementor.setServiceBinding(svcBinding);
		}
		implementor.setCallbackService(callbackService);
		implementor.setTransactionTemplate(this.getTransactionTemplate());
		

		// 5、发布服务
		Endpoint endpoint = Endpoint.create(implementor);
		endpoint.setMetadata(wsdls);
		endpoint.setProperties(properties);
		endpoint.publish(address);

		servicePublishRegistry.put(serviceQName, Boolean.TRUE);
	}
	*/
	
	/**
	 * 该段代码参考javaeye一篇帖子，作者为：步行者；
	 * 链接是：http://www.iteye.com/topic/421068
	 * @param callbackService
	 * @param _interface
	 * @param serviceName callbackService.getName()+"_"+callbackService.getVersion()
	 * @return
	 */
	/*
	protected List<Source> buildWSDL(CallbackService callbackService,
			InterfaceDef _interface,String address,String workDir,QName serviceQName,QName portQName)
			throws WSDLException,FileNotFoundException,IOException{
		
		//为该Web service创建子目录
		String fullDirName = workDir+File.separator+serviceQName.getLocalPart();
		File serviceDir = new File(fullDirName);
		if (!serviceDir.exists()){
			boolean b = serviceDir.mkdir();
			if (!b){
				throw new IOException("Can NOT create work dir for service "+serviceQName.toString()+"; the dir path is "+fullDirName);
			}
		}
		
		List<Source> result = new ArrayList<Source>();
		List<OperationDef> operationDefs = _interface.getOperations();
		if (operationDefs==null || operationDefs.size()==0){
			//TODO 记录错误信息
			return result;
		}
		OperationDef operationDef = operationDefs.get(0);//CallbackService仅允许一个Operation
		
		// 根据Interface定义构建WSDL，使用wsdl4j;
		String tns = callbackService.getTargetNamespaceUri();

		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		// 创建一个 WSDL definition
		Definition definition = wsdlFactory.newDefinition();
		// 设置 targetNamespace
		definition.setTargetNamespace(tns);
		definition.setQName(new QName(tns, callbackService.getName()));
		
		// 添加命名空间 (一些常用的命名空间)
		definition.addNamespace("tns", tns);
		definition.addNamespace("xsd", NameSpaces.XSD.getUri());
		definition.addNamespace("wsdlsoap",
				"http://schemas.xmlsoap.org/wsdl/soap/");
		definition.addNamespace("soapenc11",
				"http://schemas.xmlsoap.org/soap/encoding/");
		definition.addNamespace("soapenc12",
				"http://www.w3.org/2003/05/soap-encoding");
		definition.addNamespace("soap11",
				"http://schemas.xmlsoap.org/soap/envelope/");
		definition.addNamespace("soap12",
				"http://www.w3.org/2003/05/soap-envelope");

		//0、创建data types
		XmlSchemaCollection schemaCollection = callbackService.getXmlSchemaCollection();
		
		//将schemaCollections写入WsdlDefinitions.types段落?
		if (schemaCollection!=null){
			Types types = definition.createTypes();
			XmlSchema[] xmlSchemas = schemaCollection.getXmlSchemas();			
			for (XmlSchema xmlSchema : xmlSchemas){
				if (!NameSpaces.XSD.getUri().equals(xmlSchema.getTargetNamespace())){

					try {
						Document schemaDocument;
						schemaDocument = xmlSchema.getSchemaDocument();
						
						SchemaImpl wsdlSchema = new SchemaImpl();
						wsdlSchema.setRequired(true);
						wsdlSchema.setElementType(WSDLConstants.QNAME_SCHEMA);
						wsdlSchema.setElement(schemaDocument.getDocumentElement());
						types.addExtensibilityElement(wsdlSchema);					
						
					} catch (XmlSchemaSerializerException e) {
						throw new WSDLException(WSDLException.OTHER_ERROR,e.getMessage(),e);
					}

				}
			}
			definition.setTypes(types);
		}

		//一、创建消息
		// 创建消息（Message）
		Message requestMsg = definition.createMessage();
		requestMsg.setQName(new QName(tns, operationDef.getOperationName()+"RequestMessage"));
		requestMsg.setUndefined(false);
		
		Message responseMsg = definition.createMessage();
		responseMsg.setQName(new QName(tns,  operationDef.getOperationName()+"ResponseMessage"));
		responseMsg.setUndefined(false);
		definition.addMessage(requestMsg);
		definition.addMessage(responseMsg);		
		
		
		// 创建RequestMessage的 Part
		Part part1 = definition.createPart();
		part1.setName(operationDef.getOperationName()+"Request");
		// 设置 part1 的 Schema Type 
		QName part1Element = new QName(tns,operationDef.getOperationName()+"Request");
		part1.setElementName(part1Element);
		requestMsg.addPart(part1);
		
		// 创建ResponseMessage的Part
		Part part2 = definition.createPart();
		part2.setName(operationDef.getOperationName()+"Response");
		// 设置 part2 的 Schema Type 
		QName part2Element = new QName(tns,operationDef.getOperationName()+"Response");
		part2.setElementName(part2Element);		
		responseMsg.addPart(part2);
		
//		List<org.fireflow.model.data.Input> inputList = operationDef.getInputs();
//		for (org.fireflow.model.data.Input ffInput:inputList){
//			ffInput.getDataType();
//			
//			// 创建 Part
//			Part part1 = definition.createPart();
//			part1.setName(ffInput.getName()+"Part");
//			// 设置 part1 的 Schema Type 
//			part1.setTypeName(ffInput.getDataType());
//			requestMsg.addPart(part1);
//		}
		
//		List<org.fireflow.model.data.Output> outputList = operationDef.getOutputs();
//		for (org.fireflow.model.data.Output ffOutput : outputList){
//			Part part2 = definition.createPart();
//			part2.setName(ffOutput.getName()+"Part");
//			// 设置 part2 的 Schema Type 
//			part2.setTypeName(ffOutput.getDataType());
//			
//			responseMsg.addPart(part2);
//		}


		//二、 创建 portType
		PortType portType = definition.createPortType();
		portType.setQName(new QName(tns, _interface.getName()+"_PortType"));
		// 创建 Operation
		Operation operation = definition.createOperation();
		operation.setName(operationDef.getOperationName());
		// 创建 Input，并设置 Input 的 message
		Input input = definition.createInput();
		input.setName(operationDef.getOperationName()+"Request");
		input.setMessage(requestMsg);
		// 创建 Output，并设置 Output 的 message
		Output output = definition.createOutput();
		output.setName(operationDef.getOperationName()+"Response");
		output.setMessage(responseMsg);
		// 设置 Operation 的输入，输出，操作类型
		operation.setInput(input);
		operation.setOutput(output);
		operation.setStyle(OperationType.REQUEST_RESPONSE);
		// 这行语句很重要 ！
		operation.setUndefined(false);
		portType.addOperation(operation);
		portType.setUndefined(false);
		definition.addPortType(portType);

		//三、创建绑定（binding）
		Binding binding = definition.createBinding();
		binding.setQName(new QName(tns, _interface.getName()+"_Binding"));
		// 创建SOAP绑定（SOAP binding）
		SOAPBinding soapBinding = new SOAPBindingImpl();
		// 设置 style = "document"
		soapBinding.setStyle("document");
		// 设置 SOAP传输协议 为 HTTP
		soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
		// soapBinding 是 WSDL 中的扩展元素，
		// 为 binding 添加扩展元素 soapBinding
		binding.addExtensibilityElement(soapBinding);
		// 设置绑定的端口类型
		binding.setPortType(portType);
		// 创建绑定操作（Binding Operation）
		BindingOperation bindingOperation = definition.createBindingOperation();
		// 创建 bindingInput
		BindingInput bindingInput = definition.createBindingInput();
		bindingInput.setName(operationDef.getOperationName()+"Request");
		// 创建 SOAP body ，设置 use = "literal"
		SOAPBody soapBody1 = new SOAPBodyImpl();
		soapBody1.setUse("literal");
		bindingInput.addExtensibilityElement(soapBody1);
		BindingOutput bindingOutput = definition.createBindingOutput();
		bindingOutput.setName(operationDef.getOperationName()+"Response");
		SOAPBody soapBody2 = new SOAPBodyImpl();
		soapBody2.setUse("literal");
		bindingOutput.addExtensibilityElement(soapBody2);
		// 设置 bindingOperation 的名称，绑定输入 和 绑定输出
		bindingOperation.setName(operationDef.getOperationName());
		bindingOperation.setBindingInput(bindingInput);
		bindingOperation.setBindingOutput(bindingOutput);
		binding.addBindingOperation(bindingOperation);
		// 这行语句很重要 ！
		binding.setUndefined(false);
		definition.addBinding(binding);

		//四、创建Service及port
		// 创建 service
		Service service = definition.createService();
		service.setQName(serviceQName);
		// 创建服务端口 port
		Port port = definition.createPort();
		// 设置服务端口的 binding，名称，并添加SOAP地址
		port.setBinding(binding);
		port.setName(portQName.getLocalPart());

		SOAPAddress soapAddress = new SOAPAddressImpl();
		soapAddress.setLocationURI(address);
		port.addExtensibilityElement(soapAddress);
		service.addPort(port);
		definition.addService(service);

		// 打印刚创建的 WSDL
		String wsdlFileName = fullDirName+File.separator+serviceQName.getLocalPart()+".wsdl";
		File f = new File(wsdlFileName);
		if (f.exists()){
			boolean b = f.delete();
			if (!b){
				throw new IOException("Can NOT delete old wsdl file '"+wsdlFileName+"'");
			}
		}
		
		FileOutputStream fOut = new FileOutputStream(f);
		WSDLWriter writer = wsdlFactory.newWSDLWriter();		
		writer.writeWSDL(definition,fOut);
		fOut.flush();

		
		StreamSource streamSource = new StreamSource(f);
		result.add(streamSource);
		
		return result;
	}
*/
}
