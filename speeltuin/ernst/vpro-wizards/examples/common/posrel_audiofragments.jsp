<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/edit/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/edit/list" %>

<related:wizard  title="Audio Fragmenten" nodetype="audiofragments" >
    <edit:path name="Audiofragmenten" session="audiofragments"/>
    <edit:sessionpath/>


    <related:view relationrole="posrel" edit="false">
    </related:view>
<%--
    <related:add relationrole="posrel">
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:textareafield field="body"/>
    </related:add>
--%>

    <list:search nodetype="audiofragments" collapsed="${empty param.search}">
        <list:searchfields fields="title,subtitle,intro,body,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel"  showsearchall="false" searchall="true">
            <list:searchrow fields="title, intro"  />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
