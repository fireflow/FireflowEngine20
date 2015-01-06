<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.demo.misc.Message" %> 
<%@ page import="org.fireflow.demo.MainModule" %> 
  
<jsp:include page="/template/include/_head.jsp"/>
  
<%
	Throwable t = (Throwable)request.getAttribute("obj");
	Message m = Message.fromThrowable(t);
	request.setAttribute(MainModule.MESSAGE_OBJECT, m);
%>    

    
<table width="100%"   class="formTable">
	<tr width="5%">
		<td align="right">错误信息：</td>
		<td>
			<span class="error-message">${MESSAGE_OBJECT.message}</span>
		</td>
	</tr>
	<tr width="95%">
		<td align="right">堆栈信息：</td>
		<td><button id="openStackDialogButton">打开查看</button>  </td>
	</tr>
</table>

<div id="stackDialog" style="display:none">
	<table width="100%"   class="formTable">
		<tr>
			<td align="center">详细堆栈信息：</td>
		</tr>
		<tr>
			<td>
				<textarea rows="25" cols="120">
					${MESSAGE_OBJECT.stack }
				</textarea>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">


$(document).ready(function() {
	$("#openStackDialogButton").click(function(){
		$("#stackDialog").dialog({
			title:'详细堆栈信息',
			width:'800',
			height:'600',
			modal:true,
			dialogClass:'jtable_customized_dialog',
			buttons:[ { text: "关闭", 
						click: function() { $( this ).dialog( "close" ); } 
			        }]
		});
		return false;
	});
	
	
});

</script>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>