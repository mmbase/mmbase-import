<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.connectors.UISconnector.input.customers.model.*" %>
<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
   <mm:import externid="action"></mm:import>
   <mm:compare referid="action" value="">
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
         response.sendRedirect("login.jsp?reason=failed");
      }
      else{
         log.info("Found memberid");
         response.sendRedirect("dossier.jsp");
      }
   %>
   </mm:compare>
   <mm:compare referid="action" value="logout">
      <% String memberid="-1"; %>
      <%@include file="/natmm/includes/memberid_set.jsp" %>
      <% 
         response.sendRedirect("login.jsp");
      %>
   </mm:compare>
</mm:log>
</mm:cloud>
