<%@ page import="java.util.*" %>
<%	Hashtable pageCounter = (Hashtable) application.getAttribute("pageCounter"); 
	Integer visitorsCounter = (Integer) application.getAttribute("visitorsCounter");
	if(pageCounter!=null&&visitorsCounter!=null){ %>
		<mm:transaction id="add_pagecount" name="my_trans" commitonclose="true">
		<mm:createnode type="mmevents" id="this_event">
			<mm:setfield name="name"><%= visitorsCounter %></mm:setfield>	
	<%	Enumeration pages = pageCounter.keys(); 
		while(pages.hasMoreElements()) { 
			String thisPage = (String) pages.nextElement(); 
			Integer thisPageCount = (Integer) pageCounter.get(thisPage); 
			%>
			<mm:node number="<%= thisPage %>" id="this_page" />
			<mm:createrelation role="posrel" source="this_event" destination="this_page" >
				<mm:setfield name="pos"><%= thisPageCount %></mm:setfield>
			</mm:createrelation>
			<mm:remove referid="this_page" />
	<% } %>
		</mm:createnode>
		<mm:remove referid="this_event" />
		</mm:transaction>
		<mm:remove referid="add_pagecount" />
<% } %>