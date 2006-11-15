<%
boolean debug = false;

// *** calculations for search results ***

TreeSet searchResultSet = new TreeSet();
String searchResults = "";

if(!searchId.equals("")) {
	// fill the result set with all the products that contain the searchterm
	boolean firstTerm = true;
	String productConstraint = ""; 
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
				productConstraint += " AND ";
				articleConstraint += " AND "; 
				paragraphConstraint += " AND ";
			}
			productConstraint += "( UPPER(products.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(products.titel_fra) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(products.intro) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(products.omschrijving) LIKE '%" + searchTerm + "%' )";
			articleConstraint += "( UPPER(artikel.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(artikel.intro) LIKE '%" + searchTerm + "%' )";
			paragraphConstraint += "( UPPER(paragraaf.titel) LIKE '%" + searchTerm + "%'"
				+ " OR UPPER(paragraaf.omschrijving) LIKE '%" + searchTerm + "%' )";
			searchTermSet.add(searchTerm);
			firstTerm = false;
		}
	}

	%><mm:list nodes="<%= searchResults %>" path="products" constraints="<%= productConstraint %>"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% searchResultSet.add(products_number);
		%></mm:field
	></mm:list
	><mm:list nodes="<%= searchResults %>" path="products,posrel,artikel" constraints="<%= articleConstraint %>"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% searchResultSet.add(products_number);
		%></mm:field
	></mm:list
	><mm:list nodes="<%= searchResults %>" path="products,posrel,artikel,posrel,paragraaf"
		constraints="<%= paragraphConstraint %>"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% searchResultSet.add(products_number);
		%></mm:field
	></mm:list
	><%
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>text: <%= searchResults %><br><% }
}

if(!poolId.equals("")&&(searchId.equals("")||!searchResults.equals(""))) { // use pools to narrow down search results
	%><mm:list nodes="<%= searchResults %>" path="products,posrel,pagina"
		constraints="<%= "pagina.number = '" + poolId + "'" %>"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% searchResultSet.add(products_number);
		%></mm:field
	></mm:list><%
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>pool: <%= searchResults %><br><% }
} 

if(!keyId.equals("")&&((searchId.equals("")&&poolId.equals(""))||!searchResults.equals(""))) { // use keys to narrow down search results
	%><mm:list nodes="<%= searchResults %>" path="products,posrel,keys"
		constraints="<%= "keys.number = '" + keyId + "'" %>"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% searchResultSet.add(products_number);
		%></mm:field
	></mm:list><%
	searchResults = searchResults(searchResultSet);
	searchResultSet.clear();
	if(debug) { %>key: <%= searchResults %><br><% }
} 
%>