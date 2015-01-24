<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page import="java.util.Map" %>

<%
request.setAttribute("thePageTitle","错误信息");
%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>
<jsp:include page="/template/include/_error.jsp"/>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>