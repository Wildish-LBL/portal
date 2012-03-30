$(document).ready(function() {
	$('#upload-resource-modal').modal({
		backdrop : 'static',
		show : false
	})
});

$(document).ready(function() {
	$('#download-metadata-modal').modal({
		backdrop : 'static',
		show : false
	})
});

$(document).ready(function() {
	$('#edit-ann-modal').modal({
		backdrop : 'static',
		show : false
	})
});

function showStmtEdit(content) {
	$('#objectValue').wysiwyg('destroy');
	$('#edit-ann-modal').modal('show');
	$('#objectValue').wysiwyg({
		css : 'css/bootstrap.min.css'
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
