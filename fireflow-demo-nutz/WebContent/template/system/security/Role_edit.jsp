<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","角色维护");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>

<div id="FormTitleDiv" align="center" >
	<span>角色管理:创建及修改</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td align="right">
			<table>
				<tr>
					<form id="queryForm" action="" method="POST">
						角色名称：<input id="name" name="name"/>
					</form>
				</tr>
			</table>
		</td>
		<td width="130" align="right">
			<button id="queryButton">查询</button>
			<button id="createButton">创建角色</button>
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id="RoleTable">
			
			</div>
		</td>
	
	</tr>
</table>

<div id="addRoleDialog" class="ui-dialog-content ui-widget-content" style="display:none;align:center">
	<form id="addRoleForm"  class="jtable-dialog-form jtable-create-form" action="${pageContext.request.contextPath}/module/Role/add" method="post">

		<table width="100%">
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">角色代码<span style="color:red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input id="code" name="code" class="validate[required]"/></td>
				<td align="left" class="jtable-input-label">(建议用ROLE_做前缀，<br/>只支持数字和英文字符)</td>
			</tr>		
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">角色名称<span style="color:red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input id="name" name="name" class="validate[required]"/></td>
				<td align="left" class="jtable-input-label"></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">是否为岗位：</td>
				<td class="jtable-input jtable-text-input"><input type="radio" name="isPosition" value="true">是&nbsp;&nbsp;
					<input type="radio" name="isPosition" value="false" checked="checked"/>否</td>
				<td align="left" class="jtable-input-label"></td>
			</tr>
		</table>
		
	</form>
</div>

<div id="editRoleDialog" class="ui-dialog-content ui-widget-content" style="display:none;align:center">
	<form id="editRoleForm"  class="jtable-dialog-form jtable-create-form" action="${pageContext.request.contextPath}/module/Role/update" method="post">
		<div id="roleInfoDiv">

		
		</div>
		

	</form>
</div>

<div id="editRoleMemberDialog" class="ui-dialog-content ui-widget-content" style="display:none;align:center">
	<form id="editRoleMemberForm"  class="jtable-dialog-form jtable-create-form" action="${pageContext.request.contextPath}/module/UserRole/updateUserRole" method="post">
		<input type="hidden" name="groupCode" value="0"/>
		<table width="100%" id="editRoleMemberTable" border="0" style="background-color:white">
			<tr>
			<td width="250px" valign="top">
				<div id="_OU_TREE_WRAPER_" style="border-right: 1px solid gray;overflow:auto">
					<div id="_OU_TREE_" class="ztree"
					style=""></div>
				</div>
				<td>
				<td colspan="1" valign="top">
					<div id="currentMemberDiv" style="margin-left:10px;overflow:auto">
					
					
					</div></td>
			</tr>
		</table>
		
	</form>
</div>

<script type="text/javascript">
<!--
var ouZTreeObject = null;
var currentNode = null;
$(document).ready(function(){
	//初始化验证
	$("#addRoleForm").validationEngine();	
	
	var setting = {
			async : {
				enable : true,
				url : "${pageContext.request.contextPath}/module/Group/loadChildrenAsZTreeNodes",
				autoParam : [ "id", "level","nodeType" ,"parentId"]
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					if (treeNode.nodeType=='O' ){
						currentNode = treeNode;
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
						

					}
					else if (treeNode.nodeType=='G'){
						currentNode = treeNode;
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
					}
					else if (treeNode.nodeType=='R'){
						currentNode = treeNode;
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
					}else if (treeNode.nodeType=='U'){
						currentNode = treeNode.getParentNode();
						
						
					}

				},
				onDblClick:function(event, treeId, treeNode) {
					if (treeNode.nodeType=='U'){
						currentNode = treeNode.getParentNode();

						addMemberToRole(treeNode.id,treeNode.name);
						
						
					}
				}
			}
		};

	
	ouZTreeObject = $.fn.zTree.init($("#_OU_TREE_"), setting);

});

function deleteMemberOfRole(htmlObj){
	$(htmlObj).parent().detach();
}

function addMemberToRole(userCode,userName){
	var found = false;
	$.each($("#editRoleMemberDialog .userCodeOfRole"),function(i,theInput){
		if($(theInput).val()==userCode){
			found = true;
		}
	});
	
	if (found)return;
	
	var u = "<div  style=\"color:black\">"+
	"<img id=\"userCodeCheckbox\" src=\"${pageContext.request.contextPath}/static/images/delete.png\" onclick=\"deleteMemberOfRole(this)\" style=\"cursor:pointer\"/>"+
	"<input type=\"hidden\" class=\"userCodeOfRole\" name=\"userCode\" value=\""+userCode+"\"/>"+
	userName+
	"</div>";
	
	$("#currentMemberDiv").append(u);
}

function editRoleMember(roleCode,roleName){
	//获得当前已有的用户列表
	$("#editRoleMemberDialog #currentMemberDiv").html("");
	$.ajax({
		url:'${pageContext.request.contextPath}/module/Role/getMemebersOfRole',
		data:{roleCode:roleCode},
		type:'POST',
		success: function (data) {
			$("#editRoleMemberDialog #currentMemberDiv").html(data);
            
        }
	});
	var wd_h = $(window).height();
	var wd_w = $(window).width();
	
	var workspaceHeight = wd_h-23-52;
	var theHeight = workspaceHeight-100;
	$("#editRoleMemberTable").height(theHeight-110);
	$("#_OU_TREE_WRAPER_").height(theHeight-120);
	$("#currentMemberDiv").height(theHeight-120);
	$("#editRoleMemberDialog").dialog({
		title:'编辑['+roleName+']成员',
		width:'500',
		height:theHeight,
		modal:true,
		resizable:false,
		dialogClass:'jtable_customized_dialog',
		buttons:[ { text: "取消", 
					click: function() { $( this ).dialog( "close" ); } },
				  { text: "保存",
					click:function(){
						$("#editRoleMemberForm").ajaxSubmit({
					        dataType:"json",
					        error: function(request) {
					            alert("保存失败。");
					        },
					        success: function(data) {
					        	if(data.Result != 'OK'){
					        		alert(data.Message);
					        	}else{
					        		
					        		$("#editRoleMemberDialog").dialog('close');
					        	}
					        	
					        }
						});
					}
				  }
					]
	});
}

function editRole(roleCode,roleName){
	$("#editRoleDialog #roleInfoDiv").html("");
	$.ajax({
		url:'${pageContext.request.contextPath}/module/Role/getRoleForEdit',
		data:{code:roleCode},
		type:'POST',
		success: function (data) {
			$("#editRoleDialog #roleInfoDiv").html(data);
            
        }
	});
	$("#editRoleDialog").dialog({
		title:'编辑角色',
		width:'350',
		height:'220',
		modal:true,
		dialogClass:'jtable_customized_dialog',
		buttons:[ { text: "取消", 
					click: function() { $( this ).dialog( "close" ); } },
				  { text: "保存",
					click: function(){
						if ( $("#editRoleForm").validationEngine('validate') ) {
							$('#editRoleForm').ajaxSubmit({
						        dataType:"json",
						        error: function(request) {
						            alert("保存失败。");
						        },
						        success: function(data) {
						        	if(data.Result != 'OK'){
						        		alert(data.Message);
						        	}else{
						        		
						        		$("#editRoleDialog").dialog('close');
						        		$("#RoleTable").jtable('reload');
						        	}
						        	
						        }
							});
						}
					}
				  }]
	});	
}

function deleteRole(roleCode,roleName){
	var test = confirm('请确认是否要删除'+roleName+'角色？删除该角色的同时，会解除该角色和用户的关联。');
	if (test){
		$.ajax({
			url:'${pageContext.request.contextPath}/module/Role/delete',
			data:{code:roleCode},
			dataType:'json',
			type:'POST',
			success: function (data) {
                if (data.Result != 'OK') {
                    alert(data.Message);
                    return;
                }else{
                	$("#RoleTable").jtable('reload');
                }
                
            }
		});
	}
}

function addRole(){
	//初始化dialog的值
	$("#addRoleDialog #code").val("");
	$("#addRoleDialog #name").val("");
	
	$("#addRoleDialog").dialog({
		title:'新增角色',
		width:'430',
		height:'220',
		modal:true,
		dialogClass:'jtable_customized_dialog',
		buttons:[ { text: "取消", 
					click: function() {
							$( this ).dialog( "close" ); 
						} 
					},
				  { text: "保存",
					click: function(){
						if ( $("#addRoleForm").validationEngine('validate') ) {
							 var checkCode = $("#code").val();
							 //var usern = /^[a-zA-Z0-9_]{1,}$/;
							 var usern = /^[a-zA-Z_0-9]+$/;  
							 
						     if (!checkCode.match(usern)) {
						             alert("角色代码只能由字母数字下划线组成！");
						             $("#code").focus();
						             return false;
						     }
							
							$('#addRoleForm').ajaxSubmit({
						        dataType:"json",
						        error: function(request) {
						            alert("保存失败。");
						        },
						        success: function(data) {
						        	if(data.Result != 'OK'){
						        		alert(data.Message);
						        	}else{
						        		
						        		$("#addRoleDialog").dialog('close');
						        		$("#RoleTable").jtable('load');
						        	}
						        	
						        }
							});
						}
					}
				  }]
	});
}

$(document).ready(function(){
	$("#createButton").click(function(){
		addRole();
	});
	
	$("#queryButton").click(function(){
		$("#RoleTable").jtable('load',$('#queryForm').serializeArray());
	});
	
	$("#RoleTable").jtable({
    	jqueryuiTheme:true,
        paging: true,
        sorting: true,
        defaultSorting: 'lastUpdateTime DESC',
        pageSize: 10,
        pageSizes:[10,20,30,50],
        animationsEnabled:false,
        actions: {
            listAction: function (postData, jtParams) {
                return $.Deferred(function ($dfd) {
                	
                    $.ajax({
                        url: '${pageContext.request.contextPath}/module/Role/list?page=' + jtParams.jtStartIndex + '&rows=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
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
            id: {
                key: true,
                create: false,
                edit: false,
                list: false
            },
            code: {
                title: '角色代号',
                width: '15%',
                edit: false
            },
            name:{
            	title:'角色名称',
            	width:'20%'
            },
            isBuiltIn:{
            	title:'是否为内置角色',
            	edit:false,
            	display:function(data){
            		if (data.record.isBuiltIn){
            			return "是";
            		}else{
            			return "否";
            		}
            	}
            },
            isPosition:{
            	title:'是否为岗位',
            	display:function(data){
            		if (data.record.isPosition){
            			return "是";
            		}else{
            			return "否";
            		}
            	}
            },
            editMember:{
            	width:'1%',
            	sorting:false,
            	display:function(data){
            		if (!data.record.isPosition){
                		var code = data.record.code;
                		var name = data.record.name;
                		return '<img style="cursor:pointer" onclick="editRoleMember(\''+code+'\',\''+name+'\')" src="${pageContext.request.contextPath}/static/images/person.gif"  title="编辑成员"></img>';

            		}else{
            			return "";
            		}

            	}
            },
            editOperation:{
            	width:'1%',
            	sorting:false,
            	display:function(data){
            		if (!data.record.isBuiltIn){
                 		var code = data.record.code;
                 		var name= data.record.name;
                		return '<button class="jtable-command-button jtable-edit-command-button" title="编辑" onclick="editRole(\''+code+'\',\''+name+'\')"><span>编辑</span></button>';

            		}else{
            			return "";
            		}
            	}
            },
            deleteOperation:{
            	width:'1%',
            	sorting:false,
            	display:function(data){
            		if (!data.record.isBuiltIn){
                 		var code = data.record.code;
                		var name= data.record.name;
                		return '<button class="jtable-command-button jtable-delete-command-button" title="删除" onclick="deleteRole(\''+code+'\',\''+name+'\')"><span>删除</span></button>';

            		}else{
            			return "";
            		}
               	}
            },
            lastUpdateTime: {
                title: '最后更新时间',
                list: false,
                create: false,
                edit: false
            }
        }
	});
	
	
	//load角色数据
	$("#RoleTable").jtable('load');

});


//-->
</script>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>