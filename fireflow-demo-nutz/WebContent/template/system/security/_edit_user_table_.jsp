<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<form id="editUserForm"  class="jtable-dialog-form jtable-create-form" action="${pageContext.request.contextPath}/module/User/update" method="post">
<input type="hidden" name="id" id="recordCode" value="${obj['user'].id}" />
<table width="100%" border="0" cellspacing="0" id="editUserTable">
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">登录账号<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input name="loginName"  class="validate[required],maxSize[100],custom[email]" value="${obj['user'].loginName}" readonly/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">姓名<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input name="name"  class="validate[required],maxSize[50]" value="${obj['user'].name}" readonly/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">员工号<span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-text-input"><input name="employeeId"  class="validate[required],maxSize[50]" value="${obj['user'].employeeId}"/></td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">所属公司 <span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="orgCode" name="orgCode">
						<c:forEach var="org" items="${obj['orglist']}">
							<c:choose>
								<c:when test="${obj['user'].orgCode == org.code }">
									<option value="${org.code}" selected="selected">${org.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${org.code}">${org.name}</option>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>
			<tr>
				<td align="right" class="jtable-input-label">群组 <span style="color: red">*</span>：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="groupCode" name="groupCode" data-placeholder="请选择">
						<c:forEach var="group" items="${obj['grouplist']}">
							<c:choose>
								<c:when test="${obj['user'].groupCode == group.code }">
									<option value="${group.code}"  selected="selected">${group.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${group.code}">${group.name}</option>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>		
			<tr>
				<td align="right" class="jtable-input-label">角色：</td>
				<td class="jtable-input jtable-dropdown-input">
				<span style="color:#000000">
					<select id="roleCode" name="roleCode" multiple data-placeholder="请选择...">
						<c:forEach var="role" items="${obj['rolelist']}">
							<c:choose>
								<c:when test="${obj['userRoleMap'][role.code] == role.code}">
									<option value="${role.code}" selected="selected">${role.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${role.code}">${role.name}</option>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</select>
				</span>
				</td>
			</tr>	
			<tr>
				<td align="right" class="jtable-input-label">电话号码：</td>
				<td class="jtable-input jtable-dropdown-input">
				<input name="tel"  class="validate[maxSize[20]],custom[phone]" value="${obj['user'].tel}"/>
				</td>
				
			</tr>
	</table>
</form>
<script type="text/javascript">


    $(document).ready(function () {
    	$("#editUserForm").validationEngine("attach",{ 
    	    promptPosition:"topRight", 
    	    scroll:false 
    	});
    	$("#editUserForm #orgCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	$("#editUserForm #groupCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	$("#editUserForm #roleCode").chosen({
    		width:'200px',
			inherit_select_classes:true,
			search_contains:true
    	});
    	
    });
</script>