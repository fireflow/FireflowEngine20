<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,org.fireflow.pdl.fpdl.process.Activity"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	List<Activity> nextActivities = (List<Activity>) request
			.getAttribute("nextActivities");
	Activity thisActivity = (Activity) request
			.getAttribute("thisActivity");
%>

<%
	if (nextActivities == null || nextActivities.size() == 0) {
%>
<br />
<span style="color: red">"<%=thisActivity.getDisplayName()%>"没有后续环节</span>
<%
	} else {
%>
<div id="selectActorDiv">
	<ul>
		<%
			for (int i = 0; i < nextActivities.size(); i++) {
					Activity act = nextActivities.get(i);
		%>
		<li id="123"><a href="#tab<%=i%>"><%=act.getDisplayName()%></a></li>
		<%
			}
		%>
		<li id="abc"><a href="#tabtest">test</a>
		</li>
	</ul>
	<%
		for (int i = 0; i < nextActivities.size(); i++) {
				Activity act = nextActivities.get(i);
	%>
	<div id="tab<%=i%>">


		<table id="selected_actors_table_4_<%=act.getId() %>" class="hovertable" width="100%" activity_id="<%=act.getId()%>">
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

	</div>
	<%
		}
	%>
	<div id="tabtest">
		<table id="selected_actors_table_4_test123" class="hovertable" width="100%" activity_id="test123">
			<thead>
				<tr>
					<th>Id</th>
					<th>姓名</th>
					<th>所属部门</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>12312</td>
					<td>zhangsan</td>
					<td>test</td>
					<td><button>delete</button>
					</td>
				</tr>
			</tbody>

		</table>

	</div>
	<%
		}
	%>
</div>
