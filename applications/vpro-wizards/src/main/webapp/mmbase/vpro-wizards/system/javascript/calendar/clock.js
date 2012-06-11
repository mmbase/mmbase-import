// Copyright 2006 Edwin Martin <edwin@bitstorm.nl>

var clock = {
currentId: false,
timer: null,
onchange: null,
mouseover: false,
create: function(min) {
    var ul = document.createElement("ul");
    ul.id = "clock";
    ul.onmouseover = clock.onMouseover;
    ul.onmouseout = clock.onMouseover;
    for (i=0; i < 24; i++) {
        for (m = 0; m < 60; m += min) {
            var li = document.createElement("li");
            ms = m < 10 ? "0"+m : m;
//			li.appendChild(document.createTextNode(i+":"+ms+":00"));
            li.appendChild(document.createTextNode(i+":"+ms));
            li.onclick = clock.onClick;
            li.onmouseover = clock.highlight;
            li.onmouseout = clock.highlight;
            ul.appendChild(li);
        }
    }
    document.body.appendChild(ul);
},
attach: function(id) {
    var element = document.getElementById(id);
    element.onfocus = clock.fieldFocus;
    element.onblur = clock.fieldFocus;
},
fieldFocus: function(evt) {
    var event = evt || window.event;
    var element = event.target || event.srcElement;
    clock.clockFocus = event.type == "focus";
    if (clock.clockFocus)
        clock.show(element.id);
    else if (!clock.mouseover)
        clock.hide();
},
show: function(field) {
    clock.disablePulldowns()
    var cal = document.getElementById("clock");
    var f = document.getElementById(field);
    cal.style.top = (clock.getTop(f)+f.offsetHeight)+'px'
    cal.style.left = (clock.getLeft(f))+'px';
    cal.style.width = f.offsetWidth+'px'
    cal.style.visibility = "visible";
    clock.currentId = field;
},
hide: function() {
    document.getElementById("clock").style.visibility = "hidden";
    clock.enablePulldowns()
    clock.currentId = false;
},
onMouseover: function(evt) {
    var event = evt || window.event;
    clock.mouseover = event.type == "mouseover";
    if (!clock.clockFocus)
        if (clock.mouseover)
            clearTimeout(clock.timer);
        else
            clock.timer = setTimeout("clock.hide()", 100);
},
onClick: function(evt) {
    var event = evt || window.event;
    var elt = event.target || event.srcElement;
    document.getElementById(clock.currentId).value = elt.firstChild.nodeValue;
    document.getElementById("clock").style.visibility = "hidden";
    clock.enablePulldowns()
    clock.currentId = false;
    if(calendar.onclick)
        calendar.onclick();
},
highlight: function(evt) {
    var event = evt || window.event;
    var elt = event.target || event.srcElement;
    if (event.type == "mouseover") {
        elt.className = "highlight";
    } else {
        elt.className = "";
    }
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