<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.authorization.*,
         nl.leocms.authorization.forms.ChangePasswordAction,
         com.finalist.mmbase.util.CloudFactory,
         org.mmbase.bridge.*" %>

<%@page import="nl.leocms.connectors.UISconnector.input.customers.model.*" %>


<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
   <%
    String sLogin = request.getParameter("username");
    String sPassword = request.getParameter("password");    
    log.info("Found " + sLogin + " and " + sPassword);
    if((sLogin != null) && (sPassword != null)){
      Object object = nl.leocms.connectors.UISconnector.input.customers.process.Reciever.recieve(nl.leocms.connectors.UISconnector.UISconfig.getCustomersURL(sLogin, sPassword));
    
      if(object instanceof CustomerInformation){
         CustomerInformation customerInformation = (CustomerInformation) object;
         String memberid = nl.leocms.connectors.UISconnector.input.customers.process.Updater.update(customerInformation);
         log.info("Set memberid " + memberid);
         %><%@include file="/natmm/includes/memberid_set.jsp" %><%
    
      }
      if(object instanceof String)
      {
         log.info("Exception " + object);
      }
    }
   %>
   <%@include file="/natmm/includes/memberid_get.jsp" %>
   <%
      if(memberid == null){
         log.info("Did not find a memberid");
         response.sendRedirect("login.jsp");
      }
      else{
         log.info("Found memberid");
         %>You have logged in<%
      }
   %>
</mm:log>
</mm:cloud>
