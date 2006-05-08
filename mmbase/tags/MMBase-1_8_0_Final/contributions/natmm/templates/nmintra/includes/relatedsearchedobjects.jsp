<% 
int foundOnDatabase = 0;
int passedOnSubTest = 0;

%><mm:list nodes="<%= searchedPages %>" path="<%= "pagina," + searchPath %>" constraints="<%= searchConstraint %>"
    ><mm:first><% searchPath = searchPath + ","; %></mm:first><% 
    
    foundOnDatabase++;
    
    // *** use the subSearchTerms to narrow down the search results ***
    String textStr = "";
    for(int f = 1; f<searchFields.size(); f++) { 
	 		String sFieldName = (String) searchFields.elementAt(f);
        %><mm:field name="<%= sFieldName %>" jspvar="dummy" vartype="String" write="false"><%
            if(dummy!=null) {
					int iDotIndex = sFieldName.indexOf(".");
					String sFieldShortName = sFieldName.substring(iDotIndex + 1);
					if (sFieldShortName.equals("titel")){ %>
						<mm:field name="<%= sFieldName + "_zichtbaar" %>" jspvar="titel_zichtbaar" vartype="String" write="false">
							<% if ((titel_zichtbaar==null||!titel_zichtbaar.equals("0"))) { textStr += " " + dummy;  }%>
						</mm:field>
				<%	} else {
						textStr += " " + dummy;
					}
				}	
        %></mm:field><% 
    }
    textStr = subSearchString(textStr);
    // *** check whether the fields in searchFields really contain the searchTerms
    if(containsSearchTerms(textStr,subSearchTerms)) { 
        %><mm:field name="<%= searchPath.substring(0,searchPath.indexOf(",")) + ".number" %>" jspvar="number" vartype="String" write="false"><%
            if(!searchResultMap.containsValue(number)) { 
                while(searchResultMap.containsKey("" + rank)) rank ++;
                searchResultMap.put("" + rank, number);
            }   
    
            passedOnSubTest++;

        %></mm:field><%
    } 
%></mm:list><%
if(debug) { %>
Searched on: <%= "page," + searchPath %><br>
Searched pages: <%= searchedPages %><br>
Searched with: <%= searchConstraint %><br>
Passed on subtest / Found on database = <%= passedOnSubTest %> / <%= foundOnDatabase %><br>
Cumulative results: <%= searchResultMap %><br><br><% 
} %>