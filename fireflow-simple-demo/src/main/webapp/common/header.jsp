<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="constants" class="org.fireflow.web_client.util.Constants" scope="request"></jsp:useBean>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=request.getAttribute("PAGE_TITLE") %></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/css/redmond/jquery-ui-1.10.3.custom.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/css/jquery.ui.menubar.css">

<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/resources/jquery/jquery-1.10.2.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/resources/jquery-ui-1.10.3.custom/js/jquery.ui.menubar.js"></script>
<script language="JavaScript">
	function createProcessInstance(processId, processName, firstActivity) {
		var answer = confirm("确实要启动流程：" + processName + "?");
		if (answer) {
			window.location = "${pageContext.request.contextPath}/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.CREATE_PROCESS_INSTANCE}&${constants.PROCESS_ID}="
					+ processId
					+ "&${constants.FIRST_ACTIVITY_ID}="
					+ firstActivity;
		}
	}
	
	
	$(function() {

		$("#main-menu").menubar();
	});
</script>

</head>
<body style="margin: 0px; font-size: 14px;">
	<div style="width: 100%; height: 50px; background-color: #70a8d2">
		<table width="100%" height="100%" border="0">
			<tr>
				<td align="left" style="font-size: 24px; font-weight: bold;" rowspan="2" >Fire
					Workflow v2 演示系统</td>
				<td align="right" >当前用户：${sessionScope["CURRENT_USER_SESSION_KEY"].name
					}
				</td>
			</tr>
			<tr>
				<td align="right" ><a style="color: white"
					href="${pageContext.request.contextPath }/LoginServlet?action=LOGOUT">退出系统</a>
				</td>
			</tr>
		</table>
	</div>
	<div style="width: 100%">
		<ul id="main-menu" class="menubar">
			<li><a href="#">流程中心</a>
				<ul>
					<li><a href="#">我可启动的流程</a>
						<ul>
							<li><a
								href="JavaScript:createProcessInstance('Leave_approval_process','出差申请','Leave_approval_process.main.Apply')">出差申请</a>
							</li>
							<li><a href="#">出差申请流程</a>
							</li>
							<li><a href="#">差旅费报销流程</a>
							</li>
							<li><a href="#">销售费用报销流程</a>
							</li>
						</ul></li>
					<li><a
						href="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.LIST_TODO_WORKITEMS}">我的待办事项</a>
					</li>
					<li><a
						href="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.LIST_HAVEDONE_WORKITEMS}">我的已办事项</a>
					</li>
					<li><a
						href="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.LIST_MY_ACTIVE_PROCESS_INSTANCE}">我发起的在办业务</a>
					</li>
					<li><a
						href="${pageContext.request.contextPath }/servlet/WorkflowOperationServlet?${constants.ACTION_TYPE}=${constants.LIST_READONLY_WORKITEMS}">我的工作流待阅</a>
					</li>
				</ul></li>
			<li><a href="#">FireFlow管理控制台</a>
				<ul>
					<li><a
						href="<%=request.getContextPath() %>/fireflow_console/repository/upload_definition_step1.jsp">上传流程定义到流程库</a>
					</li>
					<li><a
						href="<%=request.getContextPath() %>/servlet/ListDefinitionsServlet">打开流程库</a>
					</li>
					<li><a
						href="<%=request.getContextPath() %>/servlet/ListDefinitionsServlet">流程实例管理</a>
					</li>
					<li><a
						href="<%=request.getContextPath() %>/servlet/ListDefinitionsServlet">工作项管理</a>
					</li>					
					<li><a
						href="<%=request.getContextPath()%>/servlet/FireflowConfigServlet?actionType=LIST_ALL_CONFIGS">FireWorkflow系统设置</a>
					</li>
					
				</ul></li>
			<li><a href="#">流程模式及API示例</a>
				<ul>
					<li><a href="#">Word Wrap</a>
					</li>
					<li><a href="#">Font...</a>
					</li>
				</ul></li>
		</ul>
	</div>