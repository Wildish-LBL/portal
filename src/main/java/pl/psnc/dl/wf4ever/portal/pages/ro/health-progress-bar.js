$(function() {
	$("#health-progress-bar").progressbar();
});

function setValue(v) {
	$("#health-progress-bar").progressbar({
		value : v
	});
	$("#health-progress-bar-main-container").popover({
		html : true,
		placement : 'bottom'
	});
}
