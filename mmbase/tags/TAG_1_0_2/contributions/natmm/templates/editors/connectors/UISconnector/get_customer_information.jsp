<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>

<%@ page import = "java.util.*" %>


<%@ page import = "nl.leocms.connectors.UISconnector.input.customers.model.*" %>
<%@ page import = "nl.leocms.connectors.UISconnector.UISconfig" %>

<mm:cloud method="http" jspvar="cloud" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">

   <%
      String sLogin = request.getParameter("login");
      String sPassword = request.getParameter("password");

      if((sLogin != null) && (sPassword != null)){

         Object object = nl.leocms.connectors.UISconnector.input.customers.process.Reciever.recieve(UISconfig.getCustomersURL(sLogin, sPassword));

         if(object instanceof CustomerInformation)
         {
            CustomerInformation customerInformation = (CustomerInformation) object;

            %>PERSONAL:<br/><%
            %>first name:<%= customerInformation.getPersonalInformation().getFirstName() %><br/><%
            %>suffix:<%= customerInformation.getPersonalInformation().getSuffix() %><br/><%
            %>last name:<%= customerInformation.getPersonalInformation().getLastName() %><br/><%
            %>initials:<%= customerInformation.getPersonalInformation().getInitials() %><br/><%
            %>gender:<%= customerInformation.getPersonalInformation().getGender() %><br/><%
            %>phone:<%= customerInformation.getPersonalInformation().getTelephoneNo() %><br/><%
            %>email:<%= customerInformation.getPersonalInformation().getEmailAddress() %><br/><%
            %>birthdate:<%= customerInformation.getPersonalInformation().getBirthDate() %><br/><%

            %><br/><br/>BUSINESS:<br/><%
            %>phone:<%= customerInformation.getBusinessInformation().getTelephoneNo() %><br/><%

            %><br/><br/>COMMON:<br/><%
            %>customerID:<%= customerInformation.getCommonInformation().getCustomerId() %><br/><%

            %><br/><br/>ADDRESS:<br/><%
            %>city:<%= customerInformation.getAddress().getCity() %><br/><%
            %>countryID:<%= customerInformation.getAddress().getCountryID() %><br/><%
            %>streetName:<%= customerInformation.getAddress().getStreetName() %><br/><%
            %>zip:<%= customerInformation.getAddress().getZipCode() %><br/><%
            %>house:<%= customerInformation.getAddress().getHouseNumber() %>-<%= customerInformation.getAddress().getHouseNumberExtension() %><br/><%
            %>ExtraInfo:<%= customerInformation.getAddress().getExtraInfo() %><br/><%


            %><br/><br/>PROPERTIES:<br/><%
            for(Iterator it = customerInformation.getPropertyList().getProperty().iterator(); it.hasNext();){
               Property property = (Property) it.next();
               %><b>id:<%= property.getPropertyId() %></b><br/><%
               %><b>description:<%= property.getPropertyDescription() %></b><br/><%
               %>--- values ---<br/> <%
               for(Iterator it2 = property.getPropertyValue().iterator(); it2.hasNext();){
                  PropertyValue propertyValue = (PropertyValue) it2.next();
                  %><%= propertyValue.getPropertyValueId() %>-<%= propertyValue.getPropertyValueDescription() %><br/><%
               }
            }

         }
         if(object instanceof String)
         {
            %>Exception:<%= object %><%
         }
      }
      else{
         %>
            <form action="">
               <input type="text" name="login"/>
               <input type="text" name="password"/>
               <input type="submit"/>
            </form>
         <%
      }
   %>
</mm:log>
</mm:cloud>
