<%--
    deze tag maakt gebruik van necohtml om de body te html-cleanen
--%>
<jsp:doBody var="body"/>
<%
    String body = (String) jspContext.getAttribute("body");
    try{
        body = nl.vpro.dvt.communities.util.HTMLFilterUtils.filter(body);
    }catch(Exception e){}
    out.write(body);
%>
