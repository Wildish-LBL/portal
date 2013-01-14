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

$(document).ready(function() {
	$('#edit-rel-modal').modal({
		backdrop : 'static',
		show : false
	})
});

$(document).ready(function() {
	$('#import-annotation-modal').modal({
		backdrop : 'static',
		show : false
	});
});
	
function showStmtEdit(content) {
	$('#objectValue').wysiwyg('destroy');
	$('#edit-ann-modal').modal('show');
	$('#objectValue').wysiwyg({
		css : 'css/bootstrap.min.css'
	}).wysiwyg('setContent', content);
	$('#propertyURI').onchange = checkStmtPropertyType;
	$('#objectType').onclick = checkStmtEditType;
	checkStmtPropertyType();
	checkStmtEditType();
}

function checkStmtEditType() {
	var type = document.getElementById('objectType');
	if (type.checked) {
		$('#objectValueDiv').style.display = "none";
		$('#objectURIDiv').style.display = "";
	} else {
		$('#objectValueDiv').style.display = "";
		$('#objectURIDiv').style.display = "none";
	}
}

function checkStmtPropertyType() {
	var type = document.getElementById('propertyURI');
	if (type.value == "") {
		$('#customPropertyURIDiv').style.display = "";
	} else {
		$('#customPropertyURIDiv').style.display = "none";
		$('#customPropertyURI').value = "";
	}
}

function showRelEdit() {
	$('#edit-rel-modal').modal('show');
	$('#relationURI').onchange = checkStmtRelationType;
	checkStmtRelationType();
}

function checkStmtRelationType() {
	var type = document.getElementById('relationURI');
	if (type.value == "") {
		$('#customRelationURIDiv').style.display = "";
	} else {
		$('#customRelationURIDiv').style.display = "none";
		$('#customRelationURI').value = "";
	}
}