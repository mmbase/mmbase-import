<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><%@ include file="../config/read.jsp" %><% response.setContentType("text/javascript"); %>      
<mm:import externid="fragment" />
<mm:import externid="dir"      required="true" />

function ExplorerFix()  { 
    for (a in document.links) document.links[a].onfocus =
    document.links[a].blur; 
}

if(document.all) document.onmousedown = ExplorerFix;


function getPlayer() {
  player = parent.frames['left'].document.body.id;
  //alert("Setting player to '" + player);
  return player;
}

function getPosition() {
  // real player
  if (getPlayer() == "real") {
     return parent.frames['left'].document.embeddedplayer.GetPosition();
  } else {
     return parseInt(parent.frames['left'].document.embeddedplayer.CurrentPosition * 1000);
  }
}

function setPosition(pos) {
  if (getPlayer() == "real") {
     parent.frames['left'].document.embeddedplayer.setPosition(pos);
  } else {
     parent.frames['left'].document.embeddedplayer.CurrentPosition = pos / 1000;
  }
}

function getLength() {
   return parent.frames['left'].document.embeddedplayer.getLength();
}
function doPlay() {
  if (getPlayer() == "real") {
     parent.frames['left'].document.embeddedplayer.DoPlay();
  } else {
    parent.frames['left'].document.embeddedplayer.Play();
  }
}
function doStop() {
 if (getPlayer() == "real") {
   parent.frames['left'].document.embeddedplayer.DoStop();
 } else {
  parent.frames['left'].document.embeddedplayer.Stop();
 }
}
function doPause() {
if (getPlayer() == "real") {
   parent.frames['left'].document.embeddedplayer.DoPause();
 } else {
  parent.frames['left'].document.embeddedplayer.Pause();
}
}

function getLeftURL(form) {
    if (form == "search") {
        return '<mm:write value="search.jsp" />';
    } else if (form == "poolselector") {
        return '<mm:write value="poolselector.jsp" />';
    } else if (form == "itemize") {    
        return '<mm:write value="${dir}player.jsp?fragment=$fragment" />';  
    } else {
        return '<mm:write value="${dir}placeholder.jsp" />';
    }
}

function getRightURL(form) {
    if (form == "entrance") {
        return '<mm:write value="${dir}placeholder.jsp" />';
    } 
    return "";
}

function setTime(millistime) {
    return "bla";
}

function getTime(formattedtime) {
    return 123;
}

function setLeftFrame(form) {
    var current = parent.frames["left"].location.href;
    if (form != "basics") {
        var player  = getLeftURL(form); 
        if (current.indexOf(player) == -1) {
            //alert("Setting player to '" + player + "' current = '" + current + "'");
            parent.frames["left"].location.replace(player);
        }
    }
}

function setRightFrame(form) {
    var current = parent.frames["content"].location.href;
    if (form != "basics") {
        var content  = getRightURL(form); 
        if (current.indexOf(content) == -1) {
            parent.frames["content"].location.replace(content);
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


// init page on the right side
function init(form) {
    //alert("hoi");
    setLeftFrame(form);
}

// init page on the left side
function initLeft(form) {
    setRightFrame(form);
}
