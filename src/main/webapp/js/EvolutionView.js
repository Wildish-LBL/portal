jsPlumb.ready(function() {
	jsPlumb.Defaults.PaintStyle = {
		lineWidth : 3,
		strokeStyle : '#056'
	}

	jsPlumb.Defaults.Anchor = 'Continuous';
	jsPlumb.Defaults.Endpoint = 'Blank';
	jsPlumb.Defaults.Connector = 'StateMachine';
	jsPlumb.Defaults.Overlays = [ [ 'PlainArrow', {
		location : 1,
		width : 20,
		length : 12
	} ] ];

});