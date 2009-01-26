<%! public String searchResults(TreeSet searchResultSet) {
	String searchResults = "";
	Iterator srsi = searchResultSet.iterator();
	while(srsi.hasNext())
	{	if(!searchResults.equals("")) searchResults += ",";
		searchResults += (String) srsi.next();
	}
	return searchResults;
}
%>
<%
String searchId = request.getParameter("s"); if(searchId==null) { searchId = ""; }
String keyId = request.getParameter("k"); if(keyId==null) { keyId = ""; }
String poolId = request.getParameter("c"); if(poolId==null) { poolId = ""; }

boolean debug = false;

// *** calculations for search results ***

TreeSet searchResultSet = new TreeSet();
String searchResults = "";

if(!searchId.equals("")) {
	// fill the result set with all the items that contain the searchterm
	boolean firstTerm = true;
	String itemConstraint = ""; 
	String articleConstraint = "";
	String paragraphConstraint = "";
	
	String searchString = searchId.toUpperCase() + " ";
	String searchTerm = "";
	TreeSet searchTermSet = new TreeSet();
	while(!searchString.equals("")) { 
		if(searchString.substring(0,1).equals("\"")) { // string whithin "  " ?
			searchString = searchString.substring(1);
			try{
				searchTerm = searchString.substring(0,searchString.indexOf("\"")); 
				searchString = searchString.substring(searchString.indexOf("\"")+1);
			} catch (Exception e) { // no closing "
				searchTerm = "";
			}
		} else { // take next word
			searchTerm = searchString.substring(0,searchString.indexOf(" ")); 
			searchString = searchString.substring(searchString.indexOf(" ")+1); 
		}
		searchTerm = searchTerm.replace('-',' '); // for 'search-fast' search on the string 'search fast'
		if(!searchTerm.equals("")){
			if(!firstTerm) {
				itemConstraint += " AND ";
				articleConstraint += " AND "; 
				paragraphConstraint += " AND ";
			}
			itemConstraint += "( UPPER(items.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(items.titel_fra) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(items.intro) LIKE '%" + searchTerm + "%'"
        + " OR UPPER(items.body) LIKE '%" + searchTerm + "%' )";
			articleConstraint += "( UPPER(artikel.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(artikel.tekst) LIKE '%" + searchTerm + "%' )";
			paragraphConstraint += "( UPPER(paragraaf.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(paragraaf.tekst) LIKE '%" + searchTerm + "%' )";
			searchTermSet.add(searchTerm);
			firstTerm = false;
		}
	}

	%><mm:list nodes="<%= searchResults %>" path="items" constraints="<%= itemConstraint %>"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><% 
      searchResultSet.add(items_number);
		%></mm:field
	></mm:list
	><mm:list nodes="<%= searchResults %>" path="items,posrel,artikel" constraints="<%= articleConstraint %>"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><%
      searchResultSet.add(items_number);
		%></mm:field
	></mm:list
	><mm:list nodes="<%= searchResults %>" path="items,posrel,artikel,posrel,paragraaf"
		constraints="<%= paragraphConstraint %>"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><% 
      searchResultSet.add(items_number);
		%></mm:field
	></mm:list><%
  
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>text: <%= searchResults %><br><% }
}

if(!poolId.equals("")&&(searchId.equals("")||!searchResults.equals(""))) { // use pools to narrow down search results
	%><mm:list nodes="<%= searchResults %>" path="items,posrel,pagina"
		constraints="<%= "pagina.number = '" + poolId + "'" %>"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"
			><% searchResultSet.add(items_number);
		%></mm:field
	></mm:list><%
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>pool: <%= searchResults %><br><% }
} 

if(!keyId.equals("")&&((searchId.equals("")&&poolId.equals(""))||!searchResults.equals(""))) { // use keywords to narrow down search results
	%><mm:list nodes="<%= searchResults %>" path="items,posrel,keywords"
		constraints="<%= "keywords.number = '" + keyId + "'" %>"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"
			><% searchResultSet.add(items_number);
		%></mm:field
	></mm:list><%
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>key: <%= searchResults %><br><% }
} 
%>