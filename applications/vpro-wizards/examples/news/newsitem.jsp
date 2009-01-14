<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Nieuws Berichtje" wizardfile="newsitem" >

    <edit:path name="Nieuws Bericht" node="${nodenr}" session="newsitem_${not empty param.secondnews ? 'second' : ''}"/>
    <edit:sessionpath/>
    
    <%--
        This editor has one special feature: among the field list there is a dropdown
        with the available magazines. When you create it, you can select on, and
        the newsitem will be connected to that. it also works for existing news items.
    --%>

    
    <form:container nodetype="news">
        <form:createrelation source="${param.parentnodenr}" referDestination="new" role="${param.relationrole}"/>
        <form:showfield field="number"/>
        <form:datefield field="date" fieldname="Publicatie datum" defaultvalue="now"/>
        <form:textfield field="title"/>
        <form:textfield field="subtitle"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="body" size="medium"/>
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
                name="gekoppelde nieuws berichten">
                <jsp:attribute name="display">
                    <mm:include page="item-publishedstatus.jsp" >
                        <mm:param name="nodenr" value="${_nodenr}" />
                    </mm:include>
                </jsp:attribute>
            </form:view>
                
            <form:view nodetype="images"
                relationrole="posrel" sortable="true"
                relatedpage="../images/related"
                name="gekoppelde plaatjes"/>
                
            <form:view nodetype="attachments"
                relationrole="posrel" sortable="true"
                relatedpage="../attachments/related"
                name="gekoppelde bijlange"/>
                
             <form:view nodetype="people" 
                relatedpage="../people/related"
                name="auteur(s) van dit artikel"/>
                
    </form:related>
</form:wizard>



