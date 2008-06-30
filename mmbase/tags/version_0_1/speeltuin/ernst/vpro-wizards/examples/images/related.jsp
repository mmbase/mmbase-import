<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
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

<related:wizard  title="Afbeeldingen" nodetype="images" relationrole="${relationrole}">

    <edit:sessionpath/>
    <edit:path name="Gekoppelde Afbeeldingen"/>


    <related:view  edit="${param.edit}">
        <form:showfield field="number"/>
        <form:textfield field="title"/>
        <form:textfield field="description"/>
        <form:filefield field="handle"/>
    </related:view>

    <c:if test="${param.create == 'true'}">
        <related:add >
             <%--We moeten helaas ook nog een linkje maken naar die categorie --%>
            <mm:listnodes type="categories" constraints="name='afbeelding'" max="1" id="_n">
                <mm:node node="_n"><c:set var="nr"><mm:field name="number" /></c:set></mm:node>
            </mm:listnodes>
            <form:createrelation source="${nr}" referDestination="new" role="posrel" />
            <form:textfield field="title"/>
            <form:textfield field="description"/>
            <form:filefield field="handle"/>

        </related:add>
    </c:if>

    <list:search  collapsed="${empty param.search}">
        <list:searchfields fields="title,description" />
        <list:parentsearchlist parentnodenr="${nodenr}" showsearchall="false" searchall="true">
            <list:searchrow fields="handle,title,description" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
