function handleBbCode(evt)
{
	var e = evt || window.event;
	var thisKey = e.which || e.keyCode;

	var ch = String.fromCharCode(thisKey).toLowerCase();
	
	if (e.altKey && ch == "b") {
		bbstyle(0);
	}
	else if (e.altKey && ch == "i") {
		bbstyle(2);
	}
	else if (e.altKey && ch == "u") {
		bbstyle(4);
	}
	else if (e.altKey && ch == "q") {
		bbstyle(6);
	}
	else if (e.altKey && ch == "c") {
		bbstyle(8);
	}
	else if (e.altKey && ch == "l") {
		bbstyle(10);
	}
	else if (e.altKey && ch == "p") {
		bbstyle(12);
	}
	else if (e.altKey && ch == "w") {
		bbstyle(14);
	}
}

function enterText(field)
{
	storeCaret(field);
	document.onkeydown = handleBbCode;
}

function leaveText()
{
	document.onkeydown = null;
}