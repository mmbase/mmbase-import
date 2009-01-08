<%
   String memberTmp = memberId;
   
   StringBuffer productsStr = new StringBuffer();
   productsStr.append("<br><br><b>De bestelde producten zijn</b><br><br>"
      + "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\"><tr><th>Produktnummer</th><th>Naam</th>");
   if(memberId.equals("")) {
      productsStr.append("<th>Prijs per stuk</th><th>Prijs</th>");
   } else {
      productsStr.append("<th>Ledenprijs per stuk</th><th>Ledenprijs</th>");
   } 
   productsStr.append("<th>Aantal</th></tr>");
   
   int totalPrice = 0;
   
   while(products.size()>0) { 
   	String thisProduct = (String) products.firstKey();
   	int numberOfItems = Integer.parseInt((String) products.get(thisProduct));
   	products.remove(thisProduct);
   
   	int price = 0;
   	%><mm:node number="<%= thisProduct %>" notfound="skipbody"
   	><%@include file="getprice.jsp"
   	%><mm:field name="titel" jspvar="titel" vartype="String" write="false"
   	><mm:field name="id" jspvar="id" vartype="String" write="false"><%
   	   productsStr.append("<tr><td  align=\"center\" valign=\"top\">" + id
   					+ "</td><td align=\"left\" valign=\"top\">" + titel + "</td>");
   		if(price==-1) { 
   		   productsStr.append("<td></td><td align=\"right\" valign=\"top\">nog onbekend");
   		} else {
   		   productsStr.append("<td>&euro; " + nf.format(((double) price )/100) + "</td><td align=\"right\" valign=\"top\">&euro; " + nf.format(((double) price )/100 * numberOfItems));
   		  		totalPrice += (price * numberOfItems);
   		}
   	%></mm:field
   	></mm:field><% 
   	%></mm:node><%
   	productsStr.append("</td><td align=\"center\" valign=\"top\">" + numberOfItems + "</td></tr>");
   }

   productsStr.append("<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Subtotaal: </td>"
      + "<td align=\"right\" valign=\"top\"></td><td>&euro; " + nf.format(((double) (totalPrice) )/100) + "</td><td></td></tr>"
      + "<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Verzendkosten: </td>"
      + "<td align=\"right\" valign=\"top\"></td><td>&euro; " + nf.format(((double) shippingCosts )/100) + "</td><td></td></tr>"   
      + "<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Totaal: </td>"
      + "<td align=\"right\" valign=\"top\"></td><td>&euro; " + nf.format(((double) (shippingCosts + totalPrice) )/100) + "</td><td></td></tr>"
      + "</table>");
%>