<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html
<head>
<mm:import externid="language">nl</mm:import>
<mm:import externid="fragment" required="true" />
<mm:locale language="$language">

<title>[ STREAM ]</title>
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;language=$language" />" language="javascript"><!--help IE--></script>
</head>

<body>
<mm:cloud>
<mm:node number="$fragment">
<table class="movie" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td valign="top" width="32" height="27"><img src="images/movie_top_left.gif" alt="" width="32" height="27" border="0"></td>
    <td valign="top" background="images/movie_top.gif" height="27">&nbsp;</td>
    <td valign="top" width="35" height="27"><img src="images/movie_top_right.gif" alt="" width="35" height="27" border="0"></td>
</tr>
<tr>
    <td valign="top" background="images/movie_left.gif" width="32" ></td>
    <td valign="top" bgcolor="#717171"><embed src="<mm:url referids="fragment" page="display.ram.jsp" />" 
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
                name="embeddedplayer"></embed></td>
    <td valign="top" background="images/movie_right.gif" width="35"></td>
</tr>
<tr>
    <td valign="top" width="32" height="43"><img src="images/movie_down_left.gif" alt="" width="32" height="43" border="0"></td>
    <td height="43" align="center" valign="bottom" background="images/movie_down.gif"><img src="images/movie_knoppen.gif" alt="" width="160" height="38" border="0" usemap="#nav">
				<map name="nav">
				<area alt="terug" shape="circle" coords="16,25,10"  href="javascript:document.embeddedplayer.SetPosition(0);" />
				<area alt="play" shape="circle" coords="48,25,10"   href="javascript:document.embeddedplayer.DoPlay();" />
				<area alt="stop" shape="circle" coords="112,25,10"   href="javascript:document.embeddedplayer.DoStop();" />
				<area alt="pauze" shape="circle" coords="87,25,10"  href="javascript:document.embeddedplayer.DoPause();" />
				<area alt="vooruit" shape="circle" coords="145,25,10"  href="javascript:document.embeddedplayer.SetPosition(document.embeddedplayer.getLength());" />
				</map>
</td>
    <td valign="top" width="35" height="43"><img src="images/movie_down_right.gif" alt="" width="35" height="43" border="0"></td>
</tr>
</table>
</mm:node>
</mm:cloud>
</body>
</mm:locale>
</html>
