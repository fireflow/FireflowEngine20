<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
	request.setAttribute("PAGE_TITLE", "上传流程定义");
    request.setAttribute("now",new java.util.Date());
%>

<jsp:include page="/common/header.jsp"></jsp:include>

<script type="text/javascript">
<!--
	$(function() {
		$("#validDateFrom")
				.datepicker(
						{
							showOn : "button",
							buttonImage : "${pageContext.request.contextPath }/fireflow_console/repository/resources/images/calendar.gif",
							buttonImageOnly : true,
							dateFormat : "yy-mm-dd",
							defaultDate: new Date()
						});

		$("#validDateTo")
				.datepicker(
						{
							showOn : "button",
							buttonImage : "${pageContext.request.contextPath }/fireflow_console/repository/resources/images/calendar.gif",
							buttonImageOnly : true,
							dateFormat : "yy-mm-dd",
							defaultDate: +3650
						});
	});
//-->
</script>

<br />
<div align="center">
	<span style="font-size: 16px; font-weight: bold">上传流程定义到流程库</span><br />
	<br />
	<form
		action="${pageContext.request.contextPath }/servlet/UploadDefinitionsServlet?${constants.ACTION_TYPE}=SINGLE_DEF_STEP2"
		method="post" >
		<table border="0">
			<tr>
				<td align="left" style="font-weight:bold">流程名称：</td>
				<td align="left">${PROCESS_DEFINITION.displayName }</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">流程包：</td>
				<td align="left">${PROCESS_DEFINITION.packageId }</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">流程Id：</td>
				<td align="left">${PROCESS_DEFINITION.id }</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">发布状态：</td>
				<td align="left"><select name="publishState">
						<option value="true" selected>已发布状态</option>
						<option value="false">未发布状态</option>
				</select>
				</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">生效日期起：</td>
				<td align="left"><input id="validDateFrom" name="validDateFrom" value="<fmt:formatDate value='${now }' type='date' pattern='yyyy-MM-dd'/>"/> (格式
					yyyy-MM-dd)</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">生效日期止：</td>
				<td align="left"><input id="validDateTo" name="validDateTo"  /> (格式
					yyyy-MM-dd)</td>
			</tr>
			<tr>
				<td align="left" style="font-weight:bold">发布说明：</td>
				<td align="left"><textarea name="updateLog" cols="60" rows="5"></textarea>
			</tr>

			<tr>
				<td align="left" style="font-weight:bold">版本号：</td>
				<td align="left"><select name="version">
						<option value="0">自动产生新的版本号</option>
						<c:forEach var="processDescriptor" items="${EXISTING_PROCESS_LIST }">
						<option value="${processDescriptor.version}" title="${ processDescriptor.updateLog}">覆盖已存在的版本${processDescriptor.version }</option>
						</c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td align="left"><input type="submit" value="提交" />
				</td>
			</tr>
		</table>
	</form>
</div>

<jsp:include page="/common/footer.jsp"></jsp:include>
