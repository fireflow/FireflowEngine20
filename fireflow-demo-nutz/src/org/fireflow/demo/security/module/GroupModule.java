package org.fireflow.demo.security.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.common.bean.JtableOptions;
import org.fireflow.demo.hr.bean.Organization;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.misc.ZTreeNode;
import org.fireflow.demo.security.bean.Group;
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

@At("/module/Group")
@IocBean(fields={"dao"})
public class GroupModule extends EntityService<Group>{

    private static final Log log = Logs.get();
	
	@At
	@Ok("json")
	public Map<String,Object> list(@Param("page") int page ,@Param("rows") int rows,@Param("jtSorting") String jtSorting,@Param("..") Group cndGroupObject){
		if (rows < 1)
			rows = 10;
		Map<String,Object> result = new HashMap<String,Object>();
		Pager pager = dao().createPager(page/rows+1, rows);
		//组织查询条件
		
		Cnd cnd = Cnd.NEW();
		cnd.where();
		
		if (cndGroupObject!=null ){
			if (cndGroupObject.getName()!=null && !cndGroupObject.getName().trim().equals("")){
				cnd.and("name", "like", "%"+cndGroupObject.getName()+"%");
			}
			if (cndGroupObject.getOrgCode()!=null && !cndGroupObject.getOrgCode().trim().equals("")){
				cnd.and("orgCode","=",cndGroupObject.getOrgCode());
			}
		}
		
		//组织排序条件
		Utils.makeJTableOrderBy(cnd,jtSorting);
		//Cnd.wrap("ORDER BY "+jtSorting);


		int groupcount = dao().count(Group.class, cnd);
		List<Group> list = dao().query(Group.class, cnd, pager);
		if (pager != null) {
			pager.setRecordCount(groupcount);
			result.put("pager", pager);
		}
		//req.setAttribute("list", list);
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORDS_KEY, list);
		result.put(MainModule.JTABLE_TOTAL_RECORD_COUNT, groupcount);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Group_edit.jsp")
	public Map<String,Object> Groupedit(){
		List<Organization> orglist = new ArrayList<Organization>();
		List<Group> groupList = new ArrayList<Group>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			groupList = dao().query(Group.class, null);
			orglist = dao().query(Organization.class,null);
			result.put("groupList", groupList);
			result.put("orglist", orglist);
		}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
		}
		return result;
	}
	@At
	@Ok("jsp:/template/system/security/Group_list.jsp")
	public Map<String,Object> Grouplist(){
		List<Organization> orglist = new ArrayList<Organization>();
		List<Group> groupList = new ArrayList<Group>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			groupList = dao().query(Group.class, null);
			orglist = dao().query(Organization.class,null);
			result.put("groupList", groupList);
			result.put("orglist", orglist);
		}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
		}
		return result;
	}
	
	/**
	 * 
	 * @param groupCode
	 * @return
	 */
	@At
	@Ok("jsp:/template/system/security/_group_members_.jsp")
	public Map<String,Object> getGroupMembersAsCheckbox(@Param("groupCode")String groupCode,@Param("roleCode")String roleCode){
		List<User> allMembers = dao().query(User.class, Cnd.where("groupCode","=",groupCode));
	
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("allMembers", allMembers);
		
		Group group = dao().fetch(Group.class, groupCode);
		result.put("group", group);
		
		Role role = dao().fetch(Role.class, roleCode);
		result.put("role", role);
		
		
		//查找已经存在的角色成员
		List<UserRole> userRoleList = dao().query(UserRole.class, Cnd.where("roleCode","=",roleCode)
					.and("groupCode","=",groupCode));

		List<String> allCheckbox = new ArrayList<String>();
		for (User u : allMembers){
			boolean exist = false;
			if (userRoleList!=null && userRoleList.size()>0){

				for (UserRole ur : userRoleList){
					if (u.getLoginName().equals(ur.getUserCode())){
						exist = true;
						break;
					}
				
				}
				
			}
			if (exist){
				
				String s = "<input checked=\"checked\" type=\"checkbox\" name=\"userCode\" value=\""+
						u.getLoginName()+"\">"+u.getName()+"</td>";
				allCheckbox.add(s);
			}
			else{

				String s = "<input  type=\"checkbox\" name=\"userCode\" value=\""+
						u.getLoginName()+"\">"+u.getName()+"</td>";
				allCheckbox.add(s);
			}
		}

		
		
		result.put("allCheckbox", allCheckbox);
		
		
		return result;
	}
	
	/**
	 * 查找组织机构中的所有群组
	 * @param orgCode
	 * @return
	 */
	@At
	@Ok("jsp:/template/system/security/_group_list_of_organization_.jsp")
	public Map<String,Object> getGroupForOrganization(@Param("orgCode")String orgCode){
		List<Group> groupList = dao().query(Group.class, Cnd.where("orgCode","=",orgCode).asc("code"));
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("groupList", groupList);
		
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> loadGroup(){
		List<Group> grouplist = new ArrayList<Group>();
		List<JtableOptions> grouplist1 = new ArrayList<JtableOptions>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			grouplist = dao().query(Group.class,null);
			for(int i=0;i<grouplist.size();i++){
				JtableOptions t =new JtableOptions();
				t.setDisplayText(grouplist.get(i).getName());
				t.setValue(grouplist.get(i).getCode()+"");
				grouplist1.add(t);
			}
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_OPTIONS_KEY, grouplist1);
			}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "获取用户组失败");
		}
		return result;
	}
	
	@At
	@Ok("json")
	public Map<String,Object> loadOrg(){
		List<Organization> orglist = new ArrayList<Organization>();
		List<JtableOptions> orglist1 = new ArrayList<JtableOptions>();
		Map<String,Object> orgmap = new HashMap<String,Object>();
		try{
			orglist = dao().query(Organization.class,null);
			for(int i=0;i<orglist.size();i++){
				JtableOptions t =new JtableOptions();
				t.setDisplayText(orglist.get(i).getName());
				t.setValue(orglist.get(i).getCode()+"");
				orglist1.add(t);
			}
			orgmap.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			orgmap.put(MainModule.JTABLE_OPTIONS_KEY, orglist1);
			}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			orgmap.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			orgmap.put(MainModule.JTABLE_MESSAGE_KEY, "获取组织名称失败");
		}
		return orgmap;
	}
	
	
	@At
	@Ok("json")
	public Map<String,Object> loadParentCode(@Param("code") String code , @Param("orgCode") String orgCode){
		List<Group> orglist = new ArrayList<Group>();
		List<JtableOptions> orglist1 = new ArrayList<JtableOptions>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			if(StringUtils.isBlank(orgCode) || "0".equals(orgCode)){
				orglist = dao().query(Group.class,null);
			}else{
				orglist = dao().query(Group.class,Cnd.where("orgCode", "=", orgCode));
			}
			//必须录入，不能选择“无”
//			JtableOptions jt =new JtableOptions();
//			jt.setDisplayText("无");
//			jt.setValue("0");
//			orglist1.add(jt);
			for(int i=0;i<orglist.size();i++){
				JtableOptions t =new JtableOptions();
				if(!orglist.get(i).getCode().toString().equals(code)){
					t.setDisplayText(orglist.get(i).getName());
					t.setValue(orglist.get(i).getCode()+"");
					orglist1.add(t);
				}
			}
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_OPTIONS_KEY, orglist1);
			}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "获取用户组失败");
		}
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Group_authorize.jsp")
	public Object gotoAuthorize(){
		List<Organization> orglist = new ArrayList<Organization>();
		List<Group> groupList = new ArrayList<Group>();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			orglist = dao().query(Organization.class,null);
			groupList = dao().query(Group.class, null);
			result.put("groupList", groupList);
			result.put("orglist", orglist);
		}
		catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
		}
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Group_authorize_function.jsp")
	public Map<String,Object> gotoAuthorizeFunction(@Param("code") String code){
		Group group = dao().fetch(Group.class, code);
		List<SysFunction> list = dao().query(SysFunction.class, Cnd.NEW().asc("code"));
		List<OkErpPermission> plist = dao().query(OkErpPermission.class, Cnd.where("granteeCode", "=", code)
				.and("granteeType","=",OkErpPermission.GRANTEE_TYPE_GROUP).asc("functionCode"));
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
		if(group != null){
			result.put("name", group.getName());
		}
		result.put("funlist", list);
		return result;
	}
	
	@At
	@Ok("jsp:/template/system/security/Group_authorize.jsp")
	public Map<String,Object> saveAuthorize(@Param("code") String code,@Param("funcodes") String funcodes){
		Group group = dao().fetch(Group.class, code);
		if(group != null){
			//第一步，先清理之前的权限
			dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", group.getCode()).and("granteeType","=",OkErpPermission.GRANTEE_TYPE_GROUP));
			
			//第二步，按提交的funcode赋予新的权限
			if(StringUtils.isNotBlank(funcodes)){
				StringTokenizer tokenizer = new StringTokenizer(funcodes,",");
				while(tokenizer.hasMoreTokens()){
					String code1 = tokenizer.nextToken();
					SysFunction sysFunction=(SysFunction)dao().fetch(SysFunction.class,  Cnd.where("code", "=", code1));
					if(sysFunction != null){
						OkErpPermission permission = new OkErpPermission();
						permission.setGranteeCode(group.getCode());
						permission.setGranteeName(group.getName());
						permission.setFunctionCode(sysFunction.getCode());
						permission.setFunctionName(sysFunction.getName());
						permission.setGranteeType(OkErpPermission.GRANTEE_TYPE_GROUP);
						
						User currentOperator = Utils.getCurrentUser();
						permission.setLastUpdatePerson(currentOperator.getName());
						
						dao().insert(permission);
					}
				}
			}
			
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_MESSAGE_KEY,"更新成功！");
		return result;
	}
	
	
	@At
	//@Ok("jsp:/template/system/security/Group_authorize.jsp")
	public Map<String,Object> saveAuthorizeByParentGroup(String code,String parentCode){
		Group group = dao().fetch(Group.class, code);
		//Group parentGroup = dao().fetch(Group.class,parentCode);
		List<OkErpPermission> ep= dao().query(OkErpPermission.class, Cnd.where("granteeCode", "=", parentCode).and("granteeType", "=", OkErpPermission.GRANTEE_TYPE_GROUP));
		if(group != null){
			//第一步，先清理之前的权限
			dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", group.getCode()));
			
			//第二步，按提交的funcode赋予新的权限
			if(ep!=null && ep.size()>0){
				for(OkErpPermission ep1: ep){
					String code1 = ep1.getFunctionCode();
					SysFunction sysFunction=(SysFunction)dao().fetch(SysFunction.class,  Cnd.where("code", "=", code1));
					if(sysFunction != null){
						OkErpPermission permission = new OkErpPermission();
						permission.setGranteeCode(group.getCode());
						permission.setGranteeName(group.getName());
						permission.setFunctionCode(sysFunction.getCode());
						permission.setFunctionName(sysFunction.getName());
						permission.setGranteeType(OkErpPermission.GRANTEE_TYPE_GROUP);
						
						User currentOperator = Utils.getCurrentUser();
						permission.setLastUpdatePerson(currentOperator.getName());
						
						dao().insert(permission);
					}
				}
			}
			
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_MESSAGE_KEY,"更新成功！");
		return result;
	}
	
	
	@At
	@Ok("json")
	public Map<String,Object> add(@Param("..") Group obj){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			obj.setLastUpdatePerson(Utils.getCurrentUser().getName());
			//根据orgCode关联orgName
			Organization org = dao().query(Organization.class, Cnd.wrap("code = '"+obj.getOrgCode()+"'")).get(0);
			obj.setOrgName(org.getName());
			FireflowDemoDao dao = new FireflowDemoDao();
			dao = (FireflowDemoDao)dao();
			obj.setCode(dao.generateGroupCode());
			Group g = dao().insert(obj);
			//根据parentGroup赋予用户组权限，//TODO ,将父权限复制给子节点，特别不合理！ 2014-09-06,非也
//			if(!obj.getParentCode().equals("0")){
//				saveAuthorizeByParentGroup(obj.getCode(),obj.getParentCode());
//			}
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_RECORD_KEY, g);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "用户组增加失败");
			return result;
		}
	}
	
	@At
	@Ok("json")
	public Map<String,Object> delete(@Param("..") Group obj){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			Group g = dao().fetch(Group.class,obj.getId());
			//首先检查该群组中还有没有用户
			List<User> uList = dao().query(User.class, Cnd.where("groupCode","=",g.getCode()));
			if (uList!=null && uList.size()>0){
				result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
				result.put(MainModule.JTABLE_MESSAGE_KEY, "还有属于该群组的用户，不能删除群组。");
				return result;
			}
			//
			
			dao().clear(OkErpPermission.class, Cnd.where("granteeCode", "=", g.getCode()).and("granteeType", "=", "G"));
			dao().delete(obj);
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "用户组删除失败");
			return result;
		}
	}
	
	@At
	@Ok("json")
	public Map<String,Object> update(@Param("..") Group obj){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			User currentUser = Utils.getCurrentUser();
			obj.setLastUpdatePerson(currentUser.getName());
			Organization org = dao().query(Organization.class, Cnd.wrap("code = '"+obj.getOrgCode()+"'")).get(0);
			obj.setOrgName(org.getName());
			Group oldGroup = dao().fetch(Group.class,obj.getCode());
			dao().update(obj);
			//根据parentGroup修改用户组权限;//TODO ,将父权限复制给子节点，特别不合理！ 2014-09-06,非也
//			if(!oldGroup.getParentCode().equals(obj.getParentCode())){
//				saveAuthorizeByParentGroup(obj.getCode(),obj.getParentCode());
//			}
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_OK);
			return result;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			result.put(MainModule.JTABLE_RESULT_KEY, MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "用户组更新失败");
			return result;
		}
	}
	
	@At
	@Ok("json")
	public List<ZTreeNode> loadChildrenAsZTreeNodes(HttpServletRequest req,@Param("id") String id,@Param("level") String level,
			@Param("nodeType")String nodeType,@Param("parentId")String parentId){
		List<ZTreeNode> result = new ArrayList<ZTreeNode>();
		String contextPath = req.getContextPath();
		if (id==null || level==null || id.trim().equals("") || level.trim().equals("")){
			//返回顶层
			Group topGroup = dao().fetch(Group.class, Cnd.where("parentCode","=","0"));
			if (topGroup!=null ){
				ZTreeNode top = new ZTreeNode();
				top.setId(topGroup.getCode());
				top.setName(topGroup.getName());
				top.setParent(true);
				top.setNocheck(false);
				top.setOpen(true);
				top.setNodeType(ZTreeNode.NODE_TYPE_GROUP);
				top.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/1_close.png");
				top.setParentId("0");
				
				result.add(top);
				
				
				
				//获得最下级的Group，下级group是这个公司的顶级group
				List<Group> groupsList = dao().query(Group.class, Cnd.where("parentCode", "=", topGroup.getCode()));
				
				if (groupsList!=null && groupsList.size()>0){
					for(Group g : groupsList){
						ZTreeNode node = new ZTreeNode();
						node.setId(g.getCode());
						node.setName(g.getName());
						node.setParent(true);
						node.setNocheck(true);
						node.setNodeType(ZTreeNode.NODE_TYPE_GROUP);
						node.setParentId(top.getId());
						node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/group_1.gif");
						top.getChildren().add(node);
					}
				}
				
				// 获得下级的ROLE（POSITION)
				List<RoleBelongs> positionList = dao().query(RoleBelongs.class,
						Cnd.where("orgGroupCode", "=", topGroup.getCode()).asc("roleCode"));
				if (positionList != null) {
					for (RoleBelongs r : positionList) {
						ZTreeNode node = new ZTreeNode();
						node.setId(r.getRoleCode());
						node.setName(r.getRoleName());
						node.setParent(true);
						node.setNocheck(false);
						node.setParentId(top.getId());
						node.setIcon(contextPath
								+ "/static/lib/ztree/zTreeStyle/img/diy/position.png");
						node.setNodeType(ZTreeNode.NODE_TYPE_ROLE);
						top.getChildren().add(node);
					}
				}
				
				
				
				//直接挂在公司下的用户
				List<User> usersList = dao().query(User.class,  Cnd.where("groupCode", "=", topGroup.getCode())
						.asc("employeeId"));
				
				if (usersList!=null && usersList.size()>0){
					for (User u : usersList){
						ZTreeNode node = new ZTreeNode();
						node.setId(u.getLoginName());
						node.setName(u.getName());
						node.setParent(false);
						node.setNocheck(false);
						node.setParentId(top.getId());
						node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/person_1.gif");
						node.setNodeType(ZTreeNode.NODE_TYPE_USER);
						top.getChildren().add(node);
					}
				}
			}

			return result;
		}

		else if (ZTreeNode.NODE_TYPE_GROUP.equals(nodeType)){
			//获得Group的下级Group和User
			List<Group> groupsList = dao().query(Group.class, Cnd.where("parentCode", "=", id)
														.asc("code"));
			
			if (groupsList!=null && groupsList.size()>0){
				for(Group g : groupsList){
					ZTreeNode node = new ZTreeNode();
					node.setId(g.getCode());
					node.setName(g.getName());
					node.setParent(true);
					node.setParentId(id);
					node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/group_1.gif");
					node.setNocheck(false);
					node.setNodeType(ZTreeNode.NODE_TYPE_GROUP);
					result.add(node);
				}
			}
			
			//获得role数据，即岗位数据
			List<RoleBelongs> positionList = dao().query(RoleBelongs.class, Cnd.where("orgGroupCode","=",id)
																.asc("roleCode"));
			if (positionList!=null){
				for (RoleBelongs r : positionList){
					ZTreeNode node = new ZTreeNode();
					node.setId(r.getRoleCode());
					node.setName(r.getRoleName());
					node.setParent(true);
					node.setNocheck(false);
					node.setParentId(id);
					node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/position.png");
					node.setNodeType(ZTreeNode.NODE_TYPE_ROLE);
					result.add(node);
				}
			}
			
			
			List<User> usersList = dao().query(User.class,  Cnd.where("groupCode", "=", id)
					.asc("employeeId"));
			
			if (usersList!=null && usersList.size()>0){
				for (User u : usersList){
					ZTreeNode node = new ZTreeNode();
					node.setId(u.getLoginName());
					node.setName(u.getName());
					node.setParent(false);
					node.setNocheck(false);
					node.setParentId(id);
					node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/person_1.gif");
					node.setNodeType(ZTreeNode.NODE_TYPE_USER);
					result.add(node);
				}
			}
			
			return result;
		}
		else if (ZTreeNode.NODE_TYPE_ROLE.equals(nodeType)){
			//首先根据parentId检测group，
			
			List<User> candidates = null;
			candidates = dao().query(User.class, Cnd.where("groupCode","=",parentId).asc("employeeId"));
			
			List<UserRole> userRoles = dao().query(UserRole.class, Cnd.where("roleCode","=",id)
					.and("groupCode","=",parentId));
			
			if (candidates!=null && candidates.size()>0 && userRoles!=null && userRoles.size()>0){
				for (User u : candidates){
					boolean isIt = false;
					for (UserRole ur : userRoles){
						if (ur.getUserCode().equals(u.getLoginName())){
							isIt = true;
							break;
						}
					}
					if (isIt){
						ZTreeNode node = new ZTreeNode();
						node.setId(u.getLoginName());
						node.setName(u.getName());
						
						node.setParent(false);
						node.setNocheck(false);
						node.setParentId(id);
						node.setIcon(contextPath+"/static/lib/ztree/zTreeStyle/img/diy/person_ref.gif");
						node.setNodeType(ZTreeNode.NODE_TYPE_USER);
						result.add(node);
					}
				}
			}
		}
		return result;
	}
}