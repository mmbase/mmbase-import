
<tr>
  <td bgcolor="#f8eee3" class="plaintext" align="center">
    <OBJECT 
        classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
        codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0"
        ID="cmshelp" 
        WIDTH="700" 
        HEIGHT="480" 
        ALIGN="">
    
      <PARAM NAME="movie" VALUE="<%=request.getParameter("url")%>">
      <PARAM "NAME"=quality VALUE="high">
      <PARAM NAME="bgcolor" VALUE="#FFFFFF">
      
      <EMBED 
          src="<%=request.getParameter("url")%>" 
          quality="high"
          bgcolor="#FFFFFF" 
          swLiveConnect="FALSE" 
          WIDTH="700" 
          HEIGHT="480"
          NAME="chat_eo" 
          ALIGN="" 
          TYPE="application/x-shockwave-flash"
          PLUGINSPAGE="http://www.macromedia.com/go/getflashplayer">
      </EMBED>
      
      <br />
    </OBJECT>
  </td>
</tr>

