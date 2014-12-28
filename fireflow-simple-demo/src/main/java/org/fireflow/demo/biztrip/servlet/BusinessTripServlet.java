package org.fireflow.demo.biztrip.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao;
import org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo;
import org.fireflow.demo.common.dao.ICommonApprovalInfoDao;
import org.fireflow.demo.fireflow_ext.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.web_client.util.Constants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class BusinessTripServlet
 */
public class BusinessTripServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String CREATE_BIZ_TRIP_APPLICATION = "CREATE_BIZ_TRIP_APPLICATION";
    
	protected WebApplicationContext springCtx = null;
	protected RuntimeContext fireContext  = null;
	protected TransactionTemplate tramsactionTemplate  = null;
	
	protected ICommonApprovalInfoDao commonApprovalInfoDao = null;
	protected IBusinessTripBasicInfoDao businessTripBasicInfoDao = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BusinessTripServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
    	springCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	fireContext = (RuntimeContext)springCtx.getBean("fireflowRuntimeContext");
    	tramsactionTemplate = (TransactionTemplate)springCtx.getBean("transactionTemplate");
    	commonApprovalInfoDao = (ICommonApprovalInfoDao)springCtx.getBean("commonApprovalInfoDao");
    	businessTripBasicInfoDao = (IBusinessTripBasicInfoDao)springCtx.getBean("businessTripBasicInfoDao");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String actionType = request.getParameter("actionType");
		if (actionType!=null && actionType.trim().equals(BusinessTripServlet.CREATE_BIZ_TRIP_APPLICATION)){
			createBizTripApplication(request,response);
		}

	}
	

	/**
	 * 创建出差申请
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void createBizTripApplication(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final BusinessTripBasicInfo basicInfo = buildBusinessTripBasicInfo(request);
	
		tramsactionTemplate.execute(new TransactionCallback(){


			public Object doInTransaction(TransactionStatus status) {
				businessTripBasicInfoDao.create(basicInfo);
				
				//******************************************//
				//*******  Fire workflow 相关的代码	********//
				//*******     (运行流程实例)	        ********//
				//******************************************//
				final User u = getCurrentUser(request);
				final WorkflowSession workflowSession = WorkflowSessionFactory
						.createWorkflowSession(fireContext, u);
				WorkflowStatement stmt = workflowSession.createWorkflowStatement();

				Map<String,Object> vars = new HashMap<String,Object>();
				vars.put("days", basicInfo.getTotalDays());
				vars.put("applicant", basicInfo.getApplicantName());
				String processInstanceId = request.getParameter(Constants.PROCESS_INSTANCE_ID);		
				//运行流程实例
				stmt.runProcessInstance(processInstanceId, basicInfo.getBizId(), vars);
				
				//TODO 流程实例运行后，对于第一个节点，应该自动提交
				
				return null;
			}
			
		});
		
		//导航到“我的在办”页面
		RequestDispatcher dispatcher = request.getRequestDispatcher("/servlet/WorkflowOperationServlet?"+Constants.ACTION_TYPE+"="+Constants.LIST_MY_ACTIVE_PROCESS_INSTANCE);
		dispatcher.forward(request, response);
	}
	
	/**
	 * 构建出差申请信息
	 * @param request
	 * @return
	 */
	protected BusinessTripBasicInfo buildBusinessTripBasicInfo(HttpServletRequest request){
		BusinessTripBasicInfo basicInfo = new BusinessTripBasicInfo();
		
		basicInfo.setBizId(request.getParameter("biz_id"));
		basicInfo.setApplicantId(request.getParameter("applicant_id"));
		basicInfo.setApplicantName(request.getParameter("applicant_name"));
		basicInfo.setDepartmentName(request.getParameter("department"));
		
		basicInfo.setPhoneNumber(request.getParameter("phone_number"));
		basicInfo.setPositionName(request.getParameter("position"));
		
		basicInfo.setDestinationCity(request.getParameter("destination_city"));
		basicInfo.setStartDate(request.getParameter("start_date"));
		basicInfo.setEndDate(request.getParameter("end_date"));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try{
			Date startDate = dateFormat.parse(basicInfo.getStartDate());
			Date endDate = dateFormat.parse(basicInfo.getEndDate());
			long days = (endDate.getTime()-startDate.getTime())/(24*60*60*1000  );
			basicInfo.setTotalDays((int)days);
		}catch(Exception e){
			e.printStackTrace();
		}

		
		basicInfo.setSubject(request.getParameter("subject"));
		
		return basicInfo;
	}

	protected User getCurrentUser(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		return WorkflowUtil.getCurrentWorkflowUser(session);
	}
}
