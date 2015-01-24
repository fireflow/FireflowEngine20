$(document).ready(function(){
	$("#toolbar").dtoolbar({
		items:[{
			id:'new',
		    ico : 'btnNew',
		    text : '新建',
		    handler : function(){
				alert($(this).html());
				alert('new');
		    }
		  },{
			id:'del',
		    ico : 'btnDel',
		    text : '删除',
		    handler : function(){
				alert('del');
		    }
		  },{
			id:'edit',
		    ico : 'btnEdit',
		    text : '修改',
			disabled:true,
		    handler : function(){
				alert('你点我');
		    }
		  },{
			id:'print',
		    ico : 'btnprint2',
		    text : '点击按钮',
			checkbox:true,
		    handler : function(){
				alert($(this).attr("checked"));
		    }
		  },{
			id:'print',
		    ico : 'btnconfig',
		    text : '我只是一个按钮而已'
		  }]
	});
	
	$("#bttest1").click(function(){
		$("#toolbar").setbtEnable("del",false);
	})
	$("#bttest2").click(function(){
		$("#toolbar").setbtEnable("del",true);
	})
});

