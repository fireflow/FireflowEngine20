<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



<script>
/*  定义一个工作流客户端对象  */

function WorkflowClient(wiId){
	this.workItemId = wiId;
}

WorkflowClient.prototype.getWorkItemId=function(){
	return this.workItemId;
};

WorkflowClient.prototype.completeWorkItem=function(){
	$('#_WORKFLOW_FORM_').attr('action','${pageContext.request.contextPath}/module/workflow/WorkflowModule/completeWorkItem');
	$('#_WORKITEM_ID_').val(this.getWorkItemId());
	$('#_WORKFLOW_FORM_').submit();
};

WorkflowClient.prototype.showNextActorCandidates = function(){
	$('#_WORKFLOW_FORM_').attr('action','${pageContext.request.contextPath}/module/workflow/WorkflowModule/showNextActorCandidates');
	$('#_WORKITEM_ID_').val(this.getWorkItemId());
	$('#_WORKFLOW_FORM_').ajaxSubmit({
		success: function(data) {
			$('#_WORKFLOW_DIALOG_').html(data);
			$('#_WORKFLOW_DIALOG_').dialog({
				modal:true,
				width:800,
				height:600,
				title:'指定后续环节操作者'
			});
		},
		error:function(data){
			$('#_WORKFLOW_DIALOG_').html(data);
			$('#_WORKFLOW_DIALOG_').dialog({
				modal:true,
				width:600,
				height:300,
				title:'指定后续环节操作者'
			});
		}
	});
};

WorkflowClient.prototype.showHistory=function(){
	$('#_WORKITEM_ID_').val(this.getWorkItemId());
	$("#_WORKFLOW_HISTORY_").jtable('load',$('#_WORKFLOW_FORM_').serializeArray());
	$('#_WORKFLOW_HISTORY_DIALOG_').dialog({
		modal:true,
		width:650,
		height:300,
		title:'查看处理历史'
	});
};

WorkflowClient.prototype.deleteCandidateActor = function(trId){
	$(".nextActorsTable:visible tr").each(function(idx,item){
		var tmp = $(item);

		if (tmp.attr("id")==trId){
			tmp.detach();
		}
	});

};

var workflowClient = new WorkflowClient('${param.workItemId}');



$(document).ready(function(){
	$("#_WORKFLOW_TOOLBAR_").dtoolbar({
		items:[{
			id:'saveForm',
		    ico : 'btnSave',
		    text : '保存',
		    disabled:(workflowClient.getWorkItemId()==''? true:false),
		    handler : function(){
		    	//该函数在通用oj.cmn.js里面，如果有需要
		    	//在自己的jsp里面覆盖，例子Order_edit.jsp，必须有返回true或者false;
		    	var b = beforeSubmit();
		    	if (!b)return false;
		    	
				$('#_MainForm_').ajaxSubmit({
			        dataType:"json",
			        error: function(request) {
			            alert("业务表单保存失败。");
			        },
			        success: function(data) {
			            if (data.Result=='OK'){
			            	alert("保存成功。");
			            }else{
			            	alert(data.Message);
			            };
			        }	
		    	}); 
		    }
		  },
		  {
			id:'completeWorkItem',
		    ico : 'btnChecked',
		    text : '提交流程',
		    disabled:(workflowClient.getWorkItemId()==''? true:false),
		    handler : function(){
		    	//该函数在通用oj.cmn.js里面，如果有需要
		    	//在自己的jsp里面覆盖，例子Order_edit.jsp，必须有返回true或者false;
		    	var b = beforeSubmit();
		    	if (!b)return false;
		    	
				$('#_MainForm_').ajaxSubmit({
			        dataType:"json",
			        error: function(request) {
			            alert("业务表单保存失败。");
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
		    }
		  },
		  {
				id:'reject',
			    ico : 'btnChecked',
			    text : '提交流程给...',
			    disabled:(workflowClient.getWorkItemId()==''? true:false),
			    handler : function(){
			    	//该函数在通用oj.cmn.js里面，如果有需要
			    	//在自己的jsp里面覆盖，例子Order_edit.jsp，必须有返回true或者false;
			    	var b = beforeSubmit();
			    	if (!b)return false;
			    	
					$('#_MainForm_').ajaxSubmit({
				        dataType:"json",
				        error: function(request) {
				            alert("业务表单保存失败。");
				        },
				        success: function(data) {
				            if (data.Result=='OK'){
				            	//继续提交流程
				            	workflowClient.showNextActorCandidates();
				            }else{
				            	alert(data.Message);
				            };
				        }		
					});			    	
					
			    }
			  
		  },{
				id:'listWorkItemHistory',
				ico:'',
			    text : '查看处理历史',
			    disabled:(workflowClient.getWorkItemId()==''? true:false),
			    handler : function(){
			    	workflowClient.showHistory();
			    }
			  }		  
		]
	});
	
	
	$("#_WORKFLOW_HISTORY_").jtable({
		jqueryuiTheme:true,
		actions:{
			listAction:'${pageContext.request.contextPath}/module/workflow/WorkflowModule/showHistoryJson'
		},

		fields:{
			workItemName:{
				title:'环节名称'
			},
			state:{
				title:'状态'
			},
			ownerName:{
				title:'处理人'
			},
			endTime:{
				tile:'处理时间'
			}
			
		}
	});	
});



</script>
<div id="_WORKFLOW_TOOLBAR_" >

</div>

<div id="_WORKFLOW_DIALOG_" style="display:none" class="jtable-dialog-form">
</div>

<div id="_WORKFLOW_HISTORY_DIALOG_" style="display:none">
<div id="_WORKFLOW_HISTORY_" >
</div>
</div>

<form style="display:none" id="_WORKFLOW_FORM_" method="POST">
	<input type="hidden" id="_WORKITEM_ID_" name="workItemId" value=""/>
</form>

