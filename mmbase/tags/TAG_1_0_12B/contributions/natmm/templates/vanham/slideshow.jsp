<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,java.text.*,java.io.*,org.mmbase.bridge.*,org.mmbase.util.logging.Logger,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<mm:import externid="language" jspvar="language" vartype="String">nl</mm:import>
<mm:cloud jspvar="cloud">
<%@include file="includes/functions.jsp" %>
<% 

boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);

String imageId = request.getParameter("i");
String offsetId = request.getParameter("offset"); if(offsetId==null){ offsetId=""; }

String previousImage = "-1";
String nextImage = "-1";
String thisImage = "";
String otherImages = "";
int totalNumberOfImages = 1;
int thisImageNumber = 1;
%><%@include file="includes/splitimagelist.jsp" 
%><% String pageUrl = "slideshow.jsp?i="; 
%><html>
<head>
   <title>VAN HAM<mm:node number="<%= thisImage %>"> - <mm:field name="title" /></mm:node></title>
   <link rel="stylesheet" type="text/css" href="css/website.css">
   <style>
   td { 
      width: 30px;
      height: 30px;
      font-size: 140%;
      text-align: center;
   } 
   </style>
   <meta http-equiv="imagetoolbar" content="no">
</head>
<body class="popup">
<table cellpadding="0" cellspacing="0" border="0" style="width:780px;">
<tr>
   <% for(int i=0; i<24; i++) { %>
      <td><img src="media/spacer.gif" style="width:30px;height:1px;" /></td>
   <% } %>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td colspan="21" class="def" style="width:690px;padding-top:65px;padding-bottom:16px;">
      <mm:node number="<%= thisImage %>" jspvar="dummy">
         <table cellpadding="0" cellspacing="0" border="0" style="margin-bottom:5px;">
            <tr>
               <td colspan="2">
                  <a href="javascript:void(0);" onClick="window.close()" title="<bean:message bundle="<%= "VANHAM." + language %>" key="slide.click.on.photo" />">
                     <img src="<mm:image template="s(600)" />" border="0"></a>
               </td>
            </tr>
            <% 
            if(!nextImage.equals("-1")) {
               %>
               <tr>
                  <td style="height:1px;width:50%;text-align:left;<%= (isIE?"":"padding-top:5px;") %>">
                     <a href="slideshow.jsp?i=<%= previousImage %>"><img src="media/arrowleft.gif" border="0" title="previous"></a>
                  </td>
                  <td style="height:1px;width:50%;text-align:right;<%= (isIE?"":"padding-top:5px;") %>">
                     <a href="slideshow.jsp?i=<%= nextImage %>"><img src="media/arrowright.gif" border="0" title="next"></a>
                  </td>
               </tr>
               <%
            } %>
         </table>
         <b><%= LocaleUtil.getField(dummy,"titel",language) %></b><br/>
         <%= LocaleUtil.getField(dummy,"omschrijving",language) %><br/>
         <mm:field name="bron">
            <mm:isnotempty><bean:message bundle="<%= "VANHAM." + language %>" key="slide.photography" />: <mm:write /><br/></mm:isnotempty>
         </mm:field>
      </mm:node>
   </td>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">V</td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">A</td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">N</td>
   <td colspan="18" style="width:90px;"></td>
</tr>
<tr>
   <td colspan="3" style="width:90px;"></td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">H</td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">A</td>
   <td class="vh" style="width:30px;<%= (isIE?"":"padding-top:3px;") %>">M</td>
   <td colspan="18"></td>
</tr>
</table>
</body>
</html>
</mm:cloud>
