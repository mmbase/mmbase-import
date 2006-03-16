<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.authorization.*,
         nl.leocms.authorization.forms.ChangePasswordAction,
         com.finalist.mmbase.util.CloudFactory,
         org.mmbase.bridge.Cloud,
         org.mmbase.bridge.Node" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
  Cloud adminCloud = CloudFactory.getCloud();
  String url = "/editors/usermanagement/changepassword.jsp" ;
  String status = "valid";

  String username = cloud.getUser().getIdentifier();
  AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
  int number = authorizationHelper.getUserNode(username).getNumber();
  Node userNode = adminCloud.getNode(number);

  int iGracelogins = userNode.getIntValue("gracelogins");
  long expireDate = userNode.getLongValue("expiredate");

  long currentDate = System.currentTimeMillis() / 1000;

  if (expireDate-10*24*60*60 < currentDate) {
    status = "warning";
    if (expireDate < currentDate) { // password expired, checks gracelogins
       if (iGracelogins > 0) {
         status = "gracelogin"; // expired, gracelogin present.
         userNode.setIntValue("gracelogins", iGracelogins-1);
         userNode.commit();
       } else { // expired
        status = "expired";
       }
    }
  }
%>
<html>
   <head>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
      <title>Beheeromgeving</title>
   </head>
   <%
   if ("expired".equals(status)) {
      if(!userNode.getStringValue("rank").equals("anonymous")) {
         userNode.setStringValue("originalrank",userNode.getStringValue("rank"));
         userNode.setStringValue("rank","anonymous");
         userNode.commit();
      }
      %>
      <body>
         <h2>Uw password is verlopen.</h2>
         Neem contact op met de systeembeheerder voor een nieuw password.
      </body>
      <%
   } else {
      if (!"valid".equals(status)) {
         url += "?status=" + status;
         %>
         <script type="text/javascript">
           window.open("<%= url %>", "ha_dialog","toolbar=no,menubar=no,personalbar=no,width=400,height=250,scrollbars=no,resizable=no");
         </script>
         <%
      }
      %>
      <html>
         <frameset rows="80,*" framespacing="2" frameborder="1">
            <frame src="topmenu.jsp" name="toppane" frameborder="0" scrolling="auto">
            <frame src="empty.html" name="bottompane" frameborder="0" scrolling="yes">
         </frameset>
      </html>
      <%
   }
%>
</html>
</mm:cloud>
