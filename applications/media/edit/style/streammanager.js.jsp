<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><% response.setContentType("text/javascript"); %>
<mm:import externid="fragment" />
<mm:import externid="language" required="true" />
<mm:import externid="dir"      required="true" />

function ExplorerFix()  { 
    for (a in document.links) document.links[a].onfocus =
    document.links[a].blur; 
}

if(document.all) document.onmousedown = ExplorerFix;


function getPlayerURL(form) {
    if (form == "entrance") {
        return '<mm:write value="poolselector.jsp?language=$language" />';
    } else if (form == "itemize") {    
        return '<mm:write value="${dir}player.jsp?fragment=$fragment" />';  
    } else {
        return '<mm:write value="${dir}placeholder.jsp" />';
    }
}

function setTime(millistime) {
    return "bla";
}

function getTime(formattedtime) {
    return 123;
}

function setPlayerFrame(form) {
    var current = parent.frames["player"].location.href;
    if (form != "basics") {
        var player  = getPlayerURL(form); 
        if (current.indexOf(player) == -1) {
            //alert("Setting player to '" + player + "' current = '" + current + "'");
            parent.frames["player"].location.replace(player);
        }
    }
}

function setContentFrame(url) {
    // alert("Setting content to '" + url);
    parent.frames["content"].location.replace(url);
}

// not used
function detach() {
    //window.open(url, "player", "menubar=yes, scrollbars=yes, locationbar=yes, status=yes, width=300, height=300, zoptions=alwaysRaise"); 
    document.frameset.cols = "400,*";
    //document.getElementById("player").style.display = "none";
}


function init(form) {
    setPlayerFrame(form);
}
