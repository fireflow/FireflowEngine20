<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="org.fireflow.engine.entity.config.FireflowConfig"%>
<%
	request.setAttribute("PAGE_TITLE", "Fireflow 设置");
%>
<%
	List<FireflowConfig> all_categories = (List<FireflowConfig>) request
			.getAttribute("ALL_CATEGORIES");
	FireflowConfig currentCategory = (FireflowConfig)request.getAttribute("currentCategory");
    
    List<FireflowConfig> configsOfCurrentCategory = (List<FireflowConfig>)request.getAttribute("configsOfCurrentCategory");
%>
<jsp:include page="/common/header.jsp"></jsp:include>
<style type="text/css">
table.hovertable {
    font-family: verdana,arial,sans-serif;
    font-size:11px;
    color:#333333;
    border-width: 1px;
    border-color: #999999;
    border-collapse: collapse;
}
table.hovertable th {
    background-color:#c3dde0;
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #a9c6c9;
}
table.hovertable tr {
    background-color:#d4e3e5;
}
table.hovertable td {
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #a9c6c9;
}
</style>
<br />
<div align="center">
	<span style="font-size: 16px;font-weight:bold">Fire workflow 设置</span>
</div>
<br/>
<table align="center" width="960">
	<tr>
		<td width="300" valign="top">
			<fieldset>
				<legend>参数类别</legend>
				<table id="categoryList" width="100%" class="hovertable">
					<tr>
						<th>类别Id</th>
						<th>类别名称</th>
					</tr>
					<%
						if (all_categories != null) {	
							for (FireflowConfig category : all_categories) {
					%>
						<% if (currentCategory!=null && currentCategory.getConfigId().equals(category.getConfigId())){ %>
						<tr style="background:#dcddc0" onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#dcddc0';">
							<td><%=category.getConfigId()%></td>
							<td><a href="<%=request.getContextPath()%>/FireflowConfigServlet?actionType=LOAD_CONFIGS_OF_CATEGORY&currentCategoryId=<%=category.getConfigId()%>">
							<%=category.getConfigName()%></a></td>
						</tr>
						<%}else{ %>
						<tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
							<td><%=category.getConfigId()%></td>
							<td><a href="<%=request.getContextPath()%>/FireflowConfigServlet?actionType=LOAD_CONFIGS_OF_CATEGORY&currentCategoryId=<%=category.getConfigId()%>">
							<%=category.getConfigName()%></a></td>
						</tr>						
						<%} %>
					<%
						}
						}
					%>
				</table>
			</fieldset></td>
		<td valign="top">
			<fieldset>
				<legend>[<%=currentCategory==null?"":currentCategory.getConfigName() %>]参数条目</legend>
				<table id="categoryList" width="100%" class="hovertable">
					<tr>
						<th>参数Id</th>
						<th>参数名称</th>
						<th>参数值</th>
						<th>父Id</th>
						<th>描述信息</th>
						<th><a href="<%=request.getContextPath()%>/FireflowConfigServlet?actionType=ADD_CONFIGS&categoryId="<%=currentCategory==null?"":currentCategory.getConfigId() %>>新增</a></th>
					</tr>
					<%if (configsOfCurrentCategory!=null){
						for (FireflowConfig fconfig : configsOfCurrentCategory){
						%>
						<tr onmouseover="this.style.backgroundColor='#ffff66';" onmouseout="this.style.backgroundColor='#d4e3e5';">
							<td><%=fconfig.getConfigId() %></td>
							<td><%=fconfig.getConfigName() %></td>
							<td><%=fconfig.getConfigValue() %></td>
							<td><%=fconfig.getParentConfigId() %></td>
							<td><%=fconfig.getDescription()%></td>
							<td><a href="<%=request.getContextPath()%>/FireflowConfigServlet?actionType=DELETE_CONFIGS&configId="<%=fconfig.getConfigId() %>>删除</a></td>
						</tr>
					<% } 
					}
					%>
				</table>
			</fieldset>
		</td>
	</tr>
</table>


<jsp:include page="/common/footer.jsp"></jsp:include>
