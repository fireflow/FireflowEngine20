package org.fireflow.clientwidget.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FireflowResourceServlet
 */
public class FireflowResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FireflowResourceServlet() {
        super();
        // TODO Auto-generated constructor stub
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
		String resourcePath = request.getPathInfo();
		//System.out.println("===你访问的资源是:"+resourcePath);
		if (resourcePath == null || resourcePath.trim().equals("")) {
			return;
		}
		String contentType = getContentType(resourcePath);
		// 设置contentType
		response.setContentType(contentType);

		// 尽量进行缓存，提高效率;缓存6天
		setCacheExpireDate(response,6*24*60*60);

		InputStream inStream = FireflowResourceServlet.class
				.getResourceAsStream(resourcePath);
		if (inStream==null){
			return;//没有资源，直接返回
		}
		OutputStream outStream = response.getOutputStream();

		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			outStream.write(buff, 0, rc);
		}
		inStream.close();

	}
	
	private String getContentType(String resourcePath){
		String lowerCasePath = resourcePath.toLowerCase();
		String contentType = "text/html";
		if (lowerCasePath.endsWith(".png")) {
			contentType = "image/png";
		} else if (lowerCasePath.endsWith(".gif")) {
			contentType = "image/gif";
		} else if (lowerCasePath.endsWith(".bmp")) {
			contentType = "image/bmp";
		} else if (lowerCasePath.endsWith(".jpeg")
				|| lowerCasePath.endsWith(".jpg")) {
			contentType = "image/jpeg";
		}
		else if (lowerCasePath.endsWith(".ico")){
			contentType = "image/x-icon";
		}
		else if (lowerCasePath.endsWith(".css")){
			contentType = "text/css";
		}
		else if (lowerCasePath.endsWith(".js") ){
			contentType = "application/x-javascript";
		}

		else if (lowerCasePath.endsWith(".xml") ){
			contentType = "text/xml";
		}
		else if (lowerCasePath.endsWith(".html") || 
				lowerCasePath.endsWith(".htm")){
			contentType = "text/html";
		}
		else if (lowerCasePath.endsWith(".svg") ){
			contentType = "image/svg+xml";
		}
		else if (lowerCasePath.endsWith(".pdf") ){
			contentType = "application/pdf";
		}

		else if (lowerCasePath.endsWith(".zip") ){
			contentType = "application/zip";
		}
		
		else if (lowerCasePath.endsWith(".z") ){
			contentType = "application/x-compress";
		}
		else if (lowerCasePath.endsWith(".z") ){
			contentType = "application/x-compress";
		}

		return contentType;
	}
	
	private void setCacheExpireDate(HttpServletResponse response, int seconds) {
		if (response != null) {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.SECOND, seconds);
			response.setHeader("Cache-Control", "PUBLIC, max-age=" + seconds
					+ ", must-revalidate");
			response.setHeader("Expires",
					htmlExpiresDateFormat().format(cal.getTime()));
		}
	}

	public DateFormat htmlExpiresDateFormat() {
		DateFormat httpDateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return httpDateFormat;
	}
	
//	public static void main(String[] args){
//		String s = "ABCDE";
//		String ss = s.toLowerCase();
//		System.out.println(s);
//		System.out.println(ss);
//		
//		int second = 5*24*60*60;
//		System.out.println(second);
//	}

}
