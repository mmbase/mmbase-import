<%
   String memberTmp = memberId;
   
   String productsStr = "<br><br><b>De bestelde producten zijn</b><br><br>";
   productsStr +=	"<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\"><tr><th>Produktnummer</th><th>Naam</th>";
   if(memberId.equals("")) {
      productsStr += "<th>Prijs</th>";
   } else {
      productsStr += "<th>Ledenprijs</th>";
   } 
   productsStr += "<th>Aantal</th></tr>";
   
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
   		productsStr += "<tr><td  align=\"center\" valign=\"top\">" + id
   					+ "</td><td align=\"left\" valign=\"top\">" + titel
   					+ "</td><td align=\"right\" valign=\"top\">";
   		if(price==-1) { 
   		  		productsStr += "nog onbekend";
   		} else {
   		  		productsStr += "&euro; " + nf.format(((double) price )/100);
   		  		totalPrice += price;
   		}
   	%></mm:field
   	></mm:field><% 
   	%></mm:node><%
   		productsStr += "</td><td align=\"center\" valign=\"top\">" + numberOfItems
   					+ "</td></tr>";
   }

   productsStr += "<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Subtotaal: </td>";
   productsStr += "<td align=\"right\" valign=\"top\">&euro; " + nf.format(((double) (totalPrice) )/100) + "</td><td></td></tr>";
   productsStr += "<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Verzendkosten: </td>";
   productsStr += "<td align=\"right\" valign=\"top\">&euro; " + nf.format(((double) shippingCosts )/100) + "</td><td></td></tr>";   
   productsStr += "<tr><td align=\"right\" valign=\"top\" colspan=\"2\">Totaal: </td>";
   productsStr += "<td align=\"right\" valign=\"top\">&euro; " + nf.format(((double) (shippingCosts + totalPrice) )/100) + "</td><td></td></tr>";
   productsStr += "</table>";
%>