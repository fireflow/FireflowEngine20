<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.engine.entity.runtime.WorkItemState,org.fireflow.engine.entity.runtime.WorkItemProperty" %>
<jsp:useBean id="constants"  class="org.fireflow.web.util.Constants" ></jsp:useBean>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/fireflow_client/resources/css/table.css"
	type="text/css">
<link
	href="<%=request.getContextPath()%>/common/resources/jquery/css/ui-lightness/jquery-ui-1.9.2.custom.css"
	rel="stylesheet">
<script
	src="<%=request.getContextPath()%>/common/resources/jquery/js/jquery-1.8.3.js"></script>
<script
	src="<%=request.getContextPath()%>/common/resources/jquery/js/jquery-ui-1.9.2.custom.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/common/resources/jstree/jquery.jstree.js"></script>

<script language="javascript">
	var menuTimer =null;

/**
 * 弹出工作项菜单
 */
function showmenu(obj1,obj2,state,location,workItemId,workItemState){
    var btn=document.getElementById(obj1);
    var obj=document.getElementById(obj2);
    var h=btn.offsetHeight;
    var w=btn.offsetWidth;
    var x=btn.offsetLeft;
    var y=btn.offsetTop;
   
    obj.onmouseover =function(){
        showmenu(obj1,obj2,'show',location,workItemId,workItemState);
    }
    obj.onmouseout =function(){
        showmenu(obj1,obj2,'hide',location,workItemId,workItemState);
    }
   
    while(btn=btn.offsetParent){y+=btn.offsetTop;x+=btn.offsetLeft;}
   
    var hh=obj.offsetHeight;
    var ww=obj.offsetWidth;
    var xx=obj.offsetLeft;//style.left;
    var yy=obj.offsetTop;//style.top;
    var obj2state=state.toLowerCase();
    var obj2location=location.toLowerCase();
   
    var showx,showy;

    if(obj2location=="left" || obj2location=="l" || obj2location=="top" || obj2location=="t" || obj2location=="u" || obj2location=="b" || obj2location=="r" || obj2location=="up" || obj2location=="right" || obj2location=="bottom"){
        if(obj2location=="left" || obj2location=="l"){showx=x-ww;showy=y;}
        if(obj2location=="top" || obj2location=="t" || obj2location=="u"){showx=x;showy=y-hh;}
        if(obj2location=="right" || obj2location=="r"){showx=x+w;showy=y;}
        if(obj2location=="bottom" || obj2location=="b"){showx=x;showy=y+h;}
    }else{
        showx=xx;showy=yy;
    }
    obj.style.left=showx+"px";
    obj.style.top=showy+"px";
    if(state =="hide"){
        menuTimer =setTimeout("hiddenmenu('"+ obj2 +"')", 500);
    }else{
        clearTimeout(menuTimer);
        obj.style.visibility ="visible";
    }
    
    var workItemIdField = document.getElementById('${constants.WORKITEM_ID}');
    workItemIdField.value=workItemId;
    updateMenu(workItemState);
}
function hiddenmenu(psObjId){
    document.getElementById(psObjId).style.visibility ="hidden";
}
/*
 * 根据 workitem state更新菜单项的状态 
 */
function updateMenu(workItemState){
	var claimWorkItemButton = document.getElementById("claimWorkItemButton");
	var disClaimWorkItemButton = document.getElementById("disClaimWorkItemButton");
	var openBizFormButton = document.getElementById("openBizFormButton");
	var completeWorkItemButton = document.getElementById("completeWorkItemButton");
	var selectNextActorButton = document.getElementById("selectNextActorButton");
	var jumpToButton = document.getElementById("jumpToButton");
	var reassignBeforeMeButton = document.getElementById("reassignBeforeMeButton");
	var reassignAfterMeButton = document.getElementById("reassignAfterMeButton");
	var openWorkItemHistoryButton = document.getElementById("openWorkItemHistoryButton");
	var openDiagramButton = document.getElementById("openDiagramButton");
	var withdrawWorkItemButton = document.getElementById("withdrawWorkItemButton");

	disClaimWorkItemButton.className="disabledItem";
    claimWorkItemButton.className="disabledItem";
    openBizFormButton.className="disabledItem";
	completeWorkItemButton.className="disabledItem";
	selectNextActorButton.className="disabledItem";
	jumpToButton.className="disabledItem";
	reassignBeforeMeButton.className="disabledItem";
	reassignAfterMeButton.className="disabledItem";
	withdrawWorkItemButton.className="disabledItem";
	
	if (workItemState=='<%=WorkItemState.INITIALIZED.getValue()%>'){//
		claimWorkItemButton.className="activeItem";
	}
	else if (workItemState=='<%=WorkItemState.RUNNING.getValue()%>'){//
		disClaimWorkItemButton.className="activeItem";
		openBizFormButton.className="activeItem";
		completeWorkItemButton.className="activeItem";
		selectNextActorButton.className="activeItem";
		jumpToButton.className="activeItem";
		reassignBeforeMeButton.className="activeItem";
		reassignAfterMeButton.className="activeItem";
	}
	

}
/**
 * 流程表单提交
 */
function submitWorkflowOperationForm(obj,actionType){
	if (obj.className=="disabledItem")return;
    var actionTypeField = document.getElementById('${constants.ACTION_TYPE}');
    actionTypeField.value=actionType;
    //alert(actionTypeField.value);
    var theForm = document.getElementById('workflowOperationForm');

    if ((typeof theForm.onsubmite)=='undefined' || theForm.onsubmit()){
    	theForm.submit();
    }
}
/**
 * 快速审批工作项
 */
function completeWorkItemQuickliy(obj,actionType){
	if (obj.className=="disabledItem")return;
    
    var theForm = document.getElementById('workflowOperationForm');
    
    //正式系统可以修改theForm的action属性，使之指向一个真实的业务URL，业务逻辑处理完毕后再导航到/test/WorkflowOperationServlet
    theForm.action="${pageContext.request.contextPath}/servlet/CommonApprovalInfoServlet";
	
	//动态增加审批结论及审批意见字段，正式系统可以修改其value值，以适合业务需求
	var decisionInput = document.createElement("input");
	decisionInput.id="decision";
	decisionInput.name="decision";
	decisionInput.value="1";
	theForm.appendChild(decisionInput);
	
	var detailInfo = document.createElement("input");
	detailInfo.id="detailInfo";
	detailInfo.name="detailInfo";
	detailInfo.value="同意！";
	theForm.appendChild(detailInfo);
    
    submitWorkflowOperationForm(obj,actionType);
}

/**
 * 退签收
 */
function disclaimWorkItem(obj,actionType){
	if (obj.className=="disabledItem")return;
	
	//弹出备注对话框，让操作员填充备注信息
	//TODO　此处待业务系统完善
    submitWorkflowOperationForm(obj,actionType);
}

function reassignTo(flag){
	var workItemIdField = document.getElementById('${constants.WORKITEM_ID}');
    //通过ajax获得后续节点，然后弹出对话框

	$.get("${pageContext.request.contextPath}/servlet/WorkflowOperationServlet",
			{ workflowActionType: "OPEN_REASSIGN_ACTOR_SELECTOR", workItemId: $("#workItemId").val(), reassignFlag:flag},
			function(data){
			    $("#next_step_and_actors").html(data);
				$('#'+'${constants.ACTION_TYPE }').val('${constants.REASSIGN_WORKITEM}');
				 
				 $( "#select_next_step_actors_dialog" ).dialog("open");
			  }
		);
}

function jumpTo(){
	var workItemIdField = document.getElementById('${constants.WORKITEM_ID}');
	$.get("${pageContext.request.contextPath}/servlet/WorkflowOperationServlet",
			{ workflowActionType: "OPEN_TARGET_ACTIVITY_SELECTOR", workItemId: $("#workItemId").val()},
			function(data){
			    $("#next_step_and_actors").html(data);
				$('#'+'${constants.ACTION_TYPE }').val('${constants.COMPLETE_WORKITEM_AND_JUMP_TO}');
				 
				 $( "#select_next_step_actors_dialog" ).dialog("open");
			  }
		);
}
/**
 * 选择下一环节的操作者
 */
function selectNextStepActors(obj,actionType){
	if (obj.className=="disabledItem")return;
	
	//弹出对话框，让操作者选择下一个步骤的操作者
	//采用window.showModalDialog()，适合IE及Firefox
	
	var workItemIdField = document.getElementById('${constants.WORKITEM_ID}');
	/*
	var left = (window.screen.availWidth -500)/2;
	var top = (window.screen.availHeight -400)/2;

	var textArea = document.createElement("TextArea");
	textArea.id="nextStepActors";
	textArea.name="nextStepActors";
	window.showModalDialog( "${pageContext.request.contextPath}/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.OPEN_NEXT_STEP_ACTOR_SELECTOR}&${constants.WORKITEM_ID}="+workItemIdField.value,
			textArea,"dialogHeight: 400px; dialogWidth: 500px;  edge: Raised; resizable: Yes; status: No;dialogTop:"+top+" px; dialogLeft:"+left+" px;" );
	
    submitWorkflowOperationForm(obj,actionType);
    */
    
    //通过ajax获得后续节点，然后弹出对话框
	$.get("${pageContext.request.contextPath}/servlet/WorkflowOperationServlet",
			{ workflowActionType: "OPEN_NEXT_STEP_ACTOR_SELECTOR", workItemId: $("#workItemId").val() },
			function(data){
			    $("#next_step_and_actors").html(data);
			    
				 $("#selectActorDiv").tabs();
 				$('#'+'${constants.ACTION_TYPE }').val('${constants.COMPLETE_WORKITEM}');
				 $( "#select_next_step_actors_dialog" ).dialog("open");
			  }
		);
    //$.get("/fireflow-simple-demo/fireflow_client/_next_step_and_actors.jsp");

}
</script>
<script  type="text/javascript">
	function deleteRow(htmlObj){
		try{
			$(htmlObj).parent("td").parent("tr").remove();  
		}catch(e){
			alert(e);
		}
		
	}
	
	$(function() {
		$("#actors_tree").jstree({ 
			"xml_data" : {
				"ajax":{
					"url":"${pageContext.request.contextPath}/servlet/WorkflowOperationServlet",
					"data":function(node){
						return {
							"workflowActionType":"LOAD_OU_AS_JSTREE_XML",
							"current_node_id":node.attr ? node.attr("id") : "-1",
							"rand" : new Date().getTime()
						};
					}
				},
				"xsl":"flat"				
			},
			"plugins" : [ "themes", "ui","xml_data" ]
		});

		 $( "#select_next_step_actors_dialog" ).dialog({
	            autoOpen: false,
	            height:300,
	            width:700,
	            modal: true,
	            buttons: {
	            	"提交": function() {
	            		var bValid = true;
	            		if (bValid){
	            			try{
		            			$('#workflowOperationForm').submit();
	            			}catch(e){
	            				alert(e);
	            			}

	            		}
	            	},
	            	"取消": function() {
	            		$( this ).dialog( "close" );
	            	}
	            }
		 
		 });
		
		$("#add_actor_button").click(function(){
			/*
			
			$("#actors_tree").jstree("deselect_all");
			*/
			var selectedNodes = $("#actors_tree").jstree("get_selected");
			$.each( selectedNodes, function(i, n){
				var nextActorsTable = $("table[activity_id]:visible");
				if (nextActorsTable){
					//增加一行
					try{
						if ("user"===n.getAttribute("node_type")){
							var id = n.getAttribute("id");
							var name = n.getAttribute("name");
							var deptName = n.getAttribute("dept_name");
							var deptId = n.getAttribute("dept_id");
							var activityId = nextActorsTable.attr("activity_id");
							//判断该人是否已经存在于列表中
							var allInputs = nextActorsTable.find("input");
							var exist = false;
							$.each(allInputs,function(k,theInput){
								if (theInput.getAttribute("actor_id")===id){
									exist = true;
								}
							});
							if (!exist){
								nextActorsTable.children("tbody").append("<tr><td><input type=\"hidden\" name=\"actor_4_"+
										activityId+
										"\" value=\""+id+"~"+name+"~"+deptId+"~"+deptName+"\" actor_id=\""+id+"\"/>"+id+
										"</td><td>"+name+
										"</td><td>"+deptName+
										"</td><td><button onclick=\"deleteRow(this)\">删除</button></td></tr>");
							}
							
						}else{
							//alert(n.getAttribute("node_type"));
						}
					}catch(e){
						alert(e);
					}

					
				}
			}); 

		});
		
		
	});
</script>
<style>
#workItemMenuDiv{
 position: absolute;
 border-top: 1px solid #cccccc;
 border-left:1px solid #cccccc;
 border-right:2px solid #000000;
 border-bottom:2px solid #000000;
 top: 0px;
 left: 0px;
 width: 150px;
 height: 265px;
 background-color: #F5F5DC;
 visibility: hidden;
 padding-top: 5px;
 overflow: hidden;
}

#workItemMenuDiv ul { padding:0px; margin:0px 0px 0px;}
#workItemMenuDiv ul li{list-style-type: none; }
#workItemMenuDiv ul li a.activeItem{color:#000000; text-align:left; font-size:12px; font-weight:normal; text-decoration:none; border-style:none; border-color:#000000; border-width:1px; padding:3px 2px 3px 3px; margin:0px; }
#workItemMenuDiv ul li a.disabledItem{color:gray; text-align:left; font-size:12px; font-weight:normal; text-decoration:none; border-style:none; border-color:#000000; border-width:1px; padding:3px 2px 3px 3px; margin:0px; cursor:default}
#workItemMenuDiv ul li:hover  {background-color:#87CEEB;}
</style>

<div id="workItemMenuDiv">
	<ul style="">
	<li><a id="claimWorkItemButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.CLAIM_WORKITEM}')">签收工作项</a></li>
	
	<li><hr/></li>		
	<li><a id="openBizFormButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.OPEN_BIZ_FORM}')">打开业务表单</a></li>
	<li><a id="completeWorkItemButton" href="#" class="disabledItem" onclick="completeWorkItemQuickliy(this,'${constants.COMPLETE_WORKITEM}')">快速审批通过</a></li>
	<li><a id="selectNextActorButton" href="#" class="disabledItem" onclick="selectNextStepActors(this,'${constants.COMPLETE_WORKITEM}')">指定下一步处理人</a></li>
	<li><a id="jumpToButton" href="#" class="disabledItem" onclick="jumpTo()">跳转到...</a></li>
	<li><a id="reassignBeforeMeButton" href="#" class="disabledItem" onclick="reassignTo('beforeme')">前加签</a></li>
	<li><a id="reassignAfterMeButton" href="#" class="disabledItem" onclick="reassignTo('afterme')">后加签</a></li>
	<li><hr/></li>	
	<li><a id="disClaimWorkItemButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.DISCLAIM_WORKITEM}')">退签收工作项</a></li>
	
	<li><a id="withdrawWorkItemButton" href="#" class="disabledItem">取回工作项</a></li>	
	<li><hr/></li>	
	<li><a id="openWorkItemHistoryButton" href="#" class="activeItem">打开审批历史记录</a></li>
	<li><a id="openDiagramButton" href="#" class="activeItem">查看流程图</a></li>
	</ul>
</div>

<div id="select_next_step_actors_dialog" align="center">
<form id="workflowOperationForm"
	action="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet"
	method="post">
	<input type="hidden" id="${constants.ACTION_TYPE }" name="${constants.ACTION_TYPE }"
		value="123" />
	<input type="hidden" id="${constants.WORKITEM_ID }"
		name="${constants.WORKITEM_ID }">	
		<table width="95%" border="1" cellspacing="0">
			<tr>
				<td valign="top" width="25%">
					<div id="actors_tree">

					</div></td>
				<td>
				<input type="button" id="add_actor_button" value="add"></button>
				</td>
				<td>
				<div id="next_step_and_actors">
				
				
				</div>
				</td>
			</tr>
		</table>
</form>		
</div>
	






