<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>	
<%@ page import="java.text.SimpleDateFormat,java.util.Date" %>
<%
request.setAttribute("PAGE_TITLE","出差申请");

//生成新的单据编号
SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd-HHss");
String bizId = "CCSQ-"+dtFormat.format(new Date());
%>
<c:set  var="currentUser" value="${sessionScope['CURRENT_USER_SESSION_KEY'] }"></c:set>
<jsp:include page="/common/header.jsp"></jsp:include>
<br/>
<div align="center" >
<div id="outerDiv" style="width:900px;border:1px solid white">

<!-- 将业务表单的Id传入到流程菜单页面中 -->
<jsp:useBean id="constants" class="org.fireflow.web.util.Constants" ></jsp:useBean>
<jsp:include page="/fireflow_client/_fireflow_toolbar_for_editview.jsp">
<jsp:param name="forwardURL" value="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.LIST_TODO_WORKITEMS}"/>
<jsp:param name="bizFormId" value="bizForm" />
</jsp:include>
<br/>

<!-- 业务表单 -->
<script type="text/javascript">
function validateForm(){
	var destination_city = document.getElementById("destination_city").value;
	destination_city = destination_city.replace(/(^\s*)|(\s*$)/g,"");
	document.getElementById("destination_city").value=destination_city;
	if (destination_city==null || destination_city==""){
		alert("出差地点不能为空");
		return false;
	}
	
	var startDate = document.getElementById("start_date").value;
	startDate = startDate.replace(/(^\s*)|(\s*$)/g,"");
	document.getElementById("start_date").value = startDate;
	if (startDate==null || startDate==""){
		alert("开始日期不能为空");
		return false;
	}
	if (isNaN(startDate)){
		alert("请填写正确的开始日期");
		return false;
	}
	
	var endDate = document.getElementById("end_date").value;
	endDate = endDate.replace(/(^\s*)|(\s*$)/g,"");
	document.getElementById("end_date").value = endDate;
	if (endDate==null || endDate==""){
		alert("结束日期不能为空");
		
		return false;
	}
	if (isNaN(endDate)){
		alert("请填写正确的结束日期");
		return false;
	}
	
	var subject = document.getElementById("subject").value;
	subject = subject.replace(/(^\s*)|(\s*$)/g,"");
	if (subject==null || subject==""){
		alert("事由不能为空");
		return false;
	}
	
	return true;
}
</script>
<div id="titleDiv" style="padding-top:4px;width:100%;height:32px;font-size:16px;font-weight:bold;">出差申请</div>
<div id="formDiv" style="width:100%;border-top:1px dotted white"">
<form id="bizForm" 
		action="${pageContext.request.contextPath }/servlet/BusinessTripServlet?actionType=CREATE_BIZ_TRIP_APPLICATION" 
		onsubmit="return validateForm();" method="post"
		style="padding:10px;">
<input type="hidden" name="${constants.PROCESS_INSTANCE_ID }" value="${requestScope[constants.PROCESS_INSTANCE_ID] }"/>
<table align="center" width="100%" cellspacing="0" border="0">
	<tr>
		<td>单据编号</td><td><input name="biz_id" value="<%=bizId%>" readonly="true"/></td>
		<td>申请人</td><td>
			<input type="hidden" name="applicant_id" value="${currentUser.userId }"/>
			<input name="applicant_name" value="${currentUser.name }" readonly="true"/></td>
		<td>所在部门</td><td><input  name="department" value="${currentUser.departmentName }" readonly="true"/></td>
	</tr>
	<tr>
		<td>职务</td><td><input name="position" value="${currentUser.positionName }" readonly="true"/></td>
		<td>联系电话</td><td><input name="phone_number"/></td>
		<td></td><td></td>
	</tr>
	<tr>
		<td>出差地点</td><td><input id="destination_city" name="destination_city"/><span style="color:red">*</span></td>
		<td>开始日期</td><td><input id="start_date" name="start_date" maxlength="8"/><span style="color:red">*</span>(yyyyMMdd)</td>
		<td>结束日期</td><td><input id="end_date" name="end_date" maxlength="8"/><span style="color:red">*</span>(yyyyMMdd)</td>
	</tr>
	<tr>
		<td align="left" colspan="6">事由：</td>
	</tr>
	<tr>
		<td align="left" colspan="6"><textarea id="subject" name="subject" style="width:98%" cols="8"></textarea><span style="color:red">*</span></td>
	</tr>
</table>
<div><input type="submit" value="保存"></div>
</form>
</div>
</div>
</div>
<jsp:include page="/common/footer.jsp"></jsp:include>
