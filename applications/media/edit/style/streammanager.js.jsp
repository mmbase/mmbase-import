<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><% response.setContentType("text/javascript"); %>
<mm:import externid="fragment" required="true" />
<mm:import externid="dir"      required="true" />

function ExplorerFix()  { 
    for (a in document.links) document.links[a].onfocus =
    document.links[a].blur; 
}

if(document.all) document.onmousedown = ExplorerFix;


function getPlayerURL(form) {
    if (form == "itemize") {    
        return '<mm:write value="${dir}player.jsp?fragment=$fragment" />';  
    } else {
        return '<mm:write value="${dir}placeholder.jsp" />';
    }
}

function setPlayerFrame(form) {
    var current = parent.frames["player"].location.href;
    var player  = getPlayerURL(form); 
    if (current.indexOf(player) == -1) {
        // alert("Setting player to '" + player + "' current = '" + current + "'");
        parent.frames["player"].location.replace(player);
    }
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
