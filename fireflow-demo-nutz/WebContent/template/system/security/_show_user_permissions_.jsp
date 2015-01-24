<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div>用户姓名：${obj['user'].name }</div>

<table width="100%" border="1" style="border-collapse: collapse">
	<thead>
		<tr>
			<th>权限代码</th>
			<th>权限名</th>
			<th>授权来源</th>
		</tr>
	</thead>
	<tbody>

<c:forEach var="item" items="${obj['mergedPermissions'] }">

	<tr>
		<td>${item.key.code }</td>
		<td>${item.key.name }</td>
		<td>${item.value }</td>
	</tr>
</c:forEach>
	</tbody>
</table>