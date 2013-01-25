function test_tree_reload(wicketUrl) {
		$( "#sortable" ).sortable();
	    $( "#sortable" ).disableSelection();
	    $( ".wicket-tree a" ).each(function(){
	 	   var resourceFolder = $(this);
	 	   $(this).droppable({
	 		   drop:function(event, ui_element) {
	 			  $("#shift-form-folder-uri").val($(event.target).children(":first").attr("href"));
	 			  $("#shift-form-file-uri").val(resourceFolder.text());
	 			  $("#hidden-files-shift-form").submit();	
				}
			});
	    });	    
}