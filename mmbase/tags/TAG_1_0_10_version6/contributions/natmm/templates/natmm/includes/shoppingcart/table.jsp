<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<%@include file="../time.jsp" %>
<%@include file="vars.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../calendar.jsp" %>
<% 
PaginaHelper ph = new PaginaHelper(cloud);  
String pageUrl = ph.createPaginaUrl("bestel",request.getContextPath());

String subsiteID = request.getParameter("si");
String memberId = request.getParameter("mi");
String styleSheet = request.getParameter("rs");
String imageTemplate ="";
int shippingCosts = Integer.parseInt(request.getParameter("sc"));
int donation = Integer.parseInt(request.getParameter("dn"));

int totalSum = 0;
TreeMap products = (TreeMap) session.getAttribute("products"); 
String formScript = "function changeIt(url) {"
      + "\nvar href = \"&pst=\";"
      + "\nif(url.indexOf(\"?\")==-1) { href = \"?pst=\"; }"
      + "\nvar valM = document.shoppingcart.elements[\"memberid\"].value;"
      + "\nhref += \"|valM=\" + escape(valM);";
				
// ************************** the membershipshorm ***************************

%><table width="100%" cellspacing="0" cellpadding="0">
<form name="shoppingcart" method="post" target="" onKeyPress="javascript:useEnterKey();"
	action="javascript:changeIt('<mm:url page="<%= pageUrl + "?t=change" %>" />');">
<tr>
	<td width="20%"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td>
	<td width="60%">
	<img src="media/trans.gif" width="1" height="11" border="0" alt=""><br/>
	<%
  String articleConstraint = "contentrel.pos='1'";
	TreeMap productsIterator = (TreeMap) session.getAttribute("productsIterator"); 
	if(productsIterator.size()>0) {
		articleConstraint = "contentrel.pos='2'"; 
	} 
	%><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= articleConstraint %>"
		><mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"
      ><jsp:include page="../artikel_1_column.jsp">
         <jsp:param name="o" value="<%= artikel_number %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
       </jsp:include
     ></mm:field> 
	</mm:list><br/>
	<img src="media/trans.gif" width="1" height="11" border="0" alt=""><br/>
	<%@include file="membershipsform.jsp"%><br/>
	<img src="media/trans.gif" width="1" height="25" border="0" alt=""><br/>
	</td>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180" style="vertical-align:top;"><%@include file="shoplinks.jsp"%></td>
</tr>
</table>
<%
// ************************** the table with products ***************************
if(productsIterator.size()>0) {

formScript += "\nvar valD = document.shoppingcart.elements[\"donation\"].value;"
		+ "\nif(valD!='') { href += \"|valD=\" + escape(valD); }";
%><table width="100%" cellspacing="0" cellpadding="0">
	<tr><td colspan="6" class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr>
	<tr>
		<td class="cartheader">ARTIKEL</td>
		<td class="cartheader">AANTAL</td>
		<td class="cartheader">AANTAL<br/>wijzigen</td>
		<% if(memberId.equals("")) {
			%><td class="cartheader" style="text-align:right;padding-right:22px;padding-left:0px;">PRIJS</td><% 
		} else {
			%><td class="cartheader" style="text-align:right;padding-left:0px;">LEDENPRIJS</td><%
		} %>
		<td class="cartheader" style="text-align:right;">TOTAAL</td>
		<td class="cartheader">VERWIJDER<br/>dit artikel</td>
	</tr>
	<tr><td colspan="6" width="100%" class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr><%

	String rowParity = "even";
	while(productsIterator.size()>0) { 
		String thisProduct = (String) productsIterator.firstKey();
		int numberOfItems = Integer.parseInt((String) productsIterator.get(thisProduct));
		productsIterator.remove(thisProduct);
		if(rowParity.equals("even")) { rowParity = "odd"; } else { rowParity = "even"; }
		int price = 0;
		int discount = 0;
		
		formScript += "\nvar val" + thisProduct + " = document.shoppingcart.elements[\"numberof" + thisProduct + "\"].value;\n"
				+ "if(val" + thisProduct + "!='') { href += \"|valP" + thisProduct + "=\" + escape(val" + thisProduct + "); } \n"; 
        
    String productUrl = "";
		%><mm:node number="<%= thisProduct %>" notfound="skipbody"
		><mm:related path="posrel,pagina"
      ><mm:field name="pagina.number" jspvar="thisPage" vartype="String" write="false"><%
        productUrl = ph.createPaginaUrl(thisPage, request.getContextPath()) + "?u=" + thisProduct;
      %></mm:field
    ></mm:related
    ><%@include file="getprice.jsp"
		%><%@include file="getdiscount.jsp" 
		%><tr>
			<td class="cart<%= rowParity %>" style="text-align:left;padding-right:0px;">
				<a href="javascript:changeIt('<mm:url page="<%= productUrl %>" 
					/>');"><mm:field name="titel" /></a><br/>
			</td>
			<td class="cart<%= rowParity %>" style="padding-right:0px;">
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
				<td width="45%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
				<td class="maincolor" style="vertical-align:middle;padding-left:1px;padding-right:1px;"><input type="text" name="numberof<%= thisProduct %>" class="cart" value="<%= numberOfItems %>"></td>
				<td width="45%" background="media/shop/pointer.gif" style="background-repeat:repeat-x;background-position: right center;"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
				</tr>
			</table>
			</td>
			<td class="cart<%= rowParity %>" style="padding-left:0px;">
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
				<td width="45%" background="media/shop/pointer.gif" style="background-repeat:repeat-x;background-position: right center;"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
				<td style="vertical-align:middle;"><a href="javascript:changeIt('<mm:url 
					page="<%= pageUrl + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();" 
					><img src="media/shop/pointer_oranje_<%= rowParity %>.gif" border="0" alt="aantal wijzigen"></a></td>
				<td width="45%"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
				</tr>
			</table>
			</td>
			<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:19px;padding-left:0px;text-align:right;"><% 
				if(price!=-1) { 
					%>&euro;&nbsp;<%= nf.format(((double) price )/100) %><%
				} else { 
					%>nog onbekend<% 
				} %></td>
			<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;"><% 
				if(price!=-1) { 
					int total = price*numberOfItems;
					if(totalSum!=-1) totalSum += total;
					%>&euro;&nbsp;<%= nf.format(((double) total  )/100) %><%
				} else {
					totalSum = -1;
					%>nog onbekend<% 
				} %></td>
			<td class="cart<%= rowParity %>"><a href="javascript:changeIt('<mm:url
				page="<%= pageUrl + "?u=" + thisProduct + "&t=delete" %>" />');" 
				><img src="media/shop/delete_<%= rowParity %>.gif" border="0" alt="verwijder dit artikel"></a></td>
			</tr>
			<tr><td colspan="6" width="100%" class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr>
		<%
		if(discount>0) {
			if(rowParity.equals("even")) { rowParity = "odd"; } else { rowParity = "even"; }
			int totaldiscount = discount*numberOfItems;
			if(totalSum!=-1) totalSum -= totaldiscount;
			%><tr>
			<td class="cart<%= rowParity %>" style="text-align:left;" colspan="3">Korting op <mm:field name="title" /></td>
			<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:19px;padding-left:0px;text-align:right;">&euro;&nbsp;<%= nf.format(((double) discount  )/100) %></td>
			<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;">&euro;&nbsp;<%= nf.format(((double) totaldiscount  )/100) %></td>
			<td class="cart<%= rowParity %>"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
			</tr>
			<tr><td colspan="6" width="100%" class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr><%
		} 
		%></mm:node><%
	} 
// ************************** subtotal ***************************
%>
<tr>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td colspan="3" class="carteven"  style="text-align:left;">
		<span class="colortitle" style="font-size:12px;font-weight:bold;">SUBTOTAAL</span></td>
	<td class="carteven" style="font-size:12px;padding-right:5px;text-align:right;"><% 
		if(totalSum!=-1) { 
			%>&euro;&nbsp;<%= nf.format(((double) totalSum  )/100) %><%
		} else {
			%>nog onbekend<% 
		} %></td>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
</tr>
<%@include file="getgeneraldiscount.jsp" %><%

// ************************** general discounts ***************************
if(generaldiscount>0) {
	%><tr>
		<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
		<td colspan="3" class="carteven"  style="text-align:left;">
			<span class="colortitle" style="font-size:12px;font-weight:bold;">KORTING</span></td>
		<td class="carteven" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;"><% 
			if(totalSum!=-1) { 
				%>&euro;&nbsp;<%= nf.format(((double) generaldiscount  )/100) %><%
			} else {
				%>nog onbekend<% 
			} %></td>
		<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	</tr><%
}
// ************************** shipping costs and donation ***************************
%><tr>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td colspan="3" class="carteven"  style="text-align:left;padding-top:0px;">
		<span class="colortitle" style="font-size:12px;font-weight:bold;">VERZENDKOSTEN</span></td>
	<td class="carteven" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;padding-top:0px;">
		&euro;&nbsp;<%= nf.format(((double) shippingCosts)/100) %></td>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
</tr>
<tr>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td colspan="3" class="carteven"  style="text-align:left;padding-top:0px;">
		<bean:message bundle="LEOCMS" key="shoppingcart.donationtext" />
	</td>
	<td class="carteven" style="padding:0px;">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
			<td style="text-align:right;padding-right:2px;width:99%;">&euro;</td>
			<td class="maincolor" style="vertical-align:middle;padding-left:1px;padding-right:1px;">
				<input type="text" name="donation" class="cart" style="width:43px;text-align:right;height:14px;" value="<%= nf.format(((double) donation)/100) %>"></td>
			</tr>
		</table>
	</td>
	<td class="carteven" style="padding:0px;padding-top:1px;">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
			<td background="media/shop/pointer.gif" style="width:10%;background-repeat:repeat-x;background-position: right center;"></td>
			<td style="vertical-align:middle;"><a href="javascript:changeIt('<mm:url 
				page="<%= pageUrl + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();">
				<img src="media/shop/pointer_oranje_even.gif" border="0" alt="bevestig extra gift"></a></td>
			<td style="vertical-align:middle;font-size:10px;padding-left:2px;padding-right:2px;"><a href="javascript:changeIt('<mm:url 
				page="<%= pageUrl + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();" class="nav">gift bevestigen</a></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td colspan="3" class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td>
		<table cellspacing="0" cellpadding="0" width="80%" align="right"><tr>
			<td style="background-color:#000000;"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
		</tr></table>
	</td>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
</tr>
<tr>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	<td colspan="3" class="carteven"  style="text-align:left;"><span style="font-size:12px;font-weight:bold;">TOTAAL</span></td>
	<td class="carteven" style="font-size:12px;font-weight:bold;padding-right:5px;padding-left:0px;text-align:right;"><% 
		if(totalSum!=-1) { 
			totalSum += shippingCosts + donation;
			%>&euro;&nbsp;<%= nf.format(((double) totalSum  )/100) %><%
		} else {
			%>nog onbekend<% 
		} %>
	</td>
	<td><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
</tr>
</table>
<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td width="80%"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td>
		<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
		<td width="180" class="maincolor"><img src="media/trans.gif" height="1" width="180" border="0" alt=""></td>
	</tr>
	<tr>
		<td width="80%"><img src="media/trans.gif" height="1" width="1" border="0" alt=""></td>
		<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
		<td width="180" style="vertical-align:top;"><%@include file="shoplinks.jsp"%></td>
	</tr>
</form>
</table><%
}

session.setAttribute("totalcosts","" + totalSum);

formScript += "if(url!=null) { document.location =  url + href; return false; } else { return href; }"
		+ "\n}";
%>
<script language="javascript" type="text/javascript">
<%= "<!--" %>
<%= formScript %>
function useEnterKey() 
{	if (window.event.keyCode == 13) changeIt('<mm:url page="<%= pageUrl + "?t=change" %>" />');
} 
<%= "//-->" %>
</script> 
</mm:cloud>
