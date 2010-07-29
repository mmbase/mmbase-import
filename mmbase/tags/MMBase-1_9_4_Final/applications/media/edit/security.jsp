<DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" type="text/html" expires="0">
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<body  onload="setContentFrame('<mm:url page="placeholder.jsp" />');" class="left">   
  <mm:import id="current">security</mm:import>
  <%@include file="submenu.jsp" %>
   <hr />
   <h1><%=m.getString("security")%></h1>
   <ul>
     <mm:node id="origin" number="$config.mediaeditors_origin" notfound="skip">
       <li><a href="javascript:setContentFrame('<mm:url referids="origin" page="pooleditor.jsp" />');"><%=m.getString("categories")%> voor <mm:field name="name" /></a></li>
     </mm:node>
     <mm:hasrank value="administrator">
       <li><a href="javascript:setContentFrame('<mm:url page="pooleditor.jsp?origin=media.allstreams&subcats=yes" />');"><%=m.getString("categories")%></a></li>
       <li><a href="javascript:setContentFrame('<mm:url page="accounts.jsp" />');">Accounts</a></li>
     </mm:hasrank>
   </ul>  
   <hr />
   <mm:cloudinfo type="user" />/<mm:cloudinfo type="rank" />
</body>
</html>
</mm:cloud>
</mm:content>
