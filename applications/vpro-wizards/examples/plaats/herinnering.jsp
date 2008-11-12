<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Herinnering van bezoeker" wizardfile="herinnering" >

    <edit:path name="herinnering" session="../plaats/herinnering.jsp?nodenr=${param.nodenr}"/>
    <edit:sessionpath/>



    <form:container nodetype="memoryresponse">
        <h4>Herinnering:</h4>
        <form:showfield field="name" fieldname="auteur"/>
        <form:showfield field="email"/>
        <form:textfield field="title"/>
        <form:textareafield size="medium" field="memory"/>

        <h4>Overigen:</h4>
        <form:showfield field="creationdate" guivalue="true"/>
        <form:radiofield field="show" defaultvalue="0">
            <form:simpleoptionlist options="0=Niet tonen,1=Tonen"/>
        </form:radiofield>
        <form:optionfield field="status">
            <form:simpleoptionlist >
                <form:option value="1" label="niet afgemaakt"/>
                <form:option value="2" label="afgemaakt, niet geactiveerd"/>
                <form:option value="3" label="geactiveerd, niet door redactie gezien"/>
                <form:option value="4" label="door de redactie gezien"/>
            </form:simpleoptionlist>
        </form:optionfield>
    </form:container>

    <form:related>
        <form:view nodetype="images" relatedpage="../common/posrel_images" relationrole="posrel" sortable="true" name="gekoppelde plaatjes"/>

    </form:related>
</form:wizard>



