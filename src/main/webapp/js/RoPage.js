$(document).ready(function() {
	$('#download-metadata-modal').modal({
		backdrop : 'static'
	})
});

$(document).ready(function() {
	$('#edit-ann-modal').modal({
		backdrop : 'static'
	})
});

function showStmtEdit(content) {
	$('#objectValue').wysiwyg('destroy');
	$('#edit-ann-modal').modal('show');
	$('#objectValue').wysiwyg(
					{
						css : 'http://twitter.github.com/bootstrap/assets/css/bootstrap-1.2.0.min.css'
					}).wysiwyg('setContent', content);
	document.getElementById('propertyURI').onchange = checkStmtPropertyType;
	document.getElementById('objectType').onclick = checkStmtEditType;
	checkStmtPropertyType();
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

function checkStmtPropertyType() {
	var type = document.getElementById('propertyURI');
	if (type.value == "") {
		document.getElementById('customPropertyURIDiv').style.display = "";
	} else {
		document.getElementById('customPropertyURIDiv').style.display = "none";
		document.getElementById('customPropertyURI').value = "";
	}
}
