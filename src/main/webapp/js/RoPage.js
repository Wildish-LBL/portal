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
	})
});

// this should be called when a tab is changed.
$('a[data-toggle="tab"]').on('shown', function (e) {
	  e.target // activated tab
	  e.relatedTarget // previous tab
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

function showRelEdit() {
	$('#edit-rel-modal').modal('show');
	document.getElementById('relationURI').onchange = checkStmtRelationType;
	checkStmtRelationType();
}

function checkStmtRelationType() {
	var type = document.getElementById('relationURI');
	if (type.value == "") {
		document.getElementById('customRelationURIDiv').style.display = "";
	} else {
		document.getElementById('customRelationURIDiv').style.display = "none";
		document.getElementById('customRelationURI').value = "";
	}
}