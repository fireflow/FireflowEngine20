"use strict";

// / CONFIGURATION
// / ====>

function SvgController(_arg_svg_id) {
	this.svgId = _arg_svg_id;
	this.init();// 注册事件
}

SvgController.prototype = {
	svgId : "",
	root : null,
	enablePan : 0,
	enableZoom : 1,
	enableDrag : 0,
	zoomScale : 0.2,
	zoomRate : 1,
	state : "none",
	stateTarget : null,
	stateOrigin : null,
	stateTf : null,
	init : function() {
		this.root = $ff("#" + this.svgId)[0];// document.getElementById(this.svgId);
		var svgObj = $ff("#" + this.svgId);
		svgObj.on("mousedown", {
			controllerObj : this
		}, function(evt) {
			var diagram = evt.data.controllerObj;
			diagram.handleMouseDown(diagram, evt);
		});
		svgObj.on("mousemove", {
			controllerObj : this
		}, function(evt) {
			var diagram = evt.data.controllerObj;
			diagram.handleMouseMove(diagram, evt);
		});
		svgObj.on("mouseup", {
			controllerObj : this
		}, function(evt) {
			var diagram = evt.data.controllerObj;
			diagram.handleMouseUp(diagram, evt);
		});
		/*
		 * svgObj.on("mouseout",{controllerObj:this},function(evt){ var diagram =
		 * evt.data.controllerObj; diagram.handleMouseOut(diagram,evt); });
		 */
	},

	handleMouseOut : function(controllerObj, evt) {
		evt.stopPropagation();
		evt.stopImmediatePropagation();
		if (controllerObj.state == 'pan' || controllerObj.state == 'drag') {
			// Quit pan mode
			controllerObj.state = '';
		}
	},

	handleMouseUp : function(controllerObj, evt) {
		evt.stopPropagation();
		evt.stopImmediatePropagation();

		if (controllerObj.state == 'pan' || controllerObj.state == 'drag') {
			// Quit pan mode
			controllerObj.state = '';
		}
	},

	handleMouseDown : function(controllerObj, evt) {
		if (!controllerObj.enableDrag && !controllerObj.enablePan) {
			return;
		}
		evt.stopPropagation();
		evt.stopImmediatePropagation();
		var evtSource = evt.target;

		var g = controllerObj.root.getElementById("viewport");

		if (evtSource.tagName == "svg" || !controllerObj.enableDrag // Pan
																	// anyway
																	// when drag
																	// is
																	// disabled
																	// and the
																	// user
																	// clicked
																	// on an
																	// element
		) {

			// Pan mode
			controllerObj.state = 'pan';

			controllerObj.stateTf = g.getCTM().inverse();

			controllerObj.stateOrigin = controllerObj.getEventPoint(
					controllerObj, evt).matrixTransform(controllerObj.stateTf);
		} else {
			// Drag mode
			controllerObj.state = 'drag';

			controllerObj.stateTarget = evt.target;

			controllerObj.stateTf = g.getCTM().inverse();

			controllerObj.stateOrigin = controllerObj.getEventPoint(
					controllerObj, evt).matrixTransform(controllerObj.stateTf);
		}
	},

	handleMouseMove : function(controllerObj, evt) {
		if (!controllerObj.enableDrag && !controllerObj.enablePan) {
			return;
		}
		evt.stopPropagation();
		evt.stopImmediatePropagation();

		var g = controllerObj.root.getElementById("viewport");

		if (controllerObj.state == 'pan' && controllerObj.enablePan) {
			// alert('pan');
			// Pan mode
			var p = controllerObj.getEventPoint(controllerObj, evt)
					.matrixTransform(controllerObj.stateTf);

			controllerObj.setCTM(g, controllerObj.stateTf.inverse().translate(
					p.x - controllerObj.stateOrigin.x,
					p.y - controllerObj.stateOrigin.y));
		} else if (controllerObj.state == 'drag' && controllerObj.enableDrag) {
			// Drag mode
			var p = controllerObj.getEventPoint(controllerObj, evt)
					.matrixTransform(g.getCTM().inverse());

			controllerObj.setCTM(controllerObj.stateTarget, controllerObj.root
					.createSVGMatrix().translate(
							p.x - controllerObj.stateOrigin.x,
							p.y - controllerObj.stateOrigin.y).multiply(
							g.getCTM().inverse()).multiply(
							controllerObj.stateTarget.getCTM()));

			controllerObj.stateOrigin = p;
		}
	},

	zoomfit : function() {
		var z = 1 / this.zoomRate;
		this.zoomRate = 1;
		var g = this.root.getElementById("viewport");

		var p = this.root.createSVGPoint();
		p.x = 1;
		p.y = 1;
		// var p = getEventPoint(evt);

		p = p.matrixTransform(g.getCTM().inverse());

		// Compute new scale matrix in current mouse position
		var k = this.root.createSVGMatrix().translate(p.x, p.y).scale(z)
				.translate(-p.x, -p.y);

		this.setCTM(g, g.getCTM().multiply(k));

		if (typeof (this.stateTf) == "undefined")
			this.stateTf = g.getCTM().inverse();

		if (this.stateTf) {
			this.stateTf = this.stateTf.multiply(k.inverse());
		}
	},

	zoomin : function() {
		var z = 1 + this.zoomScale;
		this.zoomRate = this.zoomRate * z;

		var g = this.root.getElementById("viewport");

		var p = this.root.createSVGPoint();
		p.x = 1;
		p.y = 1;
		// var p = getEventPoint(evt);

		p = p.matrixTransform(g.getCTM().inverse());

		// Compute new scale matrix in current mouse position
		var k = this.root.createSVGMatrix().translate(p.x, p.y).scale(z)
				.translate(-p.x, -p.y);

		this.setCTM(g, g.getCTM().multiply(k));

		if (typeof (this.stateTf) == "undefined") {
			this.stateTf = g.getCTM().inverse();
		}
		if (this.stateTf) {
			this.stateTf = this.stateTf.multiply(k.inverse());
		}
	},

	zoomout : function() {

		var z = 1 - this.zoomScale;

		this.zoomRate = this.zoomRate * z;

		var g = this.root.getElementById("viewport");

		var p = this.root.createSVGPoint();
		p.x = 1;
		p.y = 1;
		// var p = getEventPoint(evt);

		p = p.matrixTransform(g.getCTM().inverse());

		// Compute new scale matrix in current mouse position
		var k = this.root.createSVGMatrix().translate(p.x, p.y).scale(z)
				.translate(-p.x, -p.y);

		this.setCTM(g, g.getCTM().multiply(k));

		if (typeof (this.stateTf) == "undefined")
			this.stateTf = g.getCTM().inverse();

		if (this.stateTf) {
			this.stateTf = this.stateTf.multiply(k.inverse());
		}
	},

	getEventPoint : function(controllerObj, evt) {
		var p = controllerObj.root.createSVGPoint();

		p.x = evt.pageX;
		p.y = evt.pageY;

		return p;
	},

	setCTM : function(element, matrix) {
		var s = "matrix(" + matrix.a + "," + matrix.b + "," + matrix.c + ","
				+ matrix.d + "," + matrix.e + "," + matrix.f + ")";

		element.setAttribute("transform", s);
	}
}

function fireflowSvgInit(_svg_id) {

	var _svgDiagram = new SvgController(_svg_id);

	if (window[_svg_id]) {
		window[_svg_id] = null;
	}
	window[_svg_id] = _svgDiagram;
	
	window['SVG_VML_DIAGRAM_OBJECT'] = _svgDiagram;

}

function fireflowSvgDestroy(_svg_id) {
	window[_svg_id] = null;
}

function fireflowDiagramZoomIn(svgId) {

	var controller = window[svgId];

	controller.zoomin();
}

function fireflowDiagramZoomOut(svgId) {

	var controller = window[svgId];

	controller.zoomout();
}
function fireflowDiagramZoomFit(svgId) {

	var controller = window[svgId];

	controller.zoomfit();
}

function fireflowDiagramPan(svgId, checked) {

	var controller = window[svgId];
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
