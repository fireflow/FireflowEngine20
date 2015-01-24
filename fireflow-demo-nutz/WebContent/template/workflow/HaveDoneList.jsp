<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
request.setAttribute("thePageTitle","我的已办列表");



%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>
<div id="_WORKFLOW_TOOLBAR_" >

</div>

<div id="FormTitleDiv" align="center" >
	<span>我的已办列表</span>
	
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
<div id="TodoListDiv" align="center">
</div>

<div id="_WORKFLOW_HISTORY_DIALOG_" style="display:none">
<div id="_WORKFLOW_HISTORY_" >
</div>
</div>

<input type="hidden" id="_WORKITEM_ID_" name="workItemId" value=""/>
<form style="display:none" id="_WORKFLOW_FORM_" method="POST">
	
</form>



<script type="text/javascript">
$(function(){

	$('#queryButton').click(function(){
		$('#TodoListDiv').jtable('load',$('#queryForm').serializeArray());
	});
	
	function buildContextMenu(_workItemId,workItemState,url){	
		//然后构造下拉菜单
		  var menu =  {
				autoHide:true,
			    items: {
			        openForm:{
			        	name:"打开业务处理表单",
        				callback:function(key,opt){
        					$("#_WORKFLOW_FORM_").attr("action", url);
        					$("#_WORKFLOW_FORM_").submit();
        				}
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
	
	$("#TodoListDiv").contextMenu({
		selector: 'tbody tr', 
		trigger: 'right',
		build: function($trigger, e){
			var $selectedRows = $('#TodoListDiv').jtable('selectedRows');
			if ($selectedRows.length > 0) {
				var record = $selectedRows.first().data("record");
				var url = "${pageContext.request.contextPath}"+record.actionUrl;
				var newUrl = url.replace(/\#/g, "%23" );
				return buildContextMenu(record.id,record.stateValue,newUrl);
			}else{
				return buildContextMenu("0","-1","");
			}

		}
	});
	
	$("#TodoListDiv").jtable({
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
                        url: '${pageContext.request.contextPath}/module/workflow/WorkflowModule/listHaveDoneWorkItemsJson?jtStartIndex=' + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
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
				title:'工作项id',
				list:false,
				key:true
			},
			bizId:{
				title:'业务单据号',
				edit: false,
				create:false,
				width:'10%'
			},				
			subject:{
				title:'工作项主题',
				edit: false,
				create:false,
				width:'40%'
			},
		
			procInstCreatorName:{
				title:'业务发起人',
				edit: false,
				create:false,
				width:'10%'
			},

			procInstCreatedTime:{
				title:'发起时间',
				edit: false,
				create:false,
				type:'date',
				displayFormat:'yy-mm-dd',
				width:'10%'
			},			

			stateDisplayName:{
				title:'工作项状态',
				edit: false,
				create:false,
				columnResizable:false
			},
			endTime:{
				title:'工作项处理时间',
				edit: false,
				create:false,
				type:'date',
				displayFormat:'yy-mm-dd',
				width:'10%'
			}
		},
		recordsLoaded:function(event,data){

		},
		selectionChanged:function(event,data){//当选项变化时，更新工具栏
			var $selectedRows = $('#TodoListDiv').jtable('selectedRows');
			if ($selectedRows.length > 0) {
				var record = $selectedRows.first().data("record");
				var url = "${pageContext.request.contextPath}"+record.actionUrl;
				var state = record.stateValue;

				$("#_WORKITEM_ID_").val(record.id);
				$("#_WORKFLOW_FORM_").attr("action", url);
				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",true);
				

				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",true);				
			}else{
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",false);
			}			
		}

	});
	
	
	//加载发票数据
	$("#TodoListDiv").jtable('load');
	
	
	$("#_WORKFLOW_HISTORY_").jtable({
		jqueryuiTheme:true,
		actions:{
			listAction:'${pageContext.request.contextPath}/module/workflow/WorkflowModule/showHistoryJson'
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
			    text : '打开业务处理表单',
			    disabled:true,
			    handler : function(){
			    	$("#_WORKFLOW_FORM_").submit();
			    }
			  },
			  
		  {
				id:'listWorkItemHistoryItem',
				ico:'',
			    text : '查看处理历史',
			    disabled:true,
			    handler : function(){
			    	var wiId = $("#_WORKITEM_ID_").val();
			    	$("#_WORKFLOW_HISTORY_").jtable('load',{workItemId:wiId});
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