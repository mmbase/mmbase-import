<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>

<related:wizard  title="Categorie : Notes" nodetype="freetext">
    <edit:sessionpath/>
    <edit:path name="gerelateerde notities"  />

    <related:view>
        <form:richtextfield field="title"/>
    </related:view>

    <related:add>
        <form:richtextfield field="title"/>
    </related:add>

    <list:search nodetype="freetext" collapsed="${empty param.search}">
        <list:searchfields fields="intro,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist  parentnodenr="${nodenr}" relationrole="posrel" showsearchall="false" searchall="true">
            <list:searchrow fields="title,owner,number" harddelete="false"/>
        </list:parentsearchlist>
    </list:search>

</related:wizard>