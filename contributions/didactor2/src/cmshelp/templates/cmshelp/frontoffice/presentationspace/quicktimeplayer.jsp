<OBJECT CLASSID="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B"
CODEBASE="http://www.apple.com/qtactivex/qtplugin.cab"
WIDTH="100%" HEIGHT="100%">
    <PARAM NAME="src"        			VALUE="<%=request.getParameter("url")%>">
    <PARAM NAME="autohref"   			VALUE="false">
    <PARAM NAME="autoplay"   			VALUE="false">
    <PARAM NAME="bgcolor"    			VALUE="black">
    <PARAM NAME="controller" 			VALUE="true">
    <PARAM NAME="correction" 			VALUE="full">
    <PARAM NAME="dontflattenwhensaving" VALUE="false">
    <PARAM NAME="enablejavascript"		VALUE="false">
    <PARAM NAME="endtime"   			VALUE="">
    <PARAM NAME="fov"		   			VALUE="">
<!--    <PARAM NAME="hidden"				VALUE="">  hide the movie -->
    <PARAM NAME="hotspot"	   			VALUE="">
    <PARAM NAME="href"		   			VALUE="">
    <PARAM NAME="kioskmode"	   			VALUE="false">
    <PARAM NAME="loop"	 	  			VALUE="false">
    <PARAM NAME="movieid"	 	  		VALUE="1">
    <PARAM NAME="moviename"	 	  		VALUE="Apple">
    <PARAM NAME="node"	 	  			VALUE="">
    <PARAM NAME="pan"	 	  			VALUE="">
    <PARAM NAME="playeveryframe"		VALUE="false">
    <PARAM NAME="pluginspage"			VALUE="http://www.apple.com/quicktime/download/">
    <PARAM NAME="qtnext"				VALUE="">
<!--    <PARAM NAME="qtsrc"					VALUE="">  override src attribute -->
	<PARAM NAME="qtsrcchokespeed"		VALUE="">
	<PARAM NAME="qtsrcdontusebrowser"	VALUE="false">
	<PARAM NAME="scale"					VALUE="tofit"> <!-- tofit=define with WIDTH and HEIGHT; aspect=fit the box but maintain aspect ratio -->
	<PARAM NAME="starttime"				VALUE="">
	<PARAM NAME="target"				VALUE="">
	<PARAM NAME="targetcache"			VALUE="false">
	<PARAM NAME="tilt"					VALUE="">
	<PARAM NAME="type"					VALUE="video/quicktime">
	<PARAM NAME="urlsubstitute"			VALUE="">
	<PARAM NAME="volume"				VALUE="100">
    <EMBED SRC="<%=request.getParameter("url")%>" 
       WIDTH="100%" 
       HEIGHT="100%" 
       AUTOHREF="false" 
       AUTOPLAY="false" 
       BGCOLOR="black" 
       CONTROLLER="true" 
       CORRECTION="full" 
       DONTFLATTENWHENSAVING="false"
       ENABLEJAVASCRIPT="false"
       ENDTIME=""
       FOV=""
       HOTSPOT=""
       HREF=""
       KIOSKMODE="false"
       LOOP="false"
       MOVIEID="1"
       MOVIENAME="Apple"
       NODE=""
       PAN=""
       PLAYEVERYFRAME="false"
	   PLUGINSPAGE="http://www.apple.com/quicktime/download/"
	   QTNEXT=""
	   QTSRCCHOKESPEED=""
	   QTSRCDONTUSEBROWSER="false" 
	   SCALE="tofit"
	   STARTTIME=""
	   TARGET=""
	   TARGETCACHE="false"
	   TILT=""
	   TYPE="video/quicktime"
	   URLSUBSTITUTE=""
	   VOLUME="100"> 
	   <NOEMBED><A HREF="<%=config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>">Play with QuickTime Player.</A></NOEMBED>
	</EMBED> 
</OBJECT>	
