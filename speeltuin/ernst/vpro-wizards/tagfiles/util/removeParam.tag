<%--
    remove extra parameters that are added with the addParameter tag
--%>
<%@ tag import="java.util.*" body-content="empty"  %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="name" required="true"  %>

<%

{
    Map params = (Map)jspContext.findAttribute("___params");
    if(params != null){
        String name = (String) jspContext.getAttribute("name");
        params.remove(name);
    }
}
%>
