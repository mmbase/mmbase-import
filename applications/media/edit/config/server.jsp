<%-- Need to know the real servername sometimes --%><%!
String thisServer(javax.servlet.http.HttpServletRequest request) { 
    return "http://michiel.omroep.nl" + request.getContextPath();
} %>
