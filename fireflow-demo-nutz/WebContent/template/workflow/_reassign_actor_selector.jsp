<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,org.fireflow.engine.entity.runtime.ActivityInstance"%>	
<%
ActivityInstance actInst = (ActivityInstance)request.getAttribute("activityInstance");
String actId = actInst==null?"":actInst.getNodeId();
%>

<input type="hidden" name="reassign_flag"
 value="<%=request.getAttribute("reassignFlag") %>"/>
<table id="selected_actors_table_4_<%=actId%>" class="hovertable"
	width="100%" activity_id="<%=actId%>">
	<thead>
		<tr>
			<th>Id</th>
			<th>姓名</th>
			<th>所属部门</th>
			<th></th>
		</tr>
	</thead>
	<tbody>

	</tbody>

</table>