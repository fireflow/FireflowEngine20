package org.fireflow.samples.event;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.modules.instancemanager.event.AbsProcessInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent;
import org.fireflow.pdl.fpdl20.process.SubProcess;
import org.fireflow.samples.pojo.Company;
import org.fireflow.samples.pojo.Employee;

public class MyProcessEventListener extends AbsProcessInstanceEventListener {

	@Override
	protected void afterProcessInstanceEnd(ProcessInstanceEvent e) {
		ProcessInstance procInst = e.getSource();
		SubProcess subProcess = (SubProcess)e.getWorkflowElement();
		
		System.out.println("==调用事件MyProcessEventListener.afterProcessInstanceEnd(...)");
		System.out.println("====发起事件的流程实例是：id="+procInst.getId()+";displayName="+
				procInst.getProcessDisplayName()+"; state="+procInst.getState().getDisplayName());
		System.out.println("====该实例对应的流程定义是：id="+subProcess.getId());
		System.out.println("===============================================");

		super.afterProcessInstanceEnd(e);
	}

	/**
	 * ProcessInstance创建之后，Run之前会触发该事件
	 */
	@Override
	protected void onProcessInstanceCreated(ProcessInstanceEvent e) {
		ProcessInstance procInst = e.getSource();
		SubProcess subProcess = (SubProcess)e.getWorkflowElement();
		System.out.println("==调用事件MyProcessEventListener.onProcessInstanceCreated(...)");
		System.out.println("====发起事件的流程实例是：id="+procInst.getId()+";displayName="+
				procInst.getProcessDisplayName()+"; state="+procInst.getState().getDisplayName());
		System.out.println("====该实例对应的流程定义是：id="+subProcess.getId());
		System.out.println("====通过该事件，可以做一些复杂的初始化工作，比如初始化一个复杂类型的流程变量（请通过模拟数据查看本例的流程变量）……");
		System.out.println("===============================================");
		
		//初始化一个复杂的流程变量
		Company company = new Company();
		company.setId("company123");
		company.setName("FireSOA开源组织");
		company.setAddress("广州");
		
		List<Employee> allEmployees = new ArrayList<Employee>();
		
		Employee employee1 = new Employee();
		employee1.setId("1");
		employee1.setName("非也");
		employee1.setAge(99);
		employee1.setSalary(999999.9f);
		employee1.setDeptId("dept1");
		
		allEmployees.add(employee1);
		
		Employee employee2 = new Employee();
		employee2.setId("2");
		employee2.setName("太极");
		employee2.setAge(99);
		employee2.setSalary(999999.9f);
		employee2.setDeptId("dept2");
		
		allEmployees.add(employee2);
		
		Employee employee3 = new Employee();
		employee3.setId("3");
		employee3.setName("无极");
		employee3.setAge(999);
		employee3.setSalary(100000f);
		employee3.setDeptId("dept3");
		
		allEmployees.add(employee3);
		
		company.setAllEmployees(allEmployees);
		
		//对于Listener中抛出的异常，流程引擎自动将其全部“吃掉”，避免Listener中的错误影响流程执行。
		//所以，最好的方法是在Listener中自行处理异常信息。
		try {
			procInst.setVariableValue(e.getCurrentWorkflowSession(), "company", company);
		} catch (InvalidOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		super.onProcessInstanceCreated(e);
	}

}
