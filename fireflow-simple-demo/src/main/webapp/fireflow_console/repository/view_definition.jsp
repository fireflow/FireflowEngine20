<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="constants" class="org.fireflow.web_client.util.Constants" scope="request"></jsp:useBean>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=request.getAttribute("PAGE_TITLE") %></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/css/redmond/jquery-ui-1.10.3.custom.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/css/jquery.ui.menubar.css">

<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/resources/jquery/jquery-1.10.2.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.js"></script>


</head>
<body style="margin:0px;padding:0px">
<jsp:include page="definition_info.jsp"></jsp:include>
</body>
</html>