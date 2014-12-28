<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 
           uri="http://java.sun.com/jsp/jstl/core" %>	
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Fire Workflow Demo v2</title>
</head>
<body>
	<div align="center">
		<span style="font-size:16px;font-weight:bold">Fire Workflow Demo v2</span>
		<form action="${pageContext.request.contextPath }/LoginServlet?actionType=LOGIN" method="post">
			<table>
				<tr>
					<td colspan="2" style="color:red">${requestScope["ERROR_MESSAGE"] }</td>
				</tr>
				<tr>
					<td>用户名:</td>
					<td><input name="userName" value="" />
					</td>
				</tr>
				<tr>
					<td>密码:</td>
					<td><input type="password" name="password" value="" />
					</td>
				</tr>
				<tr>
					<td align="right" colspan="2"><input type="submit"
						name="_submit" value="登录" /></td>
				</tr>
			</table>
		</form>
	</div>
	<hr/>
	<div>本Demo演示了Fire workflow(v2)产品的如下特性
	<div>
		1、流程模式
		2、流程基本操作
		3、流程管理，
		4、web-designer及与表单系统接口
		5、SOA特性
		6、引擎扩展性
	</div>
	</div>
	<div>实例用户及密码</div>
</body>
</html>