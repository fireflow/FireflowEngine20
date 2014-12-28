<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*,org.fireflow.pdl.fpdl.process.Activity,org.fireflow.web.util.Constants"%>    
<%
List<Activity> allActivities = (List<Activity>)request.getAttribute("allActivities");
Activity thisActivity = (Activity)request.getAttribute("thisActivity");
%>
<script type="text/javascript">
<!--
function updateActivityId(obj){
	var table = document.getElementById("selected_actors_table");
	
	table.setAttribute("activity_id", obj.value);
}
//-->
</script>
<div>
选择下一个环节:
<select name="<%=Constants.TARGET_ACTIVITY_ID%>" onchange="updateActivityId(this)">
<%for (Activity act:allActivities){
%>
<option value="<%=act.getId()%>" <%if (act.getId().equals(thisActivity.getId())){ %>selected<%} %>><%=(act.getDisplayName()+"--"+act.getParent().getDisplayName()) %></option>

<%
}
%>
</select>
</div>
<table id="selected_actors_table" class="hovertable"
	width="100%" activity_id="<%=thisActivity.getId()%>">
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