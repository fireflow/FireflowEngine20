/**
 * 
 */
var contextPath="/okerp";
var oj = oj || {};
oj.cmn = new function() {
	
	function handle_error(jqXHR){
		if(jqXHR.status==403){
			alert("对不起，你没权限");
		}else if(jqXHR.status==200){
			alert("请先登录");
		}else if(jqXHR.status==500){
			alert("我累了，麻烦你找管理员来看看我");
		}
		window.location.reload();
	}
	this.handle_error = handle_error;
	
    function AsyncSelect() {
    	/**
    	 * chosen控件联动，
    	 * @param source_id 源select标签的id,
    	 * @param url load第二个select标签option选项的url
    	 * @param target_id 目标select标签的id
    	 */
        function selectTo(source_id, url, target_id) {
    		var source_selector = $("#" + source_id);
    		var target_selector = $("#" + target_id);
    		source_selector.change(function(event){
    			var source_val = source_selector.val();
        		$.get(url + source_selector.val(), function(result) {
        			target_selector.html(result);
        			source_selector.val(source_val);
        			$("#" + target_id).trigger("chosen:updated");
        		});
    		});
        }
        this.selectTo = selectTo;
        
        /**
         * chosen控件联动，target_id的变化必须基于f_id和s_id的变化
         * @param f_id
         * @param s_id
         * @param url
         * @param target_id
         */
        function second_selectTo(f_id, s_id, url, target_id) {
        	var source_first = $("#" + f_id);
        	var source_second = $("#" + s_id);
        	var target_selector = $("#" + target_id);
        	source_first.change(function(event){
        		target_selector.html("<option value=''>请选择</option>");
        	});
        	source_second.change(function(event){
        		var first_val = source_first.val();
        		var second_val = source_second.val();
        		$.get(url + first_val + "?s_id=" + second_val, function(result) {
        			target_selector.html(result);
        			source_first.val(first_val);
        			source_second.val(second_val);
        		});
        	});
        }
        this.second_selectTo = second_selectTo;
        
        /**
         * f_id,s_id随便哪个变化，target_id都会变化
         * @param f_id
         * @param s_id
         * @param url
         * @param target_id
         */
        function double_selectTo(f_id, s_id, url, target_id) {
        	var source_first = $("#" + f_id);
        	var source_second = $("#" + s_id);
        	var target_selector = $("#" + target_id);
        	source_second.change(function(event){
        		var first_val = source_first.val();
        		var second_val = source_second.val();
        		$.get(url + "?f_id=" + first_val + "&s_id=" + second_val, function(result) {
        			target_selector.html(result);
        			source_first.val(first_val);
        			source_second.val(second_val);
        		});
        	});
        	source_first.change(function(event){
        		var first_val = source_first.val();
        		var second_val = source_second.val();
        		$.get(url + "?f_id=" + first_val + "&s_id=" + second_val, function(result) {
        			target_selector.html(result);
        			source_first.val(first_val);
        			source_second.val(second_val);
        			$("#" + target_id).trigger("chosen:updated");
        		});
        	});
        }
        this.double_selectTo = double_selectTo;
    }
    var asyncSelect = new AsyncSelect();
    
    function Page() {
    	function change(page_id, container_id){
    		$("#"+page_id+" a").click(function(){
    			var url = $(this).attr("href");
    			$.ajax({
    				url: url,
    				dataType: 'json',
    				cache:false,
    				success: function(result) {
    					$("#"+container_id).html(result.html);
    				},
    				error : function(jqXHR, textStatus, errorThrown){
    					handle_error(jqXHR);
    				}
    			});
    			return false;
    		});
    	}
    	this.change = change;
    }
	var page = new Page();
	
	
	function switchTab(hideClass, showClass) {
		$("."+hideClass).hide();
		$("."+showClass).show();
	}
	this.switchTab = switchTab;

	function create_finish(finish) {
		if(finish){
			alert("创建成功");
		}
	}
	this.create_finish = create_finish;
	
	function check_int(){
		$("#int_str").change(function(){
			var str = $(this).val();
			if(!isNaN(str)){
				$("#int_msg").html("");
				$('#submit_button').attr('disabled',false);
			}else{
				$("#int_msg").html("请输入整数");
				$('#submit_button').attr('disabled',true);
			}
		});
	}
	this.check_int = check_int;
	
    function init() {
    }
    this.init = init;
    this.asyncSelect = asyncSelect;
    this.page = page;
};


$(document).ready(function(){
	set_block_size();
	$(window).resize(function(){
		set_block_size();
	});
	
	$('#navControlTd').click(function(){
		showHideNavigator();
	});
});

function set_block_size(){
	var wd_h = $(window).height();
	var wd_w = $(window).width();
	var workspaceHeight = wd_h-23-52-10;//因为有滚动条，所以多减去10px,
	$("#WorkspaceDiv").height(workspaceHeight);
	$("#WorkspaceDiv").width(wd_w-$('#navTd').width()-4-8-4-20);//减去左右边框4，菜单按钮8，以及菜单宽度，滚动条宽度
	$("#NavigatorDiv").height(wd_h-23-52-20-5);//减去banner,footer和菜单顶端标题
	
	$("#_OU_TREE_").height(workspaceHeight-100);	
}



function showHideNavigator(){
	if ($('#navTd').width()<10){
		showNavigator();
	}else{
		hideNavigator();
	}
}

function hideNavigator(){
	$('#navTd').width(0);
	$('#NavigatorTable').css('display','none');
	
	
	var wd_w = $(window).width();
	$("#WorkspaceDiv").width(wd_w-$('#navTd').width()-4-8-4-20);//减去左右边框4，菜单按钮8，以及菜单宽度，滚动条宽度
}

function showNavigator(){
	$('#navTd').width(173);
	$('#NavigatorTable').css('display','block');
	
	var wd_w = $(window).width();
	$("#WorkspaceDiv").width(wd_w-$('#navTd').width()-4-8-4-20);//减去左右边框4，菜单按钮8，以及菜单宽度，滚动条宽度
}

function convertCurrency(currencyDigits) {  
	// Constants:  
	var MAXIMUM_NUMBER = 99999999999.99;  
	// Predefine the radix characters and currency symbols for output:  
	var CN_ZERO = "零";  
	var CN_ONE = "壹";  
	var CN_TWO = "贰";  
	var CN_THREE = "叁";  
	var CN_FOUR = "肆";  
	var CN_FIVE = "伍";  
	var CN_SIX = "陆";  
	var CN_SEVEN = "柒";  
	var CN_EIGHT = "捌";  
	var CN_NINE = "玖";  
	var CN_TEN = "拾";  
	var CN_HUNDRED = "佰";  
	var CN_THOUSAND = "仟";  
	var CN_TEN_THOUSAND = "万";  
	var CN_HUNDRED_MILLION = "亿";  
	var CN_SYMBOL = "人民币";  
	var CN_DOLLAR = "元";  
	var CN_TEN_CENT = "角";  
	var CN_CENT = "分";  
	var CN_INTEGER = "整";  
	  
	// Variables:  
	var integral; // Represent integral part of digit number.  
	var decimal; // Represent decimal part of digit number.  
	var outputCharacters; // The output result.  
	var parts;  
	var digits, radices, bigRadices, decimals;  
	var zeroCount;  
	var i, p, d;  
	var quotient, modulus;  
	  
	// Validate input string:  
	currencyDigits = currencyDigits.toString();  
	if (currencyDigits == "") {  
	  //alert("Empty input!");  
	  return "";  
	}  
	if (currencyDigits.match(/[^,.\d]/) != null) {  
	  alert("Invalid characters in the input string!");  
	  return "";  
	}  
	if ((currencyDigits).match(/^((\d{1,3}(,\d{3})*(.((\d{3},)*\d{1,3}))?)|(\d+(.\d+)?))$/) == null) {  
	  alert("Illegal format of digit number!");  
	  return "";  
	}  
	  
	// Normalize the format of input digits:  
	currencyDigits = currencyDigits.replace(/,/g, ""); // Remove comma delimiters.  
	currencyDigits = currencyDigits.replace(/^0+/, ""); // Trim zeros at the beginning.  
	// Assert the number is not greater than the maximum number.  
	if (Number(currencyDigits) > MAXIMUM_NUMBER) {  
	  alert("Too large a number to convert!");  
	  return "";  
	}  
	  
	// Process the coversion from currency digits to characters:  
	// Separate integral and decimal parts before processing coversion:  
	parts = currencyDigits.split(".");  
	if (parts.length > 1) {  
	  integral = parts[0];  
	  decimal = parts[1];  
	  // Cut down redundant decimal digits that are after the second.  
	  decimal = decimal.substr(0, 2);  
	}  
	else {  
	  integral = parts[0];  
	  decimal = "";  
	}  
	// Prepare the characters corresponding to the digits:  
	digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);  
	radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);  
	bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);  
	decimals = new Array(CN_TEN_CENT, CN_CENT);  
	// Start processing:  
	outputCharacters = "";  
	// Process integral part if it is larger than 0:  
	if (Number(integral) > 0) {  
	  zeroCount = 0;  
	  for (i = 0; i < integral.length; i++) {  
	   p = integral.length - i - 1;  
	   d = integral.substr(i, 1);  
	   quotient = p / 4;  
	   modulus = p % 4;  
	   if (d == "0") {  
	    zeroCount++;  
	   }  
	   else {  
	    if (zeroCount > 0)  
	    {  
	     outputCharacters += digits[0];  
	    }  
	    zeroCount = 0;  
	    outputCharacters += digits[Number(d)] + radices[modulus];  
	   }  
	   if (modulus == 0 && zeroCount < 4) {  
	    outputCharacters += bigRadices[quotient];  
	   }  
	  }  
	  outputCharacters += CN_DOLLAR;  
	}  
	// Process decimal part if there is:  
	if (decimal != "") {  
	  for (i = 0; i < decimal.length; i++) {  
	   d = decimal.substr(i, 1);  
	   if (d != "0") {  
	    outputCharacters += digits[Number(d)] + decimals[i];  
	   }  
	  }  
	}  
	// Confirm and return the final output string:  
	if (outputCharacters == "") {  
	  outputCharacters = CN_ZERO + CN_DOLLAR;  
	}  
	if (decimal == "") {  
	  outputCharacters += CN_INTEGER;  
	}  
	outputCharacters = outputCharacters;  
	return outputCharacters;  
}

function hideDialogSaveButton(){
	   $(".ui-button-text").each(function(){
		     if($(this).text() == "保存"){
		      	$(this).hide();
	    	  }
	   });
}

function showDialogSaveButton(){
	   $(".ui-button-text").each(function(){
		     if($(this).text() == "保存"){
		      	$(this).show();
	    	  }
	   });
}

function hideDialogEditButton(){
	   $(".ui-button-text").each(function(){
		     if($(this).text() == "修改"){
		      	$(this).hide();
	    	  }
	   });
}

function showDialogEditButton(){
	   $(".ui-button-text").each(function(){
		     if($(this).text() == "修改"){
		      	$(this).show();
	    	  }
	   });
}

//计算两个日期的月份差，格式必须是2014-09-29
function countSubDate(startDate,endDate){
	if(startDate != null && startDate!="" && endDate!=null && endDate!=""){
		//2014-09-29
		var startYear = startDate.substring(0,4);
		var startMonth = startDate.substring(5,7);
		var endYear = endDate.substring(0,4);
		var endMonth = endDate.substring(5,7);
		var sub = (parseInt(endYear) - parseInt(startYear)) * 12 + parseInt(endMonth) - parseInt(startMonth) + 1;
		return sub;
	}
}

//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
//调用： var time1 = new Date().Format("yyyy-MM-dd");
//调用：var time2 = new Date().Format("yyyy-MM-dd HH:mm:ss");
Date.prototype.Format = function (fmt) { //author: meizz 
	 var o = {
	     "M+": this.getMonth() + 1, //月份 
	     "d+": this.getDate(), //日 
	     "h+": this.getHours(), //小时 
	     "m+": this.getMinutes(), //分 
	     "s+": this.getSeconds(), //秒 
	     "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	     "S": this.getMilliseconds() //毫秒 
	 };
	 if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	 for (var k in o)
	 if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	 return fmt;
};



//生成收入合同号和执行单号
//收入时category=I，成本时category=C
function generateContractCode(organizationCode,companyCode,category,dtStart){
	var contractCode="";
	
	/*  //老的生成规则
	var forShort = "";
	if(organizationCode=="OKVIEW"){
		forShort = "YX";
	}else if(organizationCode=="OKSUNAD"){
		//广州诺阳光广告有限公司
		forShort = "NYG";
	}else if(organizationCode=="OKJOYS"){
		//广州思想网络有限公司
		forShort = "SX";
	}else if(organizationCode=="OKIDEAAD"){
		//奥奇智慧广告有限公司
		forShort = "AQ";
	}
	
	if (dtStart=="" || (typeof dtStart)=="undifined" || dtStart.length!=10){
		dtStart = new Date().Format("yyyyMMdd");
	}else{
		dtStart = dtStart.replace(/\-/g, "" );
	}
	
	
	//合同号生成规则：单位汉语简称 ＋ 收入(J)/成本(Y)＋ 年月日 ＋（-）＋ 客户对应编码
	var arr = new Array();
	arr[0]=forShort;
	arr[1]=flag;
	arr[2]=dtStart;
	arr[3]="-";
	arr[4]=companyCode;
	
	var newCode=arr.join("");//直接通过js可以生成合同号，不必提交到后台，非也2014-09-28
*/	
	$.ajax({
		url:contextPath+'/module/Contract/getContractCode?organizationCode='+organizationCode+"&category="+category+"&startDate="+dtStart,		
		dataType:'json',
		cache:false, 
	    async:false, 
		success:function(data){
			contractCode=data;	
		},
		error:function(e){
			alert("异步获取执行单号失败！");
			return;
		}
	});
	
	return contractCode;
}	

//判断合同号是否已经存在
// TODO 该方法有问题，因为ajax异步执行，无法return 结果
function contractCodeExist(newCode){
	var exist = false;
	if(newCode!=""){
		//异步判断新的合同号是否已经存在数据库中
		$.ajax({
				url : contextPath+'/module/Contract/countContractCode',
				type : 'POST',
				dataType : 'json',
				data : "contractCode="+newCode, 
				cache:false, 
			    async:false, 
				success : function(data) {
					if(data=="OK"){
						exist =  false;
						
					}else{
						exist =  true;
					}
				},
				error : function() {
					alert("异步获取合同号失败！");
					exist =  true;
				}
		 });
	}  
	return exist;
}

function generateCrmCode(contractCode){
	//alert(contractCode);
	var returnValue="";
	$.ajax({
		url:contextPath+'/module/Order/getCrmCode?contractCode='+encodeURIComponent(contractCode),		
		dataType:'json',
		cache:false, 
	    async:false, 
		success:function(data){
			returnValue=data.CrmCode;	
		},
		error:function(e){
			alert("异步获取执行单号失败！");
			return;
		}
	});

	return returnValue;
}

//判断该媒体执行单号，在order表是否已经存在成本或收入执行单
//mediaCrmCode为媒体执行单号，category值I收入，C为成本
//当已经存在，那么返回1，否则返回0
function existOrder(mediaCrmCode,category){
	var returnValue="0";
	$.ajax({
		url:contextPath+'/module/Order/existOrder?mediaCrmCode='+encodeURIComponent(mediaCrmCode)+"&category="+category,		
		dataType:'json',
		cache:false, 
	    async:false, 
		success:function(data){
			returnValue=data;	
		},
		error:function(e){
			alert("根据媒体执行单号，同步判断是否已经存在成本或收入执行单失败！");
			return;
		}
	});

	return returnValue;
}

//判断textarea值长度 是否超指定长度
//element(this)--判断的元素；maxLength指定的最大长度；提示信息message
//当大于指定长度return false;
//onkeyup="javaScript:maxLengthConfirm(this,2,'详细意见不能大于200')"
function maxLengthConfirm(element,maxLength,message){
	var elementValue=$(element).val();
	if(elementValue.length > maxLength){
		alert(message);
		return false;
	}
};

//空的同名函数
//提供给_workflow_toolbar.jsp
//里面的保存、流程提交等方法调用
//如果有实际逻辑需要，请在jsp里面覆盖该函数，例子：Order_edit.jsp
function beforeSubmit(){
	return true;
};
