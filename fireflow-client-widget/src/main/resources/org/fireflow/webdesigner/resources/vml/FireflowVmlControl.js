
"use strict";

/// CONFIGURATION 
/// ====>

function DiagramController(_arg_diagram_id){
	this.diagramId = _arg_diagram_id;
	this.init();//ע���¼�
}

DiagramController.prototype={
	diagramId:"",
	root:null,
	enablePan:0,
	enableZoom:1,
	enableDrag:0,
	zoomScale:0.2,
	zoomRate:1,
	coordSizeX:null,
	coordSizeY:null,
	state:"none",
	stateOrigin:null,
	init:function(){
			var jqueryObjTmp = $ff("#"+this.diagramId);
			this.root = jqueryObjTmp[0];//document.getElementById(this.diagramId);
			this.coordSizeX = this.root.coordsize.x;
			this.coordSizeY = this.root.coordsize.y;
			this.stateOrigin = new Object();
			this.stateOrigin.x = 0;
			this.stateOrigin.y = 0;
		  jqueryObjTmp.on("mousedown",{controllerObj:this},function(evt){
		  		var diagram = evt.data.controllerObj;
		  		diagram.handleMouseDown(diagram,evt);
		  	});
		  jqueryObjTmp.on("mousemove",{controllerObj:this},function(evt){
		  		var diagram = evt.data.controllerObj;
		  		diagram.handleMouseMove(diagram,evt);
		  	});
		  jqueryObjTmp.on("mouseup",{controllerObj:this},function(evt){
		  		var diagram = evt.data.controllerObj;
		  		diagram.handleMouseUp(diagram,evt);
		  	});		  	
		 /*
		  jqueryObjTmp.on("mouseout",{controllerObj:this},function(evt){
		  		var diagram = evt.data.controllerObj;
		  		diagram.handleMouseOut(diagram,evt);
		  	});
		  	*/		  	
	},
	
	handleMouseOut:function(controllerObj,evt){
		
		evt.stopPropagation();
		evt.stopImmediatePropagation();
		if(controllerObj.state == 'pan' || controllerObj.state == 'drag') {
			// Quit pan mode
			controllerObj.state = '';
		}
	},
	

  handleMouseUp:function(controllerObj,evt){  	

		evt.stopPropagation();
		evt.stopImmediatePropagation();

		if(controllerObj.state == 'pan' || controllerObj.state == 'drag') {
			// Quit pan mode
			controllerObj.state = '';
		}
  },
  
  handleMouseDown:function(controllerObj,evt){

  	  if (!controllerObj.enableDrag && !controllerObj.enablePan){
  	  	return;
  	  }
  	  
		evt.stopPropagation();
		evt.stopImmediatePropagation();  	  
		
		controllerObj.state = 'pan';
		//controllerObj.stateOrigin = controllerObj.getEventPoint(controllerObj,evt);
		controllerObj.stateOrigin.x = evt.pageX;
		controllerObj.stateOrigin.y = evt.pageY;
  },
  
  handleMouseMove:function(controllerObj,evt){
  	if (!controllerObj.enableDrag && !controllerObj.enablePan){
  	  	return;
  	}
  	if (evt.which!="1"){//如果没有按住鼠标左键，则返回
        controllerObj.stateOrigin.x=evt.pageX;
        controllerObj.stateOrigin.y=evt.pageY;
  		return;
  	}
		evt.stopPropagation();
		evt.stopImmediatePropagation();
		
		if(controllerObj.state == 'pan' && controllerObj.enablePan) {
	    var sx=controllerObj.root.style.posLeft+evt.pageX-controllerObj.stateOrigin.x;
	  	var sy=controllerObj.root.style.posTop +evt.pageY-controllerObj.stateOrigin.y;
      controllerObj.stateOrigin.x=evt.pageX;
      controllerObj.stateOrigin.y=evt.pageY;
      controllerObj.root.style.posLeft=sx;
	  	controllerObj.root.style.posTop =sy;
		}
	
  },

	zoomfit: function () {
		this.zoomRate = 1;
 		this.root.coordsize.x=this.coordSizeX;
 		this.root.coordsize.y=this.coordSizeY;
 		
 		var textPathList = document.getElementsByTagName("textbox");
		var length = textPathList.length;
		for (var i=0;i<length;i++){
			var textPath = textPathList[i];
			textPath.style.posLeft = parseInt(textPath.posLeft)*this.zoomRate;
			textPath.style.posTop = parseInt(textPath.posTop)*this.zoomRate ;
			textPath.style.pixelWidth = parseInt(textPath.pixelWidth)*this.zoomRate ;
			textPath.style.pixelHeight =  parseInt(textPath.pixelHeight)*this.zoomRate ;

			textPath.style.fontSize = parseInt(textPath.fontSize)*this.zoomRate+"px";
		}
	},
		
	zoomin: function () {
		var z = 1 + this.zoomScale;
		this.zoomRate = this.zoomRate*z;
		
		
 		this.root.coordsize.x=this.coordSizeX/this.zoomRate;
 		this.root.coordsize.y=this.coordSizeY/this.zoomRate;
 		
 		var textPathList = document.getElementsByTagName("textbox");
		var length = textPathList.length;
		for (var i=0;i<length;i++){
			var textPath = textPathList[i];
			textPath.style.posLeft = parseInt(textPath.posLeft)*this.zoomRate;
			textPath.style.posTop = parseInt(textPath.posTop)*this.zoomRate ;
			textPath.style.pixelWidth = parseInt(textPath.pixelWidth)*this.zoomRate ;
			textPath.style.pixelHeight =  parseInt(textPath.pixelHeight)*this.zoomRate ;

			textPath.style.fontSize = parseInt(textPath.fontSize)*this.zoomRate+"px";
		} 		
	},
	
	zoomout:function () {
	  
		var z = 1 - this.zoomScale;
		
		this.zoomRate = this.zoomRate*z;
 		this.root.coordsize.x=this.coordSizeX/this.zoomRate;
 		this.root.coordsize.y=this.coordSizeY/this.zoomRate;
 		
 		//对字体进行处理
 		var textPathList = document.getElementsByTagName("textbox");
		var length = textPathList.length;
		for (var i=0;i<length;i++){
			var textPath = textPathList[i];
			textPath.style.posLeft = parseInt(textPath.posLeft)*this.zoomRate;
			textPath.style.posTop = parseInt(textPath.posTop)*this.zoomRate ;
			textPath.style.pixelWidth = parseInt(textPath.pixelWidth)*this.zoomRate ;
			textPath.style.pixelHeight =  parseInt(textPath.pixelHeight)*this.zoomRate ;

			textPath.style.fontSize = parseInt(textPath.fontSize)*this.zoomRate+"px";
		} 		
	},

	getEventPoint : function(controllerObj,evt) {
		var p = new Object();

		p.x = evt.pageX;
		p.y = evt.pageY;

		return p;
	}
}

function fireflowDiagramInit(_diagram_id){
		
		var _diagramController = new DiagramController(_diagram_id);
		
		if (window[_diagram_id]){
			window[_diagram_id]=null;
		}
		window[_diagram_id] = _diagramController;
		window['SVG_VML_DIAGRAM_OBJECT'] = _diagramController;

}

function fireflowDiagramZoomIn(_diagram_id){
    var controller = window[_diagram_id];

	controller.zoomin();
}

function fireflowDiagramZoomOut(_diagram_id) {

	var controller = window[_diagram_id];

	controller.zoomout();
}
function fireflowDiagramZoomFit(_diagram_id) {

	var controller = window[_diagram_id];

	controller.zoomfit();
}

function fireflowDiagramPan(_diagram_id, checked) {

	var controller = window[_diagram_id];
	if (checked) {

		controller.enablePan = 1;
		$ff(controller.root).css({
			cursor : "move"
		});

	} else {
		controller.enablePan = 0;
		$ff(controller.root).css({
			cursor : "default"
		});

	}
}

function on_element_click(diagramElementId,wfElementRef,wfElementType,workflowProcessId,subProcessName){
	alert(diagramElementId+","+wfElementRef+","+wfElementType+","+workflowProcessId+","+subProcessName);
}
