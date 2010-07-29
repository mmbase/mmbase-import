<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html
<head>
<mm:import externid="language">nl</mm:import>
<mm:import externid="fragment" required="true" />
<mm:locale language="$language">

<title>[ STREAM  DEMO ]</title>
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;language=$language" />" language="javascript"><!--help IE--></script>
</head>

<body onLoad="showInfo();">
<mm:cloud>

<mm:present referid="source">
  		<table width="350" height="300" border="0" class="movie" background="images/bck_movie.gif" class="movie">
			 <tr><!-- 3 collumns -->
    		<td  class="movie" colspan="3" width="340" height="25"><img src="images/extra.gif" width="340" height="25" alt="" border="0" usemap="#nav"></td>
		  	</tr>
			  <tr>
    			<td class="movie" width="35" height="180" ><img src="images/extra.gif" width="35" height="180" alt="" border="0" usemap="#nav"></td>
    			<td width="260" height="300" class="movie">
       <embed src="<mm:url referids="source" page="demo.smil" />" 
                width="260" 
                height="200"   
                type="audio/x-pn-realaudio-plugin"
                nojava="false" 
                controls="ImageWindow,PositionSlider,TACCtrl"
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                scriptcallbacks="All"
                onPresentationOpened="alert('ja');"
                name="embeddedplayer"></embed>

         </td>
    			<td class="movie" width="45" height="180" ><img src="images/extra.gif" width="45" height="180" alt="" border="0" usemap="#nav"></td>
			</tr>
			<tr>
    			<td class="movie" colspan="3" width="340" height="40"><img src="images/extra.gif" width="340" height="25" alt="" border="0" usemap="#nav">
				<map name="nav">
				<area alt="terug" shape="circle" coords="125,10,10"  href="javascript:document.embeddedplayer.SetPosition(0);" />
				<area alt="play" shape="circle" coords="178,10,10"   href="javascript:document.embeddedplayer.DoPlay();" />
				<area alt="stop" shape="circle" coords="242,10,10"   href="javascript:document.embeddedplayer.DoStop();" />
				<area alt="pauze" shape="circle" coords="217,10,10"  href="javascript:document.embeddedplayer.DoPause();" />
				<area alt="vooruit" shape="circle" coords="295,10,10"  href="javascript:document.embeddedplayer.SetPosition(document.embeddedplayer.getLength());" />
				</map>
       </td>
			</tr>
		</table>
<hr />
<br />
<h1>test</h1>
<form id="info">
<table>
  <tr>
   <td>Copyright</td><td><input id="copyright" value="hoi" /></td>
  </tr>
</table>
</form>
   <a href="javascript:showInfo();"> test </a>
    
	  </mm:present>
    <mm:notpresent referid="source">
       Could not determin source
    </mm:notpresent>
 
</mm:cloud>
</body>
</mm:locale>
</html>
