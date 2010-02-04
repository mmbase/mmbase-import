<%@ tag import="java.util.*"
    description="A description could perhaps be helpful, because I don't understand much of this."
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@ taglib uri="http://www.springframework.org/tags" prefix="spring"

%><%@ attribute name="url" description="the url for the link in the path. this defaults to the current url with all parameters."
%><%@ attribute name="name" description="This is the new entry in the crumb path"
%><%@ attribute name="node" description="I don't know what this is supposed to mean"
%><%@ attribute name="reset" type="java.lang.Boolean" description="when this is true, the list with session path elements is cleared."
%>

<c:if test="${empty url}">
    <c:set var="url" >${pageContext.request.contextPath}${pageContext.request.servletPath}<c:if test="${not empty pageContext.request.queryString}">?</c:if>${pageContext.request.queryString}</c:set>
</c:if>
<%@ attribute name="session" description="write this path element to a map in the session."  %>


<c:set var="_name" >
    <c:choose>
        <c:when test="${not empty node}">
            <mm:cloud >
                <mm:node number="${node}"><mm:nodeinfo type="guitype"/>: <em><mm:field name="gui()"/></em></mm:node>
            </mm:cloud>
        </c:when>
        <c:otherwise>${name}</c:otherwise>
    </c:choose>
</c:set>

<%--flush the session path list?--%>
<c:if test="${reset}">
    <c:remove var="__path" scope="session"/>
</c:if>

<c:if test="${not empty session}">
    <c:if test="${empty __path}">
        <jsp:useBean id="__path" scope="session" class="java.util.ArrayList"/>
    </c:if>
    <%
        //add the path element to the list, if it is there already, replace it
        List list = (List) request.getSession().getAttribute("__path");
        String name = (String)jspContext.getAttribute("_name");
        String url = (String)jspContext.getAttribute("url");
        String key = (String)jspContext.getAttribute("session");

        //check if it is there yet.
        int addAt = -1;
        for(int i = 0; i < list.size(); i++){
            Map map = (Map)list.get(i);
            if(((String)map.get("key")).equals(key)){
                addAt = i;
            }
        }
        Map map = new HashMap();
        map.put("name", name);
        map.put("key", key);
        map.put("url",url);
        if(addAt > -1){
            //replace the entry, and remove everything after it.
            while(list.size() > addAt){
                list.remove(addAt);
            }
        }
        //add the entry
        list.add(map);
    %>
</c:if>

<c:if test="${empty session}">
    <script type="text/javascript">
        var path = document.getElementById('path');
        <c:choose>
            <c:when test="${not empty url}">
                    path.innerHTML = path.innerHTML + '/&nbsp;<a href="${url}"><spring:escapeBody javaScriptEscape="true">${name}</spring:escapeBody><\/a>&nbsp;';
            </c:when>
            <c:otherwise>
                path.innerHTML = path.innerHTML + '/&nbsp;<spring:escapeBody javaScriptEscape="true">${name}</spring:escapeBody>&nbsp;';
            </c:otherwise>
        </c:choose>
    </script>
</c:if>
