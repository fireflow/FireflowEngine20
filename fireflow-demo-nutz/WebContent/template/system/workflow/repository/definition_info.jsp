<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="ff" uri="http://www.firesoa.com/fireflow-client-widget/taglib" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=request.getAttribute("PAGE_TITLE") %></title>
<script type="text/javascript">
/*
 * 流程图点击事件回调函数，请根据业务需求补充函数逻辑
 */
function on_workflow_element_click(diagramElementId,wfElementRef,wfElementType,workflowProcessId,subProcessName){
	alert("流程图点击事件回调函数，请根据业务需求补充函数逻辑");
}
</script>

</head>
<body style="margin:0px;padding:0px;font-size:12px">
	<table width="100%" border="0" style="font-size:12px"  >
		<tr>
			<td align="right" nowrap width="80px"><span style="font-weight:bold;">流程名：</span></td><td width="200px">${obj['processDescriptor'].displayName }</td>

			<td align="right" nowrap width="100px" ><span style="font-weight:bold">流程包名：</span></td><td width="150px">${obj['processDescriptor'].packageId }</td>

			<td align="right" nowrap  width="80px"><span style="font-weight:bold">流程Id：</span></td><td>${obj['processDescriptor'].processId }</td>
		</tr>

		<tr>
			<td align="right"><span style="font-weight:bold">流程版本：</span></td><td>${obj['processDescriptor'].version}</td>

			<td align="right" ><span style="font-weight:bold">流程发布状态：</span></td><td>${obj['processDescriptor'].publishState?"已发布":"未发布"}</td>

			<td align="right"><span style="font-weight:bold">流程有效期：</span></td><td>
			<fmt:formatDate value="${obj['processDescriptor'].validDateFrom }" type="date" pattern="yyyy-MM-dd"/>
			至<fmt:formatDate value="${obj['processDescriptor'].validDateTo}" type="date" pattern="yyyy-MM-dd"/></td>
		</tr>
		
		<tr>
			<td align="right" colspan="1"><span style="font-weight:bold">最后修改人：</span></td><td>${obj['processDescriptor'].lastEditor}</td>
			<td align="right" ><span style="font-weight:bold">最后修改时间：</span></td><td colspan="3">${obj['processDescriptor'].lastUpdateTime}</td>
		</tr>
		<tr>
			<td align="right" colspan="1"><span style="font-weight:bold">发布说明：</span></td><td colspan="5">${empty(obj['processDescriptor'].updateLog)?"-":obj['processDescriptor'].updateLog}</td>
		</tr>		
	</table>
	<ff:process-diagram id="procDiagram1" 
			processId="${obj['processDescriptor'].processId}" version="${obj['processDescriptor'].version}"
			useFireflowJQueryUI="true" useFireflowJQueryTheme="start"
			 width="98%" height="500" />

</body>
</html>

		