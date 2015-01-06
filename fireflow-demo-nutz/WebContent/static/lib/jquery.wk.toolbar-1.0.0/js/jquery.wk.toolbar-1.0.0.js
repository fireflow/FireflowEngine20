(function($) {
	$.fn.swapClass = function(c1, c2) {
		return this.removeClass(c1).addClass(c2);
	};
	$.fn.switchClass = function(c1, c2) {
		if (this.hasClass(c1)) {
			return this.swapClass(c1, c2);
		}
		else {
			return this.swapClass(c2, c1);
		}
	};
	$.fn.dtoolbar = function(settings) {
		var dfop ={
			items:null
		};
		$.extend(dfop, settings);
		var me = $(this);
		me.addClass("gTlBar");
		if (dfop.items) {
			for (i = 0; i < dfop.items.length; i++) {
				me.append(addtoolbar(dfop.items[i],i));
			}
		}
		function addtoolbar(item,numi) {
			var t = {
				checkbox:false,
				text:null,
				ico :null,
				handler:false,
				disabled:false
			};
			$.extend(t, item);
			var toolbarEntity = this;
			if (t.text == '-') {
			//还没想到
			//TODO
			} else {
				var bttbar = $("<div></div>");
				if(t.id==null)
					bttbar.attr("id","bt_"+numi);
				else
					bttbar.attr("id","bt_"+t.id);
				bttbar.addClass('tlbtn2');
				bttbar.attr({
					checkbox:t.checkbox,
					checked:'false',
					numberid:numi
				});
				bttbar.append('<div class="btnLe"></div>');
				bttbar.append('<b class="icoBtn '+t.ico+'"></b><span class="btnTxt">'+t.text+'</span>');
				bttbar.append('<div class="btnRi"></div>');
				if (t.handler) {
					bttbar.bind('click', t.handler);
				}
				if (t.disabled) {
					bttbar.attr("disabled", "disabled");
					bttbar.addClass('btndisabled');
					bttbar.unbind('click');
				}
				bttbar.bind('mouseover', function(){
					var isdisabled = (bttbar.attr("disabled")=='disabled')?true:false;
					if (isdisabled) {
					
					}
					else {
						
						if ($(this).attr("checkbox") == 'false' || $(this).attr("checkbox") == false) {
							$(this).addClass('on');
						}

						$(this).bind('mouseout', function(){
							$(this).removeClass('click');
							
							if ($(this).attr("checkbox") == 'false' || $(this).attr("checkbox") == false) {
								$(this).removeClass('on');
							}

							$(this).unbind('mouseout');
						});
					}
				});
				bttbar.bind('mousedown', function(){
					var isdisabled = (bttbar.attr("disabled")=='disabled')?true:false;
					if (isdisabled) {
					}
					else {
						$(this).removeClass('on');
						$(this).addClass('click');
						$(this).bind('mouseup', function(){
							$(this).removeClass('click');
							$(this).removeClass('on');
							$(this).unbind('mouseup');
						});
						if ($(this).attr("checkbox") == 'true') {
							if ($(this).attr("checked") == 'true') {
								$(this).attr("checked", 'false');
								$(this).removeClass('on');
							}
							else {
								$(this).attr("checked", 'true');
								$(this).addClass('on');
							}
						}
					}
				});
				return bttbar;
			}
		};
		me[0].t ={
			setEnable:function (id,enable){
				var bttbar = $("#bt_"+id);
				if(bttbar.length<=0) return;
				var numid = bttbar.attr("numberid");
				var t=dfop.items[numid];
				if(!enable){
					bttbar.attr("disabled", "disabled");
					bttbar.addClass('btndisabled');
					bttbar.unbind('click');
				}else{
					bttbar.removeAttr("disabled");
					bttbar.removeClass('btndisabled');
					if (t.handler) {
						bttbar.bind('click', t.handler);
					}
				}
				
			}
		};
		return me;
	};
	//将某一个按钮设为可用和不可用
	$.fn.setbtEnable=function(id,enable){
		if (this[0].t) {
			return this[0].t.setEnable(id,enable);
		}
		return null;
	};
})(jQuery);