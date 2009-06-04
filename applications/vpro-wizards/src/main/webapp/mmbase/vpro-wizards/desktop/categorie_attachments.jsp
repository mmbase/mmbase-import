<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard title="Categorie : Bijlagen"  nodetype="attachments">
    <edit:sessionpath/>
    <edit:path name="gerelateerde bijlagen"  />

    <related:view sortable="true" multipart="true">
        <form:textfield field="title"/>
        <form:textareafield field="description" size="small"/>
        <form:filefield field="handle"/>
    </related:view>

    <related:add relationrole="posrel" multipart="true">
        <form:textfield field="title"/>
        <form:textareafield field="description" size="small"/>
        <form:filefield field="handle"/>
    </related:add>


    <list:search nodetype="attachments" collapsed="${empty param.search}">
        <list:searchfields fields="title,description"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel"  searchall="true">
            <list:searchrow fields="number,title,description" harddelete="false"/>
        </list:parentsearchlist>
    </list:search>

</related:wizard>