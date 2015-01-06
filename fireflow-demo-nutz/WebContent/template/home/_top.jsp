<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>无标题文档</title>
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}

.STYLE1 {
	font-size: 12px;
	color: #FFFFFF;
}

.STYLE2 {
	font-size: 12px;
	color: #333333;
}

-->
a:link {
	font-size: 12px;
	color: #000000;
	text-decoration: none;
}

a:visited {
	font-size: 12px;
	color: #000000;
	text-decoration: none;
}

a:hover {
	font-size: 12px;
	color: #00CCFF;
	text-decoration: none;
}

.STYLE4 {
	font-size: 12px
}
</style>
</head>

<body>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td height="36"
				background="${pageContext.request.contextPath}/static/images/green/main_07.gif"><table
					width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="282" height="52"><img
							src="${pageContext.request.contextPath}/static/images/ok_idea_logo_small.png" />
						</td>
						<td><table width="100%" border="0" cellspacing="0"
								cellpadding="0">
								<tr>
									<td><span class="STYLE1"></span> <span class="STYLE4"></span>
									</td>
								</tr>
							</table></td>
						<td 
							background="${pageContext.request.contextPath}/static/images/green/main_08.gif">&nbsp;</td>
						<td width="200"
							background="${pageContext.request.contextPath}/static/images/green/main_09.gif" style="padding-right:10px">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="right" colspan="2" height="28px"><span class="STYLE2">
											当前登录用户：<shiro:principal property="name" /> </span></td>
								</tr>
								<tr>
									<td align="right"> <span class="STYLE4"><a
											href="${pageContext.request.contextPath}/module/User/logout"
											target="_top">退出系统</a> </span></td>
									<td align="right" width="60px"><span class="STYLE1"> </span> <span class="STYLE4"><a
											href="${pageContext.request.contextPath}/module/User/resetSelfPwd"
											target="mainFrame">修改密码</a> </span></td>
								</tr>

							</table></td>
					</tr>
				</table></td>
		</tr>
	</table>
</body>
</html>
