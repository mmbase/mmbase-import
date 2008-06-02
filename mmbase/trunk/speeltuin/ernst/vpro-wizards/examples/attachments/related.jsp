<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/edit/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/edit/list" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<related:wizard  title="Bijlagen" nodetype="attachments" >

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
