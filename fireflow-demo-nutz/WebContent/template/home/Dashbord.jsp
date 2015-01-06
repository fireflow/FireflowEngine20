<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %>  

<%
request.setAttribute("thePageTitle","待办列表");
%>
<jsp:include page="/template/include/_head.jsp"/>
<%
%>


该页面是/template/home/Dashbord.jsp<br/>

测试权限<br/>

<shiro:hasRole name="admin">  
    用户[<shiro:principal/>]拥有权限编号为admin的功能权限<br/>  
</shiro:hasRole>   

<shiro:hasPermission name="123">  
    用户[<shiro:principal/>]拥有权限编号为123的功能权限<br/>  
</shiro:hasPermission>   

<br/>
<shiro:hasPermission name="010301">  
    用户[<shiro:principal/>]拥有权限编号为456的功能权限<br/>  
</shiro:hasPermission>   

<jsp:include page="/template/include/_foot.jsp"></jsp:include>