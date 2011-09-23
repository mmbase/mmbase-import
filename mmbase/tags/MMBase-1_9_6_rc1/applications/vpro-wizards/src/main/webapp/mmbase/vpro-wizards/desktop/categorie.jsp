<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>

<form:wizard title="Categorie" wizardfile="categorie">

    <c:choose>
        <c:when test="${not empty param.folder}">
            <c:set var="title" >Nieuwe container in <mm:node number="${param.folder}"><mm:function name="gui" /></mm:node></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="title" value="Nieuwe container" />
        </c:otherwise>
    </c:choose>

    <edit:path node="${param.nodenr}" name="${title}" session="dt_category" url="${pageContext.request.servletPath}?${pageContext.request.queryString}"/>
    <edit:sessionpath/>

    <form:container nodetype="categories">
        <form:createrelation referSource="new" destination="${param.folder}" role="posrel"/>
            <%--
            I don't think we have to do this now.
            <util:lastpos nodenr="${param.folder}" desttype="categories" var="p"/>
            <form:hiddenfield field="pos" defaultvalue="${p+1}"/>
            --%>
        <form:showfield field="number"/>
        <form:textfield field="name" />
     </form:container>

    <form:related>
        <form:view nodetype="bookmarks" sortable="true" searchdir="source"/>
        <form:view nodetype="attachments" sortable="true" searchdir="source"/>
        <form:view nodetype="freetext" name="Note" searchdir="source"/>
    </form:related>
</form:wizard>