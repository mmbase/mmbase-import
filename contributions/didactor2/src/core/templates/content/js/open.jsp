// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><mm:content type="text/javascript" expires="300" postprocessor="none">
    <mm:cloud>

var ITEM_NONE   = '${mm:treefile("/gfx/icon_arrow_tab_none.gif", pageContext, includePath)}';
var ITEM_OPENED = '${mm:treefile("/gfx/icon_arrow_tab_open.gif", pageContext, includePath)}';
var ITEM_CLOSED = '${mm:treefile("/gfx/icon_arrow_tab_closed.gif", pageContext, includePath)}';

var may_open_future =
    <di:hasrole role="coach">true || //coach</di:hasrole>
    <di:hasrole role="teacher">true || // teacher</di:hasrole>
    <di:hasrole role="systemadministrator">true || //system administrator </di:hasrole>
    <di:getsetting component="core" setting="may_open_future" />; // may_open_future setting

// IE does not even support indexOf, fixing that here..
[].indexOf || (Array.prototype.indexOf = function(v,n){
  n = (n==null)?0:n; var m = this.length;
  for(var i = n; i < m; i++)
    if(this[i] == v)
       return i;
  return -1;
});


var currentnumber = -1;
var contenttype   = [];
var contentnumber = [];
var openDivs      = {};
var enabledPopups = false;


// legacy
function resize() {
    if(browserVersion()[0] == "IE") {
        var oBody = content.document.body;
        var oFrame = document.all("content");
        oFrame.style.height = oBody.scrollHeight + 280;
    } else {
        var frameElem = document.getElementById("content");
        frameElem.style.overflow = "";
        var frameContentHeight = frameElem.contentWindow.parent.document.body.scrollHeight;
        frameElem.style.height = frameContentHeight + 80;
        frameElem.height = frameContentHeight + 80;
        frameElem.style.overflow = "hidden";
        //alert("set height to " + (frameContentHeight + 80));
    }
}

/**
 * @deprecated
 */
function openContent(type, number, el) {
    didactor.openContent(type, number, el);
}

/**
 * Don't know where this is for.
 */
function addContent( type, number ) {
    contenttype[contenttype.length] = type;
    contentnumber[contentnumber.length] = number;
    if ( contentnumber.length == 1 ) {
        currentnumber = contentnumber[0];
    }
}


/**
 * Navigates to the next content. Should be moved to Didactor.js
 */
function nextContent() {
    for(var count = 0; count <= contentnumber.length; count++) {
        if ( contentnumber[count] == currentnumber ) {
            if ( count < contentnumber.length ) {
                if ("tests" == contenttype[count]) {
                    alert('<di:translate key="education.testalert" escape="js-single-quotes,java" />');
                    return;
                }
                var opentype = contenttype[count+1];
                var opennumber = contentnumber[count+1];
            }
        }
    }
    didactor.openContent(opentype, opennumber);
    openOnly('div' + opennumber, 'img' + opennumber);
}

/**
 * Navigates to the previous content. Should be moved to Didactor.js
 */
function previousContent() {
    for(var count = 0; count <= contentnumber.length; count++) {
        if ( contentnumber[count] == currentnumber ) {
            if ( count > 0 ) {
                if ("tests" == contenttype[count]) {
                    alert('<di:translate key="education.testalert" escape="js-single-quotes,java" />');
                    return;
                }
                var opentype = contenttype[count-1];
                var opennumber = contentnumber[count-1];
            }
        }
    }
    didactor.openContent(opentype, opennumber);
    openOnly('div' + opennumber, 'img' + opennumber);
}


/**
 * Don't know why this is usefull
 */

function invalidateCurrentFrame() {
    usedFrames[document.href_frame] = null;
}


/**
 * @deprecated
 */
function loadIconOn() {
    didactor.loadIconOn();
}
/**
 * @deprecated
 */
function loadIconOff() {
    didactor.loadIconOff();
}
/**
 *  No idea when and why you'd want to use this
 */
function disablePopups() {
    if (enabledPopups) {
        enabledPopups = false;
        var popups = $(document).find(".popup");
        for(var i = 0; i < popups.length; i++) {
            popups[i].style.display = "none";
        }
    }

}

/**
 *  No idea when and why you'd want to use this
 */
function enablePopups() {
    if (! enabledPopups) {
        enabledPopups = true;
        var popups = $(document).find(".popup");
        for(var i = 0; i < popups.length; i++) {
            popups[i].style.display = "inline";
        }
    }

}
/**
 * check what?
 */
function check(className) {
    if (/\btests\b/.test(className)) {
        disablePopups();
    } else {
        enablePopups();
    }
}



function postContent(href, form, async) {
    loadIconOn();
    var params = {};
    $(form).find("textarea").each(function() {
            params[this.name] = this.value;
        });
    $(form).find("input").each(function() {
            if (this.type == "checkbox" && ! this.checked) {
            } else {
                params[this.name] = this.value;
            }
        });

    var a = async == null ? true : async;

    $.ajax({url: href, type: "POST", async: a, dataType: "xml", data: params,
                complete: function(res, status) {
                if (status == "success") {
                    var contentEl = document.getElementById('contentFrame');
                    $(contentEl).empty();
                    $(contentEl).append(res.responseText);
                    afterPost();
                    usedFrames[document.href_frame] = null;
                    document.href_frame = href;
                    //console.log("Found result " + contentEl);
                    usedFrames[href] = contentEl.childNodes;
                } else {
                    alert(status);
                }
            }
        });


    //console.log("posting " + content);
    //scrollToTop();
}


function openClose(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);

    if (realdiv != null) {
        try {
            var o = openDivs[div];
            if (o != null) {
                openDivs[div] = null;
                realdiv.style.display = "none";
                realimg.src = ITEM_CLOSED;
            } else {
                if (! may_open_future) {
                    if (/\bnon_completed\b/.test(realimg.parentNode.className)) {
                        alert('<di:translate key="education.future" escape="js-single-quotes,java" />');
                        return false;
                    }
                }
                if (/\bblocked\b/.test(realimg.parentNode.className)) {
                    alert('<di:translate key="education.future" escape="js-single-quotes,java" />');
                    return false;
                }
                openDivs[div] = img;
                realdiv.style.display = "block";
                realimg.src = ITEM_OPENED;
            }
        } catch (ex) {
            alert(ex);
        }
    }
    return true;
}

function openOnly(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);
    // alert("openOnly("+div+","+img+"); - "+realdiv);
    if (realdiv != null) {
        try {
            if (! may_open_future) {
                if (/\bnon_completed\b/.test(realimg.parentNode.className)) {
                    alert('<di:translate key="education.future" escape="js-single-quotes,java" />');
                    return false;
                }
            }
            if (/\bblocked\b/.test(realimg.parentNode.className)) {
                alert('<di:translate key="education.future" escape="js-single-quotes,java" />');
                return false;
            }
            openDivs[div] = img;
            realdiv.style.display = "block";
            realimg.src = ITEM_OPENED;

            var className = realdiv.className;
            if (className) {
                // ignore "lbLevel" in classname to get the level depth
                var level = className.substring(7, className.length);
                // alert("level = "+level);
                var findparent = realdiv;
                var findparentClass = className;


                if (level > 1) {
                    // also open parents
                    do {
                        findparent = findparent.parentNode;
                        findparentClass = findparent.className || "";
                    } while (findparent && findparentClass.indexOf("lbLevel") != 0);

                    if (findparent) {
                        var divid = findparent.id;
                        var imgid = "img" + divid.substring(3,divid.length);
                        openOnly(divid, imgid);
                    }
                }


            }
        } catch (ex) {
            alert(ex);
        }
    } else { // find enclosing div
        try {
            var finddiv = realimg;
            while (finddiv != null && (! finddiv.className || finddiv.className.substring(0,7) != "lbLevel")) {
                finddiv = finddiv.parentNode;
                // if (finddiv.className) alert(finddiv.className.substring(0,7));
            }
            if (finddiv != null) {
                var divid = finddiv.id;
                var imgid = "img" + divid.substring(3,divid.length);
                openOnly(divid,imgid);
            }
        } catch (ex) {
            alert(ex);
        }
    }

    return true;
}

function closeAll() {
    var divs = document.getElementsByTagName("div");
    for (i=0; i<divs.length; i++) {
        var div = divs[i];
        var cl = "" + div.className;
        if (cl.match("lbLevel")) {
            divs[i].style.display = "none";
        }
    }
    removeButtons();
}
function closeAppropriate() {
    var divs = document.getElementsByTagName("div");
    for (i=0; i<divs.length; i++) {
        var div = divs[i];
        var cl = "" + div.className;
        if (cl.match("lbLevel")) {
            if (openDivs[div.id] == null) {
                divs[i].style.display = "none";
            }
        }
    }
    removeButtons();
}

function removeButtons() {
    // Remove all the buttons in front of divs that have no children
    var imgs = document.getElementsByTagName("img");
    for (i=0; i<imgs.length; i++) {
        var img = imgs[i];
        if (/\bimgClosed\b/.test(img.className)) {
            var id = img.id;
            var divid = "div" + img.id.substring(3);
            var div = document.getElementById(divid);
            if (div == null || div.childNodes.length == 1) {
                img.src = ITEM_NONE;
            }
        }
    }
}

function afterPost() {
    <mm:hasnode number="component.progress">
        reloadProgress();
    </mm:hasnode>
    reloadEducationTree();
    scrollToTop();
    loadIconOff();
}

<mm:treefile page="/education/tree.jspx" objectlist="$includePath" referids="$referids" write="false" escapeamps="false">
    function reloadEducationTree() {
        usedFrames    = new Object();
        //console.log("Updating '" + document.getElementById('education-tree') + " with url ${_}");
        $("education-tree").load('${_}');

    }
</mm:treefile>

function scrollToTop() {
    var fromElement =  document.getElementById("rows");
    if (fromElement != null) {
        var scroll = fromElement.offsetTop;
        if (document.documentElement && document.documentElement.scrollTop) {
            document.documentElement.scrollTop = scroll;
        }
        if (document.body && document.body.scrollTop) {
            document.body.scrollTop = scroll;
        }
    }
}




</mm:cloud>
</mm:content>
