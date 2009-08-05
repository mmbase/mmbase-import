<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%--
    This tag is used to alter the flushname value(s), by replacing place holders with dynamic values.
    This is done in the list result, and the purpose is to be able to create cache identifiers with specific
    node values.
    for instance: a cache idenitfier
        page_[page]
    will be converted to
        page_[page:13455]
    where 13455 is the value of the number field of the current page node in the list.
    this is only done when the type of the nodes listed by the caller list is matching the type
    in the placeholder.

    The template is preserved in the cache identifier, so if you go back to the same list later
    the idenitifier is still recognized as a placeholder, and it can be reused.

    Thet template will finally be cleaned out of the identifier in
    nl.vpro.mmbase.applications.portalettes.util.ThemasitesOSCachenameResolver
    So the abouve example will turn out as:
        page_13455

--%>
<%@ tag body-content="empty"  %>
<%@ attribute name="flushname" required="true" %>
<%@ attribute name="nodenr"  required="true" %>
<%@ attribute name="type"  required="true" %>
<%@ variable name-given="result" scope="AT_END" %>
<c:if test="${ not empty flushname}">
    <mm:cloud jspvar="cloud">
        <%jspContext.setAttribute("cloud", cloud);%>
        <mm:node number="${nodenr}" notfound="skipbody">
            <c:set var="type" ><mm:nodeinfo type="type"/></c:set>
                <jsp:useBean id="filter" class="org.mmbase.applications.vprowizards.spring.cache.FlushNameTemplateBean"/>
                <jsp:setProperty name="filter" property="template" value="${flushname}"/>
                <jsp:setProperty name="filter" property="nodenr" value="${nodenr}"/>
                <jsp:setProperty name="filter" property="type" value="${type}"/>
                <jsp:setProperty name="filter" property="cloud" value="${cloud}"/>
            <c:set var="result"><jsp:getProperty name="filter" property="template"/></c:set>
        </mm:node>
    </mm:cloud>
</c:if>
