<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard  title="Urls" nodetype="urls" >

    <edit:path name="Urls" session="urls"/>
    <edit:sessionpath/>


    <related:view  relationrole="posrel">
        <form:textfield field="url"/>
        <form:textareafield field="description"/>
    </related:view>

    <related:add  relationrole="posrel">
        <form:textfield field="url"/>
        <form:textareafield field="description"/>
    </related:add>

    <list:search nodetype="urls" collapsed="${empty param.search}">
        <list:searchfields fields="url,description,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel" showsearchall="false" searchall="true">
            <list:searchrow fields="url,description" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
