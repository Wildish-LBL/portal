jsPlumb.ready(function() {
	jsPlumb.connect({
		source : "ro1",
		target : "ro2",
		anchors : [ "BottomCenter", "TopCenter" ],
		connector : "Straight"
	});
	jsPlumb.connect({
		source : "ro1",
		target : "ro3",
		anchors : [ "BottomCenter", "TopCenter" ],
		connector : "Straight"
	});
	jsPlumb.connect({
		source : "ro1",
		target : "ro4",
		anchors : [ "BottomCenter", "TopCenter" ],
		connector : "Straight"
	});
	jsPlumb.connect({
		source : "ro3",
		target : "ro2",
		anchors : [ "LeftMiddle", "RightMiddle" ],
		connector : "Straight",
		overlays : [ "Arrow", [ "Label", {
			label : "hasPrevious",
			cssClass : "evolabel"
		} ] ]
	});
	jsPlumb.connect({
		source : "ro3",
		target : "ro4",
		anchors : [ "RightMiddle", "LeftMiddle" ],
		connector : "Straight"
	});
});