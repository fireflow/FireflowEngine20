package org.fireflow.demo.common.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao;
import org.fireflow.demo.common.dao.ICommonApprovalInfoDao;
import org.fireflow.demo.common.entity.CommonApprovalInfo;
import org.fireflow.demo.fireflow_ext.WorkflowUtil;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.web_client.util.Constants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class CommonApprovalInfoServlet
 */
public class CommonApprovalInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public static final String APPROVE_ACTION = "APPROVE_ACTION";
	protected WebApplicationContext springCtx = null;
	protected RuntimeContext fireContext  = null;
	protected TransactionTemplate tramsactionTemplate  = null;
	
	protected ICommonApprovalInfoDao commonApprovalInfoDao = null;
	protected IBusinessTripBasicInfoDao businessTripBasicInfoDao = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommonApprovalInfoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
    	springCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	fireContext = (RuntimeContext)springCtx.getBean("fireflowRuntimeContext");
    	tramsactionTemplate = (TransactionTemplate)springCtx.getBean("transactionTemplate");
    	commonApprovalInfoDao = (ICommonApprovalInfoDao)springCtx.getBean("commonApprovalInfoDao");
    	
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
		approve(request,response);
	}
	
	/**
	 * 通用审批动作，该动作结束后，导航到WorkflowOperationServlet.completeWorkItem(...)
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void approve(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String workItemId = request.getParameter(Constants.WORKITEM_ID);
		
		final User u = getCurrentUser(request);
		final WorkflowSession workflowSession = WorkflowSessionFactory
				.createWorkflowSession(fireContext, u);
		WorkflowQuery<WorkItem> workItemQuery = workflowSession.createWorkflowQuery(WorkItem.class);
		WorkItem workItem = workItemQuery.get(workItemId);
		
		final CommonApprovalInfo approveInfo = new CommonApprovalInfo();
		approveInfo.setBizId(workItem.getBizId());
		approveInfo.setStepName(workItem.getWorkItemName());
		approveInfo.setBizSubject(workItem.getSubject());
		approveInfo.setApprover(u.getName());
		approveInfo.setLastUpdateTime(new Date());
		approveInfo.setDecision(Integer.parseInt(request.getParameter("decision")));
		approveInfo.setDetailInfo(request.getParameter("detailInfo"));
		
		tramsactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status) {
				commonApprovalInfoDao.create(approveInfo);
				return null;
			}
			
		});
		
		request.setAttribute(WorkItemProperty.ATTACHMENT_ID.getPropertyName(), approveInfo.getId());
		request.setAttribute(WorkItemProperty.NOTE.getPropertyName(), approveInfo.getDetailInfo());
		
		
		//导航到WorkflowOperationServlet.completeWorkItem(...)继续处理
		RequestDispatcher dispatcher = request.getRequestDispatcher("/servlet/WorkflowOperationServlet");
		dispatcher.forward(request, response);
	}
	
	protected User getCurrentUser(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		return WorkflowUtil.getCurrentWorkflowUser(session);
	}
}
