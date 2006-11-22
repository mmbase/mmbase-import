<%@include file="../getresponse.jsp" %>
<%@page import="java.text.*" %>
<%
String imageId = request.getParameter("i"); 
String actionId = request.getParameter("t"); if (actionId==null) {actionId=""; }
String totalitemsId = (String) session.getAttribute("totalitems");
if(totalitemsId==null) totalitemsId = "0";
String shop_itemHref = "";
String extendedHref = "";
String postingStr = request.getParameter("pst"); if(postingStr==null) { postingStr=""; }
String pageUrl =  ph.createPaginaUrl(paginaID,request.getContextPath()) + "?u=" + shop_itemId;

NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
nf.setMaximumFractionDigits(2);
nf.setMinimumFractionDigits(2);

if(actionId.equals("fast")||actionId.equals("order")||actionId.equals("delete")||postingStr!=null) {
	TreeMap products = (TreeMap) session.getAttribute("products"); 
	if(products==null) {
		products = new TreeMap();
		try { session.setAttribute("products",products);
		} catch(Exception e) { } 
	}
	if((actionId.equals("order")||actionId.equals("fast"))&&(products.get(shop_itemId)==null)) { // *** add the product ***
		products.put(shop_itemId,"1");
	} else if(postingStr!=null) { // *** update shoppingcart ***
		postingStr += "$";
		if(postingStr.indexOf("$valP")>-1) { // *** update the products in the session ***
			int qpos = postingStr.indexOf("$valP");
			while(qpos>-1) {
				int vstart = postingStr.indexOf("=",qpos);
				String product = postingStr.substring(qpos+5,vstart);
				int vend = postingStr.indexOf("$",vstart+1);
				try {
					String value = "" + Integer.parseInt(postingStr.substring(vstart+1,vend));
					products.put(product,value);
				} catch(Exception e) { } 
				qpos = postingStr.indexOf("$valP",vend);
			}
		}
		String memberId = getResponseVal("valM",postingStr);
		if(memberId!=null) { // *** add the memberid to the session ***
			try { session.setAttribute("memberid",memberId);
			} catch(Exception e) { } 
		}
		String donationStr = getResponseVal("valD",postingStr); 
		if(donationStr!=null) { // *** add the donation to the session ***
			donationStr = HtmlCleaner.replace(donationStr,",",".");
			if(donationStr.indexOf(".")==-1) donationStr += ".00";
			try {
				donationStr = "" + (new Double(100*Double.parseDouble(donationStr))).intValue();
				session.setAttribute("donation",donationStr);
			} catch(Exception e) { } 
		}
		int qpos = postingStr.indexOf("$q");
		while(qpos>-1) { // *** add the answers the session *** 
			int vstart = postingStr.indexOf("=",qpos);
			String question = postingStr.substring(qpos+2,vstart);
			int vend = postingStr.indexOf("$",vstart+1);
			String answer = postingStr.substring(vstart+1,vend);
			if(answer!=null){ 
				session.setAttribute("q"+ question,answer);
			}
			qpos = postingStr.indexOf("$q",vend);
		}
		if(actionId.equals("delete")) { // *** delete the product ***
			products.remove(shop_itemId);
		}
	}
	// *** count number of items ***
	TreeMap productsIterator = (TreeMap) products.clone();
	int totalItems = 0;
	while(productsIterator.size()>0) { 
			String thisProduct = (String) productsIterator.firstKey();
			totalItems += Integer.parseInt((String) productsIterator.get(thisProduct));
			productsIterator.remove(thisProduct);
	}
	totalitemsId = "" + totalItems;
	try { session.setAttribute("totalitems",totalitemsId);
	} catch(Exception e) { } 
}
%>