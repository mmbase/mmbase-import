<%--
   Page to show after a logout.
--%>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="logout">
<html:html>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
<style type="text/css" >
a:link,
a:visited,
a:hover,
a:active  {         
        font-size: 15px;
}
</style>
   <body>
      <% request.getSession().invalidate(); %>
      
      <h1> U bent uitgelogd </h1>

      <br/>
      <br/>
      <a href="index.jsp">Klik hier om weer in te loggen</a>
   </body>
</html:html>
</mm:cloud>