<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %> 
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%@taglib uri="/WEB-INF/shiro.tld" prefix="shiro" %> 

<%
request.setAttribute("thePageTitle","用户维护");
%>
<jsp:include page="/template/include/_head.jsp"/>

<%
%>
<div id="FormTitleDiv" align="center" >
	<span>用户管理:重置密码</span>
	
	<c:if test="${not empty obj['Message']}">
		<br/>
		<span class="error-message">${obj['Message']}</span>
	</c:if>
</div>


<table width="100%" cellspacing="0" cellpadding="0">
	<tbody>
		<tr>
			<td colspan="2">
				<table width="100%">
					<tr>
						<td align="right">
							<form id="queryform" name="queryform"
								action="${pageContext.request.contextPath}/module/User/gotolist"
								method="post">
								<table>
									<tr>
										<td>登录账号：<input id="loginName_for_query" name="loginName"
											type="text" />&nbsp; 名字：<input id="name_for_query" name="name"
											type="text" />
										</td>
										<td></td>
									</tr>
								</table>
							</form></td>
						<td align="right" width="80px"><input id="queryButton"
							value="用户查询" type="button" />
						</td>
					</tr>
				</table></td>
		</tr>
		<tr>
		<td width="210px" valign="top">
			<div id="_OU_TREE_" class="ztree"
				style="border-right: 1px solid gray"></div></td>
			<td colspan="1" valign="top">
				<div id="PersonTable" style="margin-left:10px"></div></td>
		</tr>
	</tbody>
</table>

<form id="_RESETPASSWORD_FORM_" method="POST" action="${pageContext.request.contextPath}/module/User/passReset?resetup=1">
	<input id="_LOGIN_NAME_" type="hidden" name="loginName" />
</form>

<script type="text/javascript">
var ouZTreeObject = null;
var currentNode = null;

    $(document).ready(function () {
    	var setting = {
    			async : {
    				enable : true,
    				url : "${pageContext.request.contextPath}/module/Group/loadChildrenAsZTreeNodes",
    				autoParam : [ "id", "level","nodeType" ,"parentId"]
    			},
    			callback: {
    				onClick: function(event, treeId, treeNode) {
    					if (treeNode.nodeType=='O' ){
    						currentNode = treeNode;
    						//首先展开树
    						ouZTreeObject.expandNode(treeNode,true,false,false,true);
    						

    					}
    					else if (treeNode.nodeType=='G'){
    						currentNode = treeNode;
    						//首先展开树
    						ouZTreeObject.expandNode(treeNode,true,false,false,true);
    					}
    					else if (treeNode.nodeType=='R'){
    						//首先展开树
    						ouZTreeObject.expandNode(treeNode,true,false,false,true);
    					}else if (treeNode.nodeType=='U'){
    						$("#loginName_for_query").val(treeNode.id);
    						$("#name_for_query").val(treeNode.name);
    						$('#PersonTable').jtable('load',$('#queryform').serializeArray());
    					}

    				}
    			}
    		};

    		


    	ouZTreeObject = $.fn.zTree.init($("#_OU_TREE_"), setting);
    	
    	
    	$('#queryButton').click(function(){
    		$('#PersonTable').jtable('load',$('#queryform').serializeArray());
    		return false;
    	});
    	
    	var cachedOrgOptions = null;
    	var cachedGroupOptions = null;
    	
        //Prepare jtable plugin
        $('#PersonTable').jtable({
        	jqueryuiTheme:true,
            paging: true,
            sorting: true,
            defaultSorting: 'lastUpdateTime DESC',
            pageSize: 10,
            pageSizes:[10,20,30,50],

            actions: {
                listAction: function (postData, jtParams) {
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/module/User/list?page=' + jtParams.jtStartIndex + '&rows=' + jtParams.jtPageSize+ '&jtSorting=' + jtParams.jtSorting ,
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
            fields: {
                id: {
                    key: true,
                    create: false,
                    edit: false,
                    list: false
                },
                loginName: {
                    title: '登录账号',
                    width: '15%',
                    create: false,
                    edit: false
                },
                name: {
                    title: '姓名',
                    width: '10%',
                    create: false,
                    edit: false
                },
                orgCode: {
                    title: '公司名称',
                    create: false,
                    edit: false,
                    list: true,
                    width:'18%',
                    options: function () {
                        
                        if (cachedOrgOptions) { //Check for cache
                            return cachedOrgOptions;
                        }
 
                        var options = [];
 
                        $.ajax({ //Not found in cache, get from server
                            url: '${pageContext.request.contextPath}/module/User/gotoadd',
                            type: 'POST',
                            dataType: 'json',
                            async: false,
                            success: function (data) {
                                if (data.Result != 'OK') {
                                    alert(data.Message);
                                    return;
                                }
                                options = data.Options;
                            }
                        });
                         
                        return cachedOrgOptions = options; //Cache results and return options
                    }
                },
                groupCode: {
                    title: '群组',
                    create: false,
                    edit: false,
					options: function () {
                        
                        if (cachedGroupOptions) { //Check for cache
                            return cachedGroupOptions;
                        }
 
                        var options = [];
 
                        $.ajax({ //Not found in cache, get from server
                            url: '${pageContext.request.contextPath}/module/User/gotoaddGroup',
                            type: 'POST',
                            dataType: 'json',
                            async: false,
                            success: function (data) {
                                if (data.Result != 'OK') {
                                    alert(data.Message);
                                    return;
                                }
                                options = data.Options;
                            }
                        });
                         
                        return cachedGroupOptions = options; //Cache results and return options
                    }
                },     
                roleNames: {
                	title:'角色名称'
                	
                },
                tel: {
                    title: '电话号码',
                    width: '15%',
                    create: false,
                    edit: false
                },
                lastUpdateTime: {
                    title: '最后更新时间',
                    list: false,
                    create: false,
                    edit: false
                },

                pwdReset:{
                	title:'重置密码',
                    list:true,
                    create: false,
                    edit: false,
                	display:function(data){
                		//return '<a title="默认重置密码为123456" href="${pageContext.request.contextPath}/module/User/passReset?loginName='+data.record.loginName+'&resetup=1">重置密码</a>';
                		var tmpname = "'"+data.record.loginName+"'";
                		return '<a title="默认重置密码为123456" href="javascript:resetPwd('+tmpname+')">重置密码</a>';
                	}
                }                
            }

        });

        //Load person list from server
        $('#PersonTable').jtable('load');
    });

    function resetPwd(loginName){
		if(confirm("请确认是否要重置  "+loginName+" 用户的密码?")){
			$("#_LOGIN_NAME_").val(loginName);
			$("#_RESETPASSWORD_FORM_").ajaxSubmit({
				type:'post',
				dataType:'json',
				success: function (data) {
					
					alert(data['Message']);
                	$('#PersonTable').jtable('reload');
                }
			});
		}
	}
</script>


<jsp:include page="/template/include/_foot.jsp"></jsp:include>

