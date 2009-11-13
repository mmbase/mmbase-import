<%--
    deze tag maakt gebruik van necohtml om de body te html-cleanen
--%>

<%@tag import="org.mmbase.applications.vprowizards.spring.util.HTMLFilterUtils"%><jsp:doBody var="body"/>
<%
    String body = (String) jspContext.getAttribute("body");
    try{
        body = HTMLFilterUtils.filter(body);
    }catch(Exception e){}
    out.write(body);
%>
