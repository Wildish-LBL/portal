$(document).ready(function() {
	$('#busy-modal').modal({
		backdrop : 'static',
		show : false
	})
});

function showBusy() {
	$('#busy-modal').modal('show');
}

function hideBusy() {
	$('#busy-modal').modal('hide');
}

$(document).ready(function() {
	$('#signedInAs').tooltip();
});

/**
 * JS Object script used mainly in page layouts.
 */

var PageUtils = {

	init : function() {
		var that = this;
		$(".collapsible.hidden").each(function() {
			var conDiv = $(this).next();
			conDiv.hide();
			$(this).children(".sh_button").attr('src', 'images/l_close.gif');
		});
	},

	bindAccordionMechanism : function() {
		var that = this;
		$(document).on(
				"click",
				".collapsible",
				function() {
					var conDiv = $(this).next();
					if (conDiv.is(":hidden")) {
						conDiv.slideDown("fast", function() {
							conDiv.trigger("firstShow");
							conDiv.off("firstShow");
						});
						$(this).children(".sh_button").attr('src',
								'images/l_open.gif');
					} else {
						conDiv.slideUp("fast");
						$(this).children(".sh_button").attr('src',
								'images/l_close.gif');
					}
				});
	}
};

$(document).ready(function() {
	PageUtils.init();
	PageUtils.bindAccordionMechanism();
});
