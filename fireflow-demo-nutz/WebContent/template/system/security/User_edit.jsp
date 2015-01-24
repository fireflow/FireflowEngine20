<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","用户维护");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>

<div id="FormTitleDiv" align="center" >
	<span>用户管理:创建及修改</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>

<table width="100%" cellspacing="0" cellpadding="0">
	<tbody>
		<tr>
			<td colspan="2">
				<table width="100%">
					<tr>
						<td align="right">
							<form id="queryform" name="queryform"
								action="${pageContext.request.contextPath}/module/User/gotolist"
								method="post">
								<table>
									<tr>
										<td>登录账号：<input id="loginName_for_query" name="loginName"
											type="text" />&nbsp; 名字：<input id="name_for_query" name="name"
											type="text" />
										</td>
										<td></td>
									</tr>
								</table>
							</form></td>
						<td align="right" width="160px"><input id="queryButton"
							value="用户查询" type="button" />&nbsp;
							<button id="add_user_button">创建用户</button>
						</td>
					</tr>
				</table></td>
		</tr>
		<tr>
		<td width="210px" valign="top">
			<div id="_OU_TREE_" class="ztree"
				style="border-right: 1px solid gray"></div></td>
			<td colspan="1" valign="top">
				<div id="PersonTable" style="margin-left:10px"></div></td>
		</tr>
	</tbody>
</table>
<div id="insertUserDialog" class="ui-dialog-content ui-widget-content" style="display:none;align:center">
	<form id="insertUserForm"  class="jtable-dialog-form jtable-create-form" action="${pageContext.request.contextPath}/module/User/add" method="post">
		<input type="hidden" name="recordCode" id="recordCode" value=${obj['bill_code']} />
		<input type="hidden" name="companyType" id="companyType" value="C" />
		<table width="100%" border="0" cellspacing="0">
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">登录账号<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input id="loginName" name="loginName"  class="validate[required],maxSize[100],custom[email]"/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">姓名<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input id="name" name="name"  class="validate[required],maxSize[50]"/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">员工号<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input id="employeeId" name="employeeId"  class="validate[required],maxSize[50]"/></td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">所属公司 <span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="orgCode" name="orgCode">
						<c:forEach var="org" items="${obj['orglist']}">
							<c:choose>
								<c:when test="${org.code=='OKIDEAAD'}">
									<option value="${org.code}" selected="selected">${org.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${org.code}">${org.name}</option>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">群组 <span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="groupCode" name="groupCode"  data-placeholder="请选择...">
						<c:forEach var="group" items="${obj['grouplist']}">
							<option value="${group.code}">${group.name}</option>
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>		
			<tr>
				<td align="right" class="jtable-input-label">角色 ：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="roleCode" name="roleCode" multiple data-placeholder="请选择...">
						<c:forEach var="role" items="${obj['rolelist']}">
							<option value="${role.code}">${role.name}</option>
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>	
			<tr>
				<td align="right" class="jtable-input-label">电话号码：</td>
				<td class="jtable-input jtable-dropdown-input">
				<input id="tel" name="tel"  class="validate[maxSize[20]],custom[phone]"/>
				</td>
				
			</tr>
			
		</table>
	</form>
</div>
<div id="editUserDialog" class="ui-dialog-content ui-widget-content" style="align:center;display:none">

</div>
<script type="text/javascript">
var cachedOrgOptions = null;
var cachedGroupOptions = null;
var ouZTreeObject = null;
var currentNode = null;

$(document).ready(function () {
    	
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
						$("#loginName_for_query").val(treeNode.id);
						$("#name_for_query").val(treeNode.name);
						$('#PersonTable').jtable('load',$('#queryform').serializeArray());
					}

				}
			}
		};

		


	ouZTreeObject = $.fn.zTree.init($("#_OU_TREE_"), setting);
	

	
    	// 自定义参数调用 
    	$("#insertUserForm").validationEngine("attach",{ 
    	    promptPosition:"topRight", 
    	    scroll:false 
    	});
    	$("#orgCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	$("#groupCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	$("#roleCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	
    	//
    	var url = '${pageContext.request.contextPath}/module/Group/getGroupForOrganization?orgCode=';
		oj.cmn.asyncSelect.selectTo('insertUserDialog #orgCode',url,'insertUserDialog #groupCode');
    	
    	$("#add_user_button").click(function(){
    		//首先将表单中的数据字段初始化

    		$("#insertUserDialog #loginName").val("");
    		$("#insertUserDialog #name").val("");
    		$("#insertUserDialog #employeeId").val("");
    		$("#insertUserDialog #tel").val("");

    		$("#insertUserDialog").dialog({
    			title:'新增用户',
    			width:'400',
    			height:'400',
    			modal:true,
    			dialogClass:'jtable_customized_dialog',
    			buttons:[ { text: "取消", 
    						click: function() { $( this ).dialog( "close" ); } },
    					  { text: "保存",
    						click: function(){
    							if ( $("#insertUserForm").validationEngine('validate') ) {
    								$('#insertUserForm').ajaxSubmit({
    							        dataType:"json",
    							        error: function(request) {
    							            alert("保存失败。");
    							        },
    							        success: function(data) {
    							        	if(data.Result != 'OK'){
    							        		alert(data.Message);
    							        	}else{
    							        		//重新加载发票数据
    								        	//$("#CustomerTable").jtable('load',$('#insertCustomerForm').serializeArray());
    								        	//将新加入的数据作为条件，写到Form中，做查询
    								        	$("#loginName_for_query").val($("#insertUserDialog #loginName").val());
    							        		$("#PersonTable").jtable('load',$("#queryform").serializeArray());
    								        	
    								        	
    								        	$('#insertUserDialog').dialog('close');
    								        	
    								        	//刷新组织机构树    								        	
    								        	ouZTreeObject.reAsyncChildNodes(null,'refresh',false);
    							        	}
    							        	
    							        }		
    								});
    						    }
    						    else {
    						        // The form didn't validate
    						    }

    						    return false;
    								
    							}
    							
    						}] 
    		});
    		return false;//通过return false阻止对上层表单的提交
    	});
    	
    	
        //Prepare jtable plugin
        $('#PersonTable').jtable({
        	jqueryuiTheme:true,
        	//addRecordButton:$('#add_user_button'),
            paging: true,
            sorting: true,
            defaultSorting: 'lastUpdateTime DESC',
            pageSize: 10,
            pageSizes:[10,20,30,50],
            animationsEnabled:false,
            actions: {
                //listAction: '${pageContext.request.contextPath}/User/loadlist',
                listAction: function (postData, jtParams) {
                    return $.Deferred(function ($dfd) {

                        $.ajax({
                            url: '${pageContext.request.contextPath}/module/User/list?page=' + jtParams.jtStartIndex + '&rows=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
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
                },
                deleteAction: function (postData) {
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/module/User/delete',
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
                } ,
                createAction: function (postData) {
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/module/User/add',
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
            fields: {
                id: {
                    key: true,
                    create: false,
                    edit: false,
                    list: false
                },
                loginName: {
                    title: '登录账号',
                    width: '15%',
                    edit: false
                },
                name: {
                    title: '姓名',
                    width: '10%',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" readonly id="Edit-name" name="name" style="background-color:gray"  value="' + data.record.name + '" />';
                        } else {
                            return '<input type="text" id="Edit-name" name="name" value="" />';
                        }
                    }
                },
                employeeId: {
                    title: '员工号',
                    width: '10%'
                },
                orgCode: {
                    title: '所属公司',
                    list: true,
                    width: '18%',
                    options: function () {
                        
                        if (cachedOrgOptions) { //Check for cache
                            return cachedOrgOptions;
                        }
 
                        var options = [];
 
                        $.ajax({ //Not found in cache, get from server
                            url: '${pageContext.request.contextPath}/module/User/gotoadd',
                            type: 'POST',
                            dataType: 'json',
                            async: false,
                            success: function (data) {
                                if (data.Result != 'OK') {
                                    alert(data.Message);
                                    return;
                                }
                                options = data.Options;
                            }
                        });
                         
                        return cachedOrgOptions = options; //Cache results and return options
                    }
                },
                groupCode: {
                    title: '群组',
                    dependsOn: 'orgCode',
                    options: function(data) {           
                        if (data.source == 'list') {
                        	 if (cachedGroupOptions) { //Check for cache
                                 return cachedGroupOptions;
                             }
      
                             var options = [];
      
                             $.ajax({ //Not found in cache, get from server
                                 url: '${pageContext.request.contextPath}/module/User/gotoaddGroup',
                                 type: 'POST',
                                 dataType: 'json',
                                 async: false,
                                 success: function (data) {
                                     if (data.Result != 'OK') {
                                         alert(data.Message);
                                         return;
                                     }
                                     options = data.Options;
                                 }
                             });
                              
                             return cachedGroupOptions = options; //Cache results and return options
                        }
                       
                        //This code runs when user opens edit/create form to create combobox.
                        //data.source == 'edit' || data.source == 'create'
                        return '${pageContext.request.contextPath}/module/User/gotoaddGroup?orgCode='+data.dependedValues.orgCode;
                    }
                },
                roleNames: {
                	title:'角色名称',
                	width: '15%'
                },
                tel: {
                    title: '电话号码',
                    width: '15%'
                },
                lastUpdateTime: {
                    title: '最后更新时间',
                    list: false,
                    create: false,
                    edit: false
                },
                editOpration:{
                	//title:'修改',
                    list:true,
                    create: false,
                    edit: false,
                    width: '2%',
                    sorting: false,
                	display:function(data){
                		var loginName = "'"+data.record.loginName+"'";
                		return '<button class="jtable-command-button jtable-edit-command-button" title="编辑" onclick="editUser('+loginName+')"><span>编辑</span></button>';
                	}
                }
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                data.form.find('input[name="loginName"]').addClass('validate[required,custom[email],minSize[5],maxSize[50]]');
                data.form.find('input[name="name"]').addClass('validate[required,minSize[1],maxSize[50]]');
                data.form.find('input[name="tel"]').addClass('validate[optional,custom[phone],maxSize[20]]');
                data.form.find('input[name="employeeId"]').addClass('validate[required,custom[onlyLetterNumber]');
                data.form.validationEngine({validationEventTriggers:"change blur"});
            },
            //Validate form when it is being submitted
            formSubmitting: function (event, data) {
                return data.form.validationEngine('validate');
            },
            //Dispose validation logic when form is closed
            formClosed: function (event, data) {
                data.form.validationEngine('hide');
                data.form.validationEngine('detach');
            },
			recordUpdated:function(event,data){
				ouZTreeObject.reAsyncChildNodes(currentNode,'refresh',false);
				
			},
			recordDeleted:function(event,data){
				ouZTreeObject.reAsyncChildNodes(currentNode,'refresh',false);
			}
        });

        //Load person list from server
        $('#PersonTable').jtable('load');
    });

	$('#queryButton').click(function(){
		$('#PersonTable').jtable('load',$('#queryform').serializeArray());
	});
    
    function selectChange(){
    	$('#Edit-orgCode').change(function(){
    		//alert("123");
    		var checkValue = $("#Edit-orgCode").val(); 
    		var checkText = $("#Edit-orgCode").find("option:selected").text();
    		alert(checkValue);
    		alert(checkText);
    		//$('#PersonTable').jtable('load',$('#queryform').serializeArray());
    	});
    	
    	$("select[name='orgCode']").change(function(){
    		alert("123");
    	});
    }
    function editUser(loginName){
		//先获取原User信息
		//window.location.href='${pageContext.request.contextPath}/module/User/loadEditUser?loginName=' + loginName;
		var url = '${pageContext.request.contextPath}/module/User/loadEditUser?loginName=' + loginName;
		var target = $("#editUserDialog");
        $.get(url, function(result) {
        		target.html(result);
        		
        		// 建立下拉菜单的联动
        		
            	var url = '${pageContext.request.contextPath}/module/Group/getGroupForOrganization?orgCode=';
        		oj.cmn.asyncSelect.selectTo('editUserDialog #orgCode',url,'editUserDialog #groupCode');
            	
        });
        
		//生成dialog
    	$("#editUserDialog").dialog({
    		title:'修改用户',
    		width:'400',
    		height:'400',
    		modal:true,
    		dialogClass:'jtable_customized_dialog',
    		buttons:[ { text: "取消", 
    					click: function() { $( this ).dialog( "close" ); } },
    				  { text: "保存",
    					click: function(){
    						if ( $("#editUserForm").validationEngine('validate') ) {
    							$('#editUserForm').ajaxSubmit({
    						        dataType:"json",
    						        error: function(request) {
    						            alert("保存失败。");
    						        },
    						        success: function(data) {
    						        	if(data.Result != 'OK'){
    						        		alert(data.Message);
    						        	}else{
    						        		//重新加载发票数据
    							        	//$("#CustomerTable").jtable('load',$('#insertCustomerForm').serializeArray());
    							        	$("#PersonTable").jtable('load');
    							        	
    							        	$('#editUserDialog').dialog('close');
    							        	
    							        	//刷新菜单树
    							        	ouZTreeObject.reAsyncChildNodes(null,'refresh',false);

    						        	}
    						        	
    						        }		
    							});
    					    }
    					    else {
    					        // The form didn't validate
    					    }

    					    return false;
    							
    						}
    						
    					}] 
    	});
	}
</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>

