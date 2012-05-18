jsPlumb.ready(function() {
	jsPlumb.Defaults.Container = $("#evoroot");
	jsPlumb.Defaults.PaintStyle = {
		lineWidth : 2,
		strokeStyle : '#23A4FF'
	}

	jsPlumb.Defaults.Anchor = 'Continuous';
	jsPlumb.Defaults.Endpoint = 'Blank';
	jsPlumb.Defaults.Connector = 'Flowchart';
	jsPlumb.Defaults.Overlays = [ [ 'PlainArrow', {
		location : 1,
		width : 20,
		length : 12
	} ] ];

});