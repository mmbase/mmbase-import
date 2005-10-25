<%--
This a shortcut include for a button which is shown in the cockpit
(default version, used in forms and stuff like that)
See button.jsp for descriptions of the parameters
--%>
<jsp:include page="/forum/button.jsp">
    <jsp:param name="width" value="128"/>
    <jsp:param name="height" value="20"/>
    <jsp:param name="style" value="button"/>
    <jsp:param name="caption" value="<%=request.getParameter("caption")%>"/>
    <jsp:param name="link" value="<%=request.getParameter("link")%>"/>
    <jsp:param name="onclick" value="<%=request.getParameter("onclick")%>"/>
    <jsp:param name="target" value="<%=request.getParameter("target")%>"/>
</jsp:include>
