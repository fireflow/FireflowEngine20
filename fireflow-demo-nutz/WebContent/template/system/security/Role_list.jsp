<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","角色查询");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>

<div id="FormTitleDiv" align="center" >
	<span>角色查询</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td align="right">
			<table>
				<tr>
					<form id="queryForm" action="" method="POST">
						角色名称：<input id="name" name="name"/>
					</form>
				</tr>
			</table>
		</td>
		<td width="80" align="right">
			<button id="queryButton">查询</button>
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<div id="RoleTable">
			
			</div>
		</td>
	
	</tr>
</table>


<script type="text/javascript">
<!--
$(document).ready(function(){
	$("#queryButton").click(function(){
		$("#RoleTable").jtable('load',$('#queryForm').serializeArray());
	});
	
	$("#RoleTable").jtable({
    	jqueryuiTheme:true,
        paging: true,
        sorting: true,
        defaultSorting: 'lastUpdateTime DESC',
        pageSize: 10,
        pageSizes:[10,20,30,50],
        animationsEnabled:false,
        actions: {
            listAction: function (postData, jtParams) {
                return $.Deferred(function ($dfd) {
                	
                    $.ajax({
                        url: '${pageContext.request.contextPath}/module/Role/list?page=' + jtParams.jtStartIndex + '&rows=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
                        type: 'POST',
                        dataType: 'json',
                        data: postData,
                        success: function (data) {
                            $dfd.resolve(data);
                        },
                        error: function () {
                            $dfd.reject();
                        }
                    });
                });
            }
        },
        fields:{
            id: {
                key: true,
                create: false,
                edit: false,
                list: false
            },
            code: {
                title: '角色代号',
                width: '15%',
                edit: false
            },
            name:{
            	title:'角色名称',
            	width:'20%'
            },
            isBuiltIn:{
            	title:'是否为内置角色',
            	edit:false,
            	display:function(data){
            		if (data.record.isBuiltIn){
            			return "是";
            		}else{
            			return "否";
            		}
            	}
            },
            isPosition:{
            	title:'是否为岗位',
            	display:function(data){
            		if (data.record.isPosition){
            			return "是";
            		}else{
            			return "否";
            		}
            	}
            },

            lastUpdateTime: {
                title: '最后更新时间',
                list: false,
                create: false,
                edit: false
            }

        }
	});
	
	
	//load角色数据
	$("#RoleTable").jtable('load');

});


//-->
</script>

<jsp:include page="/template/include/_foot.jsp"></jsp:include>