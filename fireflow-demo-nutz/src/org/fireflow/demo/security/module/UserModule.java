package org.fireflow.demo.security.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.common.bean.JtableOptions;
import org.fireflow.demo.hr.bean.Organization;
import org.fireflow.demo.misc.Message;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.Group;
import org.fireflow.demo.security.bean.OkErpPermission;
import org.fireflow.demo.security.bean.Role;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.security.bean.UserRole;
import org.fireflow.demo.system.bean.SysFunction;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

@At("/module/User")
@IocBean(fields={"dao"})
public class UserModule extends EntityService<User>{

    private static final Log log = Logs.get();
	
	@At
	@Ok("json")
	public Map<String,Object> list(@Param("page") int page ,@Param("rows") int rows,@Param("jtSorting") String jtSorting,@Param("..")User user ){
		/*if(user != null){
			System.out.println("page="+page+" rows="+rows+" sorting="+  jtSorting + " loginName="+user.getLoginName() + " name="+user.getName());
		}*/
		
		if (rows < 1)
			rows = 10;

		Map<String,Object> result = new HashMap<String,Object>();

		int pageNumber = 1;
		//由于jtable的page开始页是0开始的，nutz开始页为1，所以传进来的page需要加1
		page=page+1;
		pageNumber=page/rows + ((page%rows)== 0 ? 0:1);
		
		
		Pager pager = dao().createPager(pageNumber, rows);
		//注意jtSorting传进来的值lastUpdateTime DESC，但是nutz不认识lastUpdateTime字段，要写last_update_time，
		//但是页面jtable又不认识last_update_time，只好写在Cnd.wrap里面，不用传进来的值，不过排序的时候，
		//最好前台后台统一用哪个字符串排序。
		//List<User> list = dao().query(User.class, Cnd.wrap("ORDER BY last_update_time DESC "), pager);
		
		//组织查询条件
		
		Cnd cnd = Cnd.NEW();
		cnd.where();
		
		if (user!=null ){
			if (StringUtils.isNotBlank(user.getLoginName())){
				cnd.and("loginName","like", "%" + user.getLoginName()+"%");
			}
			if (StringUtils.isNotBlank(user.getName())){
				cnd.and("name","like","%" + user.getName()+"%");
			}
		}
		
		//组织排序条件
		Utils.makeJTableOrderBy(cnd,jtSorting);
		
		List<User> list = dao().query(User.class, cnd, pager);
		
		int usercount = dao().count(User.class, cnd);
		
		if (pager != null) {
			pager.setRecordCount(usercount);
			//pager.setRecordCount(dao().count(User.class));
			result.put("pager", pager);
		}
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, usercount);
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> loadlist(@Param("page") int page ,@Param("rows") int rows,HttpServletRequest req){
		Map<String,Object> result = new HashMap<String,Object>();
		List<User> list = dao().query(User.class, null);
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/User_list.jsp")
	public Object gotolist(HttpServletRequest req){
		
		return null;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> gotoadd(HttpServletRequest req){
		List<Organization> orglist = new ArrayList<Organization>();
		List<JtableOptions> orglist1 = new ArrayList<JtableOptions>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			orglist = dao().query(Organization.class,null);

			for(int i=0;i<orglist.size();i++){
				JtableOptions t =new JtableOptions();
				t.setDisplayText(orglist.get(i).getName());
				t.setValue(orglist.get(i).getCode()+"");
				orglist1.add(t);
			}
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put("Options", orglist1);
			result.put("orglist", orglist);
			}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
		}
		//req.setAttribute("orglist", orglist);
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> add(@Param("..")final User usr,@Param("roleCode")final String[] roleCode){
		User currentOperator = Utils.getCurrentUser();
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			//状态，0=禁用或者未激活，3=正常状态；如果所属组织被禁用，则一律不准登录。
			usr.setStatus(3);
			usr.setLastUpdatePerson(currentOperator.getName());
			//usr.setLastUpdateTime(new Date());//数据库自动生成，不用设置，非也20140725
			usr.setIsAdmin(0);//默认创建非管理员用户,非也20140725
			Utils.initUserPassword(usr, true);//初始化密码为123456,非也20140725
			
			if (usr.getGroupCode()==null || usr.getGroupCode().equals("")){
				usr.setGroupCode("0");//默认的群组是0，表示不分群组，非也20140725
			}
			
			if(!StringUtils.isBlank(usr.getOrgCode())){
				Organization org = dao().fetch(Organization.class, usr.getOrgCode());
				if(org != null && !StringUtils.isBlank(org.getName())){
					usr.setOrgName(org.getName());
				}
			}
			
			if(!StringUtils.isBlank(usr.getGroupCode())){
				Group group = dao().fetch(Group.class, usr.getGroupCode());
				if(group != null && !StringUtils.isBlank(group.getName())){
					usr.setGroupName(group.getName());
				}
			}
			
			Trans.exec(new Atom(){
			    public void run() {
			    	dao().insert(usr);
			    	if(roleCode != null && roleCode.length>0 ){
			    		for(String role : roleCode){
			    			UserRole userRole = new UserRole();
			    			userRole.setRoleCode(role);
			    			userRole.setUserCode(usr.getLoginName());
			    			userRole.setGroupCode(usr.getGroupCode());
			    			userRole.setLastUpdatePerson(Utils.getCurrentUser().getName());
			    			dao().insert(userRole);
			    		}
			    	}
			    }
			});
			
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_MESSAGE_KEY,"添加成功！");
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,"添加失败！");
			return result;
		}
	}
	
	@At
	@Ok("json")
	public Map<String,Object> delete(@Param("..") final User obj){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			Trans.exec(new Atom(){
			    public void run() {
			    	User user = dao().fetch(User.class, obj.getId());
			    	if(user != null && StringUtils.isNotBlank(user.getLoginName())){
			    		//删除用户角色表
			    		dao().clear(UserRole.class, Cnd.where("userCode", "=", user.getLoginName()));
			    	
			    		//删除用户权限表
			    		dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", user.getLoginName()));
			    	}
			    	
			    	dao().delete(obj);
			    }
			});
			
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,"删除失败！");			
			return result;
		}
	}
	
	@At
	@Ok("jsp:/template/system/security/_edit_user_table_.jsp")
	public Object loadEditUser(@Param("loginName")String loginName){
		Map<String, Object> result = new HashMap<String,Object>();
		Map<String, Object> userRoleMap = new HashMap<String,Object>();
		List<Organization> orglist = new ArrayList<Organization>();
		List<Group> grouplist = new ArrayList<Group>();
		List<Role> rolelist = new ArrayList<Role>();
		orglist = dao().query(Organization.class,null);
		grouplist = dao().query(Group.class,null);
		rolelist = dao().query(Role.class,null);
		result.put("orglist", orglist);
		result.put("grouplist", grouplist);
		result.put("rolelist", rolelist);
		User u = dao().fetch(User.class, loginName);
		List<UserRole> urList = dao().query(UserRole.class, Cnd.where("userCode", "=", loginName));
		if(urList != null && urList.size()>0){
			for(UserRole ur : urList){
				userRoleMap.put(ur.getRoleCode(), ur.getRoleCode());
			}
		}
		result.put("userRoleMap", userRoleMap);
		result.put("user", u);
		result.put("userRoleList", urList);
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> update(@Param("..")final User obj,@Param("roleCode")final String[] roleCode){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			if(obj != null){
				User user = dao().fetch(User.class, obj.getId());
				user.setName(obj.getName());
				user.setTel(obj.getTel());
				user.setGroupCode(obj.getGroupCode());
				
				User currentOperator = Utils.getCurrentUser();
				user.setLastUpdatePerson(currentOperator.getName());
				
				//OkErpDao okdao = (OkErpDao)dao();
				//会自动更新user.setLastUpdateTime(okdao.getSysDate());
				
				//关联插入组织名称，且不相等才查询数据库
				if(!StringUtils.isBlank(obj.getOrgCode()) && !obj.getOrgCode().equals(user.getOrgCode())){
					Organization org = dao().fetch(Organization.class, obj.getOrgCode());
					if(org != null && !StringUtils.isBlank(org.getName())){
						user.setOrgCode(obj.getOrgCode());
						user.setOrgName(org.getName());
					}
				}
				
				//关联插入用户组，且不相等才查询数据库
				if(!StringUtils.isBlank(obj.getGroupCode()) && !obj.getGroupCode().equals(user.getGroupCode())){
					Group group = dao().fetch(Group.class, obj.getGroupCode());
					if(group != null && !StringUtils.isBlank(group.getName())){
						obj.setGroupName(group.getName());
					}
				}
				
				obj.setLastUpdatePerson(Utils.getCurrentUser().getName());
				Trans.exec(new Atom(){
				    public void run() {
				    	dao().updateIgnoreNull(obj);
				    	//先删除原来的
		    			dao().clear(UserRole.class,Cnd.where("userCode", "=", obj.getLoginName()));
				    	if(roleCode != null){
				    		for(String role : roleCode){
				    			UserRole userRole = new UserRole();
				    			userRole.setRoleCode(role);
				    			userRole.setUserCode(obj.getLoginName());
				    			userRole.setGroupCode(obj.getGroupCode());
				    			userRole.setLastUpdatePerson(Utils.getCurrentUser().getName());
				    			//再插入新的
				    			dao().insert(userRole);
				    		}
				    	}
				    }
				});
				
				
				String tojson = Json.toJson(user);
				result.put(MainModule.JTABLE_RECORD_KEY,Json.toJson(tojson));
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
				return result;
			}else{
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
				result.put(MainModule.JTABLE_MESSAGE_KEY,"更新失败");
				return result;
			}
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,"更新失败");
			return result;
		}
	}
	
	/**
	 * 特别注意：
	 * 
	 * 此处用于处理登陆失败的场景，所以用@Ok标签指向登陆页面。
	 * @param req
	 * @param name
	 * @param passwd
	 * @return
	 * @throws Exception
	 */
	@At
	@Ok("jsp:/template/home/Login.jsp")
	public Map<String,Object> login(HttpServletRequest req,@Param("username")String name, 
			 @Param("password")String passwd)throws Exception{
		//登陆操作已经通过FormAuthonticationFilter完成，这里仅需要做一些登陆失败的处理即可
		String errorClassName = (String) req.getAttribute("shiroLoginFailure");
		Map<String, Object> returnMap = new HashMap<String, Object>();

		if (UnknownAccountException.class.getName().equals(errorClassName)) {
			Message msg = new Message(false, "账户不存在！");
			returnMap.put(MainModule.MESSAGE_OBJECT, msg);
		} else if (IncorrectCredentialsException.class.getName().equals(
				errorClassName)) {
			Message msg = new Message(false, "密码错误！");
			returnMap.put(MainModule.MESSAGE_OBJECT, msg);
		}else if (LockedAccountException.class.getName().equals(errorClassName)){
			Message msg = new Message(false, "账户已锁定！");
			returnMap.put(MainModule.MESSAGE_OBJECT, msg);
		}
		
		else if (errorClassName != null) {
			Message msg = new Message(false, "未知错误：" + errorClassName);
			returnMap.put(MainModule.MESSAGE_OBJECT, msg);
		}

		return returnMap;

	}
	
	@At
	@Ok("json")
	public Map<String,Object> gotoaddGroup(String orgCode){		
		List<Group> grouplist = new ArrayList<Group>();
		List<JtableOptions> glist1 = new ArrayList<JtableOptions>();
		Map<String,Object> orgmap = new HashMap<String,Object>();
		try{
			if(StringUtils.isBlank(orgCode) || "0".equals(orgCode)){
				grouplist = dao().query(Group.class,null);
			}else{
				grouplist = dao().query(Group.class,Cnd.where("orgCode", "=", orgCode));
			}
			JtableOptions jt =new JtableOptions();
			jt.setDisplayText("无");
			jt.setValue("0");
			glist1.add(jt);
			
			if(grouplist != null){
				for(int i=0;i<grouplist.size();i++){
					JtableOptions t =new JtableOptions();
					t.setDisplayText(grouplist.get(i).getName());
					t.setValue(grouplist.get(i).getCode()+"");
					glist1.add(t);
				}
			}
			orgmap.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			orgmap.put("Options", glist1);
			}catch (Throwable e) {
				log.error(Utils.exceptionStackToString(e));
				orgmap.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
				orgmap.put(MainModule.JTABLE_MESSAGE_KEY,"获取用户组失败");
			}

		return orgmap;
	}

	@At
	@Ok("jsp:/template/system/security/User_edit.jsp")
	public Object gotoEdit(HttpServletRequest req){
		List<Organization> orglist = new ArrayList<Organization>();
		List<Group> grouplist = new ArrayList<Group>();
		List<Role> rolelist = new ArrayList<Role>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			orglist = dao().query(Organization.class,null);
			grouplist = dao().query(Group.class,null);
			rolelist = dao().query(Role.class,null);
			result.put("orglist", orglist);
			result.put("grouplist", grouplist);
			result.put("rolelist", rolelist);
			}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
		}
		//req.setAttribute("orglist", orglist);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Userpassword_reset.jsp")
	public Map<String,Object> gotoPassReset(){
		return null;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> passReset(String loginName,String resetup){
		System.out.println(" loginName="+loginName + " resetup="+resetup);
		Map<String,Object> result = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(resetup)&& "1".equals(resetup)){
			if(StringUtils.isNotBlank(loginName)){
				User user = dao().fetch(User.class, loginName);
				if(user!=null){
					//默认重置密码123456
					user.setPwd(Utils.encryptNewPassword(user, "123456"));
					dao().update(user);
					
					result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
					result.put(MainModule.JTABLE_MESSAGE_KEY,"重置密码成功！");
				}else{
					result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
					result.put(MainModule.JTABLE_MESSAGE_KEY,"重置密码失败！用户不存在！");
				}
			}else{
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
				result.put(MainModule.JTABLE_MESSAGE_KEY,"重置密码失败！用户不存在！");
			}
		}
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/User_authorize.jsp")
	public Object gotoAuthorize(){
		return null;
	}
	
	@At
	@Ok("jsp:/template/system/security/User_authorize_function.jsp")
	public Map<String,Object> gotoAuthorizeFunction(String loginName){
		User user = dao().fetch(User.class, loginName);
		List<SysFunction> list = dao().query(SysFunction.class, Cnd.NEW().asc("code"));
		
		List<OkErpPermission> plist = dao().query(OkErpPermission.class, Cnd.where("granteeCode", "=", user.getLoginName()).asc("functionCode"));
		Map<String,String> map = new HashMap<String,String>();
		
		if(plist != null && plist.size()>0){
			for(OkErpPermission permission: plist){
				map.put(permission.getFunctionCode(), permission.getFunctionCode());
			}
		}
		
		if(list != null && list.size()>0){
			for(SysFunction sysFunction: list){
				if(map.get(sysFunction.getCode()) != null){
					sysFunction.setIschecked(true);
				}
			}
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("loginName", loginName);
		if(user != null){
			result.put("yourName", user.getName());
		}
		result.put("funlist", list);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/User_authorize.jsp")
	public Map<String,Object> saveAuthorize(String loginName,String funcodes){
		User user = dao().fetch(User.class, loginName);
		if(user != null){
			//第一步，先清理之前的权限
			dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", user.getLoginName()));
			
			//第二步，按提交的funcode赋予新的权限
			if(StringUtils.isNotBlank(funcodes)){
				StringTokenizer tokenizer = new StringTokenizer(funcodes,",");
				while(tokenizer.hasMoreTokens()){
					String code = tokenizer.nextToken();
					SysFunction sysFunction=(SysFunction)dao().fetch(SysFunction.class,  Cnd.where("code", "=", code));
					if(sysFunction != null){
						OkErpPermission permission = new OkErpPermission();
						permission.setGranteeCode(user.getLoginName());
						permission.setGranteeName(user.getName());
						permission.setFunctionCode(sysFunction.getCode());
						permission.setFunctionName(sysFunction.getName());
						permission.setGranteeType(OkErpPermission.GRANTEE_TYPE_USER);
						
						User currentOperator = Utils.getCurrentUser();
						permission.setLastUpdatePerson(currentOperator.getName());
						
						dao().insert(permission);
					}
				}
			}
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_MESSAGE_KEY,"更新权限成功！");
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/User_resetPwdSelf.jsp")
	public Map<String,Object> resetSelfPwd(String oldPwd,String newPwd,String confirmPwd){
		if(StringUtils.isNotBlank(oldPwd) && StringUtils.isNotBlank(newPwd)&&StringUtils.isNotBlank(confirmPwd)){
			Map<String,Object> result = new HashMap<String,Object>();
			if(!newPwd.equals(confirmPwd)){
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
				result.put(MainModule.JTABLE_MESSAGE_KEY,"修改失败：新密码和确认密码不一致！请重新输入！");
				return result;
			}
			User currentOperator = Utils.getCurrentUser();
			
			String oldencrypt = Utils.encryptNewPassword(currentOperator, oldPwd);
			if(!oldencrypt.equals(currentOperator.getPwd())){
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
				result.put(MainModule.JTABLE_MESSAGE_KEY,"修改失败：输入的旧密码与原来不一致！请重新输入！");
				return result;
			}
			
			//根据新密码修改
			currentOperator.setPwd(Utils.encryptNewPassword(currentOperator, newPwd));
			dao().update(currentOperator);
			
			
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_MESSAGE_KEY,"修改成功！");
			return result;
		}
		return null;
	}
	
	
	public List<SysFunction> allSysFunction(){
		List<SysFunction> list = dao().query(SysFunction.class, Cnd.NEW().asc("code"));
		return list;
	}
	
	@At
	@Ok("jsp:/template/system/security/_show_user_permissions_.jsp")
	public Map<String,Object> getAllPermissionsOfUser(@Param("loginName")String loginName){
		User u = dao().fetch(User.class, loginName);
		
		Map<String,List<OkErpPermission>> permissions = ((FireflowDemoDao)dao()).findUserPermissions(u);
		
		Map<SysFunction,String> mergedPermissions = new TreeMap<SysFunction,String>();
		
		Iterator<String> keyIter = permissions.keySet().iterator();
		
		while(keyIter.hasNext()){
			String key = keyIter.next();
			
			List<OkErpPermission> pList = permissions.get(key);
			
			if (pList!=null){
				for (OkErpPermission p :pList){
					SysFunction func = new SysFunction();
					func.setCode(p.getFunctionCode());
					func.setName(p.getFunctionName());
					
					String existKeys = mergedPermissions.get(func);

					if (existKeys==null){
						mergedPermissions.put(func, key);
					}
					else{
						existKeys = existKeys+", "+key;
						mergedPermissions.put(func, existKeys);
					}
				}
			}
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result .put("mergedPermissions", mergedPermissions);
		result.put("user", u);
		
		return result;
	}
}