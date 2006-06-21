var selected;
function selectMenu(name) {

	var cnavElement = document.getElementById('cnav');
	if (cnavElement) {
		var tabs = cnavElement.getElementsByTagName('div');
		var numberOfTabs = tabs.length;

		var element = document.getElementById(name);
		element.className = 'active';
		if(selected != undefined && selected != name) {
			element = document.getElementById(selected);
			element.className = '';
		}
		for(var i=0; i < numberOfTabs; i++) {
			var unselected = tabs[i];
			if(unselected && unselected.getAttribute('id') != name) {
				unselected.className = '';
			}
		}
		selected = name;

	}
}

function initMenu() {
	var cnavElement = document.getElementById('cnav');
	if (cnavElement) {
		var tabs = cnavElement.getElementsByTagName('div');
		var numberOfTabs = tabs.length;

		var menuFrame = parent.frames[1];
		if (menuFrame == undefined) {
			menuFrame = document;
		}
		var framehref = menuFrame.location.href;

		var tabFound = false;
		for(var i=0; i < numberOfTabs; i++) {
			var tab = tabs[i];
			var ahref = tab.getElementsByTagName('a')[0].href;
			if (framehref.indexOf(ahref) != -1) {
			   selectMenu(tab.getAttribute('id'));
			   tabFound = true;
			   break;
			}
		}
		if (!tabFound) {
			var tab = tabs[0];
			selectMenu(tab.getAttribute('id'));
		}
	}
	else {
		alert('tab div not found');
	}	
}
