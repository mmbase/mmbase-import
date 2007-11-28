function moveUp() {
	var select = document.forms[0].channels;
	var elementToMove = select.selectedIndex;
	if (elementToMove>0) {
		swap(elementToMove,elementToMove-1);
		select.selectedIndex--;
		fillHidden();
	}
}
function moveDown() {
	var select = document.forms[0].channels;
	var elementToMove = select.selectedIndex;
	if (elementToMove!=-1) {
		if (elementToMove<document.forms[0].channels.options.length-1) {
			swap(elementToMove+1,elementToMove);
			select.selectedIndex++;
			fillHidden();
		}
	}
}

function swap(index1, index2) {
	var select = document.forms[0].channels;
	var options = select.options;

	var oldValue = values[index2];
	values[index2]=values[index1];
	values[index1]=oldValue;
	var t = options[index1].text;
	options[index1].text = options[index2].text;
	options[index2].text = t;
}

function fillHidden() {
	var value="";
	for (var i=0;i<values.length;i++) { 
		value = value +  values[i];
		if (i<values.length-1) {
			value=value+",";
		}
	}
	document.forms[0].ids.value=value;
}