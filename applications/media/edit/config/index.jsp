<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@include file="read.jsp" 
%><html>
  <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
  <title>Configuration</title>
</head>
<body>
 <h1>Configuration</h1>
 <mm:import externid="referrer">../search.jsp</mm:import>
  <form name="config" method="post">
  <table class="edit" summary="streammager configuration">  
   <tr><td><%=m.getString("language")%></td>  
         <td><nobr><input type="text" size="5" name="lang" value="<mm:write referid="config.lang" />" />
              <select name="languages" onChange="document.forms['config'].elements['lang'].value = document.forms['config'].elements['languages'].value;">
           <mm:import id="langs" vartype="list">en,nl</mm:import>
           <mm:aliaslist referid="langs">
             <option value="<mm:write />" <mm:compare referid2="config.lang"><mm:import id="found" />selected="selected"</mm:compare>><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:aliaslist>
           <mm:notpresent referid="found">
             <option value="<mm:write referid="config.lang" />" selected="selected"><mm:locale language="$config.lang" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:notpresent>            
         </select></nobr>
         </td>
     </tr>
     <tr><td><%=m.getString("quality")%></td>  
         <td>
          <select name="quality">
           <mm:write referid="config.quality">
             <option value="any" <mm:compare value="any">selected="selected"</mm:compare>><%=m.getString("any")%></option>
             <option value="sb"  <mm:compare value="sb">selected="selected"</mm:compare>><%=m.getString("smallband")%></option>
             <option value="bb" <mm:compare value="bb">selected="selected"</mm:compare>><%=m.getString("broadband")%></option>
           </mm:write>
         </select>
         </td></tr>
     <tr><td><%=m.getString("player")%></td>  
         <td>
          <select name="player">
           <mm:write referid="config.player">
             <option value="any" <mm:compare value="any">selected="selected"</mm:compare>><%=m.getString("any")%></option>
             <option value="wm"  <mm:compare value="wm">selected="selected"</mm:compare>>window media player</option>
             <option value="real" <mm:compare value="real">selected="selected"</mm:compare>>real player</option>
             <option value="qt" <mm:compare value="qt">selected="selected"</mm:compare>>quick time player</option>
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
