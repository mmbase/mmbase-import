<mm:relatednodes type="virtualclassroomsessions">
  <% boolean doPlay=true;%>
  <mm:relatednodes type="videotapes">   
    <mm:import id="urlField" jspvar="urlFieldStr" vartype="String" reset="true"><mm:field name="url"/></mm:import>
    <% 
      if ( doPlay && (  urlFieldStr.toLowerCase().endsWith(".avi")||
                  urlFieldStr.toLowerCase().endsWith(".wmv")||
                  urlFieldStr.toLowerCase().endsWith(".mow")||
                  urlFieldStr.toLowerCase().endsWith(".mpg"))) { 
         doPlay=false;
    %>
         <object id="video" width="100%" height="100%" 
           classid="clsid:6bf52a52-394a-11d3-b153-00c04f79faa6"
           type="application/x-oleobject">
           <param name="url" value="<mm:field name="url"/>"/>
           <param name="showcontrols" value="true"/>
           <param name="sendplaystatechangeevents" value="true"/>
           <param name="autostart" value="false"/>
           <param name="playcount" value="1"/>
           <param name="showdisplay" value="false"/>
           <param name="showstatusbar" value="false"/>
           <param name="stretchToFit" value="true"/>
           <param name="autosize" value="true"/>
             <embed type="application/x-mplayer2"
               pluginspage="http://www.microsoft.com/windows/windowsmedia/download/"
               filename="<mm:field name="url"/>"
               src="<mm:field name="url"/>"
               Name=MediaPlayer
               ShowControls=1
               ShowDisplay=0
               AutoStart=0
               ShowStatusBar=0
               Autosize=1
               sendplaystatechangeevents=1
               stretchToFit=1
               width="100%"
               height="100%"/>
             </embed>    
         </object>
    <%}%>
  </mm:relatednodes>
  <mm:relatednodes type="attachments">   
    <mm:import id="filenameField" jspvar="filenameFieldStr" vartype="String" reset="true"><mm:field name="filename"/></mm:import>
    <%
      if (doPlay && filenameFieldStr.toLowerCase().endsWith(".swf")) { 
        doPlay=false;
    %>    
        <object id="shell_object" name="shell.swf" 
          classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" 
          codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab#version=8,0,22,0" 
          width="100%" height="100%"> 
          <param name="movie" value="<mm:attachment/>" /> 
          <param name="quality" value="high" /> 
          <param name="menu" value="0" /> 
          <param name="bgcolor" value="#000000" /> 
          <param name=FlashVars value="loc=en_US" /> 
            <embed id="shell_object" 
              name="shell.swf" 
              type="application/x-shockwave-flash" 
              src="<mm:attachment/>" 
              flashvars="loc=en_US" 
              quality="high" 
              bgcolor="#000000" 
              menu="0" 
              width="100%" 
              height="100% 
              pluginspage="http://www.macromedia.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash&P2_Platform=Win32&P5_Language=English">
            </embed> 
        </object> 
    <%
      } else if (doPlay && filenameFieldStr.toLowerCase().endsWith(".ppt")) { 
        doPlay=false;
    %> 
        <mm:field name="description" escape="none"/>
        <a href="<mm:attachment/>"><mm:field name="title"/></a><br/>
    <%}%>
  </mm:relatednodes>   
</mm:relatednodes>
