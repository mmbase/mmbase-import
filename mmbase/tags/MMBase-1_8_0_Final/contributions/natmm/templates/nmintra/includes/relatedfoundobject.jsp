<%

pageId = "";
%><mm:relatednodes type="pagina"
    ><mm:field name="number" jspvar="pages_number" vartype="String" write="false"><%
        pageId = pages_number; 
    %></mm:field
></mm:relatednodes><%

if(!pageId.equals("")) {

    %><%@include file="../includes/getbreadcrumbs.jsp" %><%
  
    if(!nodeHref.equals("")&&(isPreview)) {
        %><p><%
        if(thisPath.equals("documents")) { nodeUrl = thisNode.getStringValue("url"); } 
        if(!titleStr.equals("")) { 
            %><span class="contenttitle"><a target="_top" href="<%= nodeUrl
                %>" class="contentreadmore"><%= highlightSearchTerms(titleStr,defaultSearchTerms,"u") %></a></span><br><% 
        }
        if(!textStr.trim().equals("")) {
            %><%= highlightSearchTerms(textStr,defaultSearchTerms,"u") %><br><%
        }

        %><%= nodeHref %></p><%
    }
} 
%>
