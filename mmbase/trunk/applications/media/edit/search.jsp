<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="readconfig.jsp" %><mm:locale language="$config.lang"><mm:cloud jspvar="cloud" loginpage="login.jsp"><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
   <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<body class="left"  onload="initLeft('entrance');">

 <h1>Zoek</h1>
  <p>
  <table>
  <form target="content" action="<mm:url page="view.jsp" />" >   
    <tr><td>Type</td><td><select name="type">
       <option value="mediafragments">Audio/Video</option>
       <option value="audiofragments">Audio</option>
       <option value="videofragments">Video</option>
     </select></td></tr>
    <tr><td>Group</td><td><select name="origin">
       <option value="">Any</option>
       <mm:node number="media.streams">
       <mm:related path="parent,pools2" orderby="pools2.name">
        <mm:context>
         <mm:node id="origin" element="pools2">
           <option value="<mm:field name="number" />"><mm:field name="name" /></option>
         </mm:node>
        </mm:context>
       </mm:related>
       </mm:node>
     </select></td></tr>
    <tr><td>Owner</td><td><input type="text" name="owner" /></td></tr>
    <tr><td>Tekst</td><td><input type="text" name="searchvalue" /></td></tr>
    <tr><td>Verzenden</td><td><button type="submit"><img src="media/search.gif" /></button></td></tr>
  </form>
  </table>
  </p>
  <p>
  <hr />
  <p align="right">
    <a target="content" href="<mm:url page="edit.jsp" />">Edit</a>
 |
    <a href="<mm:url page="config.jsp" />">Config</a>
  </p>
</body>
</html>
</mm:cloud>
</mm:locale>