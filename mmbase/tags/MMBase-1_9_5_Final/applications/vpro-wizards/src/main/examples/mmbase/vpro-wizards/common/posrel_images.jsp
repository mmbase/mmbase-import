<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard  title="Afbeeldingen" nodetype="images" >

    <edit:path name="Afbeeldingen" session="images"/>
    <edit:sessionpath/>


    <related:view multipart="true" relationrole="posrel">
        <form:textfield field="title"/>
        <form:textfield field="description"/>
        <form:filefield field="handle"/>
    </related:view>

    <related:add multipart="true" relationrole="posrel">
        <form:textfield field="title"/>
        <form:textfield field="description"/>
        <form:filefield field="handle"/>

    </related:add>

    <list:search nodetype="images" collapsed="${empty param.search}">
        <list:searchfields fields="title,description,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel"  showsearchall="false" searchall="true">
            <list:searchrow fields="handle,title,description" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
