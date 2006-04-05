<%
String titleStr = "";
String textStr = "";

for(int i = 0; i< (thisOffset*10); i++) {
    searchResultMap.remove((String) searchResultMap.firstKey());
}

int r = 0;
while(!searchResultMap.isEmpty()&&(r<10)) {

    String nextKey = (String) searchResultMap.firstKey();
    String nextNode = (String) searchResultMap.get(nextKey);
    searchResultMap.remove(nextKey);
    // no tabs for intranet: r++;
    
    TreeMap cNodePaths = (TreeMap) nodePaths.clone(); // *** make a copy for this node ***
    while(!cNodePaths.isEmpty()) { // *** find the path that fits this node ***
        String thisPath = (String) cNodePaths.firstKey();
        Vector thisFields = (Vector) cNodePaths.get(thisPath);
        cNodePaths.remove(thisPath);
        if(thisPath.indexOf(",")==-1) { // *** exclude related paths ***
            if(debug) { %><br>Related found objects for node <%= nextNode %> with path <%= thisPath %><% } 
            %><%@include file="../includes/relatedfoundobjects.jsp" %><%
        }
    }
}
%>