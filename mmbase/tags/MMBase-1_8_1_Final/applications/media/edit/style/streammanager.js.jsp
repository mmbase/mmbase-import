<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><%@ include file="../config/read.jsp" %><% response.setContentType("text/javascript"); %>      
<mm:import externid="fragment" />
<mm:import externid="dir"      required="true" />

function ExplorerFix()  { 
    for (a in document.links) document.links[a].onfocus =
    document.links[a].blur; 
}

if(document.all) document.onmousedown = ExplorerFix;

var player = null;

function getPlayer() {
  // autodetection on what kind of javascript to use
  if (player == null) {
     try {
        parent.frames['left'].document.embeddedplayer.GetPosition();
        player = "real";
     } catch (e) {
        try {
           parent.frames['left'].document.embeddedplayer.GetTime();
           player = "qt";
           if((navigator.appName.search("Internet") != -1 ) && (navigator.platform.search("Mac") != -1)) { // does this check work?
               alert("Quick-time for apple internet explorer does not support javascript");
           }

        } catch (e) {
            player="wm";
        }
     }
     //alert("Setting player to '" + player + "'" + " on " + navigator.appName + " " + navigator.platform);

   }
   //  player = parent.frames['left'].document.body.id;
   //  alert("Getting player to '" + player);
  return player;
}

function getPosition() {
  var pos;
  // real player
  if (getPlayer() == "real") {
     pos =  parent.frames['left'].document.embeddedplayer.GetPosition();
  } else if (getPlayer() == "qt") {
     pos = parent.frames['left'].document.embeddedplayer.GetTime();
  } else {
     pos =  parseInt(parent.frames['left'].document.embeddedplayer.CurrentPosition * 1000);
  }
  if (pos < 0) pos = 0; // more uniform behaviour
  return pos; // setTime(pos);

}

function setPosition(pos) {
  if (getPlayer() == "real") {
     parent.frames['left'].document.embeddedplayer.setPosition(pos);
  } else if (getPlayer() == "qt") {
     return parent.frames['left'].document.embeddedplayer.SetTime(pos);
  } else {
     parent.frames['left'].document.embeddedplayer.CurrentPosition = pos / 1000;
  }
}


function getLength() {
  if (getPlayer() == "real") {
     return parent.frames['left'].document.embeddedplayer.getLength();
  } else if (getPlayer() == "qt") {
     return parent.frames['left'].document.embeddedplayer.GetEndTime();
  } else {
   return parent.frames['left'].document.embeddedplayer.Duration * 1000;
  }
}

// Following are for the extra (currenlty unused) javascript buttons.

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


// other javascript

function getLeftURL(form) {
    if (form == "search") {
        return '<mm:write value="" />';
    } else if (form == "poolselector") {
        return '<mm:write value="poolselector.jsp" />';
    } else if (form == "itemize") {    
        return '<mm:write value="${dir}player.jsp?fragment=$fragment" />';  
    } else if (form == "security") {    
        return '<mm:write value="security.jsp" />';  
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
    var time = new Date(millistime);
    var format = time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds() + ".";
    var ms = time.getMilliseconds();
    if (ms < 10) {
        return format + "00" + ms;
    } else if (ms < 100) {
       return format + "0" + ms;
    } else {
       return format + ms;
    }    
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
    setLeftFrame(form);
}

// init page on the left side
function initLeft(form) {
    setRightFrame(form);
}
