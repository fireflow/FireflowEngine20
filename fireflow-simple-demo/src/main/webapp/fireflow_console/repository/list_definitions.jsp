<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.console.misc.TreeNode"%>

<%
	request.setAttribute("PAGE_TITLE", "流程库");

	TreeNode defsTree = (TreeNode) request
			.getAttribute("defsTree");
%>
<jsp:include page="/common/header.jsp"></jsp:include>

<style>
<!--
.Closed ul {
	display: none;
}

.Child img.s {
	background: none;
	cursor: default;
}

#AllProcessesTree ul {
	margin: 0 0 0 17px;
	padding: 0;
}

#AllProcessesTree li {
	list-style: none;
	padding: 0;
}

#AllProcessesTree img.s {
	width: 34px;
	height: 18px;
}

#AllProcessesTree .Opened img.s {
	background: url(${pageContext.request.contextPath }/fireflow_console/repository/resources/images/opened.gif
		) no-repeat 0 1px;
	cursor: hand;
	vertical-align: middle;
}

#AllProcessesTree .Closed img.s {
	background: url(${pageContext.request.contextPath }/fireflow_console/repository/resources/images/closed.gif
		) no-repeat 0 1px;
	cursor: hand;
	vertical-align: middle;
}


#AllProcessesTree .Child img.s {
	background: url(${pageContext.request.contextPath }/fireflow_console/repository/resources/images/child.gif
		) no-repeat 13px 2px;
	cursor: hand;
	vertical-align: middle;
}

#AllProcessesTree {
	margin: 0px;
	padding: 0px;
}
-->
</style>
<script type="text/javascript">	
	function ChangeStatus(o)
	{
		if (o.parentNode)
		{
			if (o.parentNode.className == "Opened")
			{
				o.parentNode.className = "Closed";
			}
			else
			{
				o.parentNode.className = "Opened";
			}
		}
	}	
</script>

<table align="center" width="98%" border="0">
	<tr>
		<td valign="top" style="width:330px;">
			<div id="AllProcessesTree" style="border:1px solid gray;width:330px;height:500px;">
				<%
					String imgPath = request.getContextPath()
							+ "/fireflow_console/repository/resources/images";
				%>
				<%=defsTree.toHtml(imgPath,
					request.getContextPath(), "Win_for_definition_detail")%>
			</div>
		</td>
		<td valign="top"><iframe id="Win_for_definition_detail"
				name="Win_for_definition_detail" style="width: 100%;border:1px solid gray;" height="500px" frameborder="0" >

			</iframe></td>
	</tr>
</table>

<jsp:include page="/common/footer.jsp"></jsp:include>
