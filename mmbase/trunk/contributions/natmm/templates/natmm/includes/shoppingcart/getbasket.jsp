<%
String productsStr = "<br><br><b>De bestelde producten zijn</b><br><br>"
		+ "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\"><tr><th>Produktnummer</th><th>Naam</th>"
		+ "<th>Prijs</th><th>Ledenprijs</th><th>Aantal</th></tr>";
String memberTmp = memberId;
while(products.size()>0) { 
	String thisProduct = (String) products.firstKey();
	int numberOfItems = Integer.parseInt((String) products.get(thisProduct));
	products.remove(thisProduct);
	memberId = "";
	int price = 0;
	%><mm:node number="<%= thisProduct %>" notfound="skipbody"
	><%@include file="getprice.jsp"
	%><mm:field name="titel" jspvar="titel" vartype="String" write="false"
	><mm:field name="id" jspvar="id" vartype="String" write="false"><%
		productsStr += "<td  align=\"center\" valign=\"top\">" + id
					+ "</td><td align=\"left\" valign=\"top\">" + titel
					+ "</td><td align=\"right\" valign=\"top\">";
		if(price==-1) { 
		  		productsStr += "nog onbekend";
		} else {
		  		productsStr += "&euro; " + nf.format(((double) price )/100);
		}
	%></mm:field
	></mm:field><% 
  memberId = "dummy";
	%><%@include file="getprice.jsp"
	%></mm:node><%
		productsStr += "</td><td align=\"right\" valign=\"top\">";
		if(price==-1) { 
	  		productsStr += "nog onbekend";
		} else {
	  		productsStr += "&euro; " + nf.format(((double) price )/100);
		}
		productsStr += "</td><td align=\"center\" valign=\"top\">" + numberOfItems
					+ "</td></tr>";
}
productsStr += "</table>";
memberId = memberTmp;
%>
