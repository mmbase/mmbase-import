<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<related:wizard  title="Nieuws Berichten" nodetype="news" >
    <edit:path name="Nieuws Berichten" session="newsitems_related"/>
    <edit:sessionpath/>

    <related:view  relationrole="${param.relationrole}">
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="body"/>
    </related:view>

    <c:if test="${not empty param.create}">
        <related:add  relationrole="${param.relationrole}">
            <form:textfield field="title"/>
            <form:textfield field="subtitle"/>
            <form:textareafield field="intro"/>
            <form:richtextfield field="body"/>
        </related:add>
    </c:if>

    <list:search  collapsed="${empty param.search}">
        <list:searchfields fields="title,subtitle,intro,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="${param.relationrole}" showsearchall="false" searchall="true">
            <list:searchrow fields="title,subtitle,intro" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
