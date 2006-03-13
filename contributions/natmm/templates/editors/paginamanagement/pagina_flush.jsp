<%@include file="../../taglibs.jsp"  %>
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Publiceer pagina</title>
   <style>
   input { width: 110px; padding-left: 3px; padding-right: 3px; }
   </style>
</head>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<%
String number = request.getParameter("number");
String refresh = request.getParameter("refresh");

if ((refresh != null) && (refresh.equals("yes"))) {

   // flush cache
   String referer = request.getParameter("referer");
   if(referer!=null&&referer.indexOf("&preview=on")>-1) {
      referer = referer.substring(0,referer.indexOf("&preview=on"));
   } else {
      referer = request.getHeader("referer");
   }
   session.setAttribute("preview","off"); 
   %>
   <cache:flush scope="application" group="<%= number %>" />
   <h2>
      <a href="<%= referer %>"><img src="../img/left.gif" title="Terug naar de preview"/></a>
      Pagina "<mm:node number="<%= number %>"><mm:field name="titel" /></mm:node>" is gepubliceerd.
   </h2>
   <%
} else {
   String referer = request.getHeader("referer");
   %>
   <body>   
   <h2>
      <a href="javascript:history.go(-1);"><img src="../img/left.gif" title="Terug naar de preview"/></a>
      Pagina "<mm:node number="<%= number %>"><mm:field name="titel" /></mm:node>"
   </h2>
   Weet u het zeker dat u deze pagina wilt publiceren?
   <form action="pagina_flush.jsp">
      <input type="hidden" name="number" value="<%= number %>">
      <input type="hidden" name="referer" value="<%= referer %>">
      <input type="hidden" name="refresh" value="yes">
      <input type="submit" value="Publiceer pagina"/>
   </form>
   <%
}
%>
</body>
</mm:cloud>
</html>
