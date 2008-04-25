<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@include file="read.jsp" 
%><mm:content language="$config.lang" type="text/html" expires="0">
<html>
  <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
  <title><%=m.getString("configuration")%></title>
</head>

<mm:cloud jspvar="cloud" method="asis">
<mm:import id="user"><%=cloud.getUser().getIdentifier()%></mm:import>
<body class="left">
  <mm:import id="current">config</mm:import>
  <mm:import id="dir">../</mm:import>
  <%@include file="../submenu.jsp" %>
  <hr />
  <h1><%=m.getString("configuration")%> <mm:present referid="config.configsubmitted">*</mm:present></h1>
  <form name="config" method="post">
    <table class="edit" summary="streammager configuration">  
      <tr>
        <td>Streammanager</td>  
        <td>
          <select name="mediaeditors_origin_<mm:write referid="user" />">
            <option value="" <mm:compare referid="config.mediaeditors_origin" value=""> selected="selected" </mm:compare>><%=m.getString("any")%></option>
            <mm:node number="media.allstreams">
              <mm:relatednodes id="origin" searchdir="destination" role="parent" type="pools" orderby="name"> 
                <option value="<mm:field id="org" name="number" />" <mm:compare referid2="config.mediaeditors_origin" referid="org"> selected="selected" </mm:compare> ><mm:field name="name" /></option>
              </mm:relatednodes>
            </mm:node>            
          </select>
        </td>
      </tr>
      <tr>
        <td><%=m.getString("language")%></td>  
        <td>
          <nobr>
            <input type="text" size="5" name="mmjspeditors_language" value="<mm:write referid="config.lang" />" />
            <select name="languages" onChange="document.forms['config'].elements['mmjspeditors_language'].value = document.forms['config'].elements['languages'].value;">
              <mm:import id="langs" vartype="list">en,nl</mm:import>
              <mm:aliaslist referid="langs">
                <option value="<mm:write />" <mm:compare referid2="config.lang"><mm:import id="found" />selected="selected"</mm:compare>><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
              </mm:aliaslist>
              <mm:notpresent referid="found">
                <option value="<mm:write referid="config.lang" />" selected="selected"><mm:locale language="$config.lang" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
              </mm:notpresent>            
            </select>
          </nobr>
        </td>
      </tr>
      <!--
      <tr>
        <td><%=m.getString("quality")%></td>  
        <td>
          <select name="mediaeditors_quality">
            <mm:write referid="config.quality">
              <option value="any" <mm:compare value="any">selected="selected"</mm:compare>><%=m.getString("any")%></option>
              <option value="sb"  <mm:compare value="sb">selected="selected"</mm:compare>><%=m.getString("smallband")%></option>
              <option value="bb" <mm:compare value="bb">selected="selected"</mm:compare>><%=m.getString("broadband")%></option>
            </mm:write>
          </select>
        </td>
      </tr>
      <tr>
        <td><%=m.getString("player")%></td>  
        <td>
          <select name="mediaeditors_player">
            <mm:write referid="config.player">
              <option value="any" <mm:compare value="any">selected="selected"</mm:compare>><%=m.getString("any")%></option>
              <option value="wm"  <mm:compare value="wm">selected="selected"</mm:compare>>window media player</option>
              <option value="real" <mm:compare value="real">selected="selected"</mm:compare>>real player</option>
              <option value="qt" <mm:compare value="qt">selected="selected"</mm:compare>>quick time player</option>
            </mm:write>
          </select>
        </td>
      </tr>
      -->
     <tr>
       <td /><td><button type="submit" value="config" name="config"><img src="../media/neworg.gif" /></button></td>
     </tr>
   </table>
 </form>
</body>
</mm:cloud>
</html>
</mm:content>