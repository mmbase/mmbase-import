<%-- Need to know the real servername sometimes --%><%!
String  getHost() { return "michiel.omroep.nl"; }
String thisServer(javax.servlet.http.HttpServletRequest request, String url) { 
    return "http://" + getHost() + request.getContextPath() + url;
} %>
