$(document).ready(function() {
	$('#busy-modal').modal({
		backdrop : 'static',
		show : false
	})
});

function showBusy() {
	$('#busy-modal').modal('show');
}

function hideBusy() {
	$('#busy-modal').modal('hide');
}

$(document).ready(function() {
	$('#signedInAs').tooltip();
});