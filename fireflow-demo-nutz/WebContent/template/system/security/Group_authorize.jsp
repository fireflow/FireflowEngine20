<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn"%>

<%
request.setAttribute("thePageTitle","群组授权");
%>
<jsp:include page="/template/include/_head.jsp" />

<%
%>
<div id="FormTitleDiv" align="center">
	<span>群组授权</span>

	<c:if test="${not empty Message}">
		<br />
		<span class="error-message">${Message}</span>
	</c:if>
</div>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2">
			<table width="100%">
				<tr>
					<td align="right">
						<form id="queryForm">
							<table>
								<tr>
									<td>所属单位：<select id="orgCode" name="orgCode">
											<option value="">所有单位</option>
											<c:forEach var="org" items="${obj['orglist']}"
												varStatus="status">
												<option value="${org.code}">${org.name}</option>
											</c:forEach>

									</select></td>
									<td>群组名称：<input id="_GROUP_NAME_" name="name" /></td>
								</tr>
							</table>


						</form></td>
					<td align="right" width="60px"><input id="queryButton"
						type="button" value="查询" />
						
					</td>
				</tr>
			</table></td>

	</tr>
	<tr>
		<td width="210px" valign="top">
			<div id="_OU_TREE_" class="ztree"
				style="border-right: 1px solid gray"></div></td>

		<td valign="top">
			<div id="GroupTable" style="margin-left:10px"></div>
		</td>
	</tr>
</table>


<div id="addDialog" class="ui-dialog-content ui-widget-content"
	style="display: none; align: center">
	<form id="addForm" class="jtable-dialog-form jtable-create-form"
		action="${pageContext.request.contextPath}/module/Group/add"
		method="post">
		<input type="hidden" name="recordCode" id="recordCode"
			value=${obj['bill_code']} />
		<table width="100%" border="0" cellspacing="0">
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">群组名称<span
					style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input"><input
					name="name" class="validate[required],maxSize[50]" />
				</td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">组织名称 <span
					style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input"><span
					style="color: #000000"> <select id="orgCode1" name="orgCode">
							<c:forEach var="org" items="${obj['orglist']}" varStatus="status">
								<option value="${org.code}">${org.name}</option>
							</c:forEach>

					</select> </span></td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">父群组 <span
					style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input"><span
					style="color: #000000"> <select id="parentCode"
						name="parentCode">
							<option value="0">无</option>
							<c:forEach var="group" items="${obj['groupList']}"
								varStatus="status">
								<option value="${group.code}">${group.name}</option>
							</c:forEach>
					</select> </span></td>
			</tr>

		</table>
	</form>
</div>
<script type="text/javascript">
var ouZTreeObject = null;
	$(document)
			.ready(
					function() {

						var setting = {
								async : {
									enable : true,
									url : "${pageContext.request.contextPath}/module/Group/loadChildrenAsZTreeNodes",
									autoParam : [ "id", "level","nodeType","parentId" ]
								},
								callback: {
									onClick: function(event, treeId, treeNode) {
										if (treeNode.nodeType=='O' ){
											//首先展开树
											ouZTreeObject.expandNode(treeNode,true,false,false,true);
											
											//更新查询条件
											$("#orgCode").val(treeNode.id);
											$("#_GROUP_NAME_").val("");
											
											//刷新组织机构列表
											$('#GroupTable').jtable('load',
													$('#queryForm').serializeArray());
										}
										else if (treeNode.nodeType=='G'){
											//首先展开树
											ouZTreeObject.expandNode(treeNode,true,false,false,true);
											//查询单条群组记录
											//更新查询条件
											$("#orgCode").val("");
											$("#_GROUP_NAME_").val(treeNode.name);
											
											//刷新组织机构列表
											$('#GroupTable').jtable('load',
													$('#queryForm').serializeArray());
										}
										else if (treeNode.nodeType=="R"){
											//首先展开树
											ouZTreeObject.expandNode(treeNode,true,false,false,true);
										}

									}
								}
							};

							


						ouZTreeObject = $.fn.zTree.init($("#_OU_TREE_"), setting);
						
						
						//下拉列表控件注册
						/*  公司只有很少数据，不需要chosen控件
						$("#orgCode").chosen({
							width : '240px',
							inherit_select_classes : true,
							search_contains : true
						});
						*/
						$("#orgCode1").chosen({
							width : '200px',
							inherit_select_classes : true,
							search_contains : true
						});
						$("#parentCode").chosen({
							width : '200px',
							inherit_select_classes : true,
							search_contains : true
						});

						//给查询按钮绑定事件

						$('#queryButton').click(
								function() {
									$('#GroupTable').jtable('load',
											$('#queryForm').serializeArray());
								});

						//Prepare jtable plugin
						$('#GroupTable')
								.jtable(
										{
											jqueryuiTheme : true,
											//addRecordButton:$('#add_group_button'),
											paging : true,
											sorting : true,
											defaultSorting : 'name ASC',
											animationsEnabled:false,
											pageSize : 10,
											pageSizes : [ 10, 20, 30, 50 ],
											actions : {
												//listAction: '${pageContext.request.contextPath}/User/loadlist',
												listAction : function(postData,
														jtParams) {
													console
															.log("Loading from custom function...");
													return $
															.Deferred(function(
																	$dfd) {
																$
																		.ajax({
																			url : '${pageContext.request.contextPath}/module/Group/list?page='
																					+ jtParams.jtStartIndex
																					+ '&rows='
																					+ jtParams.jtPageSize
																					+ '&jtSorting='
																					+ jtParams.jtSorting,
																			type : 'POST',
																			dataType : 'json',
																			data : postData,
																			success : function(
																					data) {
																				$dfd
																						.resolve(data);
																			},
																			error : function() {
																				$dfd
																						.reject();
																			}
																		});
															});
												}
												//deleteAction: '${pageContext.request.contextPath}/module/Group/delete',
												//updateAction : '${pageContext.request.contextPath}/module/Group/update',
												//createAction : '${pageContext.request.contextPath}/module/Group/add'
											},
											fields : {
												id : {
													key : true,
													create : false,
													edit : false,
													list : false
												},

												code : {
													title : '群组编码',
													input : function(data) {
														if (data.record) {
															return '<input type="text" readonly id="Edit-code" name="code"  value="' + data.record.code + '" />';
														} else {
															return '<input type="text" id="Edit-code" name="code" value="enter your name here" />';
														}
													},
													create : false
												},
												name : {
													title : '群组名称',
													inputClass : 'validate[required,maxSize[100]]'
												},
												orgCode : {
													title : '组织名称',
													//type: 'password',
													options : '${pageContext.request.contextPath}/module/Group/loadOrg'
												},
												parentCode : {
													title : '父群组',
													inputClass : 'validate[required]',
													dependsOn : 'orgCode',
													options : function(data) {
														if (data.source == 'edit') {
															//Return url all options for optimization. 
															return '${pageContext.request.contextPath}/module/Group/loadParentCode?code='
																	+ data.record.code
																	+ '&orgCode='
																	+ data.dependedValues.orgCode;
														}
														if (data.source == 'list') {
															//Return url of all cities for optimization.
															return '${pageContext.request.contextPath}/module/Group/loadParentCode?orgCode=0';
														}
														//This code runs when user opens edit/create form to create combobox.
														//data.source == 'edit' || data.source == 'create'
														return '${pageContext.request.contextPath}/module/Group/loadParentCode?orgCode='
																+ data.dependedValues.orgCode
													}
												},
								                groupAuthorize:{
								                	title:'群组授权',
								                    list:true,
								                    create: false,
								                    edit: false,
								                    sorting: false,
								                	display:function(data){
								                		//alert(data.record.loginName);
								                		//alert(data.record.id);
								                		return '<a href="${pageContext.request.contextPath}/module/Group/gotoAuthorizeFunction?code='+data.record.code+'">群组授权</a>';
								                	}
								                } ,												
												orgName : {
													type : 'hidden'
												}
											}
										});

						//Load person list from server
						$('#GroupTable').jtable('load');//

						//新增按钮单击事件
						$("#add_group_button")
								.click(
										function() {
											//打开发票明细录入对话框
											$("#addDialog")
													.dialog(
															{
																title : '新增条目',
																width : '350',
																height : '250',
																modal : true,
																dialogClass : 'jtable_customized_dialog',
																buttons : [
																		{
																			text : "取消",
																			click : function() {
																				$(
																						this)
																						.dialog(
																								"close");
																			}
																		},
																		{
																			text : "保存",
																			click : function() {

																				if ($(
																						"#addForm")
																						.validationEngine(
																								'validate'))

																					$(
																							'#addForm')
																							.ajaxSubmit(
																									{
																										dataType : "json",
																										error : function(
																												request) {
																											alert("保存失败。");
																										},
																										success : function(
																												data) {
																											if (data.Result != 'OK') {
																												alert(data.Message);
																											} else {
																												//重新加载发票数据
																												$(
																														"#GroupTable")
																														.jtable(
																																'load');

																												$(
																														'#addDialog')
																														.dialog(
																																'close');
																												//更新parentCode下拉列表
																												window.location.href = '${pageContext.request.contextPath}/module/Group/Groupedit';
																											}

																										}
																									});

																			}

																		} ]
															});
											return false;//通过return false阻止对上层表单的提交
										});
					});
</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>

