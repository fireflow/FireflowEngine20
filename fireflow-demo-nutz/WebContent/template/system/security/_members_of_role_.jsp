<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<input type="hidden" name="roleCode" value="${obj['roleCode'] }"/>

<c:forEach var="ur" items="${obj['userRoleList'] }">
	<div style="color:black">
		<img id="userCodeCheckbox" src="${pageContext.request.contextPath}/static/images/delete.png" onclick="deleteMemberOfRole(this)" style="cursor:pointer"/>
		<input type="hidden" class="userCodeOfRole" name="userCode" value="${ur.userCode }"/>
		${ur.userName }
	</div>
</c:forEach>
