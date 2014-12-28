package org.fireflow.demo.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fireflow.demo.ou_management.dao.IDemoUserDao;
import org.fireflow.demo.ou_management.entity.DemoUser;
import org.fireflow.engine.context.RuntimeContext;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected WebApplicationContext springCtx = null;
	protected RuntimeContext fireContext  = null;
	protected TransactionTemplate tramsactionTemplate  = null;
	protected IDemoUserDao demoUserDao = null;
	
	public static final String CURRENT_USER_SESSION_KEY = "CURRENT_USER_SESSION_KEY";

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
    	springCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()); 
    	fireContext = (RuntimeContext)springCtx.getBean(RuntimeContext.Fireflow_Runtime_Context_Name);
    	tramsactionTemplate = (TransactionTemplate)springCtx.getBean("demoTransactionTemplate");
    	demoUserDao = (IDemoUserDao)springCtx.getBean("demoUserDao");
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
		String actionType=request.getParameter("actionType");
		if (actionType!=null && actionType.trim().equals("LOGIN")){
			login(request,response);
		}else{
			logout(request,response);
		}
	}
	
	protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		
		DemoUser currentUser = demoUserDao.authenticateUser(userName, password);
		if (currentUser!=null){
			request.getSession().setAttribute(CURRENT_USER_SESSION_KEY, currentUser);
			//导航到index页面
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);
		}else{
			HttpSession session =request.getSession(false);
			if (session!=null){
				session.invalidate();
			}
			request.setAttribute("ERROR_MESSAGE", "用户名或者密码不正确");
			//导航到登录页面
			RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
			dispatcher.forward(request, response);
		}		
		
	}
	protected void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session!=null){
			session.invalidate();
		}
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}

}
