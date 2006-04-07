<mm:node number="<%= nextNode %>"
><mm:nodeinfo type="type"  jspvar="type" vartype="String" write="false"
><% if(type.equals(thisPath)) { 
		String sFieldName = (String)thisFields.elementAt(1);
%><mm:list nodes="<%= nextNode %>" path="<%= thisPath %>"
    ><mm:field name="<%= sFieldName %>" jspvar="dummy" vartype="String" write="false"
        ><% if(dummy!=null) {
		  			int iDotIndex = sFieldName.indexOf(".");
					String sFieldShortName = sFieldName.substring(iDotIndex + 1);
			  		if (sFieldShortName.equals("titel")){ %>
						<mm:field name="<%= sFieldName + "_zichtbaar" %>" jspvar="titel_zichtbaar" vartype="String" write="false">
							<% if ((titel_zichtbaar==null||!titel_zichtbaar.equals("0"))) { textStr += " " + dummy;  }%>
						</mm:field>
				<%	} else { 
					  titleStr += " " + dummy;
				  }
				}	  
    %></mm:field><%
    textStr= "";
    for(int f = 2; f<thisFields.size(); f++) { 
        %><mm:field name="<%= (String) thisFields.elementAt(f) %>" jspvar="dummy" vartype="String" write="false"
            ><% if(dummy!=null) { textStr += " " + dummy;  } 
        %></mm:field><%
    }
    // *** use the related paths to add more to textStr ***
    while(!cNodePaths.isEmpty()) {
        String relatedPath = (String) cNodePaths.firstKey();
        if(relatedPath.indexOf(thisPath)>-1) {
            thisFields = (Vector) cNodePaths.get(relatedPath);
            String orderbyField = "";
            if(relatedPath.indexOf("posrel")>-1) { orderbyField = "posrel.pos"; }
            %><mm:list nodes="<%= nextNode %>" path="<%= relatedPath %>" orderby="<%= orderbyField %>"><%
                for(int f = 1; f<thisFields.size(); f++) { 
					 		sFieldName = (String)thisFields.elementAt(f);
                    %><mm:field name="<%= sFieldName %>" jspvar="dummy" vartype="String" write="false"
                        ><% if(dummy!=null){
		  								 int iDotIndex = sFieldName.indexOf(".");
						             String sFieldShortName = sFieldName.substring(iDotIndex + 1);
										 if (sFieldShortName.equals("titel")){ %>
										 	<mm:field name="<%= sFieldName + "_zichtbaar" %>" jspvar="titel_zichtbaar" vartype="String" write="false">
												<% if ((titel_zichtbaar==null||!titel_zichtbaar.equals("0"))) { textStr += " " + dummy;  }%>
											</mm:field>
									<%	 } else { textStr += " " + dummy + "";  } 
									}	 
                    %></mm:field><%
                } 
            %></mm:list><%
        }
        cNodePaths.remove(relatedPath);
    } 
    %><mm:node element="<%= thisPath %>" jspvar="thisNode"><%
        if(debug) { %><br>Related found object for node <%= nextNode %> with path <%= thisPath %><% }  
        %><%@include file="../includes/relatedfoundobject.jsp"
    %></mm:node
></mm:list><% 

} 
%></mm:nodeinfo
></mm:node>
