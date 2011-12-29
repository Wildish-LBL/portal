$(document).ready(function() {
	$('#edit-ann-modal').modal({
		backdrop : 'static'
	})
});

$(document).ready(function() {
});

function showStmtEdit(content) {
	$('#objectValue').wysiwyg('destroy');
	$('#edit-ann-modal').modal('show');
	$('#objectValue').wysiwyg(
					{
						css : 'http://twitter.github.com/bootstrap/assets/css/bootstrap-1.2.0.min.css'
					}).wysiwyg('setContent', content);
	document.getElementById('objectType').onclick = checkStmtEditType;
	checkStmtEditType();
}

function checkStmtEditType() {
	var type = document.getElementById('objectType');
	if (type.checked) {
		document.getElementById('objectValueDiv').style.display = "none";
		document.getElementById('objectURIDiv').style.display = "";
	} else {
		document.getElementById('objectValueDiv').style.display = "";
		document.getElementById('objectURIDiv').style.display = "none";
	}
}
