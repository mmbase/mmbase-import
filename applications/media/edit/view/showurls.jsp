<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*,org.mmbase.applications.media.urlcomposers.*"
%><%@include file="../config/read.jsp" 
%><mm:locale language="$config.lang"><mm:cloud method="asis"><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
<script src="<mm:url page="../style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<body>
<mm:import externid="fragment" required="true" />
<mm:node number="$fragment">
  <%  
    boolean subfragment = false; 
    boolean foundNonFragments = false;
   %>
<mm:field name="issubfragment()" jspvar="bool">
     <% subfragment = ((Boolean) bool).booleanValue(); %>
</mm:field>
<h1><%= m.getString("urlsto") %>: <mm:field name="title" /></h1>

<mm:log jspvar="log">
<mm:field name="filteredurls(smil,html,ram,wmp,rm,mov)" jspvar="urls" vartype="list">
<%
      Iterator i = urls.iterator();
      int accesskey = 1;
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
         String description = uc.getDescription(options);
         out.println("<h3>" + uc.getGUIIndicator(options) + "</h3>" + 
                     "<p><a accesskey='" + accesskey++ + "' href='" + url + "'>" + url + "</a>" + completeIndication + "<//p>" + 
                     (description != null ? "<p>" + description + "</p>" : "")
                      ); 
       

      }
%>
</mm:field>
</mm:log>
<% if (foundNonFragments) { %>
  <hr />
  *: <%= m.getString("cannotpresent") %>
<% } %>
</mm:node>
</body>
</html>
</mm:cloud>
</mm:locale>