<%@page import="nl.leocms.evenementen.forms.SubscribeForm" %>
<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud" logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon">
<mm:locale language="nl">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
</cache:cache>
<%@include file="includes/shop/header.jsp" %>
<td colspan="5" style="text-align:right;vertical-align:top;">
	  <%	
    int shippingCosts = 350;

		TreeMap products = (TreeMap) session.getAttribute("products"); 
		if(products==null) {
			products = new TreeMap();
			try { session.setAttribute("products",products);
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
	
		TreeMap productsIterator = (TreeMap) products.clone();
		try { 
      session.setAttribute("productsIterator",productsIterator);
	  } catch(Exception e) { } 
	
		if(actionId.equals("proceed")) {
			%><jsp:include page="includes/shoppingcart/form.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
        <jsp:param name="rs" value="<%= styleSheet %>" />
			</jsp:include><%
		} else if(actionId.equals("send")&&products.size()>0) {
			%><%@include file="includes/shoppingcart/result.jsp" %><%
		} else {
			%><jsp:include page="includes/shoppingcart/table.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
				<jsp:param name="si" value="<%= subsiteID %>" />
				<jsp:param name="mi" value="<%= memberId %>" />
				<jsp:param name="sc" value="<%= shippingCosts %>" />
				<jsp:param name="dn" value="<%= donation %>" />
        <jsp:param name="rs" value="<%= styleSheet %>" />
			</jsp:include><%
	} %>
	</td>
<%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</mm:cloud>