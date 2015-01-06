<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
request.setAttribute("thePageTitle","我的发起的流程");



%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>
<div id="_WORKFLOW_TOOLBAR_" >

</div>

<div id="FormTitleDiv" align="center" >
	<span>我的发起的流程</span>
	
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
					</select>
				</form>
			</td>
			<td align="right" width="60px"><input id="queryButton" type="button" value="查询"/></td>

		</tr>
	</tbody>
</table>
<div id="MyProcInstListDiv" align="center">
</div>

<div id="_WORKFLOW_HISTORY_DIALOG_" style="display:none">
<div id="_WORKFLOW_HISTORY_" >
</div>
</div>


<form style="display:none" id="_WORKFLOW_FORM_" method="POST">
	<input type="hidden" id="_PROCESS_INSTANCE_ID_" name="processInstanceId" value=""/>
</form>



<script type="text/javascript">
$(function(){

	$('#queryButton').click(function(){
		$('#MyProcInstListDiv').jtable('load',$('#queryForm').serializeArray());
	});
	
	function buildContextMenu(_workItemId,workItemState){	
		//然后构造下拉菜单
		  var menu =  {
				autoHide:true,
			    items: {
			        openForm:{
			        	name:"中止流程",
        				callback:function(key,opt){
        					$("#_PROCESS_INSTANCE_ID_").val(_workItemId);
        					$("#_WORKFLOW_FORM_").attr("action",'${pageContext.request.contextPath}/module/workflow/WorkflowModule/abortProcessInstance');
        			    	$("#_WORKFLOW_FORM_").ajaxSubmit({
        			            type: 'POST',
        	                    dataType: 'json',
        	                    success: function (data) {
        	                    	
        	                    	$('#MyProcInstListDiv').jtable('reload');
        	                    },
        	                    error: function () {
        	                        alert("错误，无法完成该操作。")
        	                    }	
        			    	});
        				},
        				disabled:(workItemState<10?false:true)
			        },

			        history:{
			        	name:"查看流程历史记录",
        				callback:function(key,opt){
        			    	$("#_WORKFLOW_HISTORY_").jtable('load',{workItemId:_workItemId});
        			    	$('#_WORKFLOW_HISTORY_DIALOG_').dialog({
        			    		modal:true,
        			    		width:650,
        			    		height:300,
        			    		title:'查看处理历史'
        			    	});
        				}
			        }			        
			    }
			  };
		 return menu;
	}
	
	$("#MyProcInstListDiv").contextMenu({
		selector: 'tbody tr', 
		trigger: 'right',
		build: function($trigger, e){
			var $selectedRows = $('#MyProcInstListDiv').jtable('selectedRows');
			if ($selectedRows.length > 0) {
				var record = $selectedRows.first().data("record");
				return buildContextMenu(record.id,record.stateValue);
			}else{
				return buildContextMenu("0","-1");
			}

		}
	});
	
	$("#MyProcInstListDiv").jtable({
		jqueryuiTheme:true,
        paging: true,
        sorting: true,
        pageSize: 10,
        selecting: true, //Enable selecting
        multiselect: false, //Allow multiple selecting
        selectingCheckboxes: true, //Show checkboxes on first column
        actions:{
            listAction: function (postData, jtParams) {
                console.log("Loading from custom function...");
                return $.Deferred(function ($dfd) {
                    $.ajax({
                        url: '${pageContext.request.contextPath}/module/workflow/WorkflowModule/listMyProcInstJson?jtStartIndex=' + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
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
        },
		fields:{
			id:{
				title:'流程实例id',
				list:false,
				key:true
			},
			bizId:{
				title:'业务单据号',
				edit: false,
				create:false,
				width:'10%'
			},				
			processDisplayName:{
				title:'流程名称',
				edit: false,
				create:false,
				sorting:false,
				width:'10%'
			},
			subject:{
				title:'摘要',
				edit: false,
				create:false,
				width:'20%',
				sorting:false
			},			
			stateDisplayName:{
				title:'当前状态',
				edit: false,
				create:false,
				sorting:false,
				columnResizable:false
			},

			createdTime:{
				title:'业务发起时间',
				edit: false,
				create:false,
				type:'date',
				displayFormat:'yy-mm-dd',
				width:'10%'
			},			


			currentActivityInstances:{
				title:'当前环节',
				edit: false,
				create:false,
				sorting:false,
				width:'10%'
			}
		},
		recordsLoaded:function(event,data){

		},
		selectionChanged:function(event,data){//当选项变化时，更新工具栏
			var $selectedRows = $('#MyProcInstListDiv').jtable('selectedRows');
			if ($selectedRows.length > 0) {
				var record = $selectedRows.first().data("record");
				var url = "${pageContext.request.contextPath}"+record.actionUrl;
				var state = record.stateValue;

				$("#_PROCESS_INSTANCE_ID_").val(record.id);
				$("#_WORKFLOW_FORM_").attr("action", '${pageContext.request.contextPath}/module/workflow/WorkflowModule/abortProcessInstance');
				if (state<10){
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",true);
				}
				else{
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
				}
				

				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",true);				
			}else{
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",false);
			}			
		}

	});
	
	
	//加载发票数据
	$("#MyProcInstListDiv").jtable('load');
	
	
	$("#_WORKFLOW_HISTORY_").jtable({
		jqueryuiTheme:true,
		actions:{
			listAction:'${pageContext.request.contextPath}/module/workflow/WorkflowModule/showHistoryByProcessInstanceIdJson'
		},

		fields:{
			workItemName:{
				title:'环节名称'
			},
			stateDisplayName:{
				title:'状态'
			},
			ownerName:{
				title:'处理人'
			},
			endTime:{
				type:'date',
				displayFormat:'yy-mm-dd',
				title:'处理时间'
			}
			
		}
	});	
	
	$("#_WORKFLOW_TOOLBAR_").dtoolbar({
		items:[
		  {
				id:'openFormItem',
			    ico : 'btnEdit',
			    text : '中止流程',
			    disabled:true,
			    handler : function(){
			    	$("#_WORKFLOW_FORM_").ajaxSubmit({
			            type: 'POST',
	                    dataType: 'json',
	                    success: function (data) {
	                    	
	                    	$('#MyProcInstListDiv').jtable('reload');
	                    },
	                    error: function () {
	                        alert("错误，无法完成该操作。")
	                    }	
			    	});
			    }
			  },
			  
		  {
				id:'listWorkItemHistoryItem',
				ico:'',
			    text : '查看处理历史',
			    disabled:true,
			    handler : function(){
			    	var wiId = $("#_PROCESS_INSTANCE_ID_").val();
			    	$("#_WORKFLOW_HISTORY_").jtable('load',{processInstanceId:wiId});
			    	$('#_WORKFLOW_HISTORY_DIALOG_').dialog({
			    		modal:true,
			    		width:650,
			    		height:300,
			    		title:'查看处理历史'
			    	});
			    }
			  }		  
		]
	});
});

</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>