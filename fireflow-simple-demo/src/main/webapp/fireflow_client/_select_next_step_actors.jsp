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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/fireflow_client/resources/css/table.css"
	type="text/css">
<link
	href="<%=request.getContextPath()%>/common/resources/jquery/css/ui-lightness/jquery-ui-1.9.2.custom.css"
	rel="stylesheet">
<script
	src="<%=request.getContextPath()%>/common/resources/jquery/js/jquery-1.8.3.js"></script>
<script
	src="<%=request.getContextPath()%>/common/resources/jquery/js/jquery-ui-1.9.2.custom.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/common/resources/jstree/jquery.jstree.js"></script>
<title>选择操作者</title>
<script  type="text/javascript">
	$(function() {
		$("#selectActorDiv").tabs({
		    activate: function( event, ui ) {
		    	//alert(ui.newPanel);
		    }
		});
		$("#actors_tree").jstree({ 
			"plugins" : [ "themes", "html_data" ,"ui"]
		})
		.bind("select_node.jstree",function (event, data){
			/*
			$.each( data, function(i, n){
				  alert( "data #" + i + " is: " + n );
				}); 
			
			$.each( data.rslt, function(i, n){
				  alert( "data.rslt #" + i + " is: " + n );
				}); 
			
			$.each( data.rslt.obj, function(i, n){
				  alert( "data.rslt.obj #" + i + " is: " + n );
				}); 
			*/
			//alert(data.rslt.obj.attr("id"));
		});
		
		
		$("#add_actor_button").click(function(){
			/*
			
			$("#actors_tree").jstree("deselect_all");
			*/
			var selectedNodes = $("#actors_tree").jstree("get_selected");
			$.each( selectedNodes, function(i, n){
				var nextActorsTable = $(".hovertable:visible");
				//增加一行
				nextActorsTable.append("<tr><td>123</td><td>张三</td><td>test</td><td></td></tr>");
			}); 
		});
	});
	
	
</script>
</head>
<body>
	<div align="center">
		<span style="font-size: 16px; font-weight: bold">指定后续环节操作者</span><br />

		<table width="95%" border="1" cellspacing="0">
			<tr>
				<td valign="top" width="25%">
					<div id="actors_tree">
						<ul>
							<li id="phtml_1"><a href="#">研发部</a>
								<ul>
									<li id="phtml_2"><a href="#">张三</a></li>
									<li id="phtml_3"><a href="#">李四</a></li>
								</ul></li>
							<li id="phtml_4"><a href="#">销售部</a></li>
						</ul>
					</div></td>
				<td>
				<button id="add_actor_button">add</button>
				</td>
				<td>
					<%
						if (nextActivities == null || nextActivities.size() == 0) {
					%> <br /> <span
					style="color: red">"<%=thisActivity.getDisplayName()%>"没有后续环节</span>
					<%
						} else {
					%>
					<div id="selectActorDiv">
						<ul>
							<%
								for (int i = 0; i < nextActivities.size(); i++) {
										Activity act = nextActivities.get(i);
							%>
							<li id="123"><a href="#tab<%=i%>"><%=act.getDisplayName()%></a>
							</li>
							<%
								}
							%>
							<li id="abc"><a href="#tabtest">test</a></li> 
						</ul>
						<%
							for (int i = 0; i < nextActivities.size(); i++) {
									Activity act = nextActivities.get(i);
						%>
						<div id="tab<%=i%>">


							<table id="actor_4_<%=act.getId()%>" class="hovertable"
								width="100%">
								<thead>
									<tr>
										<th>Id</th>
										<th>姓名</th>
										<th>所属部门</th>
										<th><button>新增</button>
										</th>
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
							<table id="actor_4_123" class="hovertable"
								width="100%">
								<thead>
									<tr>
										<th>Id</th>
										<th>姓名</th>
										<th>所属部门</th>
										<th><button>新增</button>
										</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>12312</td>
										<td>zhangsan</td>
										<td>test</td>
										<td><button>delete</button></td>
									</tr>
								</tbody>

							</table>						
						
						</div>
						<%
						}
						%>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>