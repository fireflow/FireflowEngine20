package org.fireflow.demo.security.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.fireflow.demo.MainModule;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.UserRole;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;


@At("/module/UserRole")
@IocBean(fields={"dao"})
public class UserRoleModule extends EntityService<UserRole>{
	public static final String DEFAULT_GROUP_CODE = "0";

    private static final Log log = Logs.get();
	
	@At
	public Object list(@Param("page") int page ,@Param("rows") int rows){
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<UserRole> list = dao().query(UserRole.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(UserRole.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}
	
	@At
	public boolean add(@Param("..") UserRole obj){
		try{
			dao().insert(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
	
	@At
	public boolean delete(@Param("..") UserRole obj){
		try{
			dao().delete(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
	
	@At
	public boolean update(@Param("..") UserRole obj){
		try{
			dao().update(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
	
	@At
	@Ok("json")
	public Map<String,Object> updateUserRole(HttpServletRequest req){
		final String roleCode = req.getParameter("roleCode");
		String tmp = req.getParameter("groupCode");
		if (tmp==null || tmp.trim().equals("")){
			tmp = DEFAULT_GROUP_CODE;
		}
		final String groupCode = tmp;
		
//		final List<UserRole> oldUserRoleList = dao().query(UserRole.class, Cnd.where("roleCode","=",roleCode));
		
		String[] userCodeList = req.getParameterValues("userCode");
		
		final List<UserRole> newUserRoleList = new ArrayList<UserRole>();
		if (userCodeList!=null){
			for (String userCode :userCodeList){
				UserRole ur = new UserRole();
				ur.setRoleCode(roleCode);
				ur.setUserCode(userCode);
				ur.setLastUpdatePerson(Utils.getCurrentUser().getName());
				ur.setGroupCode(groupCode);
				
				newUserRoleList.add(ur);
			}
		}
		
		Trans.exec(new Atom(){
		    public void run() {
		    	Sql sql = Sqls.create("delete from T_security_user_role where role_code='"+roleCode+"' and group_code='"+groupCode+"'");
		        dao().execute(sql);//先删除
		        
		        if (newUserRoleList!=null){
		        	for (UserRole ur: newUserRoleList){
		        		dao().insert(ur);
		        	}
		        }
		    }
		});

		Map<String,Object> result = new HashMap<String,Object>();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);

		return result;
	}
}