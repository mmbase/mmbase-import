var DHTML = (document.getElementById || document.all || document.layers);

function getObj(name) {
    if (document.getElementById) {
        this.obj = document.getElementById(name);
        this.style = document.getElementById(name).style;
    } else if (document.all) {
        this.obj = document.all[name];
        this.style = document.all[name].style;
    } else if (document.layers) {
        this.obj = document.layers[name];
        this.style = document.layers[name];
    }
}

function show(ding) {
    if (!DHTML) {
        return;
    }
    var x = new getObj(ding);
    x.style.display = "block";
}

function hide(ding) {
    if (!DHTML) {
        return;
    }
    var x = new getObj(ding);
    x.style.display = "none";
}

function hideshow(a,b) {
    hide(a);
    show(b);
}


function doConfirm(shouldConfirm, message){
    if(shouldConfirm == null || shouldConfirm == 'false'){
        return true;
    }else{
        return confirm(message);
    }
}

//no idea what this is for.
//TODO: sort it out
function checkSearch(atag) {
    url = atag.href;

    // Kijk of zoeken open of dichtstaat.
    if(document.getElementById('searchclosed').style.display == 'none') {
        url = atag.href + '&search=true';
    }

    // Kijk of er gepaged wordt
    oldurl = document.location.href;
    index = oldurl.indexOf('offset');

    if(index>-1 && atag.href.indexOf('offset')==-1) {
        oldurl = oldurl.substring(index);
        index2 = oldurl.indexOf('&');
        if(index2==-1) {
            offset = oldurl.substring(7);
        } else {
            offset = oldurl.substring(7,index2);
        }
        url = url+'&offset='+offset;
    }

    document.location = url;
    return false;
}


//no idea what this is for.
//TODO: sort it out
function showSearchResults(searchKeys) {
    var searchfields=searchKeys + '13123123123';
    var e=document.getElementsByName("searchresults");
    for(var i=0;i < e.length;i++) {
        e[i].innerHTML = e[i].innerHTML.replace(new RegExp('('+searchfields+')','i'),'<b>$1</b>');
    }
}

var relationsdisabled = false;
var disableRelatedCallbacks = [];

function registerDisableRelatedCallback(callback){
    disableRelatedCallbacks.push(callback);
}

function disableRelated() {
    // save & annuleerknop aan.
    relationsdisabled = true;
    var enableButtons = document.getElementById('enableButtons');
    enableButtons.style.display = 'none';

    var relations = document.getElementById('relations');
    var disableRelated = document.getElementById('disableRelated');
    if (relations != null) {
        disableRelated.style.top = relations.offsetTop+'px';
        disableRelated.style.left = relations.offsetLeft+'px';
        disableRelated.style.height = relations.offsetHeight+'px';
        disableRelated.style.width = relations.offsetWidth+'px';
    }


    //do the callbacks
    for(var func in disableRelatedCallbacks){
        f = disableRelatedCallbacks[func];
        f();
    }
}

function gettop(obj) {
    var top = 0;
    do {
        top += obj.offsetTop;
    } while (obj = obj.offsetParent);
    return top;
}

function getleft(obj) {
    var left = 0;
    do {
        left += obj.offsetLeft;
    } while (obj = obj.offsetParent);
    return left;
}