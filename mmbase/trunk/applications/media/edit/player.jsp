<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
<mm:import externid="fragment" required="true" />
<mm:locale language="$config.lang">

<title>[ STREAM ]</title>
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
<mm:cloud>
<mm:node number="$fragment">

<%-- determin which player to use --%>
<mm:write referid="config.player">
  <mm:compare value="wm" inverse="true"><%-- ram ? --%>
      <mm:field name="format(ram)">
         <mm:compare value="wmp">
            <mm:write id="player" value="wm" write="false" />
         </mm:compare>
         <mm:compare value="wmp" inverse="true">
            <mm:write id="player" value="real" write="false" />
         </mm:compare>
      </mm:field>
  </mm:compare>
  <mm:compare value="wm">
      <mm:field name="format(wmp)">
         <mm:compare value="ram">
            <mm:write id="player" value="real" write="false" />
         </mm:compare>
         <mm:compare value="ram" inverse="true">
            <mm:write id="player" value="wm" write="false" />
         </mm:compare>
      </mm:field>
  </mm:compare>
</mm:write>

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
              <mm:field id="source" name="url(ram)" jspvar="url" />
              <embed src="<mm:write referid="source" />"
                width="260" 
                height="300"   
                type="audio/x-pn-realaudio-plugin"
                nojava="false" 
                controls="ImageWindow,PositionSlider,TACCtrl"
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                scriptcallbacks="All"
                name="embeddedplayer"></embed>
         </mm:compare>
         <mm:compare value="wm">
            <object              
              id="embeddedplayer"
              classid="CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95"
              codebase="http://activex.microsoft.com/activex/	controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701"
              standby="Loading Microsoft Windows Media Player components..."
              type="application/x-oleobject">
              <param name="fileName" value="<mm:field id="source" name="url(asf)" />">
              <param name="animationatStart" value="true">
              <param name="transparentatStart" value="true">
              <param name="autoStart" value="true">              
              <param name="showControls" value="true">            
              <!-- for netscape -->
      <embed type="application/x-mplayer2"
        pluginspage = "http://www.microsoft.com/Windows/MediaPlayer/"
        SRC="<mm:field name="url(asf)" />"
        name="embeddedplayer"
        AutoStart="true">        
      </embed>
    </object>
    </mm:compare>
         </mm:write>
       </td>
    <td valign="top" background="images/movie_right.gif" width="35"></td>
</tr>
<tr>
    <td valign="top" width="32" height="43"><img src="images/movie_down_left.gif" alt="" width="32" height="43" border="0"></td>
    <td height="43" align="center" valign="bottom" background="images/movie_down.gif"><img src="images/movie_knoppen.gif" alt="" width="160" height="38" border="0" usemap="#nav">
				<map name="nav">
				<area alt="terug" shape="circle" coords="16,25,10"  href="javascript:setPosition(0);" />
				<area alt="play" shape="circle"  coords="48,25,10"   href="javascript:doPlay();" />
				<area alt="stop" shape="circle"  coords="112,25,10"   href="javascript:doStop();" />
				<area alt="pauze" shape="circle" coords="87,25,10"  href="javascript:doPause();" />
				<area alt="vooruit" shape="circle" coords="145,25,10" href="javascript:setPosition(getLength());" /> 
				</map>
</td>
    <td valign="top" width="35" height="43"><img src="images/movie_down_right.gif" alt="" width="35" height="43" border="0"></td>
</tr>
</table>
<!--
preferred player: <mm:write referid="config.player"><mm:write /><mm:isempty>No preference</mm:isempty></mm:write><br />
used source:      <mm:write referid="source" /> <br />
used player:      <mm:write referid="player" /><br />
mimetype:      <mm:field name="mimetype()" /><br />
-->
</mm:node>
</mm:cloud>
</body>
</mm:locale>
</html>
