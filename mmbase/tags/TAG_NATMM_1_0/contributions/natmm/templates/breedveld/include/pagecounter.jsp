<%@ page import="java.util.*" %>
<%	Hashtable pageCounter = (Hashtable) application.getAttribute("pageCounter");
	HashSet visitorsSessions = (HashSet) application.getAttribute("visitorsSessions");
	Integer visitorsCounter = (Integer) application.getAttribute("visitorsCounter");
	String dayMark = (String) application.getAttribute("dayMark");
	String presentDayMark = ""; %>
<%-- can the following listing be replaced by a java call ? --%>
<mm:listnodes type="daymarks" max="1" orderby="number" directions="DOWN">
	<mm:field name="daycount" jspvar="dummy" vartype="String" write="false">
		 <%	presentDayMark = dummy; %>
	</mm:field>
</mm:listnodes>

<%	// should only be necessary after application server restart (possibility that stats of this day are lost)
	if(pageCounter==null||visitorsCounter==null||visitorsSessions==null||dayMark==null) {
		pageCounter = new Hashtable();
		visitorsSessions = new HashSet();
		visitorsCounter = new Integer(0);
		dayMark = presentDayMark;
 	}

	// on a new day, first save the stats to the database and reset the application variables
	if(!dayMark.equals(presentDayMark)) { 
		Calendar scal = Calendar.getInstance();
		Date sdd = new Date();
		scal.setTime(sdd);
		scal.add(Calendar.DATE,-1); 
		long yesterday = (scal.getTime().getTime()/1000);	%>
		<mm:transaction id="add_pagecount" name="my_trans" commitonclose="true">
		<mm:createnode type="mmevents" id="this_event">
			<mm:setfield name="name"><%= visitorsCounter %></mm:setfield>
			<mm:setfield name="start"><%= yesterday %></mm:setfield>		
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
	<%	pageCounter = new Hashtable();
		visitorsSessions = new HashSet();
		visitorsCounter = new Integer(0);
		dayMark = presentDayMark;			
	}
	%>
	
	<%-- add one to the pageCounter for pageId, check if page exists --%>
	<mm:node number="<%= pageId %>" notfound="skipbody">
	<%	Integer pageCount = (Integer) pageCounter.get(pageId);
		if(pageCount==null) pageCount = new Integer(0);
		pageCounter.put(pageId,new Integer(pageCount.intValue()+1));
		
		String thisSession =  request.getSession().toString();
		if(!visitorsSessions.contains(thisSession)) {
			visitorsSessions.add(thisSession);
			visitorsCounter = new Integer(visitorsCounter.intValue()+1);
		} 
	%>
	</mm:node>

<%	application.setAttribute("pageCounter", pageCounter);
	application.setAttribute("visitorsSessions", visitorsSessions);
	application.setAttribute("visitorsCounter", visitorsCounter);
	application.setAttribute("dayMark", dayMark);
%>
