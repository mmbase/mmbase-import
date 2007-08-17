<tr>
<td bgcolor="#f8eee3"  class="plaintext" align="center" height="400px">
<OBJECT ID="video" CLASSID="clsid:6BF52A52-394A-11d3-B153-00C04F79FAA6" 
TYPE="application/x-oleobject" WIDTH="100%" HEIGHT="100%">	    
	    <PARAM NAME="AutoStart"  		VALUE="0">
	    <PARAM NAME="baseURL" 	 		VALUE="<%=config.getServletContext().getRealPath("/")%>">
	    <PARAM NAME="captioningID"  	VALUE="MicrosoftVideo">
	    <PARAM NAME="currentPosition"  	VALUE="">
	    <PARAM NAME="currentMarker"  	VALUE="0">
	    <PARAM NAME="defaultFrame"  	VALUE="">
	    <PARAM NAME="enableContextMenu" VALUE="true">
	    <PARAM NAME="enabled" 			VALUE="true">
	    <PARAM NAME="fullScreen" 		VALUE="false">
	    <PARAM NAME="invokeURLs" 		VALUE="false">
	    <PARAM NAME="mute" 				VALUE="false">
	    <PARAM NAME="playCount" 		VALUE="1">
	    <PARAM NAME="rate" 				VALUE="1.0">
	    <PARAM NAME="SAMIFileName" 		VALUE="">
	    <PARAM NAME="SAMILang" 			VALUE="">
	    <PARAM NAME="SAMIStyle" 		VALUE="">
	    <PARAM NAME="stretchToFit" 		VALUE="true">
	    <PARAM NAME="uiMode" 			VALUE="full">
	    <PARAM NAME="URL"        		VALUE="<%=request.getParameter("url")%>">
	    <PARAM NAME="volume"    		VALUE="100">
	    <PARAM NAME="windowlessVideo"   VALUE="false">	    
	    <EMBED NAME="video"
	   	   SRC="<%=request.getParameter("url")%>"
	       WIDTH="100%" 
	       HEIGHT="100%"
	       TYPE="application/x-mplayer2" 
	       LOOP="false" 	       
	       SHOWSTATUSBAR="true"
		   PLUGINSPAGE="http://www.microsoft.com/Windows/MediaPlayer/"	        
	       AUTOSTART="0"
	       BASEURL="<%=config.getServletContext().getRealPath("/")%>"
	       CAPTIONINGID="MicrosoftVideo"
	       CURRENTPOSITION=""
	       CURRENTMARKER="0"
	       DEFAULTFRAME=""
	       ENABLECONTEXTMENU="true"
	       ENABLED="true"	   
	       FULLSCREEN="false"
	       INVOKEURLS="false"    	       
	       MUTE="false"
	       PLAYCOUNT="1"
	       RATE="1.0"
	       SAMIFILENAME=""
	       SAMILANG=""
	       SAMISTYLE=""
	       STRETCHTOFIT="true"
	       UIMODE="full"	       
	       VOLUME="100" 
		   WINDOWLESSVIDEO="false">
		   <NOEMBED><A HREF="<%=config.getServletContext().getRealPath("/")+request.getParameter("url")%>">Play with Windows Media Player.</A></NOEMBED> 
		</EMBED> 	
</OBJECT>
</td>
</tr>
