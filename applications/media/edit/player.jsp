<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<mm:import externid="language">nl</mm:import>
<mm:import externid="fragment" required="true" />
<mm:locale language="$language">

<title>[ STREAM ]</title>
<link href="style/wizard.css" type="text/css" rel="stylesheet" />
<link href="style/streammanager.css" type="text/css" rel="stylesheet" />
</head>
<body>
<mm:cloud>
<%-- determin source --%>
<mm:node number="$fragment">
  <mm:relatednodes type="mediasources" constraints="format = 6" max="1">
      <mm:node id="source" />
  </mm:relatednodes>
</mm:node>

<table width="350" height="300" align="left" background="images/bck_movie.gif" class="movie">
	<tr>
		<td width="350" height="300"  class="movie">
		
		<table width="350" height="300" border="0" class="movie">
			<tr>
    			<td  class="movie" colspan="3" width="340" height="25"><img src="images/extra.gif" width="340" height="25" alt="" border="0" usemap="#nav"></td>
			</tr>
			<tr>
    			<td class="movie" width="35" height="180" ><img src="images/extra.gif" width=35" height=180" alt="" border="0" usemap="#nav"></td>
    			<td width="260" height="180" class="movie">
       <embed src="<mm:url referids="source" page="display.ram.jsp" />" 
                width="260" 
                height="200"   
                type="audio/x-pn-realaudio-plugin"
                nojava="false" 
                controls="ImageWindow" 
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                name="embeddedplayer"></embed>
         </td>
    			<td class="movie" width="45" height="180" ><img src="images/extra.gif" width="45" height="180" alt="" border="0" usemap="#nav"></td>
			</tr>
			<tr>
    			<td class="movie" colspan="3" width="340" height="40"><img src="images/extra.gif" width="340" height="25" alt="" border="0" usemap="#nav">
				<map name="nav">
				<area alt="terug" shape="circle" coords="125,10,10" href="#">
				<area alt="play" shape="circle" coords="178,10,10" href="javascript:document.embeddedplayer.DoPlay();">
				<area alt="stop" shape="circle" coords="242,10,10" href="javascript:document.embeddedplayer.DoPause();">
				<area alt="pauze" shape="circle" coords="217,10,10" href="#">
				<area alt="vooruit" shape="circle" coords="295,10,10" href="#">
				</map></td>
			</tr>
		</table>
		
		</td>
	</tr>

</table> 
</mm:cloud>
</body>
</mm:locale>
</html>
