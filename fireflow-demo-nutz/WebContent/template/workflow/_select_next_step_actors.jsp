<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,org.fireflow.pdl.fpdl.process.Activity"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>选择操作者</title>
<script type="text/javascript">
	var ouZTreeObject = null;
	$(function() {
		if (ouZTreeObject != null) {
			ouZTreeObject.destroy();
		}

		$("#selectActorDiv").tabs({
			activate : function(event, ui) {
				//alert(ui.newPanel);
			}
		});

		var setting = {
			async : {
				enable : true,
				url : "${pageContext.request.contextPath}/module/Group/loadChildrenAsZTreeNodes",
				autoParam : [ "id", "level","nodeType" ,"parentId"]
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					if (treeNode.nodeType=='O' ){
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
						
					}
					else if (treeNode.nodeType=='G'){
						
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
						
					}
					else if (treeNode.nodeType=="R"){
						//首先展开树
						ouZTreeObject.expandNode(treeNode,true,false,false,true);
					}
				}
			},
			view : {
				fontCss : getFont
			}
		};

		function getFont(treeId, node) {
			return {
				color : 'white'
			};
		}
		


		ouZTreeObject = $.fn.zTree.init($("#actors_tree"), setting);
		
		$("#add_actor_button").click(function(){
			/*
			
			$("#actors_tree").jstree("deselect_all");
			*/
			var selectedNodes = ouZTreeObject.getSelectedNodes();
			$.each( selectedNodes, function(i, n){
				if (!n.isParent){
					var nextActorsTable = $(".nextActorsTable:visible");
					var actId = nextActorsTable.attr("id");
					//增加一行
					nextActorsTable.append("<tr id=\""+n.id+"\"><td align=\"center\"><input name=\""+actId+"\" value=\""+n.id+"\" type='hidden'>"+n.id+"</td><td align=\"center\">"+n.name+"</td><td align=\"center\"><a href=\"JavaScript:workflowClient.deleteCandidateActor('"+n.id+"')\">删除</a></td></tr>");
				}

			}); 
		});
		
		$("#SUBMIT_COMPLETE_WORKITEM_FORM_WITH_NEXT_ACTORS_").click(function(){
			$("#_COMPLETE_WORKITEM_FORM_WITH_NEXT_ACTORS_").submit();
		});

	});
</script>
</head>
<body>
	<div align="center">

		<table width="100%" border="0" cellspacing="0">
			<tr>
				<td valign="top" width="25%">
					<div id="actors_tree" class="ztree"></div>
				</td>
				<td>
					<button id="add_actor_button">>></button>
				</td>
				<td valign="top">
					<form id="_COMPLETE_WORKITEM_FORM_WITH_NEXT_ACTORS_" method="post"
						action="${pageContext.request.contextPath}/module/workflow/WorkflowModule/completeWorkItemWithNextActors">
						<input type="hidden" name="workItemId"
							value="${obj['workItemId'] }" />
						<c:if test="${ empty obj['nextActivities']}">
							<br />
							<span style="color: red">"${obj['thisActivity'].displayName }"没有后续环节</span>
						</c:if>

						<c:if test="${not empty obj['nextActivities']}">

							<div id="selectActorDiv">
								<ul>
									<c:forEach items="${obj['nextActivities'] }" var="act">
										<li id="li_${act.name }"><a href="#tab_${act.name }">${act.displayName}</a>
										</li>
									</c:forEach>
								</ul>
								<c:forEach items="${obj['nextActivities'] }" var="act">
									<input type="hidden" name="activityId" value="${act.id }" />
									<div id="tab_${act.name }">


										<table id="${act.id }" width="100%" class="nextActorsTable">
											<thead>
												<tr>
													<th align="center">Id</th>
													<th align="center">姓名</th>
													<th align="center">操作</th>
												</tr>
											</thead>
											<tbody>

											</tbody>

										</table>

									</div>
								</c:forEach>
							</div>

						</c:if>

					</form>
				</td>
			</tr>
		</table>
		<div align="center">
			<button id="SUBMIT_COMPLETE_WORKITEM_FORM_WITH_NEXT_ACTORS_">提交</button>
		</div>
	</div>
</body>
</html>