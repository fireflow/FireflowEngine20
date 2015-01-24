package org.fireflow.demo.holiday.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;

import org.fireflow.demo.MainModule;
import org.fireflow.demo.holiday.bean.Holiday;
import org.fireflow.demo.holiday.bean.LeaveRequest;
import org.fireflow.demo.holiday.bean.LeaveRequestDetail;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 请假申请完成后更新请假申请信息
 * 
 * @author apple
 *
 */
public class LeaveFormApproveOK {
	private String daoBeanName = MainModule.DAO_BEAN_NAME;;
	private Dao dao = null;
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
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
	/**
	 * 将借款申请的状态改为3，即审批通过状态。
	 * @param borrowCode
	 */
	public void updateHolidaysInfo(String billCode){
		final Dao tmpdao = dao();
		if (tmpdao==null){
			throw new NullPointerException("更新BorrowApplyStatusTo3状态值失败，无法获得dao对象。");
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		LeaveRequest leaveRequest = dao().fetch(LeaveRequest.class,billCode);
		
		String employeeId = leaveRequest.getEmployeeId();
		String leaveType = leaveRequest.getLeaveType();
		
		List<LeaveRequestDetail> detailList = dao().query(LeaveRequestDetail.class, Cnd.where("billCode","=",billCode));
		try{
			for (LeaveRequestDetail detail : detailList){
				
				Date leaveDate = detail.getLeaveDate();
				float totalDays = detail.getTimeSection()==2?1f:0.5f;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(leaveDate);
				int year = calendar.get(Calendar.YEAR);
				
				calendar.add(Calendar.DATE, -1);
				Date yesterday = calendar.getTime();
				List<Holiday> holidays = dao().query(Holiday.class, Cnd.where("employeeId","=",employeeId)
						.and("yearStart","<=",leaveDate).and("yearEnd",">=",yesterday));
				
				if (holidays==null || holidays.size()==0){
					//插入一条
					Holiday holiday = new Holiday();
					holiday.setEmployeeId(employeeId);
					try {
						holiday.setYearStart(format.parse(year+"-01-01"));
						holiday.setYearEnd(format.parse(year+"-12-31"));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					holiday.setLastUpdatePerson("sys");
					
					holidays.add(dao().insert(holiday));
					
					
				}
				
				Holiday hld = holidays.get(0);
				if (Holiday.HOLIDAY_PAID.equals(leaveType)){
					hld.setUsedPaidVacationDays(hld.getUsedPaidVacationDays()+totalDays);
				}else if (Holiday.HOLIDAY_ABSENCE.equals(leaveType)){
					hld.setUsedAbsenceLeaveDays(hld.getUsedAbsenceLeaveDays()+totalDays);
				}else if (Holiday.HOLIDAY_SICK.equals(leaveType)){
					hld.setUsedSickLeaveDays(hld.getUsedSickLeaveDays()+totalDays);
				}else if (Holiday.HOLIDAY_MARITAL.equals(leaveType)){
					hld.setUsedMaritalLeaveDays(hld.getUsedMaritalLeaveDays()+totalDays);
				}else if (Holiday.HOLIDAY_MATERNITY.equals(leaveType)){
					hld.setUsedMaternityLeaveDays(hld.getUsedMaternityLeaveDays()+totalDays);
				}else if (Holiday.HOLIDAY_FUNERAL.equals(leaveType)){
					hld.setUsedFuneralLeaveDays(hld.getUsedFuneralLeaveDays()+totalDays);
				}else{
					hld.setUsedAbsenceLeaveDays(hld.getUsedAbsenceLeaveDays()+totalDays);
				}
				
				dao().update(hld);
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
