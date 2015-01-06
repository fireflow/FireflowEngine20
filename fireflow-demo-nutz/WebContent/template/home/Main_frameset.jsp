<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>奥奇智慧业务管理平台</title>
</head>

<frameset rows="52,*" frameborder="no" border="0" framespacing="0">
  <frame src="${pageContext.request.contextPath}/template/home/_top.jsp" name="topFrame" scrolling="no" noresize="noresize" id="topFrame" />
  <frame src="${pageContext.request.contextPath}/template/workflow/TodoList.jsp" name="mainFrame" id="mainFrame" />

</frameset>
<noframes><body>
</body>
</noframes></html>
