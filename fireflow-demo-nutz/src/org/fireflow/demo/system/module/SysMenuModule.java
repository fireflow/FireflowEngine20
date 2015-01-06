package org.fireflow.demo.system.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.system.bean.SysMenu;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;

@At("/SysMenu")
@IocBean(fields={"dao"})
public class SysMenuModule extends EntityService<SysMenu>{

    private static final Log log = Logs.get();
    

    private static final String icon = "/static/images/function2.gif";
    
    @At
    @Ok("json")
    public List<MenuNode> loadMyMenus(HttpServletRequest req){
    	String contextPath = req.getContextPath();
    	Cookie stateCookie = Utils.getCookieByName(req, "okerp_1_navigator_state");
    	Map<String,String> isActiveStateMap = new HashMap<String,String>();
    	Map<String,String> isExpandedMap = new HashMap<String,String>();
    	if (stateCookie!=null){
    		String stateStr = stateCookie.getValue();
    		if (stateStr!=null){
    			StringTokenizer tokenizer = new StringTokenizer(stateStr,"_");
    			while (tokenizer.hasMoreTokens()){
    				String token = tokenizer.nextToken();
    				int idx1 = token.indexOf("-");

    				if (idx1>0){
    					String menuCode = token.substring(0,idx1);
    					String isActive = token.substring(idx1+1,idx1+2);
    					String isExpand = token.substring(idx1+2);
    					
    					isActiveStateMap.put(menuCode, isActive);
    					isExpandedMap.put(menuCode,isExpand);
    				}
    				
    			}
    		}
    	}
    	List<MenuNode> allMenu = new ArrayList<MenuNode>();
    	
    	List<SysMenu> list = queryChildren("m0");
    	//构造菜单树
    	if (list!=null){
    		for (SysMenu m : list){
    			MenuNode mn = new MenuNode();
    			mn.setId(m.getCode());
    			mn.setFolder(SysMenu.MENUGROUP_TYPE.equals(m.getType())?true:false);

    			mn.setFunctionCode(m.getFuncCode());
    			mn.setText(m.getName());
    			
    			if ("t".equals(isActiveStateMap.get(m.getCode()))){
    				mn.setActive(true);
    			}else{
    				mn.setActive(false);
    			}
    			if ("t".equals(isExpandedMap.get(m.getCode()))){
    				mn.setExpanded(true);
    			}
    			else{
    				mn.setExpanded(false);
    			}
    			
    			if (!mn.isFolder()){
        			String url = m.getUrl();
        			if (url!=null && !url.trim().equals("")){

            			if (url.startsWith("/")){
            				url = contextPath+url;
            			}else{
            				url = contextPath+"/"+url;
            			}        				

            			mn.setHref(url);             			
        			}
        			
        			mn.setIconUrl(contextPath+icon);
   				
    				//判断权限
    				Subject subject = SecurityUtils.getSubject();  
    				User curUser = (User)subject.getPrincipal();
    				//TODO root 拥有所有业务权限，这个待斟酌，目前用于测试方便。
    				if (curUser!=null && curUser.getLoginName().equals("root")){
    					allMenu.add(mn);
    				}
    				else if (subject.isPermitted(mn.getFunctionCode())){
    					allMenu.add(mn);
    				}
    			}else{
        			
    				loadChildMenuNodes(mn,contextPath,isActiveStateMap,isExpandedMap);
    				if (mn.getChildren().size()>0){//有子菜单才将他加入列表
    					allMenu.add(mn);
    				}
    			}
    			

    		}
    	}
    	
    	//检查菜单功能权限
    	
    	return allMenu;
    }
    
    protected void loadChildMenuNodes(MenuNode parent,String contextPath,Map<String,String> isActiveStateMap,
    		Map<String,String> isExpandedMap){
    	    	
    	List<SysMenu> list = queryChildren(parent.getId());
    	
    	if (list!=null){
    		for (SysMenu m : list){
    			MenuNode mn = new MenuNode();
    			mn.setId(m.getCode());
    			mn.setFolder(SysMenu.MENUGROUP_TYPE.equals(m.getType())?true:false);
    			mn.setFunctionCode(m.getFuncCode());
    			mn.setText(m.getName());
    			
    			if ("t".equals(isActiveStateMap.get(m.getCode()))){
    				mn.setActive(true);
    			}else{
    				mn.setActive(false);
    			}
    			if ("t".equals(isExpandedMap.get(m.getCode()))){
    				mn.setExpanded(true);
    			}
    			else{
    				mn.setExpanded(false);
    			}
    			
    			
    			if (!mn.isFolder()){
        			String url = m.getUrl();
        			if (url!=null && !url.trim().equals("")){
            			if (url.startsWith("/")){
            				url = contextPath+url;
            			}else{
            				url = contextPath+"/"+url;
            			}
            			mn.setHref(url);
        			}

        			mn.setIconUrl(contextPath+icon);
    				//判断权限
    				Subject subject = SecurityUtils.getSubject();  
    				User curUser = (User)subject.getPrincipal();
    				//TODO root 拥有所有业务权限，这个待斟酌，目前用于测试方便。
    				if (curUser!=null && curUser.getLoginName().equals("root")){
    					parent.getChildren().add(mn);
    				}
    				else if (subject.isPermitted(mn.getFunctionCode())){
    	    			parent.getChildren().add(mn);
    				}
    			}else{
        			
    				loadChildMenuNodes(mn,contextPath,isActiveStateMap,isExpandedMap);
    				if (mn.getChildren().size()>0){//有子菜单才将他加入列表
    					parent.getChildren().add(mn);
    				}
    			}
    		}
    	}
    }
	
    protected List<SysMenu> queryChildren(String pCode){
    	List<SysMenu> list = dao().query(SysMenu.class, Cnd.where("parentCode","=",pCode).asc("sort"));
    	
    	return list;
    }
    
	@At
	public Object list(@Param("page") int page ,@Param("rows") int rows){
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<SysMenu> list = dao().query(SysMenu.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(SysMenu.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}
	
	@At
	public boolean add(@Param("..") SysMenu obj){
		try{
			dao().insert(obj);
			return true;
		}catch (Throwable e) {
			if (log.isDebugEnabled())
				log.debug("E!!",e);
			return false;
		}
	}
	
	@At
	public boolean delete(@Param("..") SysMenu obj){
		try{
			dao().delete(obj);
			return true;
		}catch (Throwable e) {
			if (log.isDebugEnabled())
				log.debug("E!!",e);
			return false;
		}
	}
	
	@At
	public boolean update(@Param("..") SysMenu obj){
		try{
			dao().update(obj);
			return true;
		}catch (Throwable e) {
			if (log.isDebugEnabled())
				log.debug("E!!",e);
			return false;
		}
	}
}
class MenuNode{
	String id = null;
	
	List<MenuNode> children = new ArrayList<MenuNode>();
	
	boolean isFolder = false;
	
	boolean isExpanded = true;
	
	boolean isActive = false;
	
	String href = null;
	
	String hrefTarget = null;
	
	String functionCode = null;
	
	String text = null;
	
	String iconUrl = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<MenuNode> getChildren() {
		return children;
	}

	public void setChildren(List<MenuNode> children) {
		this.children = children;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHrefTarget() {
		return hrefTarget;
	}

	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public String getIconUrl(){
		return iconUrl;
	}
	
	public void setIconUrl(String iconUrl){
		this.iconUrl = iconUrl;
	}
}