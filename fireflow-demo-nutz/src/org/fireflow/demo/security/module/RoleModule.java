package org.fireflow.demo.security.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.OkErpPermission;
import org.fireflow.demo.security.bean.Role;
import org.fireflow.demo.security.bean.RoleBelongs;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.security.bean.UserRole;
import org.fireflow.demo.system.bean.SysFunction;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

@At("/module/Role")
@IocBean(fields={"dao"})
public class RoleModule extends EntityService<Role>{

    private static final Log log = Logs.get();
	
	@At
	@Ok("json")
	public Object list(@Param("page") int page ,
			@Param("rows") int rows,@Param("jtSorting") String jtSorting,
			@Param("..")Role role){
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
		
		if (role!=null ){
			if (StringUtils.isNotBlank(role.getName())){
				cnd.and("name","like", "%" + role.getName()+"%");
			}
		}
		
		//组织排序条件
		Utils.makeJTableOrderBy(cnd,jtSorting);
		
		List<Role> list = dao().query(Role.class, cnd, pager);
		
		int roleCount = dao().count(Role.class, cnd);
		
		if (pager != null) {
			pager.setRecordCount(roleCount);
			//pager.setRecordCount(dao().count(User.class));
			result.put("pager", pager);
		}
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, roleCount);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/_position_list_of_group_.jsp")
	public Map<String,Object> getGroupPositions(@Param("groupCode")String groupCode){
		List<RoleBelongs> list = dao().query(RoleBelongs.class, Cnd.where("orgGroupCode","=",groupCode));
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("positionList", list);
		return result;
	}
	
	/**
	 * 获得所有的角色
	 * @return
	 */
	@At
	@Ok("jsp:/template/system/security/_all_available_roles_.jsp")
	public Map<String,Object> getAvailablePositions(@Param("groupCode")String groupCode){
		Map<String,Object> result = new HashMap<String,Object>();
		//先查询出所有的角色是岗位的
		List<Role> allRoles = dao().query(Role.class, Cnd.where("isPosition","=",1));
		Map<String,Role> roleMap = new HashMap<String,Role>();
		if(allRoles != null && allRoles.size() > 0){
			for(Role role:allRoles){
				roleMap.put(role.getCode(), role);
			}
			
			//移除该用户组已经赋予的岗位
			List<RoleBelongs> rbList = dao().query(RoleBelongs.class, Cnd.where("orgGroupCode", "=", groupCode)); 
			if(rbList != null && rbList.size() > 0){
				for(RoleBelongs rb:rbList){
					if(roleMap.get(rb.getRoleCode()) != null){
						Role ro = roleMap.get(rb.getRoleCode());
						allRoles.remove(ro);
					}
				}
			}
		}
		result.put("allRoles", allRoles);
		
		return result;
	}
	
	/**
	 * 用户组管理：删除岗位
	 * @param groupCode
	 * @param roleCode
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> deletePosition(@Param("groupCode")final String groupCode,@Param("roleCode")final String roleCode){
		Map<String,Object> result = new HashMap<String,Object>();
		if (roleCode==null || roleCode.trim().equals("")){
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);	
			result.put(MainModule.JTABLE_MESSAGE_KEY, "没有选择被删除的岗位");
			return result;
		}

		try{
			Trans.exec(new Atom(){
			    public void run() {
			    	RoleBelongs roleBelongs = dao().fetch(RoleBelongs.class, Cnd.where("orgGroupCode","=",groupCode)
							.and("roleCode","=",roleCode));
					
					if (roleBelongs!=null){
						//先删除用户角色表里面，该岗位和用户组对应的的数据
						dao().clear(UserRole.class, Cnd.where("roleCode", "=", roleCode).and("groupCode", "=", groupCode));
						
						dao().delete(roleBelongs);
					}
			    }
			});
			
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);	
			result.put(MainModule.JTABLE_MESSAGE_KEY, "删除岗位失败，错误信息是："+e.getMessage());
			return result;
		}
	}
	
	/**
	 * 增加岗位
	 * @param obj
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String,Object> addGroupPosition(@Param("groupCode") String groupCode,@Param("roleCode") String roleCode){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			RoleBelongs belongs = new RoleBelongs();
			belongs.setOrgGroupCode(groupCode);
			belongs.setRoleCode(roleCode);
			belongs.setLastUpdatePerson(Utils.getCurrentUser().getName());
			
			dao().insert(belongs);
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);	
			result.put(MainModule.JTABLE_MESSAGE_KEY, "增加岗位失败，错误信息是："+e.getMessage());
			return result;
		}
	}
	
	@At
	@Ok("json")
	public Map<String,Object> delete(@Param("code")final String code){
		Map<String,Object> result = new HashMap<String,Object>();
		
		try{
			Trans.exec(new Atom(){
			    public void run() {
			    	//先删除用户角色表里面，该角色的数据
					dao().clear(UserRole.class, Cnd.where("roleCode", "=", code));
					
					//删除用户组和岗位表数据
					dao().clear(RoleBelongs.class, Cnd.where("roleCode", "=", code));
					
					//再删除角色
					dao().delete(Role.class, code);
			    }
			});
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "删除角色失败，"+e.getMessage());
			
			return result;
		}
	}
	
	@At
	@Ok("jsp:/template/system/security/_edit_role_.jsp")
	public Map<String,Object> getRoleForEdit(@Param("code")String code){
		Map<String,Object> result = new HashMap<String,Object>();
		Role role = dao().fetch(Role.class, code);
		
		result.put("role", role);
		
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> update(@Param("..") Role obj){
		Map<String,Object> result = new HashMap<String,Object>();

		try{
			obj.setLastUpdatePerson(Utils.getCurrentUser().getName());
			dao().update(obj);
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {	
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "更新角色失败，"+e.getMessage());
		
			return result;
		}
	}
	
	/**
	 * 插入角色，
	 * @param role
	 * @return
	 * @author 非也
	 */
	@At
	@Ok("json")
	public Map<String,Object> add(@Param("..") Role role){
		if (role.getCode()==null || role.getCode().equals("")){
			String roleCode = ((FireflowDemoDao)dao()).generateRoleCode();
			role.setCode(roleCode);
		}

		role.setLastUpdatePerson(Utils.getCurrentUser().getName());
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{

			dao().insert(role);
			

			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch(Throwable e){
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "新增角色失败，"+e.getMessage());
			
			return result;
		}
	}
	
	@At
	@Ok("jsp:/template/system/security/_members_of_role_.jsp")
	public Map<String,Object> getMemebersOfRole(@Param("roleCode") String roleCode){
		List<UserRole> userRoleList = dao().query(UserRole.class, Cnd.where("roleCode","=",roleCode));
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("userRoleList", userRoleList);
		result.put("roleCode", roleCode);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Role_edit.jsp")
	public Map<String,Object> gotoRoleEdit(){
		return null;//暂时无需任何逻辑
	}
	
	@At
	@Ok("jsp:/template/system/security/Role_list.jsp")
	public Map<String,Object> gotoRoleList(){
		return null;//暂时无需任何逻辑
	}
	
	@At
	@Ok("jsp:/template/system/security/Role_authorize.jsp")
	public Map<String,Object> gotoAuthorize(){
		return null;//暂时无需任何逻辑
	}
	

	@At
	@Ok("jsp:/template/system/security/Role_authorize_function.jsp")
	public Map<String,Object> gotoAuthorizeFunction(@Param("code") String code){
		Role role = dao().fetch(Role.class, code);
		List<SysFunction> list = dao().query(SysFunction.class, Cnd.NEW().asc("code"));
		List<OkErpPermission> plist = dao().query(OkErpPermission.class, Cnd.where("granteeCode", "=", code)
				.and("granteeType","=",OkErpPermission.GRANTEE_TYPE_ROLE).asc("functionCode"));
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
		
		result.put("code", code);
		if(role != null){
			result.put("name", role.getName());
		}
		result.put("funlist", list);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Role_authorize.jsp")
	public Map<String,Object> saveAuthorize(@Param("code")String code,@Param("funcodes")String funcodes){
		Role role = dao().fetch(Role.class, code);
		if(role != null){
			//第一步，先清理之前的权限
			dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", role.getCode()).and("granteeType","=",OkErpPermission.GRANTEE_TYPE_ROLE));
			
			//第二步，按提交的funcode赋予新的权限
			if(StringUtils.isNotBlank(funcodes)){
				StringTokenizer tokenizer = new StringTokenizer(funcodes,",");
				while(tokenizer.hasMoreTokens()){
					String funcCode = tokenizer.nextToken();
					SysFunction sysFunction=(SysFunction)dao().fetch(SysFunction.class,  Cnd.where("code", "=", funcCode));
					if(sysFunction != null){
						OkErpPermission permission = new OkErpPermission();
						permission.setGranteeCode(role.getCode());
						permission.setGranteeName(role.getName());
						permission.setFunctionCode(sysFunction.getCode());
						permission.setFunctionName(sysFunction.getName());
						permission.setGranteeType(OkErpPermission.GRANTEE_TYPE_ROLE);
						
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
	
}