<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.web.util.Constants"%>
<%
	request.setAttribute("PAGE_TITLE", "流程错误页面");
	String errorMessage = (String) request
			.getAttribute(Constants.ERROR_MESSAGE);
	String errorStack = (String) request
			.getAttribute(Constants.ERROR_STACK);
%>
<jsp:include page="/common/header.jsp"></jsp:include>

<div align="center" style="width:100%;">
	<br /> <span style="font-size: 16px; font-weight: bold">流程操作发生错误</span><br />
	<br />
	<div align="left" style="width: 90%">
		<div>
			<%=errorMessage%>
		</div>
		<div style="color: red; width: 100%; overflow-x: auto">
			<%=errorStack%>
		</div>
	</div>
</div>

<jsp:include page="/common/footer.jsp"></jsp:include>
