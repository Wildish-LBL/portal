function initRoEvo(instance) {
	instance.Defaults.Container = $("#evoroot");
	instance.Defaults.PaintStyle = {
		lineWidth : 2,
		strokeStyle : '#23A4FF'
	}

	instance.Defaults.Anchor = 'Continuous';
	instance.Defaults.Endpoint = 'Blank';
	instance.Defaults.Connector = 'Flowchart';
	instance.Defaults.Overlays = [ [ 'PlainArrow', {
		location : 1,
		width : 20,
		length : 12
	} ] ];

};
$(document).ready(function() {
	$('div[rel="popover"]').popover();
});
