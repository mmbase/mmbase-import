<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*,org.mmbase.applications.media.urlcomposers.*"
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
  <%  
    boolean subfragment = false; 
    boolean foundNonFragments = false;
   %>
<mm:field name="subfragment()" jspvar="bool">
     <% subfragment = ((Boolean) bool).booleanValue(); %>
</mm:field>
<h1><mm:field name="title" /></h1>
<table>
<tr><th>Description</th><th>URL</th></tr>
<mm:log jspvar="log">
<mm:field name="filteredurls(ram,wmp,rm)" jspvar="urls" vartype="list">
   <%
      Iterator i = urls.iterator();
      while(i.hasNext()) {
         URLComposer uc = (URLComposer) i.next();
         String url = uc.getURL();
         if (url.indexOf("://") == -1 ) url = thisServer(request,  url);
         String completeIndication;
         if (uc instanceof FragmentURLComposer || ! subfragment) {
           completeIndication = "";
         } else {
           completeIndication = " (*)";
           foundNonFragments = true;
         }
         out.println("<tr><td>" + uc.getGUIIndicator(locale) + "</td><td><a href='" + url + "'>" + url + "</a>" + completeIndication + "</td></tr>"); 
       

      }
   %>
</mm:field>
</mm:log>
</table>
<% if (foundNonFragments) { %>
  <hr />
  *: This URL cannot present this fragment. It is only contained by this URL.
<% } %>
</mm:node>
</body>
</html>
</mm:cloud>
</mm:locale>