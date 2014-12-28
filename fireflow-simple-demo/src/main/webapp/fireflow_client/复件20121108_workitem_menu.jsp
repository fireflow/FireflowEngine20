<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.engine.entity.runtime.WorkItemState" %>
<jsp:useBean id="constants"  class="org.fireflow.web.util.Constants" ></jsp:useBean>

<script language="javascript">
	var menuTimer =null;
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
	var openBizFormButton = document.getElementById("openBizFormButton");
	var completeWorkItemButton = document.getElementById("completeWorkItemButton");
	var selectNextActorButton = document.getElementById("selectNextActorButton");
	var jumpToButton = document.getElementById("jumpToButton");
	var reassignBeforeMeButton = document.getElementById("reassignBeforeMeButton");
	var reassignAfterMeButton = document.getElementById("reassignAfterMeButton");
	var openWorkItemHistoryButton = document.getElementById("openWorkItemHistoryButton");
	var openDiagramButton = document.getElementById("openDiagramButton");
	var withdrawWorkItemButton = document.getElementById("withdrawWorkItemButton");

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
    
    var theForm = document.getElementById('workflowOperationForm');

    if ((typeof theForm.onsubmite)=='undefined' || theForm.onsubmit()){
    	theForm.submit();
    }
}
/**
 * 直接结束工作项
 */
function completeWorkItemQuickliy(obj,actionType){
	var fadeDiv = document.getElementById("fadeDiv");
	fadeDiv.style.visibility="visible";
	var completeWorkItemQuicklyDiv = document.getElementById("completeWorkItemQuicklyDiv");
	completeWorkItemQuicklyDiv.style.visibility="visible";
	
}
</script>
<script type="text/javascript">
<!--
var completeWorkItemQuicklyDiv_Id=''  ;
    document.onmouseup=MUp  ;
    document.onmousemove=MMove  ;
      
    function MDown(Object){  
    	completeWorkItemQuicklyDiv_Id=Object.id  ;
        document.getElementById(completeWorkItemQuicklyDiv_Id).setCapture()  ;
        pX=event.x-document.getElementById(completeWorkItemQuicklyDiv_Id).style.pixelLeft;  
        pY=event.y-document.getElementById(completeWorkItemQuicklyDiv_Id).style.pixelTop;  
    }  
          
    function MMove(){  
        if(completeWorkItemQuicklyDiv_Id!=''){  
           document.getElementById(completeWorkItemQuicklyDiv_Id).style.left=event.x-pX;  
           document.getElementById(completeWorkItemQuicklyDiv_Id).style.top=event.y-pY;  
        }  
    }  
          
    function MUp(){  
        if(completeWorkItemQuicklyDiv_Id!=''){  
           document.getElementById(completeWorkItemQuicklyDiv_Id).releaseCapture();  
           completeWorkItemQuicklyDiv_Id='';  
        }  
    }  
//-->
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
 height: 250px;
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
<style>
.message_box { 
	visibility: hidden; 
	position: absolute; 
	top: 25%; left: 25%; width: 50%; height: 50%;
	padding: 10px; 
	border: #2D9ECA solid; 
	border-width: 1 1 3 1;  
	background-color: white; 
	color: #2D9ECA; 
	z-index:1002; 
	font-size: 12px;  
	overflow: auto; }

.massage {  
position: absolute;  
left: expression((body.clientWidth-350)/2);  
top: expression((body.clientHeight-200)/2);  
filter: dropshadow(color=#666666,offx=3,offy=3,positive=2);  
border: #2D9ECA solid;  
border-width: 1 1 3 1;  
width: 500px;  
height: 200px;  
background: #fff;  
color: #2D9ECA;  
font-size: 12px;  
line-height: 150%;  
z-index:2;
visibility: hidden; 
}  
.header {  
background: #2D9ECA;  
height: 25px;  
font-family: Verdana, Arial, Helvetica, sans-serif;  
font-size: 14px;  
padding: 3 5 0 5;  
color: #fff;  
}  


.fade{ 	visibility: hidden;  position: absolute; top: 0%; left: 0%; width: 100%; height: 100%;
background-color: black; z-index:1001; -moz-opacity: 0.8; opacity:.80; filter: alpha(opacity=80); }  

</style>  
<div id="workItemMenuDiv">
	<ul style="">
	<li><a id="claimWorkItemButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.CLAIM_WORKITEM}')">签收工作项</a></li>
	<li><a id="disClaimWorkItemButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.CLAIM_WORKITEM}')">退签收工作项</a></li>
	
	<li><hr/></li>		
	<li><a id="openBizFormButton" href="#" class="disabledItem" onclick="submitWorkflowOperationForm(this,'${constants.OPEN_BIZ_FORM}')">打开业务表单</a></li>
	<li><a id="completeWorkItemButton" href="#" class="disabledItem" onclick="completeWorkItemQuickliy(this,'${constants.COMPLETE_WORKITEM}')">直接审批通过</a></li>
	<li><a id="selectNextActorButton" href="#" class="disabledItem">指定下一步处理人</a></li>
	<li><a id="jumpToButton" href="#" class="disabledItem">跳转到...</a></li>
	<li><a id="reassignBeforeMeButton" href="#" class="disabledItem">前加签</a></li>
	<li><a id="reassignAfterMeButton" href="#" class="disabledItem">后加签</a></li>
	<li><hr/></li>	
	<li><a id="withdrawWorkItemButton" href="#" class="disabledItem">取回工作项</a></li>	
	<li><hr/></li>	
	<li><a id="openWorkItemHistoryButton" href="#" class="activeItem">打开审批历史记录</a></li>
	<li><a id="openDiagramButton" href="#" class="activeItem">查看流程图</a></li>
	</ul>
</div>
<form id="workflowOperationForm" action="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet" method="post" style="display:none">
<input id="${constants.ACTION_TYPE }" name="${constants.ACTION_TYPE }" value=""/>
<input id="${constants.WORKITEM_ID }" name="${constants.WORKITEM_ID }">
</form>

<div id="completeWorkItemQuicklyDiv" class="message_box">
           <div class="header">  
            <div style="display: inline; width: 150px; position: absolute">  
             <span id="a">审批意见</span> </div>  
            <span onclick="document.getElementById('fadeDiv').style.visibility='hidden';document.getElementById('completeWorkItemQuicklyDiv').style.visibility='hidden'; " style="float: right; display: inline; cursor: hand">× </span></div> 
<form>
		<table>
			<tr>
				<td>审批结论：</td>
				<td><input type="radio" name="decision" value="1">同意</td>
				<td><input type="radio" name="decision" value="0">不同意</td>
			</tr>
			<tr>
				<td>详细意见：</td>
				<td colspan="2">
					<textarea name="detail" rows="3" cols="50"></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>
<!-- 下面的div用于弹出对话框时，显示阴影背景 -->
<div id="fadeDiv" class="fade"> 
</div> 