<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>

<related:wizard  title="Shop producten" nodetype="shopproducts" >

    <edit:path name="Shop producten" session="shopproducts"/>
    <edit:sessionpath/>


    <related:view  relationrole="posrel" edit="false"/>

    <%--
    <related:add  relationrole="posrel">
        <form:textfield field="url"/>
        <form:textareafield field="description"/>
    </related:add>
    --%>

    <list:search nodetype="shopproducts" collapsed="${empty param.search}">
        <list:searchfields fields="name,subtitle,owner,number" defaultmaxage="365"/>
        <list:parentsearchlist parentnodenr="${nodenr}" relationrole="posrel" showsearchall="false" searchall="true">
            <list:searchrow fields="name,subtitle" />
        </list:parentsearchlist>
    </list:search>

</related:wizard>
