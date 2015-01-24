package org.fireflow.demo.holiday.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.demo.holiday.bean.LeaveRequestDetail;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;

@At("/HrLeaveRequestDetail")
@IocBean(fields={"dao"})
public class LeaveRequestDetailModule extends EntityService<LeaveRequestDetail>{

    private static final Log log = Logs.get();
	
	@At
	public Object list(@Param("page") int page ,@Param("rows") int rows){
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<LeaveRequestDetail> list = dao().query(LeaveRequestDetail.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(LeaveRequestDetail.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}
	
	@At
	public boolean add(@Param("..") LeaveRequestDetail obj){
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
	public boolean delete(@Param("..") LeaveRequestDetail obj){
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
	public boolean update(@Param("..") LeaveRequestDetail obj){
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