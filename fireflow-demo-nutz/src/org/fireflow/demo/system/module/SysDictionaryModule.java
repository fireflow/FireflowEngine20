package org.fireflow.demo.system.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fireflow.demo.system.bean.SysDictionary;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@At("/SysDictionary")
@IocBean(fields={"dao"})
public class SysDictionaryModule extends EntityService<SysDictionary>{

    private static final Log log = Logs.get();
	
	@At
	public Object list(@Param("page") int page ,@Param("rows") int rows){
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<SysDictionary> list = dao().query(SysDictionary.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(SysDictionary.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}
	
	@At
	public boolean add(@Param("..") SysDictionary obj){
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
	public boolean delete(@Param("..") SysDictionary obj){
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
	public boolean update(@Param("..") SysDictionary obj){
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