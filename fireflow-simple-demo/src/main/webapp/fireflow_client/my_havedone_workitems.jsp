<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>	
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>           
<%@ page import="java.text.SimpleDateFormat,java.util.Date" %>
<%
request.setAttribute("PAGE_TITLE","我的已办事项");
%>
<jsp:include page="/common/header.jsp"></jsp:include>
<link rel="stylesheet" href="<%=request.getContextPath() %>/fireflow_client/resources/css/table.css" type="text/css">

<div align="center">
<span style="font-size:16px;font-weight:bold">我的已办事项</span>

<table class="hovertable">
	<tr>
		<th>主题</th>
		<th>业务单据编号</th>
		<th>发起时间</th>
		<th>当前环节</th>

		<th>处理人</th>
		<th>处理时间</th>
		<th>备注信息或者审批信息</th>
	</tr>
	<c:forEach var="workItem" items="${workItemList }">
		<tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#ffffff';">
			<td>${workItem.subject}</td>
			<td>${workItem.activityInstance.bizId}</td>
			<td><fmt:formatDate value="${workItem.activityInstance.procInstCreatedTime }" type="both"/></td>
			<td>${workItem.activityInstance.displayName}</td>
			
			<td>${workItem.ownerName}</td>
			<td><fmt:formatDate value="${workItem.endTime}"  type="both" /></td>
			<td>${workItem.note}</td>			
		</tr>
	</c:forEach>
</table>
</div>

<jsp:include page="/common/footer.jsp"></jsp:include>