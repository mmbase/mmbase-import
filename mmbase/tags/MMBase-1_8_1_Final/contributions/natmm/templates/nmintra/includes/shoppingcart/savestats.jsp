<%
shop_itemsIterator = (TreeMap) shop_items.clone();
%><mm:createnode type="responses" id="response"
	><mm:setfield name="title"><%= responseTitle %></mm:setfield
	><mm:setfield name="account"><%= username %></mm:setfield
     ><mm:setfield name="responsedate"><%= (new Date()).getTime()/1000 %></mm:setfield
></mm:createnode><%

int i=0;
while(shop_itemsIterator.size()>0) { 
	String thisShop_item = (String) shop_itemsIterator.firstKey();
	String numberOfItems = (String) shop_itemsIterator.get(thisShop_item);
	
	%><mm:node referid="response"
	     ><mm:setfield name="<%= "question"+ (i+1) %>"><%= thisShop_item %></mm:setfield
  	     ><mm:setfield name="<%= "answer"+ (i+1) %>"><%= numberOfItems %></mm:setfield
	></mm:node><%
	i++;
	shop_itemsIterator.remove(thisShop_item); 
}
%>