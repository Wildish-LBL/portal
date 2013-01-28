function test_tree_reload() {
	    $( ".wicket-tree  a" ).each(function(){
		console.log(resourceFolder);
	 	   var resourceFolder = $(this);
	 	   resourceFolder.addClass("cosik"); 
	 	   $(this).droppable({
	 		   drop:function(event, ui_element) {
	 			  $("#shift-form-file-uri").val($(event.target).attr("uri"));
	 			  $("#shift-form-folder-uri").val(resourceFolder.parent().parent().attr('uri'));
	 			  $("#hidden-files-shift-form-submit-link").click();
				}
			});
	    });	    
}

function test_tree_reload_sortable() {
	$( "#sortable" ).sortable({ cursor: "move" });
    $( "#sortable" ).disableSelection();
}