<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/edit/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/edit/list" %>

<related:wizard  title="Afleveringen" nodetype="episodes" >

    <edit:path name="Afleveringen" session="afleveringen"/>
    <edit:sessionpath/>


    <related:view  relationrole="posrel" edit="false">
    <%--
        <form:textfield field="url"/>
        <form:textareafield field="description"/>
        --%>
    </related:view>

    <%--
    <related:add  relationrole="posrel">
        <form:textfield field="url"/>
        <form:textareafield field="description"/>
    </related:add>
    --%>

    <list:search nodetype="episodes" collapsed="${empty param.search}">
        <list:searchfields fields="title,subtitle,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel"  showsearchall="false" searchall="true">
            <list:searchrow fields="title,subtitle"  />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
