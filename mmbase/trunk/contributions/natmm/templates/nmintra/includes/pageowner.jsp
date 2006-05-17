<%--
String ownerFirstname = ""; 
String ownerSuffix = ""; 
String ownerLastname = ""; 
String ownerEmail = ""; 
boolean ownerFound = false; 

%><mm:node number="<%= pageId %>"><%@include file="../includes/relatedemployee.jsp" %></mm:node><% 
if(!ownerFound) { 
	%><mm:node number="<%= rubriekId %>"><%@include file="../includes/relatedemployee.jsp" %></mm:node><% 
} 
if(!ownerFound) { 
	%><mm:node number="<%= rootId %>"><%@include file="../includes/relatedemployee.jsp" %></mm:node><% 
}

if(ownerFound) {
    %><p><div align="right">Informatie over deze pagina: <a href="mailto:<%= ownerEmail %>?subject=<% 
        %><mm:node number="<%= rootId %>"><mm:field name="title" /></mm:node
        > - <mm:node number="<%= pageId %>"><mm:field name="title" /></mm:node
        >"><%= ownerFirstname %> <%= ownerSuffix %> <%= ownerLastname 
        %></a><% if(postingStr.equals("|action=print")) { %> <%= ownerEmail %><% } %></div></p><% 
}
--%>