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

<mm:cloud method="http" jspvar="cloud" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<%
   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   //--------------------Let's get the document--------------------
   URL url = new URL(UISconfig.PRODUCT_URL);

//   URL url = new URL("file:///Z:/getProducts.jsp.xml");
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
/*

   for(Iterator it = arliModel.iterator(); it.hasNext();){
      Product product = (Product) it.next();

      %>-----<br/><%
      %><%=product.getExternID() %><br/><%
      %><%=product.getPrice() %><br/><%
      %><%=product.getEmbargoDate() %><br/><%
      %><%=product.getExpireDate() %><br/><%
      %><%=product.isMembershipRequired() %><br/><%

      %>__<br/><%
      for(Iterator it2 = product.getPaymentTypes().iterator(); it2.hasNext();){
         PaymentType paymentType = (PaymentType) it2.next();
         %><%= paymentType.getId() %> - <%= paymentType.getDescription() %><br/><%
      }
   }
*/

   //Let's update the db
   ArrayList arliChanges = Updater.update(cloud, arliModel);
   if(arliChanges.size()==0) {
   	%>No changes found in <%= UISconfig.PRODUCT_URL %><br/><%
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

   	         %></tr><%
	    }
	}
	%></table><%
    }
%>

</mm:log>
</mm:cloud>
