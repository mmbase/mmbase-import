<%
if(actionId.equals("fast")||actionId.equals("order")||actionId.equals("delete")||postingStr!=null) {
	TreeMap shop_items = (TreeMap) session.getAttribute("shop_items"); 
	if(shop_items==null) {
		shop_items = new TreeMap();
		try { session.setAttribute("shop_items",shop_items);
		} catch(Exception e) { } 
	}
	if((actionId.equals("order")||actionId.equals("fast"))&&(shop_items.get(shop_itemId)==null)) { // *** add the shop_item ***
		shop_items.put(shop_itemId,"1");
	} else if(postingStr!=null) { // *** update shoppingcart ***
		postingStr += "|";
		if(postingStr.indexOf("|valP")>-1) { // *** update the shop_items in the session ***
			int qpos = postingStr.indexOf("|valP");
			while(qpos>-1) {
				int vstart = postingStr.indexOf("=",qpos);
				String shop_item = postingStr.substring(qpos+5,vstart);
				int vend = postingStr.indexOf("|",vstart+1);
				try {
					String value = "" + Integer.parseInt(postingStr.substring(vstart+1,vend));
					shop_items.put(shop_item,value);
				} catch(Exception e) { } 
				qpos = postingStr.indexOf("|valP",vend);
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
		int qpos = postingStr.indexOf("|q");
		while(qpos>-1) { // *** add the answers the session *** 
			int vstart = postingStr.indexOf("=",qpos);
			String question = postingStr.substring(qpos+2,vstart);
			int vend = postingStr.indexOf("|",vstart+1);
			String answer = postingStr.substring(vstart+1,vend);
			if(answer!=null){ 
				session.setAttribute("q"+ question,answer);
			}
			qpos = postingStr.indexOf("|q",vend);
		}
		if(actionId.equals("delete")) { // *** delete the shop_item ***
			shop_items.remove(shop_itemId);
		}
	}
	// *** count number of items ***
	TreeMap shop_itemsIterator = (TreeMap) shop_items.clone();
	int totalItems = 0;
	while(shop_itemsIterator.size()>0) { 
			String thisShop_item = (String) shop_itemsIterator.firstKey();
			totalItems += Integer.parseInt((String) shop_itemsIterator.get(thisShop_item));
			shop_itemsIterator.remove(thisShop_item);
	}
	totalitemsId = "" + totalItems;
	try { session.setAttribute("totalitems",totalitemsId);
	} catch(Exception e) { } 
}
%>