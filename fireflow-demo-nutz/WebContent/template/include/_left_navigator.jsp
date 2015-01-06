<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<table id="NavigatorTable" width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed;">
  <tr>
    
    <td valign="top">
    
    <table width="100%" border="0" cellspacing="0" cellpadding="0" >
      <tr>
        <td height="20" background="${pageContext.request.contextPath}/static/images/green/main_11.gif">&nbsp;</td>
      </tr>
      <tr>
        <td>

			<div id="NavigatorDiv" style="overflow-x:hidden;overflow-y:hidden;width:173px;"> 
			<!-- <div id="NavigatorDiv"> -->

			</div>

		</td>
      </tr>
    </table></td>
  </tr>
</table>





<script type="text/javascript">
<!--
function navigatorStateChanged(nodes, nodesJson) {

    var t = makeCookie(nodes);
    $.cookie('okerp_1_navigator_state', t,{path:'/'});
}

function makeCookie(nodesJson){
	var state = "";
	for (var i=0;i<nodesJson.length;i++){
		var menu = nodesJson[i];
		state = state+toStateStr(menu);
	}
	return state;
}

function toStateStr(menu){
	var state = menu.id+"-"+(menu.isActive?'t':'f')+(menu.isExpanded?'t':'f')+"_";
	if ($.isArray( menu.children)){
		for (var j=0;j<menu.children.length;j++){
			var child = menu.children[j];
			state = state+toStateStr(child);
		}
	}
	return state;
}

$(document).ready(function(){
	var minMenu = $('#NavigatorDiv').easytree({
		dataUrl:'${pageContext.request.contextPath}/SysMenu/loadMyMenus',
        stateChanged: navigatorStateChanged,
        disableIcons:false
	});

});
//-->
</script>