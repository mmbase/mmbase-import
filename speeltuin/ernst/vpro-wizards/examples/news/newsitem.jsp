<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Nieuws Bericht" wizardfile="newsitem" >

    <edit:path name="Nieuws Bericht" node="${nodenr}" session="newsitem"/>
    <edit:sessionpath/>


    <form:container nodetype="news">
        <form:showfield field="number"/>
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="body"/>
    </form:container>

        <form:related>
            <form:view nodetype="urls"
                relatedpage="../urls/related"
                relationrole="posrel"
                sortable="true"
                name="gekoppelde url's"/>

            <form:view nodetype="news"
                relationrole="sorted"
                sortable="true"
                relatedpage="../news/related"
                openwizard="newsitem.jsp"
                name="gekoppelde nieuws berichten"/>
    </form:related>
</form:wizard>



