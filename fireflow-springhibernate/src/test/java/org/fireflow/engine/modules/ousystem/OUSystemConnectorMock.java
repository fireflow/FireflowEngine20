package org.fireflow.engine.modules.ousystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.ousystem.impl.OUSystemConnectorEmptyImpl;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;

/**
 * 测试用组织机构子系统链接器
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class OUSystemConnectorMock extends OUSystemConnectorEmptyImpl {
	Map<String,User> usersList = new HashMap<String,User>();
	Map<String,List<User>> departmentUsersMap = new HashMap<String,List<User>>();
	Map<String,List<User>> roleUsersMap = new HashMap<String,List<User>>();
	Map<String,List<User>> groupUsersMap = new HashMap<String,List<User>>();
	
	
	public OUSystemConnectorMock(){
		//构造用户列表
		UserImpl u = new UserImpl();
		u.setDeptId("ResearchDept");
		u.setDeptName("研发部");
		u.setId("zhangsan");
		u.setName("张三");		
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("ResearchDept");
		u.setDeptName("研发部");
		u.setId("lisi");
		u.setName("李四");
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("ResearchDept");
		u.setDeptName("研发部");
		u.setId("wangwu");
		u.setName("王五");
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("ResearchDept");
		u.setDeptName("研发部");
		u.setId("Mgr_C");
		u.setName("陈经理");
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("TestDept");
		u.setDeptName("测试部");
		u.setId("liuyan");
		u.setName("柳叶");
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("Sales");
		u.setDeptName("市场部");
		u.setId("wanghaha");
		u.setName("王哈哈");
		usersList.put(u.getId(), u);
		
		u = new UserImpl();
		u.setDeptId("Sales");
		u.setDeptName("市场部");
		u.setId("limou");
		u.setName("李某");
		usersList.put(u.getId(), u);
		
		//构造depargmentUser Map
		List<User> deptUsersList = new ArrayList<User>();
		for (User tmpU : usersList.values()){
			if (tmpU.getDeptId().equals("ResearchDept")){
				deptUsersList.add(tmpU);
			}
		}
		departmentUsersMap.put("ResearchDept", deptUsersList);
		
		deptUsersList = new ArrayList<User>(); ;
		for (User tmpU : usersList.values()){
			if (tmpU.getDeptId().equals("TestDept")){
				deptUsersList.add(tmpU);
			}
		}
		departmentUsersMap.put("TestDept", deptUsersList);
		
		deptUsersList = new ArrayList<User>();
		for (User tmpU : usersList.values()){
			if (tmpU.getDeptId().equals("Sales")){
				deptUsersList.add(tmpU);
			}
		}
		departmentUsersMap.put("Sales", deptUsersList);
		
		
		//构造roleUser Map
		List<User> tmp = new ArrayList<User>();
		tmp.add(usersList.get("zhangsan"));
		tmp.add(usersList.get("lisi"));
		tmp.add(usersList.get("wangwu"));
		roleUsersMap.put("coder", tmp);
		
		tmp = new ArrayList<User>();
		tmp.add(usersList.get("Mgr_C"));
		roleUsersMap.put("manager",tmp );
		
	}
	
	public User login(String userId,String password){
		return findUserById(userId);
	}
	public User findUserById(String userId) {
		
		return usersList.get(userId);
	}

	public List<User> findUsersInDepartment(String deptId) {
		
		return departmentUsersMap.get(deptId);
	}

	public List<User> findUsersInRole(String roleId) {
		
		return roleUsersMap.get(roleId);
	}

	public List<User> findUsersInGroup(String groupId) {
		
		return groupUsersMap.get(groupId);
	}


}
