<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<related:wizard  title="Auteurs" nodetype="people" >
    <edit:path name="Auteurs" session="people_related"/>
    <edit:sessionpath/>

    <c:set var="fields" >
        <form:textfield field="firstname"/>
        <form:textfield field="lastname"/>
        <form:textfield field="email"/>
    </c:set>
    <related:view  relationrole="${param.relationrole}">
        <c:out value="${fields}" escapeXml="false"/>
        </related:view>
        
    <related:add  relationrole="${param.relationrole}">
        <c:out value="${fields}" escapeXml="false"/>
    </related:add>

    <list:search collapsed="${empty param.search}">
        <list:searchfields fields="firstname,lastname,email,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="${param.relationrole}" showsearchall="false" searchall="true">
            <list:searchrow fields="firstname,lastname,email" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
