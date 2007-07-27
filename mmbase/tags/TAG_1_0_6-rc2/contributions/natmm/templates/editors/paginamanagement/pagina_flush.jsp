<%@include file="../../taglibs.jsp"  %>
<%@page import="nl.leocms.servlets.UrlConverter" %>
<html>
<head>
   <link href="<%= editwizard_location %>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<%= editwizard_location %>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Publiceer pagina</title>
   <style>
   input { width: 110px; padding-left: 3px; padding-right: 3px; }
   </style>
</head>
<mm:cloud jspvar="cloud">
<%
String number = request.getParameter("number");
String refresh = request.getParameter("refresh");

if ((refresh != null) && (refresh.equals("yes"))) {

   // flush cache
   String referrer = request.getParameter("referrer");
   if(referrer!=null&&referrer.indexOf("&preview=on")>-1) {
      referrer = referrer.substring(0,referrer.indexOf("&preview=on"));
   } else {
      referrer = request.getHeader("referer"); // html specs are wrong
   }
   referrer += "&rnd=" + (int) (Math.random()*100000);
   session.setAttribute("preview","off");
   UrlConverter.getCache().flushAll();
   %>
   <cache:flush scope="application" group="<%= number %>" />
   <h2>
      <a href="<%= referrer %>"><img src="../img/left.gif" title="Terug naar de preview"/></a>
      Pagina "<mm:node number="<%= number %>"><mm:field name="titel" /></mm:node>" is gepubliceerd.
   </h2>
	<span style="color:red">Let op:</span> Pagina's kunnen in de beheeromgeving ook door andere redacteuren of automatisch worden gepubliceerd.<br/>
	De door u opgeslagen wijzigingen kunnen dus voor de bezoeker van de website zichtbaar worden, zonder dat u de pagina heeft gepubliceerd.<br/>
   <%
} else {
   String referrer = request.getHeader("referer"); // html-specs are wrong
   %>
   <body>   
   <h2>
      <a href="javascript:history.go(-1);"><img src="../img/left.gif" title="Terug naar de preview"/></a>
      Pagina "<mm:node number="<%= number %>"><mm:field name="titel" /></mm:node>"
   </h2>
   Weet u het zeker dat u deze pagina wilt publiceren?
   <form action="pagina_flush.jsp">
      <input type="hidden" name="number" value="<%= number %>" />
      <input type="hidden" name="referrer" value="<%= referrer %>" />
      <input type="hidden" name="refresh" value="yes" />
      <input type="hidden" name="rnd" value="<%= (int) (Math.random()*100000) %>" />
      <input type="submit" value="Publiceer pagina"/>
   </form>
   <%
}
%>
</body>
</mm:cloud>
</html>
