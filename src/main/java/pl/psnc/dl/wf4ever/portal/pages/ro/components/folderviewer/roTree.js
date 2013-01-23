function test_tree_reload(wicketUrl) {
		$( "#sortable" ).sortable();
	    $( "#sortable" ).disableSelection();
	    
	    $( ".wicket-tree a" ).each(function(){
	 	   var resourceFolder = $(this);
	 	   $(this).droppable({
	 		   drop:function(event, ui_element) {
					console.log($(event.target).children(":first").attr("href"));
					console.log(resourceFolder.text())
					$.post(wicketUrl, {dupa:"dupa"})
				}
			});
	    });	    
}