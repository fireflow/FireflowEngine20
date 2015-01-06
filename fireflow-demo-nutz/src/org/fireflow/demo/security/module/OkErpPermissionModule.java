package org.fireflow.demo.security.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.OkErpPermission;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@At("/Permissions")
@IocBean(fields={"dao"})
public class OkErpPermissionModule extends EntityService<OkErpPermission>{

    private static final Log log = Logs.get();
	
	@At
	public Object list(@Param("page") int page ,@Param("rows") int rows){
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<OkErpPermission> list = dao().query(OkErpPermission.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(OkErpPermission.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}
	
	@At
	public boolean add(@Param("..") OkErpPermission obj){
		try{
			dao().insert(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
	
	@At
	public boolean delete(@Param("..") OkErpPermission obj){
		try{
			dao().delete(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
	
	@At
	public boolean update(@Param("..") OkErpPermission obj){
		try{
			dao().update(obj);
			return true;
		}catch (Throwable e) {
			log.error(Utils.exceptionStackToString(e));
			return false;
		}
	}
}