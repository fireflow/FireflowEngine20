	<script language="JavaScript">
		// 工具栏
		$ff(function(){
			
			// 源代码窗口
			$ff("#DIAGRAM_ID_source_xml_dialog").dialog({
				autoOpen: false,
				modal: true,
				height: 400,
				width:600,
				draggable: true,
				resizable: true,
				resizeStop: function( event, ui ) {
			 			var width=ui.size.width-16;
			 			var height = ui.size.height-68;
			 			$ff("#DIAGRAM_ID_source_xml_iframe").attr("height",height) ;
			 			$ff("#DIAGRAM_ID_source_xml_iframe").attr("width",width) ;
			 		}
				}
			);			
			
			var svgWrapperIframe = $ff("#SVG_VML_WRAPPER_ID")[0];
			
			//放大
			$ff("#DIAGRAM_ID_zoom_in").button({
		      	text: false,
		      	icons: {
		        		primary: "ui-icon-zoomin"
		      		}
		    	}).click(function(){
		    		svgWrapperIframe.contentWindow.fireflowDiagramZoomIn('SVG_VML_DIAGRAM_OBJECT');
		    	}
		    );
		    	
			$ff("#DIAGRAM_ID_zoom_out").button({
		      	text: false,
		      	icons: {
		        		primary: "ui-icon-zoomout"
		      		}
		    	}).click(function(){
		    		svgWrapperIframe.contentWindow.fireflowDiagramZoomOut('SVG_VML_DIAGRAM_OBJECT');
		    	}
		    );
    	
	    	$ff("#DIAGRAM_ID_zoom_fit").button({
		      	text: false,
		      	icons: {
		        		primary: "ui-icon-search"
		      		}
		    	}).click(function(){
		    		svgWrapperIframe.contentWindow.fireflowDiagramZoomFit('SVG_VML_DIAGRAM_OBJECT');

		    	}
		    );
    	
	    	$ff("#DIAGRAM_ID_move").button({
		      	text: false,
		      	icons: {
		        		primary: "ui-icon-arrow-4"
		      		}
		    	}).click(function(e){
					if ($ff("#DIAGRAM_ID_move").is(':checked')){
						svgWrapperIframe.contentWindow.fireflowDiagramPan('SVG_VML_DIAGRAM_OBJECT',true);	
					}else{
						svgWrapperIframe.contentWindow.fireflowDiagramPan('SVG_VML_DIAGRAM_OBJECT',false);	
									
					}
		    	}
		    );
    	
 
	    	//弹出源代码窗口
	    	$ff("#DIAGRAM_ID_source_xml").button({
	      	text: false,
	      	icons: {
	        		primary: "ui-icon-carat-2-e-w"
	      		}
	    	}).click(function(event){
	    		// 设置iframe的src
	    		$ff("#DIAGRAM_ID_source_xml_iframe").attr("src","CONTEXT_PATH/FireflowClientWidgetServlet?workflowActionType=GET_PROCESS_DEFS&processId=PROCESS_ID&processVersion=PROCESS_VERSION&processType=PROCESS_TYPE") ;
	    		// 打开dialog
	    		$ff("#DIAGRAM_ID_source_xml_dialog").dialog("open");

	    		event.preventDefault();
	    	}); 
	    	
	    	$ff("#DIAGRAM_ID_subprocess_selector").change(function(){
	    		var subProcessName = $ff(this).val(); 
	    		
	    		$ff("#SVG_VML_WRAPPER_ID").attr("src",
	    				"CONTEXT_PATH/FireflowClientWidgetServlet?workflowActionType=GET_PROCESS_DIAGRAM&processId=PROCESS_ID&processVersion=PROCESS_VERSION&processType=PROCESS_TYPE&subProcessName="+subProcessName) ;
	    	});
	    			
		});
		
		
	</script>	