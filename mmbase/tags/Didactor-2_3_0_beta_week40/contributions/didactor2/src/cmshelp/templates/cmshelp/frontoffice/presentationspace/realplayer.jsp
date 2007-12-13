<OBJECT ID="Real" CLASSID="clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA" WIDTH="100%" HEIGHT="93%" NOJAVA="true">
	<PARAM NAME="src" 				VALUE="<%=config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>">
	<PARAM NAME="controls" 			VALUE="ImageWindow">
	<PARAM NAME="console" 			VALUE="_master">
	<PARAM NAME="autogotourl" 		VALUE="true">
	<PARAM NAME="autostart" 		VALUE="false">
	<PARAM NAME="backgroundcolor" 	VALUE="black">
	<PARAM NAME="center" 			VALUE="false">
	<PARAM NAME="imagestatus" 		VALUE="true">
	<PARAM NAME="loop" 				VALUE="false">
	<PARAM NAME="maintainaspect" 	VALUE="false">
	<PARAM NAME="nolabels" 			VALUE="false">
	<PARAM NAME="nologo" 			VALUE="false">		
	<PARAM NAME="numloop" 			VALUE="1">
	<PARAM NAME="prefetch" 			VALUE="false">
	<PARAM NAME="region" 			VALUE="">
	<PARAM NAME="scriptcallbacks" 	VALUE="">
	<PARAM NAME="shuffle" 			VALUE="false">
	<PARAM NAME="type" 				VALUE="audio/x-pn-realaudio-plugin">	
	<EMBED SRC="<%="file:///"+ config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>" 
		WIDTH="100" 
		HEIGHT="93" 
		NOJAVA="true"
		CONTROLS="ImageWindow"
		CONSOLE="_master"
		AUTOGOTOURL="true"
		AUTOSTART="false"
		BACKGROUNDCOLOR="black"		
		CENTER="false"
		IMAGESTATUS="true"
		LOOP="false"
		MAINTAINASPECT="false"
		NAME="Real"		
		NOLABELS="false"
		NOLOGO="false"		
		NUMLOOP="1"
		PREFETCH="false"
		REGION=""
		SCRIPTCALLBACKS=""		
		SHUFFLE="false"
		TYPE ="audio/x-pn-realaudio-plugin">
		<NOEMBED><A HREF="<%=config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>">Play with RealOne Player.</A></NOEMBED>
	</EMBED>
</OBJECT>
<OBJECT ID="Real1" CLASSID="clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA" WIDTH="75%" HEIGHT="30">
	<PARAM NAME="src" VALUE="<%=config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>">
	<PARAM NAME="controls" VALUE="ControlPanel">
	<PARAM NAME="console" VALUE="video">
	<EMBED SRC="<%="file:///"+ config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>" 
		WIDTH="75%" HEIGHT="30" NOJAVA="true" CONTROLS="ControlPanel" CONSOLE="video">
	</EMBED>
</OBJECT>
<OBJECT ID="Real2" CLASSID="clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA" WIDTH="25%" HEIGHT="30">
	<PARAM NAME="src" VALUE="<%=config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>">
	<PARAM NAME="controls" VALUE="PositionField">
	<PARAM NAME="console" VALUE="video">
	<EMBED SRC="<%="file:///"+ config.getServletContext().getRealPath("/")+"virtualclassroom\\"+request.getParameter("url")%>" 
		WIDTH="25%" HEIGHT="30" NOJAVA="true" CONTROLS="PositionField" CONSOLE="video">
	</EMBED>
</OBJECT>
