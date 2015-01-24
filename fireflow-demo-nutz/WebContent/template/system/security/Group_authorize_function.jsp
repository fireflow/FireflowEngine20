<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","群组维护");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>

<div id="FormTitleDiv" align="center" >
	<span>群组授权</span>
	
	<c:if test="${not empty Message}">
		<br/>
		<span class="error-message">${MESSAGE_OBJECT}</span>
		<span class="error-message">${obj['MESSAGE_OBJECT'].message }</span>
	</c:if>
	<div align="center">${obj['Message']}</div>
</div>

<table border="0">
	<tr>
		<td valign="top" width="180px">
			<br/>
			<table>
				<tr>
					<td align="right">群组名称：</td>
					<td>${obj['name']}</td>
				</tr>

				<tr>

					<td colspan="2" height="40px" align="right">
						<div>
							<form id="saveform" name="saveform" action="${pageContext.request.contextPath}/module/Group/saveAuthorize">
								<input id="funcodes" name="funcodes" type="hidden" />
								<input id="code" name="code" type="hidden" value="${obj['code']}" />
								<input id="saveFunction" name="saveFunction" type="button" value="保   存"/>
							</form>
							
						</div>					
					</td>
				</tr>
			</table>					
		</td>
		<td>
			<div align="center" id="_FUNCTION_TREE_" style="width:300px;overflow:auto">
				<ul id="treeDemo" class="ztree"></ul>
			</div>
		</td>
	</tr>

</table>


<script  type="text/javascript">
var zTreeObject = null;
 		var setting = {
			check: {
				enable: true,
				chkStyle: "checkbox"
			},
			data: {
				simpleData: {
					enable: true
				}
			}
		};

		var zNodes =[
			<c:forEach items="${obj['funlist']}" var="r">
			{ id:"${r.code}", pId:"${r.parentCode}", name:"${r.name}", checked:"${r.ischecked}" },
			</c:forEach>
		];
		
        
		var code;
		
		function setCheck() {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			py = $("#py").attr("checked")? "p":"",
			sy = $("#sy").attr("checked")? "s":"",
			pn = $("#pn").attr("checked")? "p":"",
			sn = $("#sn").attr("checked")? "s":"",
			type = { "Y":py + sy, "N":pn + sn};
			zTree.setting.check.chkboxType = { "Y" : "ps", "N" : "ps" };
			//zTree.setting.check.chkboxType = type;
			//showCode('setting.check.chkboxType = { "Y" : "' + type.Y + '", "N" : "' + type.N + '" };');
		}
		function showCode(str) {
			if (!code) code = $("#code");
			code.empty();
			code.append("<li>"+str+"</li>");
		}
		
		$(document).ready(function(){
			zTreeObject = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
			setCheck();
			$("#py").bind("change", setCheck);
			$("#sy").bind("change", setCheck);
			$("#pn").bind("change", setCheck);
			$("#sn").bind("change", setCheck);
		});
		
		 $(document).ready(function () {
		    	$('#saveFunction').click(function(){
		    		 var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
	                    nodes = zTree.getCheckedNodes(true),
	                    id = "";
		            //alert(nodes.length);
		            nodes.sort(function compare(a,b){return a.id-b.id;});
		            for (var i=0, l=nodes.length; i<l; i++) {
		                id += nodes[i].id + ",";
		            }
		            if (id.length > 0 ) id = id.substring(0, id.length-1);
		            $("#funcodes").val(id);
		    		//alert(id);
		    		$('#saveform').submit();
		    		//window.location.href="${pageContext.request.contextPath}/module/User/saveAuthorize?loginName="+${obj['loginName']}+"&funcodes=" + codes;
		    	});
		 });
		 
		 $(document).ready(function(){
			 $("#_FUNCTION_TREE_").height($("#WorkspaceDiv").height()-60);
			 zTreeObject.expandAll(true);
		 });
</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>

