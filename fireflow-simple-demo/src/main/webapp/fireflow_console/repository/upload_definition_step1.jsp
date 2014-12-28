<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.clientwidget.BrowserUtils" %>
<%
	request.setAttribute("PAGE_TITLE", "上传流程定义");
%>

<jsp:include page="/common/header.jsp"></jsp:include><br />

<script language="JavaScript">
<%if (BrowserUtils.checkBrowse(request).equals("Chrome")){ %>
	function checkFileField(){
		var field = document.getElementById("textfield");

		if(field.value==null || field.value=="" || (typeof field.value)=="undefined"){
			alert("请先选择要上传的流程定义文件。");
			return false;
		}
		return true;
	}
<%}else{ %>	
	function checkFileField(){
		var field = document.getElementById("fileField");

		if(field.value==null || field.value=="" || (typeof field.value)=="undefined"){
			alert("请先选择要上传的流程定义文件。");
			return false;
		}
		return true;
	}
<%}%>
</script>

<div align="center">
	<span style="font-size: 16px; font-weight: bold">上传流程定义到流程库（步骤1/2）</span><br />
	<br />

	<form
		action="${pageContext.request.contextPath }/servlet/UploadDefinitionsServlet?${constants.ACTION_TYPE}=SINGLE_DEF_STEP1"
		method="post" ENCTYPE="multipart/form-data"
		onsubmit="return checkFileField();">
		<table border="0">
			<tr>
				<td align="left">流程定义文件：
					<%if (BrowserUtils.checkBrowse(request).equals("Chrome")){ %>
						<input type='text' name='textfield'
							id='textfield' style="width:350px" disabled/> 
						<input type='button' 
							value='浏览...'
							onclick="document.getElementById('fileField').click();" />
	
						<input
							type="file" name="process_definition_file" class="file"
							style="display:none" id="fileField" 
							onchange="document.getElementById('textfield').value=this.value" />
					<%}else{ %>
						<input
							type="file" name="process_definition_file" class="file"
							style="width:350px;height:24px" id="fileField" />
					<%} %>
				</td>
				<td align="right"><input type="submit" value="下一步" /></td>
			</tr>

		</table>
	</form>
</div>

<jsp:include page="/common/footer.jsp"></jsp:include>