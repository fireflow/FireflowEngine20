<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="ff" uri="http://www.firesoa.com/fireflow-client-widget/taglib" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div align="center" style="width:100%;height:100%">
	<br /> <span style="font-size: 16px; font-weight: bold">流程详细信息</span> <br /> <br />
	<table width="98%" border="0" >
		<tr>
			<td align="left" nowrap><span style="font-weight:bold">流程名：</span>${process_repository.displayName }</td>

			<td align="left" nowrap ><span style="font-weight:bold">流程包名：</span>${process_repository.packageId }</td>

			<td align="left" nowrap ><span style="font-weight:bold">流程Id：</span>${process_repository.processId }</td>
		</tr>

		<tr>
			<td align="left"><span style="font-weight:bold">流程版本：</span>${process_repository.version}</td>

			<td align="left" ><span style="font-weight:bold">流程发布状态：</span>${process_repository.publishState?"已发布":"未发布"}</td>

			<td align="left"><span style="font-weight:bold">流程有效期：</span>
			<fmt:formatDate value="${process_repository.validDateFrom }" type="date" pattern="yyyy-MM-dd"/>
			至<fmt:formatDate value="${process_repository.validDateTo}" type="date" pattern="yyyy-MM-dd"/></td>
		</tr>
		
		<tr>
			<td align="left" colspan="2"><span style="font-weight:bold">最后修改人：</span>${process_repository.lastEditor}</td>
			<td align="left" ><span style="font-weight:bold">最后修改时间：</span>${process_repository.lastUpdateTime}</td>
		</tr>
		<tr>
			<td align="left" colspan="3"><span style="font-weight:bold">发布说明：</span>${empty(process_repository.updateLog)?"-":process_repository.updateLog}</td>
		</tr>		
	</table>
	<ff:process-diagram id="procDiagram1" 
			processId="${process_repository.processId}" version="${process_repository.version}"
			useFireflowJQueryUI="false" 
			 width="98%" height="500" />
</div>


		