// Copyright 2005, 2006 Edwin Martin <edwin@bitstorm.nl>

var calendar = {
//months: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
months: ['jan', 'feb', 'maa', 'apr', 'mei', 'jun', 'jul', 'aug', 'sep', 'okt', 'nov', 'dec'],

//weekdays: ["M", "T", "W", "T", "F", "S", "S"],
weekdays: ["M", "D", "W", "D", "V", "Z", "Z"],

currentId: false,
currentDayId: false,
mouseover: false,
onchange: null,
create: function(format) {
	var table = document.createElement("table");
	calendar.format = format;
	table.id = "calendar";
	table.border = 0;
	table.cellPadding = 0;
	table.cellSpacing = 0;
	table.onmousewheel = calendar.wheel;
	table.onmouseover = calendar.onMouseover;
	table.onmouseout = calendar.onMouseover;
	table.onclick = calendar.onMouseover;
	var uitr = table.insertRow(0);
	uitr.onclick = calendar.uiClick;
	uitr.className = "ui";
	var uitd = uitr.insertCell(0);
	uitd.setAttribute("nowrap", "nowrap");
	uitd.style.whiteSpace = "nowrap";
	uitd.style.width = "11em";
	uitd.colSpan = 7;
	var monthinput = document.createElement("select");
	monthinput.id = "monthinput";
	monthinput.onblur = calendar.fieldBlur;
	monthinput.onclick = calendar.onMouseover;
	monthinput.onchange = calendar.changeMonth;
	for (var i = 1; i <= calendar.months.length; i++) {
		var mon = document.createElement("option");
		mon.appendChild(document.createTextNode(calendar.months[i-1]));
		monthinput.appendChild(mon);
	}
	uitd.appendChild(monthinput);
	var yearDec = document.createElement("button");
	yearDec.onclick = calendar.yearDecrease;
	yearDec.ondblclick = calendar.yearDecrease;
	yearDec.onblur = calendar.fieldBlur;
	yearDec.className = "counter";
	yearDec.appendChild(document.createTextNode("<"));
	uitd.appendChild(yearDec);
	var yearinput = document.createElement("input");
	yearinput.id = "yearinput";
	yearinput.onblur = calendar.fieldBlur;
	yearinput.setAttribute("type", "text");
	yearinput.setAttribute("size", "4");
	yearinput.setAttribute("value", "2005");
	yearinput.onkeyup = calendar.changeMonth;
	uitd.appendChild(yearinput);
	var yearInc = document.createElement("button");
	yearInc.onclick = calendar.yearIncrease;
	yearInc.ondblclick = calendar.yearIncrease;
	yearInc.onblur = calendar.fieldBlur;
	yearInc.className = "counter";
	yearInc.appendChild(document.createTextNode(">"));
	uitd.appendChild(yearInc);
	
	var wktr = table.insertRow(1);
	wktr.className = "week";
	for (var i = 0; i <= calendar.weekdays.length - 1; i++) {
		var wkday = wktr.insertCell(i);
		wkday.appendChild(document.createTextNode(calendar.weekdays[i]));
	}
	
	for (var w = 0; w < 6; w++) {
		var dtr = table.insertRow(2 + w);
		dtr.onmouseover = calendar.highlight;
		dtr.onmouseout = calendar.highlight;
		dtr.onclick = calendar.clicked;
		for (var d = 0; d < 7; d++) {
			var day  = dtr.insertCell(d);
			day.id = "d"+(w*7+d);
			day.appendChild(document.createTextNode(w*7+d));
		}
	}
	document.body.appendChild(table);
},
wheel: function(evt) {
	var event = evt || window.event;
	var month = document.getElementById("monthinput").selectedIndex + 1;
	var year = document.getElementById("yearinput").value;
	if (event.wheelDelta >= 120) {
		month++;
		if (month > 12 ) {
			month = 1;
			year++;
		}
	} else if (event.wheelDelta <= -120) {
		month--;   
		if (month < 1 ) {
			month = 12;
			year--;
		}
	}
	calendar.populate(month, year);
	return false;
},
yearIncrease: function(evt) {
	calendar.yearOff(1);
},
yearDecrease: function(evt) {
	calendar.yearOff(-1);
},
yearOff: function(d) {
	calendar.populate( document.getElementById("monthinput").selectedIndex + 1, Math.floor(document.getElementById("yearinput").value) + d);
},
changeMonth: function() {
	var month = document.getElementById("monthinput");
	var year = document.getElementById("yearinput");
	if (year.value.length == 4)
		calendar.populate(month.selectedIndex + 1, year.value);
},
populate: function(month, year) {
	document.getElementById("yearinput").value = year;
	document.getElementById("monthinput").selectedIndex = month - 1;
	var d = new Date(year, month - 1, 1);
	var wkday = (d.getDay() + 6) % 7;
	var dlast = new Date(year, month, -1);
	var monthdays = dlast.getDate() + 1;
	for (var i = 0; i < wkday; i++)
		document.getElementById("d" + i).firstChild.nodeValue = "";
	for (var i = 0; i < monthdays; i++)
		document.getElementById("d" + (i + wkday)).firstChild.nodeValue = i + 1;
	for (var i = monthdays + wkday; i <= 41; i++)
		document.getElementById("d" + i).firstChild.nodeValue = "";
	if (calendar.currentDayId)
		document.getElementById(calendar.currentDayId).className = "";
	if (month == calendar.month && year == calendar.year) {
		calendar.currentDayId = "d" + (Math.floor(calendar.day) + wkday - 1);
		document.getElementById(calendar.currentDayId).className = "current";
	}
},
highlight: function(evt) {
	var event = evt || window.event;
	var elt = event.target || event.srcElement;
	if (elt.firstChild.nodeValue != "") {
		if (event.type == "mouseover") {
			if (elt.className == "current")
				elt.className = "current highlight";
			else
				elt.className = "highlight";
		} else {
			if (elt.className == "current highlight")
				elt.className = "current";
			else
				elt.className = "";
		}
	}
},
clicked: function(evt) {
	var event = evt || window.event;
	var elt = event.target || event.srcElement;
	if (elt.firstChild.nodeValue != "") {
		var month = document.getElementById("monthinput");
		var year = document.getElementById("yearinput");
		var val;
		if (calendar.format == "dmy")
			val = elt.firstChild.nodeValue+"-"+(month.selectedIndex + 1)+"-"+year.value;
		else
			val = month.options[month.selectedIndex].value+"-"+elt.firstChild.nodeValue+"-"+year.value;
		document.getElementById(calendar.currentId).value = val;
	}
	document.getElementById("calendar").style.visibility = "hidden";
	calendar.enablePulldowns();
	calendar.currentId = false;
	if(calendar.onclick)
		calendar.onclick();
},
toggle: function(field) {
	if (calendar.currentId == field) {
		var cal = document.getElementById("calendar");
		cal.style.visibility = "hidden";
		calendar.currentId = false;
	} else {
		calendar.show(field);
		calendar.focused = true;
	}
},
show: function(field) {
	calendar.disablePulldowns();
	var cal = document.getElementById("calendar");
	var f = document.getElementById(field);
	var a = /([0-9]+)[-\/]([0-9]+)[-\/]([0-9]{4})/.exec(f.value);
	if (a) {
		if (calendar.format == "dmy") {
			calendar.day = a[1];
			calendar.month = a[2];
		} else {
			calendar.day = a[2];
			calendar.month = a[1];
		}
		calendar.year = a[3];
	} else {
		var d = new Date();
		calendar.day = d.getDate();
		calendar.month = d.getMonth();
		calendar.year = d.getFullYear();
	}
	calendar.populate(calendar.month, calendar.year);
	cal.style.top = (calendar.getTop(f)+f.offsetHeight)+'px';
	cal.style.left = calendar.getLeft(f)+'px';
	cal.style.visibility = "visible";
	calendar.currentId = field;
},
attach: function(id) {
	var element = document.getElementById(id);
	if (element) {
		element.onfocus = calendar.fieldFocus;
		element.onblur = calendar.fieldFocus;
		element.onkeyup = calendar.sync;
	}
},
onShow: function(evt) {
	var event = evt || window.event;
	var element = event.target || event.srcElement;
	calendar.show(element.id);
},
onHide: function(event) {
	document.getElementById("calendar").style.visibility = "hidden";
	calendar.enablePulldowns();
	calendar.currentId = false;
},
onMouseover: function(evt) {
	var event = evt || window.event;
	calendar.mouseover = event.type == "mouseover" || event.type == "click";
	if (!calendar.focused)
		if (calendar.mouseover)
			clearTimeout(calendar.timer);
		else
			calendar.timer = setTimeout("calendar.onHide()", 200);
},
fieldFocus: function(evt) {
	var event = evt || window.event;
	var element = event.target || event.srcElement;
	calendar.focused = event.type == "focus";
	if (calendar.focused)
		calendar.show(element.id);
	else if (!calendar.mouseover)
		calendar.onHide();
},
fieldBlur: function(evt) {
	if (!calendar.mouseover)
		calendar.onHide();
},
sync: function(evt) {
	calendar.show(calendar.currentId);
},
uiClick: function() {
	calendar.focused = true;
},
getTop: function(obj) {
	var top = 0;
	do {
		top += obj.offsetTop;
	} while (obj = obj.offsetParent);
	return top;
},
getLeft: function(obj) {
	var left = 0;
	do {
		left += obj.offsetLeft;
	} while (obj = obj.offsetParent);
	return left;
},
disablePulldowns: function() {
/*
	if (/MSIE/.exec(navigator.userAgent) && parseInt(navigator.appVersion) <= 6) {
		var pd = document.getElementsByTagName('select');
		for (var i=0; i<pd.length; i++)
			if (pd[i].id != 'monthinput')
				pd[i].style.visibility = 'hidden';
	}
	*/
},
enablePulldowns: function() {
/*
	if (/MSIE/.exec(navigator.userAgent) && parseInt(navigator.appVersion) <= 6) {
		var pd = document.getElementsByTagName('select');
		for (var i=0; i<pd.length; i++)
			if (pd[i].id != 'monthinput')
				pd[i].style.visibility = 'visible';
	}
	*/
}
}
