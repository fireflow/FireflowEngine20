package org.fireflow.demo.workflow.ext;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.fireflow.demo.MainModule;
import org.fireflow.demo.misc.Utils;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.modules.instancemanager.event.AbsActivityInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent;
import org.fireflow.pdl.fpdl.process.Activity;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

public class OnActivityCreatedListener extends AbsActivityInstanceEventListener {
	public static final String PROCESS_STATUS = "PROCESS_STATUS";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String TABLE_COLUMN = "processStatus";
	public static final String CODE = "CODE";
	public static final String CLASS_NAME = "CLASS_NAME";
	
	private String daoBeanName = MainModule.DAO_BEAN_NAME;
	Dao dao = null;
	
	private static final Log log = Logs.get();
	
	@Override
	protected void onActivityInstanceCreated(ActivityInstanceEvent e) {
		ActivityInstance activityInstance = e.getSource();
		
		Activity activity = (Activity)e.getWorkflowElement();
		
		if (activity==null)return;
		
		String className = activity.getExtendedAttribute(CLASS_NAME);
		if (className==null || className.trim().equals("")){
			return;//什么都不做
		}
		
		 try {
			Class dealClass = Class.forName(className);
			
			String bizId = activityInstance.getBizId();
			
			String processStatus = activity.getExtendedAttribute(PROCESS_STATUS);
			if (processStatus==null || processStatus.trim().equals("")){
				processStatus = activity.getDisplayName();
			}
			
			String code = activity.getExtendedAttribute(CODE);
			if(code == null && "".equals(code.trim())){
				return;
			}
			
			//TODO 根据tableName,bizId，更新数据库的processStatus字段
			//dao()..update(tableName, chain, cnd)；
			dao().update(dealClass, Chain.make(TABLE_COLUMN, processStatus), Cnd.where(code, "=", bizId));
			//dao().update(tableName, Chain.make(TABLE_COLUMN, processStatus), Cnd.where(code, "=", bizId));
			super.onActivityInstanceCreated(e);
			
		} catch (ClassNotFoundException e1) {
			log.error(Utils.exceptionStackToString(e1));
		}
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
	
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
			throw new RuntimeException("NutDao not found!!");
		}
		return dao;
	}
}
