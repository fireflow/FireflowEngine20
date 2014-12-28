<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.web.util.Constants" %>    
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Fire Workflow工具栏，适合于可编辑的表单页面 -->
<jsp:useBean id="constants"  class="org.fireflow.web.util.Constants" ></jsp:useBean>

<div id="fireflowToolbarDiv" style="background-color:#C0C0C0;">
<script type="text/javascript">
function submitBizForm(){
	var bizFormObject = document.getElementById("${param[constants.BIZ_FORM_ID]}");
	if (bizFormObject==null){
		return;
	}
	if ((typeof bizFormObject.onsubmite)=='undefined' || bizFormObject.onsubmit()){
		bizFormObject.submit();//提交表单
	}

}
function workflowOperation(actionType,target){
	var actionTypeInput = document.getElementById("_actionType");
	actionTypeInput.value = actionType;
	var workflowForm = document.getElementById("_workflowForm");
	if (target!=null){
		workflowForm.target=target;
	}
	workflowForm.submit();	
}
</script>
<form id="_workflowForm" action="${pageContext.request.contextPath}/servlet/WorkflowOperationServlet" >
<input type="hidden" id="_actionType" name="${constants.ACTION_TYPE}" value=""/>
<input type="hidden" id="_forwardURL" name="${constants.FORWARD_URL }" value="${requestScope[constants.FORWARD_URL]}"/>
<input type="hidden" id="_processInstanceId" name="${constants.PROCESS_INSTANCE_ID}" value="${requestScope[constants.PROCESS_INSTANCE_ID]}"/>
<input type="hidden" id="_activityInstanceId" name="${constants.ACTIVITY_INSTANCE_ID}" value="${requestScope[constants.ACTIVITY_INSTANCE_ID]}"/>
<input type="hidden" id="_workItemId" name="${constants.WORKITEM_ID}" value="${requestScope[constants.WORKITEM_ID]}"/>

<table width="100%">
<tr>
	<td><a href="#" onclick="submitBizForm()">保存表单</a></td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="#" onclick="workflowOperation('${constants.COMPLETE_WORKITEM}')">提交流程</a>
	    	</c:when>
	   	 	<c:otherwise>
				提交流程
	    	</c:otherwise>
		</c:choose>
	</td>
	<td>

		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.OPEN_NEXT_STEP_ACTOR_SELECTOR }')">指定下一步处理人</a>
	    	</c:when>
	   	 	<c:otherwise>
				指定下一步处理人
	    	</c:otherwise>
		</c:choose>
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.OPEN_TARGET_ACTIVITY_SELECTOR }')">跳转到...</a>
	    	</c:when>
	   	 	<c:otherwise>
				跳转到...
	    	</c:otherwise>
		</c:choose>
				

	</td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.PREPARE_REASSIGN_TO_BEFORE_ME }')">前加签</a>
	    	</c:when>
	   	 	<c:otherwise>
				前加签
	    	</c:otherwise>
		</c:choose>	
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.PREPARE_REASSIGN_TO_AFTER_ME }')">后加签</a>
	    	</c:when>
	   	 	<c:otherwise>
				后加签
	    	</c:otherwise>
		</c:choose>		
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.LIST_WORKITEMS_IN_PROCESS_INSTANCE }','_blank')">查看办理记录</a>
	    	</c:when>
	   	 	<c:otherwise>
				查看办理记录
	    	</c:otherwise>
		</c:choose>		
	</td>
	<td>
		<c:choose>
			<c:when test="${not empty(requestScope[constants.WORKITEM_ID])}">
				<a href="# onclick="workflowOperation('${constants.LIST_WORKITEMS }')">查看流程跟踪图</a>
	    	</c:when>
	   	 	<c:otherwise>
				查看流程跟踪图
	    	</c:otherwise>
		</c:choose>			
	</td>
</tr>
</table>
</form>
</div>