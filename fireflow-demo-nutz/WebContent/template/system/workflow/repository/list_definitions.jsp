<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
request.setAttribute("thePageTitle","流程列表");
%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>
<script type="text/javascript">
<!--
var processZTreeObject = null;
$(function(){
	var setting = {
	};
	var zNodes =[${obj['rootNodeAsJson']}
	                 ];
	processZTreeObject = $.fn.zTree.init($("#_PROCESS_TREE_"), setting,zNodes);
	
	var wd_h = $(window).height();
	var wd_w = $(window).width();
	var workspaceHeight = wd_h-23-52-10;//因为有滚动条，所以多减去10px,

	$("#_PROCESS_TREE_WRAPPER_").height(workspaceHeight-40);
	$("#Win_for_definition_detail").height(workspaceHeight-40);
});
//-->
</script>
<div id="FormTitleDiv" align="center">
	<span>流程定义列表</span>

	<c:if test="${not empty Message}">
		<br />
		<span class="error-message">${Message}</span>
	</c:if>
</div>

<table align="center" width="100%" border="0" style="margin:0px">
	<tr>
		<td align="left" valign="top" width="205px" style="border-right: 1px solid gray;">
			<div id="_PROCESS_TREE_WRAPPER_" style="width:200px;overflow:auto;">
				<div id="_PROCESS_TREE_" class="ztree" >
				</div>
			</div>
		</td>
		<td valign="top" style="padding-left:3px;"><iframe id="Win_for_definition_detail"
				name="Win_for_definition_detail" style="width: 100%;border:0px solid gray;"  frameborder="0" >

			</iframe></td>
	</tr>
</table>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>

