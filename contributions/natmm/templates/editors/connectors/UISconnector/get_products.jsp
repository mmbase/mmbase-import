<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<%@ page import = "java.net.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "javax.xml.parsers.*" %>
<%@ page import = "org.xml.sax.*" %>
<%@ page import = "org.w3c.dom.Document" %>
<%@ page import = "java.text.SimpleDateFormat" %>

<%@ page import = "nl.leocms.connectors.UISconnector.*" %>
<%@ page import = "nl.leocms.connectors.UISconnector.input.products.xml.*" %>
<%@ page import = "nl.leocms.connectors.UISconnector.input.products.model.*" %>
<%@ page import = "nl.leocms.connectors.UISconnector.input.products.process.*" %>

<%@ page import = "nl.leocms.connectors.UISconnector.shared.properties.model.*" %>

<mm:cloud method="http" jspvar="cloud" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<%
   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   //--------------------Let's get the document--------------------
   URL url = new URL(UISconfig.getProductUrl());

   URLConnection connection = url.openConnection();

   // to do: add test on availability of url, otherwise execution ends without any output
   BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

   //--------------------Let's try to parse it--------------------
   Document document = null;
   try{
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dbuilder = dfactory.newDocumentBuilder();

      InputStream is = new BufferedInputStream(in);
      document = dbuilder.parse(is);

   }
   catch (ParserConfigurationException pce){
      log.info(pce);
   }
   catch (SAXException se){
      log.info(se);
   }
   catch (IOException ie){
      log.info(ie);
   }

   //Let's get Model
   ArrayList arliModel = Decoder.decode(document);
   System.out.println("========" + arliModel.size());


   //Let's update the db
   ArrayList arliChanges = Updater.update(cloud, arliModel);
   if(arliChanges.size()==0) {
      %>No changes found in <%= UISconfig.getProductUrl() %><br/><%
   } else {
   %>
   <table border="1" cellpadding="5" cellspacing="0">
    <tr>
       <td>Status</td>
       <td>Evenement Node</td>
       <td>externid</td>
       <td>embargo</td>
       <td>verloopdatum</td>
       <td>price</td>
       <td>registration</td>
       <td>Payment types</td>
       <td>Properties</td>
    </tr>
   <%
   for(Iterator it = arliChanges.iterator(); it.hasNext();){
       Result result = (Result) it.next();

         if(result.getStatus() == Result.EXCEPTION){
          %><tr><td>Exception:</td><td><%= result.getProduct().getExternID() %></td><td colspan="3"><%=result.getException() %></td></tr><%
       } else {

              %><tr><%

           %><td><%
          switch(result.getStatus()){
             case Result.ADDED:{
           %>Added<%;
           break;
             }
             case Result.UPDATED:{
           %>Updated<%;
           break;
             }

          }
       %></td><%

       %><td><%= result.getEvenementNode().getNumber() %></td><%
       %><td><%= result.getProduct().getExternID() %></td><%
       %><td><%= df.format(result.getProduct().getEmbargoDate()) %></td><%
       %><td><%= df.format(result.getProduct().getExpireDate()) %></td><%
       %><td><%= result.getProduct().getPrice() %></td><%
       %><td><%= result.getProduct().isMembershipRequired() %></td><%

       %><td><%
          for(Iterator it2 = result.getProduct().getPaymentTypes().iterator(); it2.hasNext();){
             PaymentType paymentType = (PaymentType) it2.next();
             %><%= paymentType.getId() %> - <%= paymentType.getDescription() %><br/><%
          }
       %></td><%



       %><td><%

          %>
          <table width="100%" border="1" cellpadding="5" cellspacing="0">
           <tr>
              <td>Property ID</td>
              <td>Description</td>
              <td>Property Value</td>
           </tr>
          <%
          for(Iterator it2 = result.getProduct().getProperties().iterator(); it2.hasNext();){
              Property property = (Property) it2.next();

              %></tr><%

              %><td><%= property.getPropertyId() %></td><%
              %><td><%= property.getPropertyDescription() %></td><%
              %><td><table width="100%" border="1" cellpadding="3" cellspacing="0">
                 <tr><td>Externid</td><td>Description</td></tr>
                 <%
                 for(Iterator it3 = property.getPropertyValues().iterator(); it3.hasNext();){
                    PropertyValue propertyValue = (PropertyValue) it3.next();
                    %><tr><td><%= propertyValue.getPropertyValueId() %></td><td><%= propertyValue.getPropertyValueDescription() %> </td></tr><%
                 }
              %></table></td><%

              %></tr><%
          }
          %></table><%


       %></td><%


               %></tr><%
       }
   }
   %></table><%
    }
%>

</mm:log>
</mm:cloud>
