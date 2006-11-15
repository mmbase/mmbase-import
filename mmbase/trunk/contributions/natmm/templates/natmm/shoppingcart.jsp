<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/shoppingcart/update.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<mm:locale language="nl">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<td colspan="5" style="text-align:right;">
	<%	int shippingCosts = 350;
		%><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel"
			constraints="contentrel.pos=15" fields="artikel.titel_fra"
			><mm:field name="artikel.titel_fra" jspvar="articles_titel_fra" vartype="String" write="false"><%
				try{ shippingCosts = Integer.parseInt(articles_titel_fra);
				} catch (Exception e) {}
			%></mm:field
		></mm:list><%

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
		try { session.setAttribute("productsIterator",productsIterator);
			} catch(Exception e) { } 
		int totalSum = 0;
	
		if(actionId.equals("proceed")) {
			%><jsp:include page="includes/shoppingcart/form.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
			</jsp:include><%
		} else if(actionId.equals("send")&&products.size()>0) {
			%><jsp:include page="includes/shoppingcart/result.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
			</jsp:include><%
		} else {
			%><jsp:include page="includes/shoppingcart/table.jsp">
				<jsp:param name="p" value="<%= paginaID %>" />
				<jsp:param name="pu" value="<%= pageUrl %>" />
				<jsp:param name="mi" value="<%= memberId %>" />
				<jsp:param name="sc" value="<%= shippingCosts %>" />
				<jsp:param name="ts" value="<%= totalSum %>" />
				<jsp:param name="dn" value="<%= donation %>" />
			</jsp:include><%
			session.setAttribute("totalcosts","" + totalSum);
	} %>
	</td>
<%@include file="includes/shop/footer.jsp" %>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</cache:cache>
</mm:cloud>