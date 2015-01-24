package org.fireflow.demo.workflow.ext;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.hr.bean.Organization;
import org.fireflow.demo.security.bean.UserRole;
import org.fireflow.demo.workflow.WorkflowUtil;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.OUSystemConnectorEmptyImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

public class OkErpOUSystemConnector extends OUSystemConnectorEmptyImpl {
	private FireflowDemoDao dao = null;

	public FireflowDemoDao getDao() {
		return dao;
	}

	public void setDao(FireflowDemoDao dao) {
		this.dao = dao;
	}
	
	public Dao dao(){
		return dao;
	}

	@Override
	public List<Department> findAllTopDepartments() {
		List<Organization> orgList = dao().query(Organization.class, Cnd.where("parentCode","=","0"));
		List<Department> result = new ArrayList<Department>();
		
		if (orgList!=null){
			for (Organization org : orgList){
				Department dept = WorkflowUtil.convertAppDept2FireflowDept(org);
				result.add(dept);
			}
		}
		return result;
	}

	@Override
	public List<Department> findChildDepartments(String parentId) {
		List<Organization> orgList = dao().query(Organization.class, Cnd.where("parentCode","=",parentId));
		List<Department> result = new ArrayList<Department>();
		
		if (orgList!=null){
			for (Organization org : orgList){
				Department dept = WorkflowUtil.convertAppDept2FireflowDept(org);
				result.add(dept);
			}
		}
		return result;
	}

	@Override
	public Department findDepartmentById(String id) {
		Organization org = dao().fetch(Organization.class, id);
		
		if (org!=null){
			Department dept = WorkflowUtil.convertAppDept2FireflowDept(org);
			return dept;
		}
		return null;
	}

	@Override
	public Group findGroupById(String id) {
		org.fireflow.demo.security.bean.Group org = dao().fetch(org.fireflow.demo.security.bean.Group.class, id);
		
		if (org!=null){
			Group dept = WorkflowUtil.convertAppGroup2FireflowGroup(org);
			return dept;
		}
		return null;
	}

	@Override
	public Role findRoleById(String id) {
		org.fireflow.demo.security.bean.Role org = dao().fetch(org.fireflow.demo.security.bean.Role.class, id);
		
		if (org!=null){
			Role dept = WorkflowUtil.convertAppRole2FireflowRole(org);
			return dept;
		}
		return null;
	}

	@Override
	public User findUserById(String userId) {
		org.fireflow.demo.security.bean.User appUser = dao().fetch(org.fireflow.demo.security.bean.User.class, userId);
		
		if (appUser!=null){
			User u = WorkflowUtil.convertAppUser2FireflowUser(appUser);
			return u;
		}
		return null;
	}

	/**
	 * TODO 暂不递归查找用户
	 */
	@Override
	public List<User> findUsersInDepartment(String deptId) {
		List<org.fireflow.demo.security.bean.User> uList = dao().query(org.fireflow.demo.security.bean.User.class,
				Cnd.where("orgCode","=",deptId));
		List<User> result = new ArrayList<User>();
		if (uList!=null){
			for (org.fireflow.demo.security.bean.User u: uList){
				User fireUser = WorkflowUtil.convertAppUser2FireflowUser(u);
				result.add(fireUser);
			}

		}
		return result;
	}

	@Override
	public List<User> findUsersInGroup(String groupId) {
		List<org.fireflow.demo.security.bean.User> uList = dao().query(org.fireflow.demo.security.bean.User.class,
				Cnd.where("groupCode","=",groupId));
		List<User> result = new ArrayList<User>();
		if (uList!=null){
			for (org.fireflow.demo.security.bean.User u: uList){
				User fireUser = WorkflowUtil.convertAppUser2FireflowUser(u);
				result.add(fireUser);
			}
		}
		return result;
	}

	@Override
	public List<User> findUsersInRole(String roleId) {
		String sqlStr = "select t.*,'' as roleNames from T_security_user t,T_security_user_role t2 where t.login_name=t2.user_code and t2.role_code=@roleCode";
		
		Sql sql = Sqls.create(sqlStr);
		sql.params().set("roleCode", roleId);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(org.fireflow.demo.security.bean.User.class));
		dao.execute(sql);
		List<org.fireflow.demo.security.bean.User> list = sql.getList(org.fireflow.demo.security.bean.User.class);

		List<User> result = new ArrayList<User>();
		if (list!=null){
			for (org.fireflow.demo.security.bean.User u: list){
				User fireUser = WorkflowUtil.convertAppUser2FireflowUser(u);
				result.add(fireUser);
			}
		}
		return result;
	}
	
	
}
