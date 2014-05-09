<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%!
 String header(String title) {
    return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml/DTD/transitional.dtd\">\n"+
    "<html xmlns=\"http://www.w3.org/TR/xhtml\">\n"+
    "<head>\n"+
    "<title>"+title+"</title>\n"+
    "<link rel=\"stylesheet\" type=\"text/css\" href=\"/mmadmin/jsp/css/mmbase.css\" />\n"+
    "</head>\n"+
    "<body class=\"basic\" >\n";
}

String footer() {
    return "</body>\n"+
    "</html>\n";
 }
%>
