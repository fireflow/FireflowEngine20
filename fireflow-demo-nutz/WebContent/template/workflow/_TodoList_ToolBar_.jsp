<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
request.setAttribute("thePageTitle","我的待办列表");



%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>
<div id="_WORKFLOW_TOOLBAR_" >

</div>

<div id="_WORKFLOW_HISTORY_DIALOG_" style="display:none">
<div id="_WORKFLOW_HISTORY_" >
</div>
</div>

<form style="display:none" id="_WORKFLOW_FORM_" method="POST">
	<input type="hidden" id="_WORKITEM_ID_" name="workItemId" value=""/>
</form>

<script type="text/javascript">
$(function(){
	
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
		items:[{
			id:'claimItem',
		    ico : 'btnChecked',
		    text : '签收',
		    disabled:true,
		    handler : function(){
				$("#_WORKFLOW_FORM_").attr("action", '${pageContext.request.contextPath}/module/workflow/WorkflowModule/claimWorkItemJson');
				
				$("#_WORKFLOW_FORM_").ajaxSubmit({
		            type: 'POST',
	                dataType: 'json',
	                success: function (data) {
	                	
	                	$('#TodoListDiv').jtable('reload');
	                },
	                error: function () {
	                    $dfd.reject();
	                }			                    
				});		    	
		    }
		  },
		  {
				id:'openFormItem',
			    ico : 'btnEdit',
			    text : '打开业务处理表单',
			    disabled:true,
			    handler : function(){
			    	var formUrl = $("#_WORKFLOW_FORM_").attr("action");
			    	var newUrl = formUrl.replace(/\#/g, "%23" );
					$("#_WORKFLOW_FORM_").attr("action", newUrl);
			    	$("#_WORKFLOW_FORM_").submit();
			    }
			  },
			  {
					id:'dicclaimItem',
				    ico : 'btnrs',
				    text : '退签收',
				    disabled:true,
				    handler : function(){
	
				    }
				  },			  
		  {
				id:'listWorkItemHistoryItem',
				ico:'',
			    text : '查看处理历史',
			    disabled:true,
			    handler : function(){
			    	$("#_WORKFLOW_HISTORY_").jtable('load',$('#_WORKFLOW_FORM_').serializeArray());
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