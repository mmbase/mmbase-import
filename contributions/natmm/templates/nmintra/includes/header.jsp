   <%@include file="../includes/getids.jsp" 
   %><%@include file="../includes/pagecounter.jsp"    
   %><%@include file="../includes/getresponse.jsp" 
	%><%--@include file="../includes/shoppingcartupdate.jsp" 
   --%><% templateTitle = "indexsite"; 
   %><mm:list nodes="<%= pageId %>" path="pagina,gebruikt,paginatemplate"
         ><mm:field name="paginatemplate.naam" jspvar="dummy" vartype="String" write="false"><%
               templateTitle = dummy; 
         %></mm:field
   ></mm:list
   ><%@include file="../includes/cacheopen.jsp" 
   %><%--cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"
   --%><%@include file="../includes/templatesettings.jsp" 
   %><html>
     <head>
         <link rel="stylesheet" type="text/css" href="css/website.css">
         <title><% if(isPreview) { %>PREVIEW: <% } 
             %><mm:node number="<%= websiteId %>" notfound="skipbody"><mm:field name="naam" /></mm:node
             > - <mm:node number="<%= pageId %>" notfound="skipbody"><mm:field name="titel" /></mm:node></title>
         <meta http-equiv="imagetoolbar" content="no">
         <script language="javascript" src="scripts/launchcenter.js"></script>
         <script language="javascript" src="scripts/cookies.js"></script>
         <script language="javaScript" src="scripts/screensize.js"></script>
     </head>
     <body class="<%= cssClassName %>" scroll="auto" onUnLoad="javascript:setScreenSize()">
   <table background="media/<%= cssClassName %>.jpg" cellspacing="0" cellpadding="0">
   <%@include file="../includes/searchbar.jsp" %>
   <tr>
      <td class="black"><img src="media/spacer.gif" width="195" height="1"></td>
      <td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
      <td class="black"><img src="media/spacer.gif" width="251" height="1"></td>
   </tr>
   <tr>

   <% if(!isPreview&&!isVisible(cloud,websiteId,rubriekId,pageId,visitorGroup,out)) { pageId = ""; } %>
   <td rowspan="2"><%@include file="../includes/nav.jsp" %></td>