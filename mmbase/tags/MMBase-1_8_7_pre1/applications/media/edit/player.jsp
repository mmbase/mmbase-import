<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" import="org.mmbase.applications.media.Format"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><?xml version="1.0" encoding="UTF-8"?>
<mm:content language="$config.lang" type="text/html" expires="0">
<html>
<head>
<mm:import externid="fragment" required="true" />
<mm:import externid="forceplayer" />
<title>[ STREAM ]</title>
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
<%
String userAgent = request.getHeader("User-Agent");
boolean isMac = false;
boolean isMacIE = false;
if ((userAgent.indexOf("Mac") != -1)) {
    isMac = true;
    if (userAgent.indexOf("MSIE") != -1) {
        isMacIE = true;
    }
}

%>
<mm:cloud method="asis">
<mm:node number="$fragment" notfound="skip">
<%-- determin which player to use --%>
<mm:write referid="config.player">
  <mm:compare value="any">
    <%@ include file="config/realplayer.jsp" %>
  </mm:compare>
  <mm:compare value="real">
    <%@ include file="config/realplayer.jsp" %>
  </mm:compare>
  <mm:compare value="wm">
    <%@ include file="config/wmplayer.jsp" %>
  </mm:compare>
  <mm:compare value="qt">
    <%@ include file="config/qtplayer.jsp" %>
  </mm:compare>
</mm:write>
<mm:present referid="forceplayer">
 <mm:remove referid="player" />
 <mm:import id="player"><mm:write referid="forceplayer" /></mm:import>
</mm:present>

<body id="<mm:write referid="player" />">

<table class="movie" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td valign="top" width="32" height="27"><img src="images/movie_top_left.gif" alt="" width="32" height="27" border="0"></td>
    <td valign="top" background="images/movie_top.gif" height="27">&nbsp;</td>
    <td valign="top" width="35" height="27"><img src="images/movie_top_right.gif" alt="" width="35" height="27" border="0"></td>
</tr>
<tr>
    <td valign="top" background="images/movie_left.gif" width="32" ></td>
    <td valign="top" bgcolor="#717171">
          <mm:write referid="player">
          <mm:compare value="real">
              <mm:field id="source" name="url(rm,ram)"  write="false" />
              <embed src="<mm:write referid="source" />"
                width="260" 
                height="300"   
                type="audio/x-pn-realaudio-plugin"
                nojava="true" 
                <%-- controls="ImageWindow,PositionSlider,TACCtrl,StatusBar" --%>
                controls="ImageWindow,All"
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                scriptcallbacks="All"
                name="embeddedplayer"></embed>
         </mm:compare>
         <mm:compare value="wm">
            <object              
              id="embeddedplayer" width="260" height="300" 
              classid="CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95"
              codebase="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701"
              standby="Loading Microsoft Windows Media Player components..."
              type="application/x-oleobject">
              <param name="fileName" value="<mm:field id="source" name="url(wmv,asf,wma,wmp)" />">
              <param name="animationatStart" value="true">
              <param name="transparentatStart" value="true">
              <param name="autoStart" value="true">              
              <param name="showControls" value="true">            
              <param name="showStatusBar" value="true">            
              <!-- for netscape -->
              <embed type="application/x-mplayer2"
                     pluginspage = "http://www.microsoft.com/Windows/MediaPlayer/"
                     SRC="<mm:field name="url(wmv,asf,wma,wmp)" />"
                     enablejavascript="true"
                     name="embeddedplayer"
                     AutoStart="true">        
                   </embed>
                 </object>
    </mm:compare>
    <mm:compare value="qt">
         <object id="embeddedplayer"
                 classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" width="260" height="300" 
                 codebase="http://www.apple.com/qtactivex/qtplugin.cab">
                 <param name="SRC" VALUE="<mm:field id="source" name="url(mov,ram,wmp)" />" > 
                 <param name="AUTOPLAY" VALUE="true">
                 <param name="CONTROLLER" VALUE="true">
                 <embed src="<mm:field name="url(mov,ram,wmp)" />" width="260" height="300" autoplay="true"
                    controller="true"
                    name="embeddedplayer"
                    enablejavascript="true"
                    pluginspage="http://www.apple.com/quicktime/download/"> </embed> </object>
     </mm:compare>
         </mm:write>
       </td>
    <td valign="top" background="images/movie_right.gif" width="35"></td>
</tr>
<tr>
    <td valign="top" width="32" height="43"><img src="images/movie_down_left.gif" alt="" width="32" height="43" border="0"></td>
<mm:write referid="player">
<mm:compare value="nobuttonsreal"><!-- use own controls for real -->
    <td height="43" align="center" valign="bottom" background="images/movie_down.gif"><img src="images/movie_knoppen.gif" alt="" width="160" height="38" border="0" usemap="#nav">
				<map name="nav">
				<area alt="terug" shape="circle" coords="16,25,10"  href="javascript:setPosition(0);" />
				<area alt="play" shape="circle"  coords="48,25,10"   href="javascript:doPlay();" />
				<area alt="stop" shape="circle"  coords="112,25,10"   href="javascript:doStop();" />
				<area alt="pauze" shape="circle" coords="87,25,10"  href="javascript:doPause();" />
				<area alt="vooruit" shape="circle" coords="145,25,10" href="javascript:setPosition(getLength());" /> 
				</map>
</td>
</mm:compare>
<mm:compare value="extrabuttsreal" inverse="true">
    <td valign="top" background="images/movie_down.gif" height="27">&nbsp;</td>
</mm:compare>
</mm:write>
    <td valign="top" width="35" height="43"><img src="images/movie_down_right.gif" alt="" width="35" height="43" border="0"></td>
</tr>
</table>
<!--
preferred player: <mm:write referid="config.player" /><br />
used source:      <mm:write referid="source" /> <br />
used fragment:      <mm:field name="number" /> <br />
used player:      <mm:write referid="player" /><br />
mimetype:      <mm:field name="mimetype()" /><br />
mimetype:      <mm:field name="contenttype()" /><br />
format:      <mm:field name="format(ram,wmf)" /><br />
-->
<!--
<hr />
<form name="force">
  <select name="forceplayer" onChange="document.forms['force'].submit()">
     <option value="">---</option>
     <option value="wm">window media player</option>
     <option value="real">real player</option>
     <option value="qt">quick time player</option>
  </select>
  <input type="hidden" name="fragment" value="<mm:field name="number" />" />
</form>
-->
<mm:import id="foundnode" />
</mm:node>
<mm:notpresent referid="foundnode">
<h1><%=m.getString("nostream")%></h1>
</mm:notpresent>
</mm:cloud>
</body>
</html>
</mm:content>