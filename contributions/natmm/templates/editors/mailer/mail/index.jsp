<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.authorization.*,
         nl.leocms.authorization.forms.ChangePasswordAction,
         com.finalist.mmbase.util.CloudFactory,
         org.mmbase.bridge.*" %>

<%@page import="nl.leocms.connectors.UISconnector.input.customers.model.*" %>


<mm:cloud method="http" rank="basic user" jspvar="cloud">

   <%
      if((request.getParameter("username") != null) && (request.getParameter("password") != null)){
         String sLogin = request.getParameter("username");
         String sPassword = request.getParameter("password");

         if((sLogin != null) && (sPassword != null)){
            Object object = nl.leocms.connectors.UISconnector.input.customers.process.Reciever.recieve(nl.leocms.connectors.UISconnector.UISconfig.getCustomersURL(sLogin, sPassword));

            if(object instanceof CustomerInformation){
               CustomerInformation customerInformation = (CustomerInformation) object;
               String memberid = nl.leocms.connectors.UISconnector.input.customers.process.Updater.update(customerInformation);
               %><%@include file="/natmm/includes/memberid_set.jsp" %><%

            }
            if(object instanceof String)
            {
               %>Exception:<%= object %><%
            }
         }
      }
   %>




   <%@include file="/natmm/includes/memberid_get.jsp" %>
   <%
      if(memberid == null){
         response.sendRedirect("login.jsp");
      }
      else{
         %>You have logged in<%
      }
   %>
</mm:cloud>
