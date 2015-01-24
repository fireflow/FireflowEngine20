<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div id="TodoListDiv" align="center">
</div>


<script type="text/javascript">
$(function(){

	function buildContextMenu(_workItemId,workItemState,url){	
		//然后构造下拉菜单
		  var menu =  {
				autoHide:true,
			    items: {
			        claimWorkItem: {name: "签收工作项",
			        				callback:function(key,opt){
			        					$("#_WORKITEM_ID_").val(_workItemId);

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

			        				},
			        				disabled:(workItemState!="0"?true:false)
			        },
			        openForm:{
			        	name:"打开业务处理表单",
        				callback:function(key,opt){
        					$("#_WORKITEM_ID_").val(_workItemId);
        					//var newUrl = url.replace("#","%23");
        					var newUrl = url.replace(/\#/g, "%23" );
        					
        					$("#_WORKFLOW_FORM_").attr("action", newUrl);
        					$("#_WORKFLOW_FORM_").submit();
        				},
        				disabled:(workItemState!="1"?true:false)
			        },
			        disClaim:{
			        	name:"退签收工作项",
        				callback:function(key,opt){
        					
        				},
        				disabled:true
			        },
			        history:{
			        	name:"查看流程历史记录",
        				callback:function(key,opt){
        			    	$("#_WORKFLOW_HISTORY_").jtable('load',$('#_WORKFLOW_FORM_').serializeArray());
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
				return buildContextMenu(record.id,record.stateValue,url);
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
            listAction: listTodoList
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
				width:'12%'
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
				width:'8%'
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
			createdTime:{
				title:'工作项创建时间',
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
				
				if (state==0){
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("claimItem",true);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("dicclaimItem",false);					
				}else if (state==1){
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("claimItem",false);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",true);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("dicclaimItem",false);
				}
				else{
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("claimItem",false);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
					$("#_WORKFLOW_TOOLBAR_").setbtEnable("dicclaimItem",false);					
				}
				

				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",true);				
			}else{
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("claimItem",false);
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("openFormItem",false);
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("dicclaimItem",false);
				
				$("#_WORKFLOW_TOOLBAR_").setbtEnable("listWorkItemHistoryItem",false);
			}			
		}

	});
	
	
	//加载待办
	$("#TodoListDiv").jtable('load');
	
});

</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>