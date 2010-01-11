<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
    <c:when test="${empty param.relationrole}">
        <c:set var="relationrole" value="related" />
    </c:when>
    <c:otherwise>
        <c:set var="relationrole" value="${param.relationrole}" />
    </c:otherwise>
</c:choose>

<related:wizard  title="Bijlagen" nodetype="attachments" relationrole="${relationrole}">

    <edit:sessionpath/>
    <edit:path name="Gekoppelde Bijlagen"/>


    <related:view  edit="${param.edit}" multipart="true">
        <form:textfield field="title"/>
        <form:textareafield size="small" field="description"/>
        <form:filefield field="handle"/>
    </related:view>

    <c:if test="${param.create == 'true'}">
        <related:add >
        <form:textfield field="title"/>
        <form:textareafield size="small" field="description"/>
        <form:filefield field="handle"/>
        </related:add>
    </c:if>

    <list:search collapsed="${empty param.search}">
        <list:searchfields fields="title,description,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}"  showsearchall="false" searchall="true">
            <list:searchrow fields="title,description,handle" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
