package org.fireflow.demo.holiday.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowStatementLocalImpl;
import org.fireflow.demo.MainModule;
import org.fireflow.demo.FireflowDemoDao;
import org.fireflow.demo.common.bean.Dictionary;
import org.fireflow.demo.holiday.bean.Holiday;
import org.fireflow.demo.holiday.bean.LeaveRequest;
import org.fireflow.demo.holiday.bean.LeaveRequestDetail;
import org.fireflow.demo.misc.Utils;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.workflow.Constants;
import org.fireflow.demo.workflow.WorkflowUtil;
import org.fireflow.demo.workflow.ext.BusinessObjectWrappImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.model.InvalidModelException;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;

@At("/module/holiday/LeaveRequest")
@IocBean(fields = { "dao", "fireflowRuntimeContext" })
public class LeaveRequestModule extends EntityService<LeaveRequest> {
	private RuntimeContext fireflowRuntimeContext = null;

	public RuntimeContext getFireflowRuntimeContext() {
		return fireflowRuntimeContext;
	}

	public void setFireflowRuntimeContext(RuntimeContext fireflowRuntimeContext) {
		this.fireflowRuntimeContext = fireflowRuntimeContext;
	}

	private static final Log log = Logs.get();

	@At
	public Object list(@Param("page") int page, @Param("rows") int rows) {
		if (rows < 1)
			rows = 10;
		Pager pager = dao().createPager(page, rows);
		List<LeaveRequest> list = dao().query(LeaveRequest.class, null, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		if (pager != null) {
			pager.setRecordCount(dao().count(LeaveRequest.class));
			map.put("pager", pager);
		}
		map.put("list", list);
		return map;
	}

	// @At
	// public boolean delete(@Param("..") LeaveRequest obj){
	// try{
	// dao().delete(obj);
	// return true;
	// }catch (Throwable e) {
	// if (log.isDebugEnabled())
	// log.debug("E!!",e);
	// return false;
	// }
	// }

	/**
	 * 从待办列表打开请假单进行编辑
	 * 
	 * @return
	 */
	@At
	@Ok("jsp:/template/holiday/LeaveRequest_approve.jsp")
	public Map<String, Object> loadLeaveRequestForApprove(
			@Param("workItemId") String workItemId,
			@Param("billCode") String billCode,
			@Param("activityId") String activityId) {
		if (workItemId == null || workItemId.trim().equals("")) {
			throw new RuntimeException("没有发现workItemId,打开只读表单");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MainModule.WORKITEM_ID, workItemId);// 将workitem id传递到页面上
		result.put("activityId", activityId);

		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();

		LeaveRequest leaveRequest = dao().fetch(LeaveRequest.class, billCode);

		result.put("LeaveRequest", leaveRequest);

		// 获得假期类别字典
		List<Dictionary> leaveTypes = dao().query(Dictionary.class,
				Cnd.where("dicType", "=", "leave_type").asc("sort"));
		result.put("LeaveTypes", leaveTypes);

		// 查询当前用户的所有请假情况//
		Date now = ((FireflowDemoDao) dao()).getSysDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		List<Holiday> holidays = dao().query(
				Holiday.class,
				Cnd.where("employeeId", "=", demoUser.getEmployeeId()).and(
						Cnd.exps("yearStart", "<=", now).or("yearEnd", ">=",
								yesterday)));

		result.put("Holidays", holidays);

		return result;
	}

	/**
	 * 从待办列表打开请假单进行编辑
	 * 
	 * @return
	 */
	@At
	@Ok("jsp:/template/holiday/LeaveRequest_edit.jsp")
	public Map<String, Object> loadLeaveRequestForEdit(
			@Param("workItemId") String workItemId,
			@Param("billCode") String billCode) {
		if (workItemId == null || workItemId.trim().equals("")) {
			throw new RuntimeException("没有发现workItemId,打开只读表单");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MainModule.WORKITEM_ID, workItemId);// 将workitem id传递到页面上

		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();

		LeaveRequest leaveRequest = dao().fetch(LeaveRequest.class, billCode);

		result.put("LeaveRequest", leaveRequest);

		// 获得假期类别字典
		List<Dictionary> leaveTypes = dao().query(Dictionary.class,
				Cnd.where("dicType", "=", "leave_type").asc("sort"));
		result.put("LeaveTypes", leaveTypes);

		// 查询当前用户的所有请假情况//
		Date now = ((FireflowDemoDao) dao()).getSysDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		List<Holiday> holidays = dao().query(
				Holiday.class,
				Cnd.where("employeeId", "=", demoUser.getEmployeeId()).and(
						Cnd.exps("yearStart", "<=", now).or("yearEnd", ">=",
								yesterday)));

		result.put("Holidays", holidays);

		return result;
	}

	/**
	 * 更新请假单信息
	 * 
	 * @param obj
	 * @return
	 */
	@At
	@Ok("jsp:/template/holiday/LeaveRequest_edit.jsp")
	public Map<String, Object> update(@Param("billCode") String billCode,
			@Param("leaveType") String leaveType) {
		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();
		Map<String, Object> result = updateJson(billCode, leaveType);

		// 查询当前用户的所有请假情况
		Date now = ((FireflowDemoDao) dao()).getSysDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		List<Holiday> holidays = dao().query(
				Holiday.class,
				Cnd.where("employeeId", "=", demoUser.getEmployeeId()).and(
						Cnd.exps("yearStart", "<=", now).or("yearEnd", ">=",
								yesterday)));

		result.put("Holidays", holidays);
		return result;
	}

	/**
	 * 更新请假单信息
	 * 
	 * @param obj
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String, Object> approveJson(@Param("billCode") String billCode,
			@Param("activityId") String activityId,
			@Param("approveFlag") int approveFlag,
			@Param("approveInfo") String approveInfo) {
		Map<String, Object> result = new HashMap<String, Object>();

		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();
		try {
			LeaveRequest leaveRequest = dao().fetch(LeaveRequest.class,
					billCode);
			if (activityId != null && activityId.indexOf("DeptMgrApprove") >= 0) {
				// 部门经理审批
				leaveRequest.setDeptApproveFlag(approveFlag);
				leaveRequest.setDeptApproveInfo(approveInfo);
				leaveRequest.setDeptApproveTime(new Date());
				leaveRequest.setDeptMgrId(demoUser.getLoginName());
				leaveRequest.setDeptMgrName(demoUser.getName());
			} else if (activityId != null
					&& activityId.indexOf("DirectorApprove") >= 0) {
				leaveRequest.setDirectorApproveFlag(approveFlag);
				leaveRequest.setDirectorApproveInfo(approveInfo);
				leaveRequest.setDirectorApproveTime(new Date());
				leaveRequest.setDirectorId(demoUser.getLoginName());
				leaveRequest.setDirectorName(demoUser.getName());
			}
			dao().update(leaveRequest);
			result.put("LeaveRequest", leaveRequest);
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_OK);

			return result;
		} catch (Throwable e) {
			log.error("E!!", e);
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,
					"请假单审批信息保存失败，原因：" + e.getMessage());
			return result;
		}
	}

	/**
	 * 更新请假单信息
	 * 
	 * @param obj
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String, Object> updateJson(@Param("billCode") String billCode,
			@Param("leaveType") String leaveType) {
		Map<String, Object> result = new HashMap<String, Object>();

		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();
		try {
			// 获得假期类别字典
			List<Dictionary> leaveTypes = dao().query(Dictionary.class,
					Cnd.where("dicType", "=", "leave_type").asc("sort"));
			result.put("LeaveTypes", leaveTypes);

			LeaveRequest leaveRequest = dao().fetch(LeaveRequest.class,
					billCode);
			List<LeaveRequestDetail> list = dao().query(
					LeaveRequestDetail.class,
					Cnd.where("billCode", "=", billCode));

			Date fromDate = null;
			Date toDate = null;
			for (LeaveRequestDetail detail : list) {
				Date d = detail.getLeaveDate();
				if (fromDate == null || fromDate.after(d)) {
					fromDate = d;
				}
				if (toDate == null || toDate.before(d)) {
					toDate = d;
				}
			}

			leaveRequest.setFromDate(fromDate);
			leaveRequest.setToDate(toDate);
			leaveRequest.setLastUpdatePerson(demoUser.getName());
			leaveRequest.setLeaveType(leaveType);
			boolean find = false;
			for (Dictionary type : leaveTypes) {
				if (type.getDicKey().equals(leaveType)) {
					leaveRequest.setLeaveTypeName(type.getDicValue());
					find = true;
					break;
				}
			}
			if (!find) {
				leaveRequest.setLeaveTypeName(leaveType);
			}

			Float totalDays = this.calculateTotalLeaveDays(list);
			leaveRequest.setTotalDays(totalDays);
			dao().update(leaveRequest);

			result.put("LeaveRequest", leaveRequest);
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_OK);

			return result;
		} catch (Throwable e) {
			log.error("E!!", e);
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,
					"请假单保存失败，原因：" + e.getMessage());
			return result;
		}
	}

	/**
	 * 新增请假单，并创建流程
	 * 
	 * @param obj
	 * @return
	 */
	@At
	@Ok("jsp:/template/holiday/LeaveRequest_edit.jsp")
	public Map<String, Object> add() {
		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();

		Map<String, Object> result = new HashMap<String, Object>();
		LeaveRequest leaveRequest = null;
		try {
			Object obj = Mvcs.getHttpSession().getAttribute(
					MainModule.BIZ_OBJECT_IN_SESSION_KEY);

			// 1、首先保存业务数据
			if (obj != null && (obj instanceof LeaveRequest)) {
				leaveRequest = (LeaveRequest) obj;
				List<LeaveRequestDetail> list = leaveRequest
						.getLeaveRequestDetailList();

				Date fromDate = null;
				Date toDate = null;
				for (LeaveRequestDetail detail : list) {
					Date d = detail.getLeaveDate();
					if (fromDate == null || fromDate.after(d)) {
						fromDate = d;
					}
					if (toDate == null || toDate.before(d)) {
						toDate = d;
					}
				}

				leaveRequest.setFromDate(fromDate);
				leaveRequest.setToDate(toDate);
				leaveRequest.setLastUpdatePerson(demoUser.getName());

				dao().insert(leaveRequest);

				for (LeaveRequestDetail detail : list) {
					detail.setLastUpdatePerson(demoUser.getName());
					dao().insert(detail);
				}
			}

			// 2、然后启动流程
			final org.fireflow.engine.modules.ousystem.User currentUser = WorkflowUtil
					.getCurrentWorkflowUser();
			final WorkflowSession fireSession = WorkflowSessionFactory
					.createWorkflowSession(fireflowRuntimeContext, currentUser);
			final WorkflowStatement statement = fireSession
					.createWorkflowStatement();
			// 启动流程

			Map<String, Object> vars = new HashMap<String, Object>();
			BusinessObjectWrappImpl wrapper = new BusinessObjectWrappImpl();
			wrapper.setBizId(leaveRequest.getBillCode());
			wrapper.setBizClzName(LeaveRequest.class.getName());
			vars.put("LeaveRequestForm", wrapper);

			// 取得申请人得group id
			User user = dao().fetch(User.class,
					Cnd.where("employeeId", "=", leaveRequest.getEmployeeId()));

			if (user != null) {// 将申请人的群组号作为流程变量保存
				vars.put(Constants.VAR_NAME_APPLICANT_GROUP_CODE,
						user.getGroupCode());

			}

			ProcessInstance processInstance = statement.startProcess(
					"Leave_Request_Process", "Leave_Request_Process.main",
					leaveRequest.getBillCode(), vars);

			// 拿到当前的workItem
			List<WorkItem> wiList = ((WorkflowStatementLocalImpl) statement)
					.getNewWorkItemsForCurrentUser();
			if (wiList != null && wiList.size() > 0) {

				WorkItem wi = wiList.get(0);
				if (wi != null) {
					result.put(MainModule.WORKITEM_ID, wi.getId());
					// returnValue.put(MainModule.WORKITEM, wi);
				}
			}

			result.put("LeaveRequest", leaveRequest);

			// 获得假期类别字典
			List<Dictionary> leaveTypes = dao().query(Dictionary.class,
					Cnd.where("dicType", "=", "leave_type").asc("sort"));
			result.put("LeaveTypes", leaveTypes);

			// 查询当前用户的所有请假情况
			Date now = ((FireflowDemoDao) dao()).getSysDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(now);
			calendar.add(Calendar.DATE, -1);
			Date yesterday = calendar.getTime();
			List<Holiday> holidays = dao().query(
					Holiday.class,
					Cnd.where("employeeId", "=", demoUser.getEmployeeId()).and(
							Cnd.exps("yearStart", "<=", now).or("yearEnd",
									">=", yesterday)));

			result.put("Holidays", holidays);

			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_OK);
			return result;

		} catch (InvalidModelException e) {
			throw new RuntimeException("流程定义错误", e);
		} catch (WorkflowProcessNotFoundException e) {
			throw new RuntimeException("特定的流程未找到", e);
		} catch (InvalidOperationException e) {
			throw new RuntimeException("非法的流程操作", e);
		}

		catch (Throwable e) {
			log.error("E!!", e);
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY,
					"请假单保存失败，原因：" + e.getMessage());
			return result;
		} finally {
			Mvcs.getHttpSession().removeAttribute(
					MainModule.BIZ_OBJECT_IN_SESSION_KEY);
		}
	}

	/**
	 * 增加一条明细
	 * 
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String, Object> addRequestDetail(
			@Param("..") LeaveRequestDetail requestDetail) {
		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();

		Map<String, Object> result = new HashMap<String, Object>();

		String billCode = requestDetail.getBillCode();
		List<LeaveRequestDetail> list = dao().query(LeaveRequestDetail.class,
				Cnd.where("billCode", "=", billCode));

		LeaveRequestDetail exist = getExistLeaveRequestDetail(list,
				requestDetail);
		if (exist != null) {
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "错误，请假明细已经存在！");
			return result;
		}
		requestDetail.setLastUpdatePerson(demoUser.getName());
		dao().insert(requestDetail);
		list = dao().query(LeaveRequestDetail.class,
				Cnd.where("billCode", "=", billCode));

		result.put(MainModule.JTABLE_RESULT_KEY,
				MainModule.JTABLE_RESULT_VALUE_OK);
		result.put(MainModule.JTABLE_RECORD_KEY, requestDetail);
		result.put("TotalDays", this.calculateTotalLeaveDays(list));

		return result;
	}

	@At
	@Ok("json")
	public Map<String, Object> deleteRequestDetail(@Param("id") int id,
			@Param("billCode") String billCode) {
		Map<String, Object> result = new HashMap<String, Object>();

		dao().delete(LeaveRequestDetail.class, id);

		List<LeaveRequestDetail> list = dao().query(LeaveRequestDetail.class,
				Cnd.where("billCode", "=", billCode));
		// calculateTotalLeaveDays
		result.put("TotalDays", calculateTotalLeaveDays(list));

		result.put(MainModule.JTABLE_RESULT_KEY,
				MainModule.JTABLE_RESULT_VALUE_OK);
		return result;
	}

	@At
	@Ok("json")
	public Map<String, Object> listRequestDetail(
			@Param("billCode") String billCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MainModule.JTABLE_RESULT_KEY,
				MainModule.JTABLE_RESULT_VALUE_OK);

		List<LeaveRequestDetail> list = dao().query(LeaveRequestDetail.class,
				Cnd.where("billCode", "=", billCode));
		result.put(MainModule.JTABLE_RECORDS_KEY, list);

		return result;
	}

	@At
	@Ok("json")
	public Map<String, Object> listRequestDetailInSession(
			@Param("billCode") String billCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MainModule.JTABLE_RESULT_KEY,
				MainModule.JTABLE_RESULT_VALUE_OK);

		Object obj = Mvcs.getHttpSession().getAttribute(
				MainModule.BIZ_OBJECT_IN_SESSION_KEY);

		if (obj != null && (obj instanceof LeaveRequest)) {
			LeaveRequest leaveRequest = (LeaveRequest) obj;
			if (billCode != null && billCode.equals(leaveRequest.getBillCode())) {
				result.put(MainModule.JTABLE_RECORDS_KEY,
						leaveRequest.getLeaveRequestDetailList());
			}

		}

		return result;
	}

	private void calculateTotalLeaveDays(LeaveRequest leaveRequest) {
		List<LeaveRequestDetail> list = leaveRequest
				.getLeaveRequestDetailList();

		Float total = calculateTotalLeaveDays(list);
		leaveRequest.setTotalDays(total);
	}

	private float calculateTotalLeaveDays(List<LeaveRequestDetail> list) {
		Float total = 0f;
		for (LeaveRequestDetail detail : list) {
			if (detail.getTimeSection() == 2) {
				total = total + 1;// 全天
			} else {
				total = total + 0.5f;
			}
		}
		return total;
	}

	private LeaveRequestDetail getExistLeaveRequestDetail(LeaveRequest request,
			int sn) {
		List<LeaveRequestDetail> list = request.getLeaveRequestDetailList();

		for (LeaveRequestDetail tmp : list) {

			if (tmp.getSn() == sn) {
				return tmp;
			}
		}
		return null;
	}

	private LeaveRequestDetail getExistLeaveRequestDetail(
			List<LeaveRequestDetail> list, LeaveRequestDetail detail) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for (LeaveRequestDetail tmp : list) {
			String d1 = format.format(tmp.getLeaveDate());
			String d2 = format.format(detail.getLeaveDate());
			if (d1.equals(d2)
					&& tmp.getTimeSection().equals(detail.getTimeSection())) {
				return tmp;
			}
		}
		return null;
	}

	private int getSn(LeaveRequest leaveRequest) {
		List<LeaveRequestDetail> detailList = leaveRequest
				.getLeaveRequestDetailList();
		int max = 0;
		for (LeaveRequestDetail detail : detailList) {
			if (detail.getSn() > max) {
				max = detail.getSn();
			}
		}
		return max + 1;
	}

	/**
	 * 增加一条明细
	 * 
	 * @return
	 */
	@At
	@Ok("json")
	public Map<String, Object> addRequestDetailInSession(
			@Param("..") LeaveRequestDetail requestDetail) {
		Map<String, Object> result = new HashMap<String, Object>();

		Object obj = Mvcs.getHttpSession().getAttribute(
				MainModule.BIZ_OBJECT_IN_SESSION_KEY);

		if (obj != null && (obj instanceof LeaveRequest)) {
			LeaveRequest leaveRequest = (LeaveRequest) obj;

			LeaveRequestDetail exist = getExistLeaveRequestDetail(
					leaveRequest.getLeaveRequestDetailList(), requestDetail);
			if (exist != null) {
				result.put(MainModule.JTABLE_RESULT_KEY,
						MainModule.JTABLE_RESULT_VALUE_ERROR);
				result.put(MainModule.JTABLE_MESSAGE_KEY, "错误，请假明细已经存在！");
				return result;
			}

			requestDetail.setBillCode(leaveRequest.getBillCode());

			// 产生sn
			int max = getSn(leaveRequest);
			requestDetail.setSn(max);

			leaveRequest.getLeaveRequestDetailList().add(requestDetail);

			calculateTotalLeaveDays(leaveRequest);

			Mvcs.getHttpSession().setAttribute(
					MainModule.BIZ_OBJECT_IN_SESSION_KEY, leaveRequest);

			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_OK);
			result.put(MainModule.JTABLE_RECORD_KEY, requestDetail);
			result.put("TotalDays", leaveRequest.getTotalDays());
		} else {
			result.put(MainModule.JTABLE_RESULT_KEY,
					MainModule.JTABLE_RESULT_VALUE_ERROR);
			result.put(MainModule.JTABLE_MESSAGE_KEY, "非法操作，原因：请假但对象未创建");
		}

		return result;
	}

	@At
	@Ok("json")
	public Map<String, Object> deleteRequestDetailInSession(@Param("sn") int sn) {
		Map<String, Object> result = new HashMap<String, Object>();

		Object obj = Mvcs.getHttpSession().getAttribute(
				MainModule.BIZ_OBJECT_IN_SESSION_KEY);

		if (obj != null && (obj instanceof LeaveRequest)) {
			LeaveRequest leaveRequest = (LeaveRequest) obj;
			LeaveRequestDetail exist = getExistLeaveRequestDetail(leaveRequest,
					sn);
			if (exist != null) {
				leaveRequest.getLeaveRequestDetailList().remove(exist);
				calculateTotalLeaveDays(leaveRequest);

				Mvcs.getHttpSession().setAttribute(
						MainModule.BIZ_OBJECT_IN_SESSION_KEY, leaveRequest);
			}
			result.put("TotalDays", leaveRequest.getTotalDays());
		}

		result.put(MainModule.JTABLE_RESULT_KEY,
				MainModule.JTABLE_RESULT_VALUE_OK);
		return result;
	}

	public Map<String, Object> updateRequestDetail() {
		return null;
	}

	/**
	 * 初始化请假单
	 * 
	 * @return
	 */
	@At
	@Ok("jsp:/template/holiday/LeaveRequest_create.jsp")
	public Map<String, Object> initRequest() {
		Map<String, Object> result = new HashMap<String, Object>();

		org.fireflow.demo.security.bean.User demoUser = Utils.getCurrentUser();

		// 获得新的单据号
		String billCode = ((FireflowDemoDao) dao()).generateBillCode();

		// 获得假期类别字典
		List<Dictionary> leaveTypes = dao().query(Dictionary.class,
				Cnd.where("dicType", "=", "leave_type").asc("sort"));
		result.put("LeaveTypes", leaveTypes);

		// 构造一个空的请假单
		Date now = ((FireflowDemoDao) dao()).getSysDate();

		LeaveRequest leaveRequest = new LeaveRequest();
		leaveRequest.setBillCode(billCode);
		leaveRequest.setEmployeeId(demoUser.getEmployeeId());
		leaveRequest.setEmployeeName(demoUser.getName());
		leaveRequest.setCreatorId(demoUser.getLoginName());
		leaveRequest.setCreateTime(now);

		leaveRequest.setLeaveType("paid");// 默认为带薪假
		leaveRequest.setLeaveTypeName("带薪年休假");
		result.put("LeaveRequest", leaveRequest);

		// 查询当前用户的所有请假情况

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		List<Holiday> holidays = dao().query(
				Holiday.class,
				Cnd.where("employeeId", "=", demoUser.getEmployeeId()).and(
						Cnd.exps("yearStart", "<=", now).or("yearEnd", ">=",
								yesterday)));

		result.put("Holidays", holidays);

		// 暂存在session中
		HttpSession session = Mvcs.getHttpSession();
		session.setAttribute(MainModule.BIZ_OBJECT_IN_SESSION_KEY, leaveRequest);

		return result;
	}
}