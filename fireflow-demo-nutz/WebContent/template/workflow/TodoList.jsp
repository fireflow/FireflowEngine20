<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="/template/workflow/_TodoList_ToolBar_.jsp"/>

<div id="FormTitleDiv" align="center" >
	<span>我的待办列表</span>
	
	<c:if test="${not empty Message}">
		<br/>
		<span class="error-message">${Message}</span>
	</c:if>
</div>
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
	<tbody>
		<tr>
			<td align="right">
				<form id="queryForm">
					业务分类：
					<select name="processId">
						<option value="--">--所有--</option>
						<c:forEach var="descriptor" items="${obj['processDescriptors'] }">
							<c:choose>
								<c:when test="${descriptor.processId==obj['currentProcessId'] }">
									<option value="${descriptor.processId }" selected="selected">${descriptor.displayName }</option>
								</c:when>
								<c:otherwise>
									<option value="${descriptor.processId }" >${descriptor.displayName }</option>
								</c:otherwise>
							</c:choose>
					
						</c:forEach>
					</select> 工作项状态： <select name="state">
						<option value="--">--所有--</option>
						<c:choose>
							<c:when test="${'0'==obj['currentState'] }">
								<option value="0" selected="selected">待签收</option>
							</c:when>
							<c:otherwise>
								<option value="0">待签收</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${'1'==obj['currentState'] }">
								<option value="1" selected="selected">待处理</option>
							</c:when>
							<c:otherwise>
								<option value="1">待处理</option>
							</c:otherwise>
						</c:choose>

					</select>
				</form>
			</td>
			<td align="right" width="60px"><input id="queryButton" type="button" value="查询"/></td>

		</tr>
	</tbody>
</table>

<script type="text/javascript">
<!--
$(function(){
	$('#queryButton').click(function(){
		$('#TodoListDiv').jtable('load',$('#queryForm').serializeArray());
	});
});

function listTodoList(postData, jtParams) {
    console.log("Loading from custom function...");
    return $.Deferred(function ($dfd) {
        $.ajax({
            url: '${pageContext.request.contextPath}/module/workflow/WorkflowModule/listTodoWorkItemsJson?jtStartIndex=' + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
            type: 'POST',
            dataType: 'json',
            data: postData,
            success: function (data) {
                $dfd.resolve(data);
            },
            error: function () {
                $dfd.reject();
            }
        });
    });
}

//-->
</script>
<jsp:include page="/template/workflow/_TodoList_Body_.jsp"/>
