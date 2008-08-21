<mm:node number="<%= rubriekId %>" 
	><mm:present referid="ishomepage"
		><p><mm:field name="naam_eng"/><br><span class="projecttitle"><mm:field name="naam"/></span></p></mm:present
	><mm:notpresent referid="ishomepage"
		><p><mm:field name="naam" /><br><span class="projecttitle"><mm:node number="<%= paginaID %>"
			><mm:field name="titel" /></mm:node></span></p></mm:notpresent><%
	if(!rubriekId.equals(rootId)) {
		RubriekHelper rubriekHelper = new RubriekHelper(cloud);
		TreeMap pages = (TreeMap) rubriekHelper.getSubObjects(rubriekId);
		int i = 0;
		while(! pages.isEmpty()) { 
			Integer thisKey = (Integer) pages.firstKey();
			String sThisObject = (String) pages.get(thisKey);
			if(i!=0) { %> | <% }
			%><mm:node number="<%= sThisObject %>"
				><mm:field name="number" jspvar="thispage_number" vartype="String" write="false"><%
	
					if(!thispage_number.equals(paginaID)) { 
						%><a href="<%= ph.createPaginaUrl(thispage_number,request.getContextPath()) %>"><%
					} 
						%><mm:field name="titel" /><% 
					if(!thispage_number.equals(paginaID)) { 
						%></a><%
					}
	
				%></mm:field
			></mm:node><%
			pages.remove(thisKey);
			i++; 
		}
	}
%></mm:node>
