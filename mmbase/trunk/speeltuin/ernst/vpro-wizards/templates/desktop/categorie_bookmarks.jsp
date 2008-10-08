<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard  title="Categorie : Bookmarks" nodetype="bookmarks">
    <edit:sessionpath/>
    <edit:path name="gerelateerde bookmarks"  />

    <related:view sortable="true">
        <form:textfield field="title"/>
        <form:textfield field="url"/>
    </related:view>

    <related:add relationrole="posrel">
        <form:textfield field="title"/>
        <form:textfield field="url"/>
    </related:add>

    <list:search nodetype="bookmarks" collapsed="${empty param.search}">
        <list:searchfields fields="title,url,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist  parentnodenr="${nodenr}" relationrole="posrel" showsearchall="false" searchall="true">
            <list:searchrow fields="title,url,owner,number" harddelete="false"/>
        </list:parentsearchlist>
    </list:search>

</related:wizard>