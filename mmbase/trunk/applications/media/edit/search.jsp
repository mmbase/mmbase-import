<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="language">nl</mm:import><mm:locale language="$language"><mm:cloud jspvar="cloud" loginpage="login.jsp"><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.util.media.resources.mediaedit", locale);
%>
</mm:write>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;language=$language" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<body class="left"  onload="initLeft('entrance');">

 <h1>Zoek</h1>
  <p>
  <table>
  <form target="content" action="view.jsp" >   
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
    <input type="hidden" name="language" value="<mm:write referid="language" />" />
  </form>
  </table>
  </p>
  <p>
  <hr />
  <p align="right">
    <a target="content" href="<mm:url page="edit.jsp" />">Edit</a>
  </p>
</body>
</html>
</mm:cloud>
</mm:locale>