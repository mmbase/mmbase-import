//********************************
// POPUP WINDOW STUFF
//********************************
// override showSearchScreen() from editwizard.jsp
function showSearchScreen(cmd, url) {
	showPopup(url);
}

function showPopup(url) {
	var left = (screen.width - 400)/2;
	var top = (screen.height - 400)/2;
	var windowPopup = window.open("","Search", "width=400,height=400,left=" + left + ",top=" + top + "scrollbars=yes,toolbar=no,status=yes,resizable=yes");
	try {
		windowPopup.document.writeln('<span>...searching...</span>');
	} catch (e) {
		windowPopup.close();
	}
   	windowPopup.document.location.replace(url);
}


// override doOnloadSearch() from search.js
function doOnloadSearch() {
    window.onresize = function(e){ resizeSelectTable(); };
//    centerWindow();
}

// override addItem(selected) from search.js
function addItem(selected) {
    opener.doAdd(selected, cmd);
}

// override closeSearch() from search.js
function closeSearch() {
    window.close();
}

function centerWindow() {
	var w = 0;
	var h = 0;
	if (document.all) {
		w = document.body.clientWidth;
		h = document.body.clientHeight;
	}
	else {
		if (document.layers) {
			w = window.innerWidth;
			h = window.innerHeight;
		}
	}
	if (window.moveTo) {
		window.moveTo((screen.width - w)/2,(screen.height - h)/2);
	}
}