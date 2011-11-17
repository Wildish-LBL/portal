/**
 * This is dLibra content modal jquery plugin which customize nyromodal.
 * 
 * @requires: dlibra.js, nyromodal.js, jquery-sizes.js
 */

jQuery( function($) {

	var cookiePrefix = 'dL';

	/**
	 * Creates content modal while referencing dLibra from Iframe
	 */
	jQuery.dL_externalContentModal = function(options) {
		var baseUrl = options.baseUrl;
		var modalUrl = options.url;
		var style = options.styleVariant;
		var isIE6 = jQuery.browser.msie && jQuery.browser.version == "6.0";
		var w = parentDocumentWidth();
		var h = parentDocumentHeight();
		if (isIE6) {
			var t = '50%';
		} else {
			h = h - 110;
			var t = '48%';
		}
		parent.jQuery.nyroModalManual( {
			width : w,
			height : h,
			bgColor : "#ddd",
			windowResize : false,
			url : modalUrl,
			css : {
				wrapper : {
					top : t
				}
			},
			endShowContent : function(elts, settings) {
				createWeb2Toolbar(true, baseUrl, style);
			},
			beforeHideContent : function(elts, settings, callback) {
				parent.$("#web2Tools").remove();
				callback();
			}
		});

	};

	function parentDocumentWidth() {

		return parent.window.innerWidth != null ? parent.window.innerWidth
				: parent.document.documentElement
						&& parent.document.documentElement.clientWidth ? parent.document.documentElement.clientWidth
						: document.body != null ? parent.document.body.clientWidth
								: null;

	}

	function parentDocumentHeight() {

		return parent.window.innerHeight != null ? parent.window.innerHeight
				: parent.document.documentElement
						&& parent.document.documentElement.clientHeight ? parent.document.documentElement.clientHeight
						: document.body != null ? parent.document.body.clientHeight
								: null;

	}

	var createWeb2Toolbar = function(includeStyle, baseUrl, styleVariant) {
		var web2ToolsCss = {};
		var web2TogglerOffBack = {};
		var web2TogglerOnBack = {};
		var web2TogglerCss = {};
		if (includeStyle) {
			var web2ToolsCss = {
				'display' : 'none',
				'width' : '100%',
				'height' : '51px',
				'position' : 'fixed',
				'bottom' : '0',
				'left' : '0',
				'background' : 'url(\"' + baseUrl + '/style/dlibra/'
						+ styleVariant
						+ '/w2tools-background.png\") top left repeat-x',
				'z-index' : '4000',
				'padding-top' : '10px'
			};
		}
		var body = parent.document.getElementsByTagName("body")[0];
		$(body).append("<div id='web2Tools'></div>");
		parent.$("#web2Tools").css(web2ToolsCss);
	};

	jQuery.fn.dL_contentModal = function(options) {

		if (options.cookiePrefix) {
			cookiePrefix = options.cookiePrefix;
		}

		var dLSettings = getDefaultSettings();

		var isIE6 = jQuery.browser.msie && jQuery.browser.version == "6.0";
		var width = parentDocumentWidth();
		var height = parentDocumentHeight();
		if (isIE6) {
			t = '50%';
		} else {
			height = height - 110;
			t = '48%';
		}

		if (typeof options.showGallery != 'undefined'
				&& options.showGallery === false) {
			dLSettings.showGallery = false;
		}
		if (options.dLTitle) {
			dLSettings.dLTitle = options.dLTitle;
		}
		dLSettings.width = width;
		dLSettings.height = height;
		dLSettings.bgColor = "#ddd";
		dLSettings.titleFromIframe = 'false';
		dLSettings.processHandler = forceIframeProcessHandler;
		dLSettings.windowResize = false;
		dLSettings.css = {
			wrapper : {
				top : t
			}
		};
		return this.nyroModal(dLSettings);
	}

	/** This function creates modal dialog with login type selection*/
	jQuery.fn.dL_simpleModal = function(options) {

		var dLSettings = getDefaultSettings();

		var maxWidth = $("document").maxSize().width;
		if (maxWidth > 600)
			maxWidth = 600;
		dLSettings.width = maxWidth;
		dLSettings.bgColor = "#ddd";
		dLSettings.web2ToolbarOff = true;
		return this.nyroModal(dLSettings);
	}

	jQuery.fn.dL_smallModal = function(options) {

		var dLSettings = getDefaultSettings();

		var maxWidth = $("document").maxSize().width;
		if (maxWidth > 600)
			maxWidth = 600;
		dLSettings.width = maxWidth;
		dLSettings.bgColor = "#ddd";
		dLSettings.minWidth = 420;
		dLSettings.minHeight = 250;
		dLSettings.endShowContent = refreshTitle;
		if (typeof options != 'undefined') {
			dLSettings.dLTitle = options.dLTitle;
		}
		return this.nyroModal(dLSettings);

	}

	function getDefaultSettings() {
		return {
			endFillContent : customizeBackground,
			endShowContent : customizeContentModal,
			beforeHideContent : hideCustomBackground
		};
	}

	function forceIframeProcessHandler(settings) {
		settings.type = "iframe";
		// autoresize should be turned off
		settings.autoSizable = false;
	}

	function hideCustomBackground(elts, settings, callback) {
		$("#web2Tools").remove();
		$("#dLNavigation").css( {
			'visibility' : 'hidden'
		});
		callback();
	}

	// use title mechanism other than nyromodal
	function refreshTitle(elts, settings) {
		var titleCont = $("#nyroModalTitle");
		var niframe = document.getElementById("nyroModalIframe");
		// title for other things than content
		if (niframe == null || typeof niframe == 'undefined') {
			tparent = elts.contentWrapper;
			if (typeof settings.dLTitle != 'undefined'
					&& settings.dLTitle != "") {
				tparent.append("<h1 id='dL_nyroModalTitle'>"
						+ shorter(settings.dLTitle) + "</h1>");
			}
			return;
		}
		var iframeTitle = niframe.contentWindow.document.title;
		var that = niframe;
		// retrieve title from nyromodal gallery generators
		var aSelection = $("a[rel='gal']");
		var ai = aSelection.index(settings.from) + 1;
		var Ni = aSelection.size();
		for ( var i = 0; i < aSelection.length; i++) {
			var isrc = $(that).attr('src');
			// ! depends on nyromodal implementation
			if (isrc.indexOf("javascript:") != -1) {
				isrc = settings.from;
			}
			var asrc = $(aSelection[i]).attr('href');
			if (isrc == asrc) {
				var atitle = $(aSelection[i]).attr("title");
				if (atitle.lastIndexOf("-") > 0) {
					atitle = atitle.substring(0, atitle.lastIndexOf("-"));
				}
				if (PageUtils.pageId == "docmetadata"
						|| PageUtils.pageId == "publication") {
					atitle += " - " + ai + "/" + Ni;
				}
				iframeTitle = atitle;
			}
		}
		var tparent = elts.contentWrapper;
		if (typeof iframeTitle != 'undefined' && iframeTitle != null
				&& iframeTitle != "") {
			tparent.append("<h1 id='dL_nyroModalTitle'>" + shorter(iframeTitle)
					+ "</h1>");
		} else {
			var dtitle;
			if (settings.dLTitle) {
				dtitle = settings.dLTitle;
			} else {
				dtitle = document.title;
				dtitle = dtitle.substring(0, dtitle.lastIndexOf("-"));
			}
			tparent.append("<h1 id='dL_nyroModalTitle'>" + shorter(dtitle)
					+ "</h1>");

		}
	}

	function shorter(text) {
		if (typeof PageUtils != 'undefined') {
			return PageUtils.shorterString(text, 70);
		} else {
			return text;
		}
	}

	function customizeBackground(elts, settings) {
		elts.bg
				.css( {
					'background' : 'url("' + homePageUrl + '/style/common/js/nyromodal/nyro-background.gif")'
				});
		$("#nyroModalIframe").css({
			'height' : '100%'
		});
		if (jQuery.browser.msie) {
			elts.bg.css( {
				'height' : document.documentElement.offsetHeight + 'px'
			});
		}
	}

	function customizeContentModal(elts, settings) {
		$("#dLNavigation").remove();
		$("#web2Tools").remove();

		refreshTitle(elts, settings);
		var iconsPath = homePageUrl + '/style/common/img/icons';

		if (this.gallery != null && this.showGallery != false) {
			var navigation = '<div id="dLNavigation">';

			var prevLink = getGalleryLink(settings, -1);
			if (prevLink) {
				navigation += '<a href="' + prevLink.attr('href')
						+ '" id="prevPub">' + '<img src="' + iconsPath
						+ '/go-prev.png' + '" alt="&lt;"/></a>';
			}

			navigation += '<img src="' + iconsPath + '/content.gif' + '" alt="."/>'

			var nextLink = getGalleryLink(settings, 1);
			if (nextLink) {
				navigation += '<a href="' + nextLink.attr('href')
						+ '" id="nextPub">' + '<img src="' + iconsPath
						+ '/go-next.png' + '" alt="&gt;"/></a>';
			}
			navigation += '</div>';

			var jqnavigation = $(navigation);
			jqnavigation.insertBefore(elts.contentWrapper);

			$("#nextPub").click( function(e) {
				$.nyroModalNext();
				return false;
			});

			$("#prevPub").click( function(e) {
				$.nyroModalPrev();
				return false;
			});

		}

		// web 2.0 toolbar
		if (settings.web2ToolbarOff != true) {
			createWeb2Toolbar(false, null, null);
		}

	}

	//taken from jquery-nyromodal-1.4.2
	function getGalleryLink(settings, dir) {
		if (settings.gallery) {
			if (!settings.ltr)
				dir *= -1;
			// next
			var gallery = $('[rel="' + settings.gallery + '"]');
			var currentIndex = gallery.index(settings.from);
			var gallength = gallery.length;
			var index = currentIndex + dir;
			if ((PageUtils.pageId == "results" || PageUtils.pageId == "aresults")
					&& mainServletName.indexOf(DLApp.DM_SERVLET()) != -1) {
				var Ni = gallery.length / 2;
				if (currentIndex >= Ni) {
					index = (currentIndex + dir) - Ni;
					gallength = Ni;
				} else {
					gallength = Ni;
				}
			}
			if (index >= 0 && index < gallength)
				return gallery.eq(index);
		}
		return false;
	}

});