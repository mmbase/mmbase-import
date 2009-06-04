<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Magazine" wizardfile="magazine" >

    <edit:path name="magazine" node="${nodenr}" session="mag"/>
    <edit:sessionpath/>


    <form:container nodetype="mags">
        <form:showfield field="number"/>
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="body" size="medium"/>
    </form:container>

        <form:related>

            <form:view nodetype="news"
                relationrole="posrel"
                sortable="true"
                relatedpage="../news/related"
                openwizard="../news/newsitem.jsp"
                newwizard="../news/newsitem.jsp"
                name="gekoppelde nieuws berichten">
                <jsp:attribute name="display">
                    <mm:include page="../news/item-publishedstatus.jsp" >
                        <mm:param name="nodenr" value="${_nodenr}" />
                    </mm:include>
                </jsp:attribute>
                </form:view>
        <%--

        <form:related>
            <form:view nodetype="urls"
                relatedpage="../urls/related"
                relationrole="posrel"
                sortable="true"
                name="gekoppelde url's"/>

        <form:view nodetype="urls" relatedpage="../common/posrel_urls" relationrole="posrel" sortable="true" name="gekoppelde urls" delete="true"/>

            <form:view nodetype="binders" relatedpage="../common/posrel_binders" relationrole="posrel" sortable="true" name="gekoppelde dossiers" edit="false" create="false" />

            <form:view nodetype="attachments" relatedpage="../common/posrel_attachments" relationrole="posrel" sortable="true" name="gekoppelde bijlagen" edit="false" create="false" />

            <form:view nodetype="audiofragments" relatedpage="../common/posrel_audiofragments" relationrole="posrel" sortable="true" name="gekoppelde audio fragmenten" edit="false" create="false" />

            <form:view nodetype="videofragments" relatedpage="../common/posrel_videofragments" relationrole="posrel" sortable="true" name="gekoppelde video fragmenten" edit="false" create="false" />

            <form:view nodetype="episodes" relatedpage="../common/posrel_episodes" relationrole="posrel" sortable="true" name="gekoppelde afleveringen" edit="false" create="false" />

            <form:view nodetype="items" relatedpage="../common/posrel_items" relationrole="posrel" sortable="true" name="gekoppelde items" edit="false" create="false" />

            <form:view nodetype="news" relatedpage="../common/posrel_news" relationrole="posrel" sortable="true" name="gekoppelde nieuws items" edit="false" create="false" />

            <form:view nodetype="shopproducts" relatedpage="../common/posrel_shopproducts" relationrole="posrel" sortable="true" name="gekoppelde winkel producten" edit="false" create="false" />
        --%>

    </form:related>
</form:wizard>



