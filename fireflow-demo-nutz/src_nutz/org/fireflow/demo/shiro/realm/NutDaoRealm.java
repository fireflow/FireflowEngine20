package org.fireflow.demo.shiro.realm;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.security.bean.Group;
import org.fireflow.demo.security.bean.OkErpPermission;
import org.fireflow.demo.security.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

public class NutDaoRealm extends AuthorizingRealm {
	
	private static final Log log = Logs.get();
	
	protected Dao dao;
	
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		
		User user = (User) principalCollection.getPrimaryPrincipal();

		if (user != null) {
			if (user.getStatus()!=user.STATUS_OK) 
				throw new LockedAccountException("Account [" + user.getName() + "] is locked.");
		}

		SimpleAuthorizationInfo  authorizationInfo = new SimpleAuthorizationInfo() ;
	
		Map<String,FunctionCodePermission> allFunctionCodePermissions = new HashMap<String,FunctionCodePermission>();
		
		Map<String,List<OkErpPermission>> permissions = ((FireflowDemoDao)dao()).findUserPermissions(user);
		Iterator<List<OkErpPermission>> iterPermissions = permissions.values().iterator();
		
		while (iterPermissions.hasNext()){
			List<OkErpPermission> list = iterPermissions.next();
			if (list!=null){
				for (OkErpPermission p : list){
					allFunctionCodePermissions.put(p.getFunctionCode(), new FunctionCodePermission(p.getFunctionCode()));
				}
			}
		}
		
		Set	set = new  HashSet(allFunctionCodePermissions.values());   	
		authorizationInfo.setObjectPermissions(set);
		
		
		
		//将角色名称列表设置到当前的SimpleAccount中，暂时不需要
//		authorizationInfo.setRoles(user.getRoleStrSet());
		//将角色名称列表设置到当前的SimpleAccount中，暂时不需要
//		authorizationInfo.setStringPermissions(user.getPermissionStrSet());
		
		return authorizationInfo;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)  {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		String username = upToken.getUsername();
		User user = dao().fetch(User.class, Cnd.where("login_name", "=", username));
		if (user != null) {
			if (user.getStatus()!=user.STATUS_OK) 
				throw new LockedAccountException("Account [" + username + "] is locked.");
			
			SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo (user,user.getPwd(),getName());
			authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getLoginName()+user.getSalt()));
			
			return authenticationInfo;
		}else{
			throw new UnknownAccountException("账户不存在");
		}

//		SimpleAuthenticationInfo

	}
	
	public NutDaoRealm() {
		this("nutz");
	}

	public NutDaoRealm(String name) {
		this.name = name;
	}
	
	private String name;
	
	@SuppressWarnings("unchecked")
	public Dao dao() {
		if (dao == null) {
			ServletContext servletContext = Mvcs.getServletContext();
			if (servletContext != null) {
				//也行我能直接拿到Ioc容器
				Ioc ioc = Mvcs.getIoc();
				if (ioc != null) {
					dao = ioc.get(Dao.class, daoBeanName);
					return dao;
				}
				else {
					//Search in servletContext.attr
					Enumeration<String> names = servletContext.getAttributeNames();
					while (names.hasMoreElements()) {
						String attrName = (String) names.nextElement();
						Object obj = servletContext.getAttribute(attrName);
						if (obj instanceof Ioc) {
							dao = ((Ioc)obj).get(Dao.class, daoBeanName);
							return dao;
						}
					}
					
					//还是没找到? 试试新版Mvcs.ctx
					ioc = Mvcs.ctx.getDefaultIoc();
					if (ioc != null) {
						dao = ioc.get(Dao.class, daoBeanName);
						return dao;
					}
				}
			}
			log.warn("No dao found!!");
			throw new RuntimeException("NutDao not found!!");
		}
		return dao;
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dao = new NutDao(dataSource);
	}
	
	private String daoBeanName = "dao";
	
	public void setDaoBeanName(String daoBeanName) {
		this.daoBeanName = daoBeanName;
	}
	
	//获取群组权限（包括该组的父组的权限）
	private String getCode(String code){
		if(StringUtils.isBlank(code)){
			return code;
		}else{
			Group group = dao().fetch(Group.class, code);
			if(group != null && StringUtils.isNotBlank(group.getParentCode()) && !"0".equals(group.getParentCode())){
				String tmp = "'"+code+"',"+getCode(group.getParentCode());
				return tmp;
			}
			return "'"+code+"'";
		}
	}
	
	public static void main(String[] args){
		Ioc ioc = new NutIoc(new JsonLoader("conf/core.js"));
		DataSource ds = ioc.get(DataSource.class);
		Dao dao = new NutDao(ds);
		
		//System.out.println(new NutDaoRealm().getCode("xingz",dao));
		
	}
}
