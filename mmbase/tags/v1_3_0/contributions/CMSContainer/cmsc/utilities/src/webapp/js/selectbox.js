//---------------- Selectbox stuff ---------------
function one2two(select1, select2, sortByText) {
	select1 = getElement(select1);
	select2 = getElement(select2);
	moveSelectedOptions(select1, select2);
	if (sortByText) {
		optionSortByText(select2.options);	
	}
	else {
		optionSortByValue(select2.options);
	}
}

function two2one(select1, select2, sortByText) {
	select1 = getElement(select1);
	select2 = getElement(select2);
	moveSelectedOptions(select2, select1);
	if (sortByText) {
		optionSortByText(select1.options);	
	}
	else {
		optionSortByValue(select1.options);
	}
}

// function for switching options from one select box to the other 

function moveSelectedOptions(select1, select2) {
	select1 = getElement(select1);
	select2 = getElement(select2);
	select1len = select1.options.length ;
    for ( i=0; i<select1len ; i++){
        if (select1.options[i].selected == true ) {
            select2len = select2.options.length;
            select2.options[select2len] = new Option(select1.options[i].text, select1.options[i].value);
            select2.options[select2len].selected=true;
        }
    }
    for ( i = (select1len -1); i>=0; i--){
        if (select1.options[i].selected == true ) {
            select1.options[i] = null;
        }
    }
}

function moveUp(select) {
	select = getElement(select);
	selectlen = select.options.length ;
	for ( i=1; i < selectlen ; i++){
		if (select.options[i].selected == true ) {
			flipOptions(select.options[i-1], select.options[i]);
		}
	}
}

function moveDown(select) {
	select = getElement(select);
	selectlen = select.options.length ;
	for ( i=0; i < selectlen-1 ; i++){
		if (select.options[i].selected == true ) {
			flipOptions(select.options[i+1], select.options[i]);
		}
	}
}

// functions for sorting options

function optionSortByValue(arry) {
	start = 0;
	stop = arry.length - 1;
	for (k = start; k <= stop; k++) {
		for (l = k + 1; l <= stop; l++) {
			if (arry[k].value > arry[l].value) {
				flipOptions(arry[k], arry[l]);
			}
		}
	}
}

function optionSortByText(arry) {
	start = 0;
	stop = arry.length - 1;
	for (k = start; k <= stop; k++) {
		for (l = k + 1; l <= stop; l++) {
			if (arry[k].text > arry[l].text) {
				flipOptions(arry[k], arry[l]);
			}
		}
	}
}

function flipOptions(option1, option2) {
	var value = option1.value;
	var text = option1.text;
	var selected = option1.selected;
	option1.value = option2.value;
	option1.text = option2.text;
	option1.selected = option2.selected;
	option2.value = value;
	option2.text = text;
	option2.selected = selected;
}

// functions for selecting options or not

function selectAll(a) {
	a = getElement(a);
    for (i = 0; i < a.length; i++){
		a.options[i].selected = true;
    }
}

function deSelectAll(a) {
	a = getElement(a);
    for (i = 0; i < a.length; i++){
		a.options[i].selected = false;
    }
}

function getElement(element) {
    if (typeof element == 'string')
      element = document.getElementById(element);
    return element;
}

function selectboxesOnSubmit(select1, select2) {
	deSelectAll(select1);
	selectAll(select2);
	return true;
}