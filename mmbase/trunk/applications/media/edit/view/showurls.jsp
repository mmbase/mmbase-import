<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*,org.mmbase.module.builders.media.ResponseInfo"
%><%@include file="../config/read.jsp" 
%><mm:locale language="$config.lang"><mm:cloud><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<body>
<mm:import externid="fragment" required="true" />
<mm:node number="$fragment">
<h1><mm:field name="title" /></h1>
<table>
<tr><th>Format</th><th>URL</th></tr>
<mm:log jspvar="log">
<mm:field name="sortedurls(ram,wmp)" jspvar="urls" vartype="list">
   <%
      Iterator i = urls.iterator();
      while(i.hasNext()) {
         ResponseInfo ri = (ResponseInfo) i.next();
         String url = ri.getURL();
         if (url.indexOf("://") == -1 ) url = thisServer(request, ri.getURL());
         out.println("<tr><td>" + ri.getFormat() + "</td><td><a  href='" + url + "'>" + url + "</a></td></tr>"); 

      }
   %>
</mm:field>
</mm:log>
</table>
</mm:node>
</body>
</html>
</mm:cloud>
</mm:locale>