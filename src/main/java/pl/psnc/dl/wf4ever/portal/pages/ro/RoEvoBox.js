function initRoEvo(instance) {
	instance.Defaults.Container = $("#evoroot");
	instance.Defaults.PaintStyle = {
		lineWidth : 1.5,
		strokeStyle : '#23A4FF'
	}

	instance.Defaults.Anchor = 'Continuous';
	instance.Defaults.Endpoint = 'Blank';
	instance.Defaults.Connector = 'Flowchart';
	instance.Defaults.Overlays = [ [ 'PlainArrow', {
		location : 1,
		width : 8,
		length : 12
	} ] ];

};
var arrows_drawn = false;
var arrows_draw_script = "";

function drawArrows() {
	
}

$(document).ready(function() {
//	$('div[rel="popover"]').popover();
});
