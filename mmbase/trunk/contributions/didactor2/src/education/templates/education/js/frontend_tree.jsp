// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content type="text/javascript" expires="600">
    <mm:cloud>

var ITEM_NONE   = '${mm:treefile("/gfx/icon_arrow_tab_none.gif", pageContext, includePath)}';
var ITEM_OPENED = '${mm:treefile("/gfx/icon_arrow_tab_open.gif", pageContext, includePath)}';
var ITEM_CLOSED = '${mm:treefile("/gfx/icon_arrow_tab_closed.gif", pageContext, includePath)}';

var may_open_future = <di:getsetting component="core" setting="may_open_future" />;

// IE does not even support indexOf, fixing that here..
[].indexOf || (Array.prototype.indexOf = function(v,n){
  n = (n==null)?0:n; var m = this.length;
  for(var i = n; i < m; i++)
    if(this[i] == v)
       return i;
  return -1;
});


var currentnumber = -1;
var contenttype = new Array();
var contentnumber = new Array();

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
                    alert('<di:translate key="education.testalert" />');
                    return;
                }
                var opentype = contenttype[count+1];
                var opennumber = contentnumber[count+1];
            }
        }
    }
    openContent( opentype, opennumber );
    openOnly('div'+opennumber,'img'+opennumber);
}

function previousContent() {
    for(var count = 0; count <= contentnumber.length; count++) {
        if ( contentnumber[count] == currentnumber ) {
            if ( count > 0 ) {
                if ("tests" == contenttype[count]) {
                    alert("<di:translate key="education.testalert" />");
                    return;
                }
                var opentype = contenttype[count-1];
                var opennumber = contentnumber[count-1];
            }
        }
    }
    openContent( opentype, opennumber );
    openOnly('div'+opennumber,'img'+opennumber);
}


function openContent( type, number ) {
    
    if (document.getElementById('content-'+currentnumber)) {
        var el = document.getElementById('content-'+currentnumber);
        var classNames = el.className.split(" ");
        var newClassNames = new Array();
        for (var c in classNames) {
            if (classNames[c] != 'selectedContent') {
                newClassNames.push(classNames[c]);
            }
        }
        el.className = newClassNames.join(" ");
    }
    if ( number > 0 ) {
        currentnumber = number;
    }
    
    
    switch ( type ) {
    case "educations":
        
        //    note that document.content is not supported by mozilla!
        //    so use frames['content'] instead
        
        frames['content'].location.href = addParameter('<mm:treefile page="/education/educations.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>', 'edu='+number);
        break;
    case "learnblocks":
    case "htmlpages":
        frames['content'].location.href= addParameter('<mm:treefile page="/education/learnblocks/index.jsp" objectlist="$includePath" referids="$referids,fb_madetest?" escapeamps="false"/>', 'learnobject='+number);
        break;
    case "tests":
        frames['content'].location.href= addParameter('<mm:treefile page="/education/tests/index.jsp" objectlist="$includePath" referids="$referids,fb_madetest?,justposted?" escapeamps="false"/>', 'learnobject='+number);
        break;
    case "pages":
        frames['content'].location.href= addParameter('<mm:treefile page="/education/pages/index.jsp" objectlist="$includePath" referids="$referids,fb_madetest?" escapeamps="false"/>', 'learnobject='+number);
        break;
    case "flashpages":
        frames['content'].location.href= addParameter('<mm:treefile page="/education/flashpages/index.jsp" objectlist="$includePath" referids="$referids,fb_madetest?" escapeamps="false"/>', 'learnobject='+number);
        break;
    }
    frames['content'].scrollTop = '0px';
    document.body.scrollTop = '0px';
    if (document.getElementById('content-'+currentnumber)) {
        var el = document.getElementById('content-'+currentnumber);
        var orig = el.className;
        var classNames = el.className.split(" ");
        if (classNames.indexOf("selectedContent") == -1) {
            classNames.push("selectedContent");
        }
        el.className = classNames.join(" ");
    }
    
}

function openClose(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);
    if (realdiv != null) {
        if (realdiv.getAttribute("opened") == "1") {
            realdiv.setAttribute("opened", "0");
            realdiv.style.display = "none";
            realimg.src = ITEM_CLOSED;
        } else {
            if (! may_open_future) {
                if (/\bnon_completed\b/.test(realimg.parentNode.className)) {
                    alert('<di:translate key="education.future" />');
                    return false;
                }
            }
            if (/\bblocked\b/.test(realimg.parentNode.className)) {
                alert('<di:translate key="education.future" />');
                return false;
            }
            realdiv.setAttribute("opened", "1");
            realdiv.style.display = "block";
            realimg.src = ITEM_OPENED;
        }
    }
    return true;
}

function openOnly(div, img) {
    var realdiv = document.getElementById(div);
    var realimg = document.getElementById(img);
    // alert("openOnly("+div+","+img+"); - "+realdiv);
    if (realdiv != null) {
        if (! may_open_future) {
            if (/\bnon_completed\b/.test(realimg.parentNode.className)) {
                alert('<di:translate key="education.future" />');
                return false;
            }
        }
        if (/\bblocked\b/.test(realimg.parentNode.className)) {
            alert('<di:translate key="education.future" />');
            return false;
        }
        realdiv.setAttribute("opened", "1");
        realdiv.style.display = "block";
        realimg.src = ITEM_OPENED;

        var className = realdiv.className;
        if (className) {
            // ignore "lbLevel" in classname to get the level depth
            var level = className.substring(7, className.length);
            // alert("level = "+level);
            var findparent = realdiv;
            var findparentClass = className;


            //There is a JS error here
            if (level > 1) {
                // also open parents
                do {
                    findparent = findparent.parentNode;
                    findparentClass = findparent.className || "";
                } while (findparent && findparentClass.indexOf("lbLevel") != 0);

                if (findparent) {
                    var divid = findparent.id;
                    var imgid = "img"+divid.substring(3,divid.length);
                    openOnly(divid, imgid);
                }
            }
                

        }
    } else { // find enclosing div
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

<mm:treefile page="/education/tree.jspx" objectlist="$includePath" referids="$referids" write="false" escapeamps="false">
    function reloadEducationTree() {
        var xmlhttp =  new XMLHttpRequest();
        xmlhttp.open('GET', '${_}', true);              
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
                var ser = new XMLSerializer();
                var s = ser.serializeToString(xmlhttp.responseXML);
                document.getElementById('education-tree').innerHTML = s; 
                closeAll();
                if (contentnumber.length >= 1) {
                    openContent(contenttype[0], contentnumber[0]);
                    openOnly('div'+contentnumber[0], 'img'+contentnumber[0]);
                }
            }
        }
        xmlhttp.send(null);
    }
</mm:treefile>

</mm:cloud>
</mm:content>
