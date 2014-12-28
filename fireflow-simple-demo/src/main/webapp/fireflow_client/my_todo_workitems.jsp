<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>	
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>           
<%@ page import="java.text.SimpleDateFormat,java.util.Date" %>
<%
request.setAttribute("PAGE_TITLE","我的待办事项");
%>
<jsp:include page="/common/header.jsp"></jsp:include>
<link rel="stylesheet" href="<%=request.getContextPath() %>/fireflow_client/resources/css/table.css" type="text/css">
<jsp:include page="/fireflow_client/_fireflow_popup_menu.jsp"></jsp:include>


<div align="center">
<span style="font-size:16px;font-weight:bold">我的待办事项</span>

<table class="hovertable">
	<tr>
		<th>主题</th>
		<th>业务单据编号</th>
		<th>发起时间</th>
		<th>当前环节</th>

		<th>处理人</th>
		<th>任务创建时间</th>
		<th>任务到期时间</th>
		<th>任务状态</th>
		<th>操作</th>
	</tr>
	<c:forEach var="workItem" items="${workItemList }">
		<tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#ffffff';">
			<td>${workItem.subject}</td>
			<td>${workItem.activityInstance.bizId}</td>
			<td><fmt:formatDate value="${workItem.activityInstance.procInstCreatedTime }" type="both"/></td>
			<td>${workItem.activityInstance.displayName}</td>
			
			<td>${workItem.ownerName}</td>
			<td><fmt:formatDate value="${workItem.createdTime}"  type="both" /></td>
			<td><fmt:formatDate value="${workItem.expiredTime}"  type="both" /></td>			
			<td>${workItem.state.displayName }</td>
			<td><button id='OperBt_${workItem.id }' 
					onclick="showmenu('OperBt_${workItem.id }','workItemMenuDiv','show','left','${workItem.id}','${workItem.state.value }')"
					onMouseOut="showmenu('OperBt_${workItem.id }','workItemMenuDiv','hide','left','${workItem.id}','${workItem.state.value }')">操作</button></td>
		</tr>
	</c:forEach>
</table>
</div>

<jsp:include page="/common/footer.jsp"></jsp:include>