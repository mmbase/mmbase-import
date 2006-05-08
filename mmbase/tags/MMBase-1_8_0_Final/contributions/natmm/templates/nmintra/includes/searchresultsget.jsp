<%
// *** calculations for search results ***

TreeMap searchResultMap = new TreeMap();
Vector defaultSearchTerms = new Vector();
Vector superSearchTerms = new Vector();
Vector subSearchTerms = new Vector();

if(!searchId.equals(defaultSearchId)&&!searchId.equals("")) {
    
    // *** create searchTerms on basis of the searchId ***
    // the superSearchTerms is used for database searching
    // the subSearchTerms is used to narrow down the result set
    
    defaultSearchTerms = createSearchTerms(searchId);
    superSearchTerms = createSearchTerms(superSearchString(searchId)); 
    subSearchTerms = createSearchTerms(subSearchString(searchId));

    if(debug) { %>searchTermSets:<br>default: <%= defaultSearchTerms %><br>super: <%= superSearchTerms %><br>sub: <%= subSearchTerms %><br><%   }

    // *** use superSearchTerms to search all the paths ***
    TreeMap cNodePaths = (TreeMap) nodePaths.clone(); // *** make a copy to create queries on all the paths ***
    while(!cNodePaths.isEmpty()) {
        String searchPath = (String) cNodePaths.firstKey();
        Vector searchFields = (Vector) cNodePaths.get(searchPath);
        cNodePaths.remove(searchPath);

        int rank =  ((Integer) searchFields.elementAt(0)).intValue();
        
        String searchConstraint = "";
        if(searchPath.indexOf("artikel")>-1) { // *** should this hold for all articles, or only for news and events ? ***
            searchConstraint = "( artikel.embargo < '" + nowSec + "' AND  artikel.verloopdatum > '" + nowSec + "') AND ";
        }
        Iterator searchTermList = superSearchTerms.iterator(); // *** make a copy for this path ***
        boolean firstTerm = true;
        while(searchTermList.hasNext())
        {   String searchTerm = (String) searchTermList.next();
            if(!firstTerm) { searchConstraint += " AND "; }
            searchConstraint += "( ";
            for(int f = 1; f<searchFields.size(); f++) { 
                if(f!=1) searchConstraint += " OR ";
                searchConstraint += "UPPER(" + (String) searchFields.elementAt(f) + ") LIKE '%" + searchTerm + "%'";
            }
            searchConstraint += " )";
            firstTerm = false;
        }
        
        %><%@include file="../includes/relatedsearchedobjects.jsp" %><%
    }
}
%>