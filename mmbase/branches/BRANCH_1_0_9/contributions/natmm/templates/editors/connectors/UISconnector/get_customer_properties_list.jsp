<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<%@ page import = "java.net.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "javax.xml.parsers.*" %>
<%@ page import = "org.xml.sax.*" %>
<%@ page import = "org.w3c.dom.Document" %>
<%@ page import = "java.text.SimpleDateFormat" %>

<%@ page import = "nl.leocms.connectors.UISconnector.*" %>
<%@ page import = "nl.leocms.connectors.UISconnector.shared.properties.model.*" %>


<mm:cloud method="http" jspvar="cloud" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
<head>
   <title>Import products from UIS</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <style>
      p { margin: 0px; }
   </style>
</head>
<body style="overflow:auto;">
<%
   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   //--------------------Let's get the document--------------------
   URL url = new URL(UISconfig.getCustomerPropertiesURL());

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
   ArrayList arliProperties = nl.leocms.connectors.UISconnector.shared.properties.xml.Decoder.decode(document.getDocumentElement());

   //Let's update the db
   nl.leocms.connectors.UISconnector.shared.properties.process.PropertyUtil.setProperties(cloud, null, arliProperties);


   %>
   Imported customer properties from <%= UISconfig.getCustomerPropertiesURL() %><br/><br/>
   <table class="formcontent" border="1"><%
          for(Iterator it2 = arliProperties.iterator(); it2.hasNext();){
              Property property = (Property) it2.next();

              %><tr><%

              %><td style="vertical-align:top;"><%= property.getPropertyId() %></td><%
              %><td style="vertical-align:top;"><%= property.getPropertyDescription() %></td><%
              %><td><table width="100%" border="1" class="formcontent">
                 <tr><td>Externid</td><td>Description</td></tr>
                 <%
                 for(Iterator it3 = property.getPropertyValues().iterator(); it3.hasNext();){
                    PropertyValue propertyValue = (PropertyValue) it3.next();
                    %><tr><td><%= propertyValue.getPropertyValueId() %></td><td><%= propertyValue.getPropertyValueDescription() %> </td></tr><%
                 }
              %></table></td><%

              %></tr><%
          }
   %></table>

 </body>
</html>
</mm:log>
</mm:cloud>
