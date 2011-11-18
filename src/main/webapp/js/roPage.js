$(function() {
	$("#ro")
	.jstree(
			{
				"types" : {
					"types" : {
						"file" : {
							"icon" : {
								"image" : "theme1/file.gif"
							},
							"select_node" : true,
							"hover_node"   : true,
							"close_node"  : true,
							"create_node" : true,
							"delete_node" : true
						},
						"default" : {
							"valid_children" : [ "default" ]
						}
					}
				}, 
				"plugins" : [ "themes",
				              "html_data",
				              "types" ]

			}
	)
});
