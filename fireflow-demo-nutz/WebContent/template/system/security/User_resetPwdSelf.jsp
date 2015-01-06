<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","用户维护");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>
<br/>
<div id="FormTitleDiv" align="center" >
	<span>修改密码</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>
<div>
<form id="queryform" name="queryform" action="${pageContext.request.contextPath}/module/User/resetSelfPwd" method="post">
<table align="center" >
		<tr><td align="right">旧密码:</td><td><input id="oldPwd" name="oldPwd" type="password" class="validate[required,minSize[6],maxSize[50]]"/></td></tr>
		<tr><td align="right">新密码:</td><td><input id="newPwd" name="newPwd" type="password" class="validate[required,minSize[6],maxSize[50]]"/></td></tr>
		<tr><td align="right">确认密码:</td><td><input id="confirmPwd" name="confirmPwd" type="password" class="validate[required,minSize[6],maxSize[50],equals[newPwd]]"/></td></tr>
		<tr><td colspan="2" align="center"><input id="queryButton"  value="修改密码"  type="button" /></td></tr>
</table>
</form>
</div>
<script type="text/javascript">

    $(document).ready(function () {
    	jQuery("#queryform").validationEngine();
    	queryButton();
    });

    function queryButton(){
    	$('#queryButton').click(function(){
    		$('#queryform').submit();
    	});
    }
</script>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>

