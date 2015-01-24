<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>     
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="../../static/css/common.css" />
<title>系统登录</title>
</head>
<body>
	<br/>
	<br/>
	<div >
		<div align="center" style="font-size:18px;font-weight:bold">
			Fire Workflow Demo
		</div>
		<br/>
		<div class="login_con">
			<form action="${pageContext.request.contextPath}/module/User/login"
				method="POST">
				<table align="center">
					<tr>
						<td>登录名：</td>
						<td><input name="username" value="" style="width:150px"/>
						</td>
					</tr>
					<tr>
						<td>密码：</td>
						<td><input name="password" value="" type="password" style="width:150px"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center"><input type="submit"
							value="登录" />
						</td>
					</tr>
					<tr></tr>
				</table>
			</form>
		</div>
	</div>
	<br/>
<c:if test="${not obj['MESSAGE_OBJECT'].ok }"></c:if>
<div align="center">${obj['MESSAGE_OBJECT'].message }</div>
</body>
</html>