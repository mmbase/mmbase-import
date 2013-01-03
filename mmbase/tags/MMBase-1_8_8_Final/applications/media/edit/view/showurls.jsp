<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page import="java.util.*,org.mmbase.applications.media.urlcomposers.*"
%><%@include file="../config/read.jsp" 
%><mm:content language="$config.lang" type="text/html" expires="0">
<mm:cloud method="asis">
<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
  <link href="../style/streammanager.css" type="text/css" rel="stylesheet" />
  <script src="<mm:url page="../style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>
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
    <mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
    <mm:field name="intro" escape="p" />
    <mm:field name="body" escape="p" />
    <mm:relatednodes id="super" type="mediafragments" role="posrel" searchdir="source">
      (<a href="<mm:url referids="super@fragment" />"><mm:field name="title" /></a>)
    </mm:relatednodes>
    <mm:log jspvar="log">
      <mm:functioncontainer>
        <mm:param name="format" value="smil,html,ram,rm,wmp,asf,mov" />
        <% char accesskey = '1'; %>
        <mm:listfunction name="filteredurls" jspvar="object">
          <%
          URLComposer uc = (URLComposer) object;
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
                     "<!--" + uc.getClass().getName() + " -->" +
                     "<p title='" + uc.getFormat() + "/" + uc.getMimeType() + "'><a accesskey='" + accesskey++ + "' href='" + url + "'>" + url + "</a>" + completeIndication + "</p>" + 
                     (description != null ? "<p>" + description + "</p>" : "")
                      ); 
       

       %>
     </mm:listfunction>
   </mm:functioncontainer>
   </mm:log>
   <% if (foundNonFragments) { %>
   <hr />
   *: <%= m.getString("cannotpresent") %>
   <% } %>
 </mm:node>
</body>
</html>
</mm:cloud>
</mm:content>