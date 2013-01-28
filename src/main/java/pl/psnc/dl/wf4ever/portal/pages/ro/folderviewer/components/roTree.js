function test_tree_reload(wicketUrl) {
		$( "#sortable" ).sortable({ cursor: "move" });
	    $( "#sortable" ).disableSelection();
	    $( ".wicket-tree a" ).each(function(){
	 	   var resourceFolder = $(this);
	 	   $(this).droppable({
	 		   drop:function(event, ui_element) {
	 			  $("#shift-form-file-uri").val($(event.target).attr("uri"));
	 			  $("#shift-form-folder-uri").val(resourceFolder.parent().parent().attr('uri'));
	 			  $("#hidden-files-shift-form").submit();
				}
			});
	    });	    
}