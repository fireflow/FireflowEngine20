package org.fireflow.service.file.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.Message;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.OutputAssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.AbsTestContext;
import org.fireflow.service.file.FileObject;
import org.fireflow.service.java.JavaService;
import org.firesoa.common.schema.NameSpaces;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml", 
		"classpath:/org/fireflow/service/file/reader/FileReadServiceInvokerTest-context.xml"
                              })
public class FileReadServiceInvokerTest extends AbsTestContext {
	private static final String originalFilesDir = System.getProperty("user.dir")+File.separator+"original_files";
	private static final String targetFilesDir = System.getProperty("user.dir")+File.separator+"target_files";
	private static final String bakupDir = System.getProperty("user.dir")+File.separator+"bakup_files";
	
	private static final String textFileName_1 = "text_file_1.txt";
	private static final String textFileName_2 = "text_file_2.txt";
	
	private static final String textFileName_3 = "text_file_3.proerties";
	private static final String textFileName_4 = "text_file_4.proerties";
	
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	
	private static long lastModified = 0;
	
	private static void deleteFile(File f){
		if (f.isFile()) f.delete();
		else if (f.isDirectory()){
			File[] files = f.listFiles();
			if (files==null || files.length==0){
				f.delete();
			}else{
				for (File ftmp : files){
					deleteFile(ftmp);
				}
				f.delete();
			}
		}
	}
	/**
	 * 准备初始化数据，方便测试
	 */
	@BeforeClass
	public static void beforeClass(){
		File f = new File(originalFilesDir);
		if (f.exists()){
			deleteFile(f);
		}		
		f.mkdir();
		
		f = new File(targetFilesDir);
		if (f.exists()){
			deleteFile(f);
		}		
		f.mkdir();
		
		f = new File(bakupDir);
		if (f.exists()){
			deleteFile(f);
		}		
		f.mkdir();
		
		try{
			f = new File(originalFilesDir+File.separator+textFileName_1);
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write("This is Text file 1; 这是文件1".getBytes("UTF-8"));
			fOut.flush();
			fOut.close();
			lastModified = f.lastModified();
		}catch(IOException e){
			e.printStackTrace();
		}

		try {
			Thread.currentThread().sleep(3*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			f = new File(originalFilesDir+File.separator+textFileName_2);
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write("This is Text file 2; 这是文件2".getBytes("UTF-8"));
			fOut.flush();
			fOut.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try {
			Thread.currentThread().sleep(3*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			f = new File(originalFilesDir+File.separator+textFileName_3);
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write("#注释\n username=a \n pwd=123".getBytes("UTF-8"));
			fOut.flush();
			fOut.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try {
			Thread.currentThread().sleep(3*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			f = new File(originalFilesDir+File.separator+textFileName_4);
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write("#注释\n filename=文件2.properties".getBytes("UTF-8"));
			fOut.flush();
			fOut.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除测试数据
	 */
	@AfterClass
	public static void afterClass(){
		File f = new File(originalFilesDir);
		if (f.exists()){
			deleteFile(f);
		}		
		
		f = new File(targetFilesDir);
		if (f.exists()){
			deleteFile(f);
		}		
		
		f = new File(bakupDir);
		if (f.exists()){
			deleteFile(f);
		}		
	}
	
	@Test
	public void testInvoke() {
		final ServiceDef service = this.buildService1();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("readFile",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(1, inputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"java.lang.Long"),inputs.get(0).getDataType());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(1, outputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.engine.invocation.Message"), outputs.get(0).getDataType());
		
		//

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("readFile");
//					serviceBinding.setOperation(operations.get(0));

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					//arg0
					AssignmentImpl inputAssignment = new AssignmentImpl();
					//貌似org.apache.commons.beanutils.MethodUtils.invokeMethod不能用null,做参数
					//String jsBody = "null";
					String jsBody = Long.toString(lastModified);
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("JavaScript");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);
					
					//构造输出映射
					List<Assignment> outputAssignments = new ArrayList<Assignment>();
					
					//  1、 输出到实例级别的流程变量
					OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
					ExpressionImpl expression = new ExpressionImpl();
					expression.setLanguage("xpath");
					expression.setBody("outputs/"+operations.get(0).getOutputs().get(0).getName());
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/processVars/result");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.engine.invocation.Message",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					serviceBinding.setOutputAssignments(outputAssignments);
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, null);
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}

				catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		

		//验证
		//1、验证processInstance中是否有result
		Object result = procInst.getVariableValue(session, "result");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Message<?>);
		List<FileObject> fileObjects = ((Message<List<FileObject>>)result).getPayload();
		Assert.assertEquals(1,fileObjects.size());
		
		String fName = fileObjects.get(0).getFileName();
		Assert.assertEquals(textFileName_2, fName);
		
		File f = new File(originalFilesDir+File.separator+textFileName_2+FileReadServiceDef.READ_FILE_SUFFIX);
		Assert.assertTrue(f.exists());

		lastModified = Long.parseLong((String)((Message<List<FileObject>>)result).getHeaders().get("NEXT_START_TIME"));
	}

	public ServiceDef buildService1(){
		FileReadServiceDef svc = new FileReadServiceDef();
		svc.setName("FileReadService_1");
		svc.setDisplayName("测试读文件服务");
		svc.setDescription("This is a file read service");
		svc.setBizCategory("test\\modules\\filereadservice");
		
		svc.setDirectory(originalFilesDir);
		svc.setBakupDirectory(bakupDir);
		
		svc.setFileNameSuffix(".txt");
		
		svc.setStrategyAfterReading(FileReadServiceDef.RENAME_AFTER_READING);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		return svc;
	}
	
	public ServiceDef buildService2(){
		JavaService svc = new JavaService();
		svc.setName("java_service_2");
		svc.setDisplayName("测试Calculator2");
		svc.setDescription("This is a java service");
		svc.setBizCategory("test\\modules\\java");
		svc.setJavaBeanName("calculator");
		
		svc.setInvokerBeanName("javaServiceInvoker");
		
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.fireflow.service.java.mock.Calculator");
		svc.setInterface(javaInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		return svc;
	}
}
