<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>
	<c:when test="${obj['role'].builtIn }">
		内置角色不可以编辑。
	</c:when>
	<c:otherwise>
		<input type="hidden" name="id" value="${obj['role'].id }"/>
	
		<table width="100%">
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">角色代码：</td>
				<td class="jtable-input jtable-text-input"><input type="text" name="code" value="${obj['role'].code }" readonly="readonly"/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">角色名称：</td>
				<td class="jtable-input jtable-text-input"><input id="name" name="name" value="${obj['role'].name }"/></td>
			</tr>
			<tr class="jtable-input-field-container">
				<td align="right" class="jtable-input-label">是否为岗位：</td>
				<td class="jtable-input jtable-text-input">
					<c:choose>
						<c:when test="${obj['role'].position }">
							<input type="radio" name="isPosition" value="true" checked="checked">是&nbsp;&nbsp;<input type="radio" name="isPosition" value="false" />否
						</c:when>
						<c:otherwise>
							<input type="radio" name="isPosition" value="true">是&nbsp;&nbsp;<input type="radio" name="isPosition" value="false" checked="checked"/>否
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>	
	</c:otherwise>
</c:choose>
