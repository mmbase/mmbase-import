<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@include file="readconfig.jsp" 
%><html>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
  <title>Configuration</title>
</head>
<body>
 <h1>Configuration</h1>
 <mm:import externid="referrer">search.jsp</mm:import>
  <form name="config" method="post">
  <table class="edit" summary="streammager configuration">  
   <tr><td>Language</td>  
         <td><input type="text" size="30" name="lang" value="<mm:write referid="config.lang" />" />
              <select name="languages" onChange="document.forms['config'].elements['lang'].value = document.forms['config'].elements['languages'].value;">
           <mm:import id="langs" vartype="list">en,nl</mm:import>
           <mm:aliaslist referid="langs">
             <option value="<mm:write />" <mm:compare referid2="config.lang"><mm:import id="found" />selected="selected"</mm:compare>><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:aliaslist>
           <mm:notpresent referid="found">
             <option value="<mm:write referid="config.lang" />" selected="selected"><mm:locale language="$config.lang" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:notpresent>            
         </select>
         </td>
     </tr>
     <tr><td>Format</td>  
         <td>
          <select name="format">
           <mm:write referid="config.format">
             <option value="" <mm:compare value="">selected="selected"</mm:compare>>Any</option>
             <option value="rm"  <mm:compare value="rm">selected="selected"</mm:compare>>Real audio</option>
             <option value="asf" <mm:compare value="asf">selected="selected"</mm:compare>>Windows Media</option>
           </mm:write>
         </select>
         </td></tr>
     <tr><td>Quality</td>  
         <td>
          <select name="quality">
           <mm:write referid="config.quality">
             <option value="" <mm:compare value="">selected="selected"</mm:compare>>Any</option>
             <option value="sb"  <mm:compare value="sb">selected="selected"</mm:compare>>Small band</option>
             <option value="bb" <mm:compare value="bb">selected="selected"</mm:compare>>Broad band</option>
           </mm:write>
         </select>
         </td></tr>
     <tr><td>Player</td>  
         <td>
          <select name="player">
           <mm:write referid="config.player">
             <option value="" <mm:compare value="">selected="selected"</mm:compare>>Any</option>
             <option value="wm"  <mm:compare value="sb">selected="selected"</mm:compare>>window media player</option>
             <option value="real" <mm:compare value="bb">selected="selected"</mm:compare>>real player</option>
           </mm:write>
         </select>
         </td></tr>
     <tr><td colspan="2"><button type="submit" value="config" name="config">Config</button></td></tr>
     </tr>
  </table>
  </form>
  <hr />
  <p>
    <a href="<mm:url page="$referrer" />">Back</a>
  </p>   
</body>
</html>
