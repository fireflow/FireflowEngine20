<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>

<%
request.setAttribute("thePageTitle","审批请假条");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>
<!-- 引入工作流菜单栏 -->
<jsp:include page="/template/workflow/_workflow_toolbar.jsp">
	<jsp:param value="${obj['workItemId'] }" name="workItemId"/>
</jsp:include>
<div id="FormTitleDiv" align="center" >
	<span>请假申请</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>

<form id="_MainForm_"  action="${pageContext.request.contextPath}/module/holiday/LeaveRequest/approveJson" method="POST" >
<input type="hidden" name="billCode" value="${obj['LeaveRequest'].billCode }"/>
<input type="hidden" name="activityId" value="${obj['activityId'] }"/>
<input type="hidden" name="workItemId" value="${obj['workItemid']}"/>
<div id="tabs">
  <ul>
    <li><a href="#tabs-1">请假单</a></li>
    <li><a href="#tabs-2">审批信息</a></li>
    <li><a href="#tabs-3">请假情况统计</a></li>
  </ul>
  <div id="tabs-1">
  	<table width="100%">
  		<tr>
  			<td>流水号：${obj['LeaveRequest'].billCode }</td>
  			<td></td>
  			<td align="right">请假类别：</td>
  			<td>
  				<select id="leaveType" name="leaveType">
	  				<c:forEach var="leaveType" items="${obj['LeaveTypes'] }">
	  					<c:choose>
		  					<c:when test="${leaveType.dicKey eq obj['LeaveRequest'].leaveType }">
		  					
		  						<option value="${ leaveType.dicKey}" selected="true">${leaveType.dicValue }</option>
		  					</c:when>	  					
	  						<c:otherwise>
	  							<option value="${ leaveType.dicKey}" >${leaveType.dicValue }</option>
	  						
	  						</c:otherwise>
	  					</c:choose>

	  					
	  				</c:forEach>
  			
  				</select>
  			</td>
  			<td align="right">合计：</td>
  			<td><span id="totalDaysSpan">${obj['LeaveRequest'].totalDays }</span>天</td>
  			
  			<td align="right"><button id="addDateButton">增加日期</button>
  		</tr>
  		<tr>
  			<td colspan="7">
  				<div id="LeaveRequestDetailTable" >
				
				</div>
  			</td>
  		
  		</tr>
  		
  	</table>

  </div>
  <div id="tabs-2">
  	<table width="100%" border="1" style="border-collapse:collapse;border-color:white">
					<tr>
						<td align="right" style="height:36px" width="100px">部门经理：</td>
						<td width="120px">${(empty(obj['LeaveRequest'].deptMgrName))?"--":obj['LeaveRequest'].deptMgrName}</td>

						<td align="right" width="100px">审批意见：</td>
						<td>${(empty(obj['LeaveRequest'].deptApproveInfo))?"--":obj['LeaveRequest'].deptApproveInfo}</td>
					
						<td align="right" width="100px" align="right">审批时间：</td>
						<td width="100px">
							<c:choose>
								<c:when test="${empty(obj['LeaveRequest'].deptApproveTime)}">
									--
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${obj['LeaveRequest'].deptApproveTime}" pattern="yyyy-MM-dd"/>
								</c:otherwise>
							</c:choose>						
						</td>					
					</tr>
					<tr>
						<td align="right" style="height:36px" width="100px">业务主管：</td>
						<td width="120px">${(empty(obj['LeaveRequest'].directorName))?"--":obj['LeaveRequest'].directorName}</td>

						<td align="right">审批意见：</td>
						<td>${(empty(obj['LeaveRequest'].directorApproveInfo))?"--":obj['LeaveRequest'].directorApproveInfo}</td>
					
						<td align="right" width="100px" align="right">审批时间：</td>
						<td>
							<c:choose>
								<c:when test="${empty(obj['LeaveRequest'].directorApproveTime)}">
									--
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${obj['LeaveRequest'].directorApproveTime}" pattern="yyyy-MM-dd"/>
								</c:otherwise>
							</c:choose>						
						</td>					
					</tr>
					<tr>
						<td align="right" style="height:36px" width="100px">总经理：</td>
						<td width="120px">${(empty(obj['LeaveRequest'].ceoName))?"--":obj['LeaveRequest'].ceoName}</td>

						<td align="right">审批意见：</td>
						<td>${(empty(obj['LeaveRequest'].ceoApproveInfo))?"--":obj['LeaveRequest'].ceoApproveInfo}</td>
					
						<td align="right" width="100px" align="right">审批时间：</td>
						<td>
							<c:choose>
								<c:when test="${empty(obj['LeaveRequest'].ceoApproveTime)}">
									--
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${obj['LeaveRequest'].ceoApproveTime}" pattern="yyyy-MM-dd"/>
								</c:otherwise>
							</c:choose>						
						</td>					
					</tr>										
  	</table>
  
  </div>
  <div id="tabs-3">
  	<table width="100%" class="jtable ui-widget-content" border="1" style="border-collapse:collapse;border-color:white">
  		<thead>
  			<tr><th class="jtable-column-header ui-state-default" style="border-color:white">年度起始日期</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">年度结束日期</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">年假天数</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请年假</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请事假</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请病假</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请婚假</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请产假</th>
  				<th class="jtable-column-header ui-state-default" style="border-color:white">已请丧假</th>
  			</tr>
  		</thead>
  		<tbody>
  			<tr>
  				<c:forEach var="holiday" items="${ obj['Holidays']}">
  					<td><fmt:formatDate value="${holiday.yearStart }" pattern="yyyy-MM-dd"/></td>
  					<td><fmt:formatDate value="${holiday.yearEnd }" pattern="yyyy-MM-dd"/></td>
  					<td>${holiday.paidVacationDays }</td>
  					<td>${holiday.usedPaidVacationDays }</td>
  					<td>${holiday.usedAbsenceLeaveDays }</td>
  					<td>${holiday.usedSickLeaveDays }</td>
  					<td>${holiday.usedMaritalLeaveDays }</td>
  					<td>${holiday.usedMaternityLeaveDays }</td>
  					<td>${holiday.usedFuneralLeaveDays }</td>
  					
  				</c:forEach>
  				
  				<c:if test="${empty obj['Holidays'] }">
  					<td colspan="9" align="center">无请假数据</td>
  				
  				</c:if>
  			</tr>
  		</tbody>
  	</table>
  </div>
 </div>
<br/>
<div>
	<table width="100%">
		<tr>
			<td width="40%" align="right">审批意见：</td>
			<td><select id="approveFlag" name="approveFlag">
					<option value="1">同意</option>
					<option value="0">不同意</option>
				</select>
		</tr>
		<tr>
			<td align="right">详细意见：</td>
			<td><textarea id="approveInfo" name="approveInfo" cols="50" rows="6"></textarea></td>
		</tr>
	</table>
</div>
<div id="FormButtonDiv" align="center">
	<input id="save_button" type="button" value="保存"/> 
	
	<input id="submit_workflow_button" type="button" value="提交流程" />
</div>
 </form>
<script type="text/javascript">
<!--
//发票列表初始化
$(document).ready(function(){
	//保存按钮
	$("#save_button").click(function(){
		$('#_MainForm_').ajaxSubmit({
	        dataType:"json",
	        error: function(request) {
	            alert("保存失败。");
	        },
	        success: function(data) {
	            if (data.Result=='OK'){
	            	alert("保存成功!");
	            	//$("input[name='workItemId']").val(data.workItemId);
	            	//$("#submit_workflow_button").removeAttr("disabled");
	            	return false;
	            }else{
	            	alert(data.Message);
	            };
	        }		
		});
	});
	//流程提交按钮
	$("#submit_workflow_button").click(function(){
		$('#_MainForm_').ajaxSubmit({
	        dataType:"json",
	        error: function(request) {
	            alert("保存失败。");
	        },
	        success: function(data) {
	            if (data.Result=='OK'){
	            	//继续提交流程
	            	workflowClient.completeWorkItem();
	            }else{
	            	alert(data.Message);
	            };
	        }		
		});
		
	});
	
	$( "#tabs" ).tabs();//构造tab
	
	
	$("#LeaveRequestDetailTable").jtable({
		jqueryuiTheme:true,
		addRecordButton :$('#addDateButton'),
		animationsEnabled:false,
		actions:{
			listAction:"${pageContext.request.contextPath}/module/holiday/LeaveRequest/listRequestDetail?billCode=${obj['LeaveRequest'].billCode}",
			createAction:function(postData){
                return $.Deferred(function ($dfd) {
                    $.ajax({
                        url: "${pageContext.request.contextPath}/module/holiday/LeaveRequest/addRequestDetail?billCode=${obj['LeaveRequest'].billCode}",
                        type: 'POST',
                        dataType: 'json',
                        data: postData,
                        success: function (data) {
                            $dfd.resolve(data);
                        	$("#totalDaysSpan").text(data['TotalDays']);
                        },
                        error: function () {
                            $dfd.reject();
                        }
                    });
                });				
			},
	        deleteAction: function (postData) {
                return $.Deferred(function ($dfd) {
                    $.ajax({
                        url: "${pageContext.request.contextPath}/module/holiday/LeaveRequest/deleteRequestDetail?billCode=${obj['LeaveRequest'].billCode}",
                        type: 'POST',
                        dataType: 'json',
                        data: postData,
                        success: function (data) {
                            $dfd.resolve(data);
                        	$("#totalDaysSpan").text(data['TotalDays']);
                        },
                        error: function () {
                            $dfd.reject();
                        }
                    });
                });
            }
		},

		fields:{
			id:{
				key: true,
                create: false,
                edit: false,
                list: false
			},
			billCode:{
				title:'单据号',
				list:false,
				edit:false,
				create:false,
				defaultValue:"${obj['LeaveRequest'].billCode}"
			},

			leaveDate:{
				title:'请假日期',
				type:'date',
				displayFormat:'yy-mm-dd'
			},

			timeSection:{
				title:'时段',
				edit: true,
				create:true,
				options:{ '2': '全天', '1': '上午', '0': '下午' },
				optionsSorting:"value-desc"
			}
			
		},
		//Initialize validation logic when a form is created
        formCreated: function (event, data) {
            data.form.validationEngine();
        },
        //Validate form when it is being submitted
        formSubmitting: function (event, data) {
            return data.form.validationEngine('validate');
        },
        //Dispose validation logic when form is closed
        formClosed: function (event, data) {
            data.form.validationEngine('hide');
            data.form.validationEngine('detach');
        }

	});	
	
	//加载请假单明细
	$("#LeaveRequestDetailTable").jtable('load');
});
	
//-->
</script>
<jsp:include page="/template/include/_foot.jsp"></jsp:include>