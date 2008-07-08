function changeImages() {
	if (document.images) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
		}
	}
}

function changeDoubleImages() {
	if (document.images) {
			document[changeDoubleImages.arguments[0]].src = changeDoubleImages.arguments[1];
	}
	if(parent.nav.document.images) {
			parent.nav.document[changeDoubleImages.arguments[2]].src = changeDoubleImages.arguments[3];
	}
}
