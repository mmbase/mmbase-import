<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="../templateheader.jsp" 
%><%@include file="../calendar.jsp"
%><%  

String memberId = (String) session.getAttribute("memberid");
if(memberId==null) { memberId = ""; }

int shippingCosts = 0;
int totalSum = 0;
int donation = 0;

String donationStr = (String) session.getAttribute("donation"); 
if(donationStr!=null) { 
   try { donation = Integer.parseInt(donationStr); 
   } catch(Exception e) { } 
}

TreeMap shop_items = (TreeMap) session.getAttribute("shop_items");
TreeMap shop_itemsIterator = (TreeMap) shop_items.clone();

String formScript = "function changeIt(url) {"
				+ "\nvar href = \"&pst=\";";
 if(bShowPrices&&bMemberDiscount) {
      formScript += "\nvar valM = document.shoppingcart.elements[\"memberid\"].value;"
         + "\nhref += \"|valM=\" + escape(valM);";
}
				
// ************************** the membershipshorm ***************************
%><table width="100%" cellspacing="0" cellpadding="0">
<form name="shoppingcart" method="post" target="" onKeyPress="javascript:useEnterKey();"
	action="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');">
<tr>
	<td width="20%"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td>
	<td width="60%">
	<img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
	<% String articleConstraint = "contentrel.pos='1'"; 
		if(shop_itemsIterator.size()==0) {
			articleConstraint = "contentrel.pos='0'"; 
		} 
	%><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= articleConstraint %>"
		><%@include file="../relatedarticle.jsp" 
	%></mm:list><br>
	<img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
	<%
	if(bShowPrices&&bMemberDiscount) { 
	   %>
		<jsp:include page="membershipsform.jsp.jsp">
          <jsp:param name="mi" value="<%= memberId %>" />
      </jsp:include><br>
	   <img src="media/spacer.gif" width="1" height="25" border="0" alt=""><br>
		<%
	} %>
	</td>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<td width="180"><%@include file="shoplinks.jsp"%></td>
</tr>
</table>
<% 
// ************************** the table with shop_items ***************************
if(shop_itemsIterator.size()>0) {

	if(bShowPrices&&bExtraCosts) {
		formScript += "\nvar valD = document.shoppingcart.elements[\"donation\"].value;"
				+ "\nif(valD!='') { href += \"|valD=\" + escape(valD); }";	   	
	}
	%>
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr><td colspan="6" class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr>
		<tr>
			<td class="cartheader">ARTIKEL</td>
			<td class="cartheader">AANTAL</td>
			<td class="cartheader">AANTAL<br>wijzigen</td>
			<% 
			if(bShowPrices) {
				if(memberId.equals("")) {
					%><td class="cartheader" style="text-align:right;padding-right:22px;padding-left:0px;">PRIJS</td><% 
				} else {
					%><td class="cartheader" style="text-align:right;padding-left:0px;">LEDENPRIJS</td><%
				} %>
				<td class="cartheader" style="text-align:right;">TOTAAL</td><% 
			} %>
			<td class="cartheader">VERWIJDER<br>dit artikel</td>
		</tr>
		<tr><td colspan="6" width="100%" class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr><%
	
		String rowParity = "even";
		while(shop_itemsIterator.size()>0) { 
			String thisShop_item = (String) shop_itemsIterator.firstKey();
			int numberOfItems = Integer.parseInt((String) shop_itemsIterator.get(thisShop_item));
			shop_itemsIterator.remove(thisShop_item);
			if(rowParity.equals("even")) { rowParity = "odd"; } else { rowParity = "even"; }
			int price = 0;
			int discount = 0;
			
			formScript += "\nvar val" + thisShop_item + " = document.shoppingcart.elements[\"numberof" + thisShop_item + "\"].value;\n"
					+ "if(val" + thisShop_item + "!='') { href += \"|valP" + thisShop_item + "=\" + escape(val" + thisShop_item + "); } \n"; 
			
			%><mm:node number="<%= thisShop_item %>" notfound="skipbody"
			><%@include file="getprice.jsp"
			%><%@include file="getdiscount.jsp" 
			%><tr>
				<td class="cart<%= rowParity %>" style="text-align:left;padding-right:0px;padding-left:5px;">
					<a href="javascript:changeIt('<mm:url page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) + "?u=" + thisShop_item %>" 
						/>');" onclick="needToConfirm = false;"><mm:field name="titel" /></a><br>
				</td>
				<td class="cart<%= rowParity %>" style="padding-right:0px;">
				<table cellspacing="0" cellpadding="0" width="100%">
					<tr>
					<td width="45%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
					<td class="titlebar" style="vertical-align:middle;padding-left:1px;padding-right:1px;"><input type="text" name="numberof<%= thisShop_item %>" class="cart" value="<%= numberOfItems %>"></td>
					<td width="45%" background="media/pointer.gif" style="background-repeat:repeat-x;background-position: right center;"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
					</tr>
				</table>
				</td>
				<td class="cart<%= rowParity %>" style="padding-left:0px;">
				<table cellspacing="0" cellpadding="0" width="100%">
					<tr>
					<td width="45%" background="media/pointer.gif" style="background-repeat:repeat-x;background-position: right center;"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
					<td style="vertical-align:middle;"><a href="javascript:changeIt('<mm:url 
						page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();" 
						onclick="needToConfirm = false;"><img src="media/pointer_oranje_<%= rowParity %>.gif" border="0" alt="aantal wijzigen"></a></td>
					<td width="45%"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
					</tr>
				</table>
				</td>
				<% if(bShowPrices) { %>
					<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:19px;padding-left:0px;text-align:right;"><% 
						if(price!=-1) { 
							%>&euro;&nbsp;<%= nf.format(((double) price )/100) %><%
						} else { 
							%>--<% 
						} %></td>
					<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;"><% 
						if(price!=-1) { 
							int total = price*numberOfItems;
							if(totalSum!=-1) totalSum += total;
							%>&euro;&nbsp;<%= nf.format(((double) total  )/100) %><%
						} else {
							// *** for resetting totalSum to not defined use  totalSum = -1; ***
							%>--<% 
						} %></td>
				<% } %>
				<td class="cart<%= rowParity %>"><a href="javascript:changeIt('<mm:url
					page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) +"?u=" + thisShop_item +"&t=delete" %>" />');" onclick="needToConfirm = false;"
					><img src="media/delete_<%= rowParity %>.gif" border="0" alt="verwijder dit artikel"></a></td>
				</tr>
				<tr><td colspan="6" width="100%" class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr>
			<%
			if(discount>0) {
				if(rowParity.equals("even")) { rowParity = "odd"; } else { rowParity = "even"; }
				int totaldiscount = discount*numberOfItems;
				if(totalSum!=-1) totalSum -= totaldiscount;
				%><tr>
				<td class="cart<%= rowParity %>" style="text-align:left;" colspan="3">Korting op <mm:field name="title" /></td>
				<% if(bShowPrices) { %>
					<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:19px;padding-left:0px;text-align:right;">&euro;&nbsp;<%= nf.format(((double) discount  )/100) %></td>
					<td class="cart<%= rowParity %>" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;">&euro;&nbsp;<%= nf.format(((double) totaldiscount  )/100) %></td>
				<% } %>
				<td class="cart<%= rowParity %>"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
				</tr>
				<tr><td colspan="6" width="100%" class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr><%
			} 
			%></mm:node><%
		} 
	// ************************** subtotal ***************************
	if(bShowPrices&&bExtraCosts) { %>
	<tr>
		<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
		<td colspan="3" class="carteven"  style="text-align:left;">
			<span class="subtitle" style="font-size:12px;font-weight:bold;">SUBTOTAAL</span></td>
		<td class="carteven" style="font-size:12px;padding-right:5px;text-align:right;"><% 
			if(totalSum!=-1) { 
				%>&euro;&nbsp;<%= nf.format(((double) totalSum  )/100) %><%
			} else {
				%>nog onbekend<% 
			} %></td>
		<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	</tr>
	<% } %>
	<%@include file="getgeneraldiscount.jsp" %><%
	
	// ************************** general discounts ***************************
	if(generaldiscount>0) {
		%><tr>
			<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
			<td colspan="3" class="carteven"  style="text-align:left;">
				<span class="subtitle" style="font-size:12px;font-weight:bold;">KORTING</span></td>
			<td class="carteven" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;"><% 
				if(totalSum!=-1) { 
					%>&euro;&nbsp;<%= nf.format(((double) generaldiscount  )/100) %><%
				} else {
					%>nog onbekend<% 
				} %></td>
			<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
		</tr><%
	} 
	// ************************** shipping costs and donation ***************************
	if(bShowPrices) {
		if(bExtraCosts) {
			%>
			<tr>
				<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
				<td colspan="3" class="carteven"  style="text-align:left;padding-top:0px;">
					<span class="subtitle" style="font-size:12px;font-weight:bold;">VERZENDKOSTEN</span></td>
				<td class="carteven" style="font-size:12px;padding-right:5px;padding-left:0px;text-align:right;padding-top:0px;">
					&euro;&nbsp;<%= nf.format(((double) shippingCosts)/100) %></td>
				<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
			</tr>
			<tr>
				<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
				<td colspan="3" class="carteven"  style="text-align:left;padding-top:0px;">
					<span class="subtitle" style="font-size:12px;font-weight:bold;">EXTRA GIFT</span><br>
					Ik steun Natuurmonumenten graag met een <STRONG>extra gift </STRONG>
					Ik geef extra:
				</td>
				<td class="carteven" style="padding:0px;">
					<table cellspacing="0" cellpadding="0" width="100%">
						<tr>
						<td style="text-align:right;padding-right:2px;width:99%;">&euro;</td>
						<td class="titlebar" style="vertical-align:middle;padding-left:1px;padding-right:1px;">
							<input type="text" name="donation" class="cart" style="width:43px;text-align:right;" value="<%= nf.format(((double) donation)/100) %>"></td>
						</tr>
					</table>
				</td>
				<td class="carteven" style="padding:0px;padding-top:1px;">
					<table cellspacing="0" cellpadding="0" width="100%">
						<tr>
						<td background="media/pointer.gif" style="width:10%;background-repeat:repeat-x;background-position: right center;"></td>
						<td style="vertical-align:middle;"><a href="javascript:changeIt('<mm:url 
							page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();" onclick="needToConfirm = false;">
							<img src="media/pointer_oranje_even.gif" border="0" alt="bevestig extra gift"></a></td>
						<td style="vertical-align:middle;font-size:10px;padding-left:2px;padding-right:2px;"><a href="javascript:changeIt('<mm:url 
							page="<%= ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');document.shoppingcart.target='';document.shoppingcart.submit();" 
							onclick="needToConfirm = false;" class="nav">gift bevestigen</a></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
				<td colspan="3" class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
				<td>
					<table cellspacing="0" cellpadding="0" width="80%" align="right"><tr>
						<td style="background-color:#000000;"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
					</tr></table>
				</td>
				<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
			</tr>
			<% 
		} %>
		<tr>
			<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
			<td colspan="3" class="carteven"  style="text-align:left;"><span style="font-size:12px;font-weight:bold;">TOTAAL</span></td>
			<td class="carteven" style="font-size:12px;font-weight:bold;padding-right:5px;padding-left:0px;text-align:right;"><% 
				if(totalSum!=-1) { 
					totalSum += shippingCosts + donation;
					%>&euro;&nbsp;<%= nf.format(((double) totalSum  )/100) %><%
				} else {
					%>nog onbekend<% 
				} %>
			</td>
			<td><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
		</tr>
		<% 
	} %>
	</table>
	<table width="100%" cellspacing="0" cellpadding="0">
		<% 
		if(bShowPrices) { 
			%>
			<tr>
				<td width="80%"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td>
				<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
				<td width="180" class="titlebar"><img src="media/spacer.gif" height="1" width="180" border="0" alt=""></td>
			</tr>
			<%
		} %>
		<tr>
			<td width="80%"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td>
			<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
			<td width="180"><%@include file="shoplinks.jsp"%></td>
		</tr>
	</form>
	</table>
   <%
} else {
   %>
	<table width="100%" cellspacing="0" cellpadding="0">
   <tr>
		<td width="20%"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td>
		<td width="60%"><b>Uw winkelwagen is op dit moment (nog) leeg.</b></td>
		<td width="20%"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td>
	</tr>
	</table>
	<%
}

formScript += "if(url!=null) { document.location =  url + href; return false; } else { return href; }"
		+ "\n}";

session.setAttribute("totalcosts","" + totalSum);
%>
<script language="javascript" type="text/javascript">
<%= "<!--" %>
var needToConfirm = true;
window.onbeforeunload = confirmExit;
function confirmExit()
{
   if (needToConfirm) {
      return 'De reeds ingevoerde wijzigingen worden dan niet opgeslagen.\nAls u niets heeft gewijzigd klik op "Ok"';
   }
}
<%= formScript %>
function useEnterKey() 
{	if (window.event.keyCode == 13) {
         needToConfirm = false;
         changeIt('<mm:url page="<%=  ph.createPaginaUrl("bestel",request.getContextPath()) + "?t=change" %>" />');
   }
} 
<%= "//-->" %>
</script>
</mm:cloud>
