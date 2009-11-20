<%--
    use this tag inside a search tag, to set the parent node of the intems you want to search.
    if you want to use a parent node you have to at least have a parent node nr and a relation role
--%>
<%@ attribute name="nodenr" required="true"  %>
<%@ attribute name="relationrole" required="true"  %>
<%@ attribute name="showsearchall" %>

<c:set var="nodenr" scope="request" value="${nodenr}"/>
<c:set var="relationrole" scope="request" value="${relationrole}"/>
<c:set var="showsearchall" scope="request" value="${showsearchall}"/>
