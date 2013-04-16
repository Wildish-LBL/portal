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
	$('#propertyURI').on("change", checkStmtPropertyType);
	$('#objectType').on("click", checkStmtEditType);
	checkStmtPropertyType();
	checkStmtEditType();
}

function checkStmtEditType() {
	var type = document.getElementById('objectType');
	if (type.checked) {
		$('#objectValueDiv').css("display", "none");
		$('#objectURIDiv').css("display", "");
	} else {
		$('#objectValueDiv').css("display", "");
		$('#objectURIDiv').css("display", "none");
	}
}

function checkStmtPropertyType() {
	var type = document.getElementById('propertyURI');
	if (type.value == "") {
		$('#customPropertyURIDiv').css("display", "");
	} else {
		$('#customPropertyURIDiv').css("display", "none");
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
		$('#customRelationURIDiv').css("display", "");
	} else {
		$('#customRelationURIDiv').css("display", "none");
		$('#customRelationURI').value = "";
	}
}