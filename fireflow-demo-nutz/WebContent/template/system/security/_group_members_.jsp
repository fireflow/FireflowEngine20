<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<input type="hidden" name="roleCode" id="roleCode" value="${obj['role'].code }"/>
<input type="hidden" name="groupCode" id="groupCode" value="${obj['group'].code }"/>
<table width="100%">
<tr>
	<td colspan="3">群组：${obj['group'].name }</td>
</tr>
<tr>
	<td colspan="3">岗位：${obj['role'].name }</td>
</tr>
<tr>
	<td colspan="3"><hr/></td>
</tr>
<c:forEach var="str" items="${obj['allCheckbox']}" varStatus="status"> 
	<c:if test="${(status.index % 3)==0}"><tr></c:if>
		<td>${str }</td>
	<c:if test="${(status.index mod 3)==2 || status.last}"></tr></c:if>
</c:forEach>
</table>