<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Plaats van herinnering" wizardfile="plaats" >

    <edit:path name="plaats" node="${nodenr}" session="plaats"/>
    <edit:sessionpath/>


    <form:container nodetype="memorylocation">
        <h4>Plaats:</h4>
        <%--
            test the updateRelationActions:
            we will create a checkbox to some specific urls object
            (a test object nodenr: 35890196
        --%>

        <mm:cloud method="asis">

                <input type="hidden" name="updateNodeActions[35890196].id" value="dest" />
                <input type="hidden" name="updateNodeActions[35890196].number" value="35890196" />

                <input type="hidden" name="updateCheckboxRelationActions[1].referDestination" value="dest" />
                <input type="hidden" name="updateCheckboxRelationActions[1].role" value="posrel" />
                <c:choose>
                    <c:when test="${modifier == 'create'}">
                        <input type="hidden" name="updateCheckboxRelationActions[1].referSource" value="new" />
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="updateCheckboxRelationActions[1].source" value="${param.nodenr}" />
                        <%--see if we have a current relation--%>
                        <mm:list path="memorylocation,posrel,urls" nodes="${params.nodenr}" constraints="urls.number=35890196">
                            <c:set var="checked" value="checked" />
                        </mm:list>
                    </c:otherwise>
                </c:choose>
                <form:fieldcontainer label="update Checkbox Relations test">
                    <input type="checkbox" onclick="disableRelated()" style="width: 15px" ${checked} name="updateCheckboxRelationActions[1].relate" value="true" />
                </form:fieldcontainer>
        </mm:cloud>

        <form:textfield field="location"/>
        <form:datefield field="date" defaultvalue="now"/>
        <form:textfield field="title"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="text" size="medium"/>
        <h5>Posities in het flash filmpje:</h5>
        <form:textfield field="xposition"/>
        <form:textfield field="yposition"/>
        <h5>Overigen:</h5>
        <c:set var="now" ><mm:time time="now"/></c:set>
        <form:datefield edit="false" field="creationdate" defaultvalue="${now}"/>
        <form:radiofield field="show" defaultvalue="0">
            <form:simpleoptionlist options="0=Niet tonen,1=Tonen"/>
        </form:radiofield>

    </form:container>

    <mm:cloud >
        <form:related>
            <form:viewcontainer title="Gerelateerde herinneringen">
                <jsp:attribute name="items">
                    <ul>
                        <li>
                            <a href="herinneringen.jsp?nodenr=${nodenr}&status=1">niet afgemaakt</a>
                            <mm:listcontainer path="memorylocation,memoryresponse" nodes="${param.nodenr}">
                                <mm:constraint field="memoryresponse.status" value="1" operator="="/>
                                (<mm:size/>)
                            </mm:listcontainer>
                        </li>
                        <li>
                            <a href="herinneringen.jsp?nodenr=${nodenr}&status=2">afgemaakt, niet geactiveerd</a>
                            <mm:listcontainer path="memorylocation,memoryresponse" nodes="${param.nodenr}">
                                <mm:constraint field="memoryresponse.status" value="2" operator="="/>
                                (<mm:size/>)
                            </mm:listcontainer>
                            </li>
                        <li>
                            <a href="herinneringen.jsp?nodenr=${nodenr}&status=3">geactiveerd, niet door redactie gezien</a>
                            <mm:listcontainer path="memorylocation,memoryresponse" nodes="${param.nodenr}">
                                <mm:constraint field="memoryresponse.status" value="3" operator="="/>
                                (<mm:size/>)
                            </mm:listcontainer>
                        </li>
                        <li>
                            <a href="herinneringen.jsp?nodenr=${nodenr}&status=4">door de redactie gezien</a>
                            <mm:listcontainer path="memorylocation,memoryresponse" nodes="${param.nodenr}">
                                <mm:constraint field="memoryresponse.status" value="1" operator="="/>
                                (<mm:size/>)
                            </mm:listcontainer>
                        </li>
                    </ul>
                </jsp:attribute>
            </form:viewcontainer>

        <form:view nodetype="images" relatedpage="../common/posrel_images" relationrole="posrel" sortable="true" name="gekoppelde plaatjes"/>

        <form:view nodetype="urls" relatedpage="../common/posrel_urls" relationrole="posrel" sortable="true" name="gekoppelde urls" delete="true"/>

        <form:view nodetype="binders" relatedpage="../common/posrel_binders" relationrole="posrel" sortable="true" name="gekoppelde dossiers" edit="false" create="false" />

        <form:view nodetype="attachments" relatedpage="../common/posrel_attachments" relationrole="posrel" sortable="true" name="gekoppelde bijlagen" edit="false" create="false" />

        <form:view nodetype="audiofragments" relatedpage="../common/posrel_audiofragments" relationrole="posrel" sortable="true" name="gekoppelde audio fragmenten" edit="false" create="false" />

        <form:view nodetype="videofragments" relatedpage="../common/posrel_videofragments" relationrole="posrel" sortable="true" name="gekoppelde video fragmenten" edit="false" create="false" />

        <form:view nodetype="episodes" relatedpage="../common/posrel_episodes" relationrole="posrel" sortable="true" name="gekoppelde afleveringen" edit="false" create="false" />

        <form:view nodetype="items" relatedpage="../common/posrel_items" relationrole="posrel" sortable="true" name="gekoppelde items" edit="false" create="false" />

        <form:view nodetype="news" relatedpage="../common/posrel_news" relationrole="posrel" sortable="true" name="gekoppelde nieuws items" edit="false" create="false" />

        <form:view nodetype="shopproducts" relatedpage="../common/posrel_shopproducts" relationrole="posrel" sortable="true" name="gekoppelde winkel producten" edit="false" create="false" />

    </form:related>
    </mm:cloud>
</form:wizard>



