<%@include file="includes/templateheader.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
<td colspan="2" class="transperant" style="text-align:right;">
<div class="<%= infopageClass %>">
<%	int shippingCosts = 0;

	TreeMap shop_items = (TreeMap) session.getAttribute("shop_items"); 
	if(shop_items==null) {
		shop_items = new TreeMap();
		try { session.setAttribute("shop_items",shop_items);
		} catch(Exception e) { } 
	}
	
	int donation = 0;
	String donationStr = (String) session.getAttribute("donation"); 
	if(donationStr!=null) { 
		try { donation = Integer.parseInt(donationStr); 
		} catch(Exception e) { } 
	}
	
	String memberId = (String) session.getAttribute("memberid");
	if(memberId==null) { memberId = ""; }
	
	TreeMap shop_itemsIterator = (TreeMap) shop_items.clone();
	int totalSum = 0;
	
	if(actionId.equals("proceed")) {
		%><%@include file="includes/shoppingcartform.jsp" 
		%><%@include file="includes/shoppingcartscript.jsp" %><%
	} else if(actionId.equals("send")&&shop_items.size()>0) {
		%><%@include file="includes/shoppingcartresult.jsp" %><%
	} else {
		%><%@include file="includes/shoppingcarttable.jsp" %><%
		session.setAttribute("totalcosts","" + totalSum);
	} 
%>
</div>
</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
