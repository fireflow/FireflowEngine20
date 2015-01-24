<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:forEach var="position" items="${obj['positionList']}"> 
	<input type="radio" name="roleCode" value="${position.roleCode }">${position.roleName }<br/>
</c:forEach>
<c:if test="${empty obj['positionList']}">
	该群组无岗位。
</c:if>