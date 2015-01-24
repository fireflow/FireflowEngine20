<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
request.setAttribute("thePageTitle","流程提交成功");
%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>

<div id="FormTitleDiv" align="center" >
	<span>流程提交成功</span>
	
</div>

<div id="FormDiv" align="center">
<table>
<c:forEach var="item" items="${obj}">   
	<tr><td>下一处理环节是：</td><td>${item.key} ></td></tr>
	<tr><td>处理人是：</td><td>${item.value}</td></tr>
</c:forEach>  
</table>
</div>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>