<%-- Need to know the real servername sometimes --%><%!
String  getHost() { return org.mmbase.applications.media.urlcomposers.Config.host; }
String thisServer(javax.servlet.http.HttpServletRequest request, String url) { 
    return "http://" + getHost() + request.getContextPath() + url;
} %>
