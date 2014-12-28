package org.fireflow.clientwidget.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FireflowClientWidgetServlet
 */
public class FireflowClientWidgetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Map<String, ActionHandler> handlers = new HashMap<String, ActionHandler>();

	/**
	 * Default constructor.
	 */
	public FireflowClientWidgetServlet() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		InputStream inStream = FireflowClientWidgetServlet.class
				.getResourceAsStream("handlers.properties");

		Properties properties = new Properties();
		try {
			properties.load(inStream);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		Iterator keys = properties.keySet().iterator();

		if (keys != null) {
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String className = (String) properties.get(key);
				try {
					Class clz = Class.forName(className);
					ActionHandler handler = (ActionHandler) clz.newInstance();
					handlers.put(key, handler);
				} catch (ClassNotFoundException e) {
					throw new ServletException("ActionType["+key+"]对应的handler类["+className+"]未找到。",e);
				} catch (InstantiationException e) {
					throw new ServletException("ActionType["+key+"]对应的handler类["+className+"]实例化异常。",e);
				} catch (IllegalAccessException e) {
					throw new ServletException("ActionType["+key+"]对应的handler类["+className+"]实例化异常。",e);
				} finally {

				}
			}
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String actionType = request.getParameter(Constants.ACTION_TYPE);
		ActionHandler handler = handlers.get(actionType);
		if (handler!=null){
			handler.handleRequest(request, response);
		}
	}

}
