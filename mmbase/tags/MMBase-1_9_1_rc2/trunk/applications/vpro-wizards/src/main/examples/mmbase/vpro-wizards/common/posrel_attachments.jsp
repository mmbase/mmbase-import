<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard  title="Bijlagen" nodetype="attachments" >
    <edit:path name="Bijlagen" session="attachments"/>
    <edit:sessionpath/>


    <related:view relationrole="posrel" edit="false">
    <%--
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:textareafield field="body"/>
        --%>
    </related:view>
<%--
    <related:add relationrole="posrel">
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:textareafield field="body"/>
    </related:add>
--%>

    <list:search nodetype="binders" collapsed="${empty param.search}">
        <list:searchfields fields="title,description,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel"  showsearchall="false" searchall="true">
            <list:searchrow fields="title, description,filename" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
