<%-- Need to know the real servername sometimes --%><%!
String thisServer(javax.servlet.http.HttpServletRequest request, String url) { 
    return "http://michiel.omroep.nl" + url;
} %>
