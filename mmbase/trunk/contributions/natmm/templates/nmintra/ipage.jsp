<%@include file="includes/templateheader.jsp" 
%><% 

if(editorsName.indexOf(request.getServerName())>-1&&!isPreview) { // *** redirect to website servername ***
    response.sendRedirect("http://" + websiteName + request.getServletPath()+"?"+request.getQueryString());
} else {

%><%@include file="includes/authenticate.jsp" 
%><mm:cloud
><%@include file="includes/getids.jsp" 
%><%@include file="includes/getresponse.jsp" %><%
postingStr += "|";
String action = getResponseVal("action",postingStr);

%><html>
    <head>
        <title><mm:node number="<%= websiteId %>" notfound="skipbody"><mm:field name="naam" /></mm:node
        > - <mm:node number="<%= pageId %>" notfound="skipbody"><mm:field name="titel" /></mm:node></title>
        <meta http-equiv="imagetoolbar" content="no">
        <link rel="stylesheet" type="text/css" href="css/website.css">
    </head>
    <style>
        td, p { color: #000000; }
    </style>
    <body scroll="auto" <% if(!action.equals("noprint")) { %>onLoad="self.print();"<% } %>>
    <table border="0" cellpadding="0" cellspacing="0"><tr><% 
    
if(!pageId.equals("")) { 
    
    %><% String template_url = ""; 
    %><mm:list nodes="<%= pageId %>" path="pagina,paginatemplate" fields="paginatemplate.url,paginatemplate.naam"
    ><mm:field name="paginatemplate.url" jspvar="dummy" vartype="String" write="false"
        ><% template_url = dummy; 
    %></mm:field
    ></mm:list><%
    
    if(!template_url.equals("")) { 
            %><jsp:include page="<%= template_url + "p=" + pageId + "&category=" + categoryId %>" 
            ></jsp:include><% 
            
    } else {
    
        %><td colspan="2"><b><font color="#CC0000">Error:</font></b><br>Er moet nog een template gekoppeld worden aan de '<mm:node number="<%= pageId 
            %>"><mm:field name="titel" /></mm:node>' pagina.</td><% 
    }
    
} else {

    %><td colspan="2"><b><font color="#CC0000">Error:</font></b><br>Er is geen pagina gespecificeerd.</td><% 
} 

%>
    </tr></table>
</body>
</html>
</mm:cloud><%

}
%>
