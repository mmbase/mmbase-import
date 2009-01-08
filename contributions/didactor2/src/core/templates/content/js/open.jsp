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
var currentel = -1;
var contenttype   = new Array();
var contentnumber = new Array();
var openDivs      = new Object();
var usedFrames    = new Object();
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


function addContent( type, number ) {
    contenttype[contenttype.length] = type;
    contentnumber[contentnumber.length] = number;
    if ( contentnumber.length == 1 ) {
        currentnumber = contentnumber[0];
    }
}

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
    openContent(opentype, opennumber);
    openOnly('div' + opennumber, 'img' + opennumber);
}

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
    openContent(opentype, opennumber);
    openOnly('div' + opennumber, 'img' + opennumber);
}


function invalidateCurrentFrame() {
    usedFrames[document.href_frame] = null;
}


function loadIconOn() {
    var ajax = document.getElementById("ajax_loader");
    if (ajax) ajax.style.display = "inline";
}
function loadIconOff() {
    var ajax = document.getElementById("ajax_loader");
    if (ajax) ajax.style.display = "none";
}

function disablePopups() {
    if (enabledPopups) {
        enabledPopups = false;
        var popups = $(document).find(".popup");
        for(var i = 0; i < popups.length; i++) {
            popups[i].style.display = "none";
        }
    }

}

function enablePopups() {
    if (! enabledPopups) {
        enabledPopups = true;
        var popups = $(document).find(".popup");
        for(var i = 0; i < popups.length; i++) {
            popups[i].style.display = "inline";
        }
    }

}

function check(className) {
    if (/\btests\b/.test(className)) {
        disablePopups();
    } else {
        enablePopups();
    }
}

function requestContent(href, number) {
    var contentEl = document.getElementById('contentFrame');
    $(document).trigger("didactorContentBeforeUnload",  { unloaded: contentEl });
    var content = usedFrames[href];
    if (content == null) {
        loadIconOn();
        $.ajax({async: true, url: href, type: "GET", dataType: "xml", data: null,
                    complete: function(res, status){
                    loadIconOff();
                    if (status == "success") {
                        $(contentEl).empty();
                        $(document).trigger("didactorContentBeforeLoaded",  { response: res, number: number });
                        $(contentEl).append(res.responseText);
                        // console.log("updating " + contentEl + "with" + xmlhttp.responseXML);
                        contentEl.validator = new MMBaseValidator();
                        //contentEl.validator.logEnabled = true;
                        //contentEl.validator.traceEnabled = true;
                        contentEl.validator.validateHook = function(valid) {
                            var buttons = $(contentEl).find("input.formbutton");
                            for (i = 0; i < buttons.length; i++) {
                                var disabled = (contentEl.validator.invalidElements > 0);
                                buttons[i].disabled = disabled;
                                // just because IE does not recognize input[disabled]
                                // IE SUCKS
                                buttons[i].className = "formbutton " + (disabled ? "disabled" : "enabled");
                            }
                        };
                        contentEl.validator.validatePage(false, contentEl);
                        contentEl.validator.addValidation(contentEl);
                        check(res.responseXML.documentElement.getAttribute('class'));
                        document.href_frame = href;
                        var array = new Array();
                        // in case it is more than one element (e.g. comments or so), store all childnodes.

                        try {
                            for (var i = 0; i < contentEl.childNodes.length; i++) {
                                array.push(contentEl.childNodes[i]);
                            }
                        } catch (ex) {
                            alert(ex);
                        }
                        usedFrames[href] = array;
                        $(document).trigger("didactorContentLoaded",  { loaded: contentEl, number: number });
                        $(document).trigger("didactorContent",  { loaded: contentEl, number: number });
                        if ($.browser.msie) {
                            if ($.browser.version.substr(0, 3) <= 6.0) {
                                // alert("IE 6 is a horrible browser which cannot do this correctly at once
                                setTimeout(function() {
                                        $(contentEl).empty();
                                        for (var i=0; i < array.length; i++) {
                                            contentEl.appendChild(array[i]);
                                        }
                                    }, 500);
                            }
                        }
                    }
                }
           });
   } else {
       $(contentEl).empty();
       for (var i=0; i < content.length; i++) {
           contentEl.appendChild(content[i]);
       }
       document.href_frame = href;
       $(document).trigger("didactorContent",  { loaded: contentEl, number: number });
   }
    scrollToTop();
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
    scrollToTop();
}

function openContent(type, number, el) {
    if (currentel != null) {
        $(currentel).removeClass("active");
    }
    if (/^[+-]?\d+$/.test(type)) {
        el = number;
        number = type;
        type = null;
    }

    if (el != null) {
        $(el).addClass("active");
    }
    if (document.getElementById('content-' + currentnumber)) {
        $("#content-" + currentnumber).removeClass("selectedContent");
    }
    if ( number > 0 ) {
        currentnumber = number;
    }
    currentel = el;



    var href = addParameter('<mm:url page="/content/" />', 'object=' + number);
    if (type != null && type != '') {
        href = addParameter(href, 'type=' + type);
    }
    requestContent(href, number);
    $("#content-" + currentnumber).addClass("selectedContent");

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
