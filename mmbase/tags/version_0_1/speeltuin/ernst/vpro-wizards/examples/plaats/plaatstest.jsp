<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Plaats van herinnering" wizardfile="plaats" >
    <edit:path name="plaats" node="${nodenr}" session="../plaats/plaats.jsp?nodenr=${nodenr}"/>
    <edit:sessionpath/>



    <form:container nodetype="memorylocation">

        <p>test: kijken of we een relatie en een node van type url kunnen aanmaken en bewerken
        als onderdeel van het formulier
        </p>

        <c:choose>
            <c:when test="${modifier == 'create'}">
                <p>maak een nieuwe node aan</p>
                <%-- maak een relatie en een node aan--%>
                <input type="hidden" name="createRelationActions[1].role"  value="posrel" />
                <input type="hidden" name="createRelationActions[1].referSource"  value="new" />
                <input type="hidden" name="createRelationActions[1].referDestination"  value="new2" />
                <input type="hidden" name="createNodeActions[2].id"  value="new2" />
                <input type="hidden" name="createNodeActions[2].type"  value="urls" />
                <form:fieldcontainer label="url">
                    <input type="text" onkeydown="disableRelated();" name="createNodeActions[2].fields[url]"  value=""/>
                </form:fieldcontainer>
            </c:when>

            <%--
                zoek de node
            --%>
            <c:otherwise>
                <p>node bewerken</p>
                <mm:cloud method="asis">
                    <mm:node number="${param.nodenr}" notfound="skipbody">
                        <mm:related path="posrel,urls" >
                            <mm:first>
                                <c:set var="_relationnr" ><mm:field name="posrel.number" /></c:set>
                                <c:set var="_nodenr" ><mm:field name="urls.number" /></c:set>
                            </mm:first>
                        </mm:related>
                    </mm:node>
                </mm:cloud>
                <p>still there</p>

                <%--  toon de velden --%>
                <c:choose>
                    <c:when test="${not empty _nodenr}">
                        <mm:cloud method="asis">
                            <mm:node number="${_nodenr}">
                                <p>nodenr: ${_nodenr}, relationnr: ${_relationnr}</p>
                                <form:fieldcontainer label="url">
                                    <input type="hidden"  name="updateNodeActions[${_nodenr}].number" value="${_nodenr}"/ >
                                    <c:set var="_url" ><mm:field name="url" /></c:set>
                                    <input type="text" onkeydown="disableRelated();" name="updateNodeActions[${_nodenr}].fields[url]"  value="${_url}"/>
                                </form:fieldcontainer>
                            </mm:node>
                        </mm:cloud>
                    </c:when>
                    <c:otherwise>
                        <p>error: no url found!</p>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

        <h4>Plaats:</h4>
        <form:textfield field="location"/>
        <form:datefield field="date" defaultvalue="now"/>
        <form:textfield field="title"/>
        <form:textareafield field="intro"/>
        <form:richtextfield field="text" size="medium"/>
        <h5>Posities in het flash filmpje:</h5>
        <form:textfield field="xposition"/>
        <form:textfield field="yposition"/>
        <object>
            <param name="movie" value="pvh_coordinate_picker.swf">
            <embed src="pvh_coordinate_picker.swf" width="520"  height="650" > </embed>
        </object>
        <h5>Overigen:</h5>
        <c:set var="now" ><mm:time time="now"/></c:set>
        <form:datefield edit="false" field="creationdate" defaultvalue="${now}"/>
        <form:radiofield field="show" defaultvalue="0">
            <form:simpleoptionlist options="0=Niet tonen,1=Tonen"/>
        </form:radiofield>

    </form:container>


    <mm:cloud >
        <form:related>
        <c:import url="quickvalidate.jsp" />

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
                                <mm:constraint field="memoryresponse.status" value="4" operator="="/>
                                (<mm:size/>)
                            </mm:listcontainer>
                        </li>
                    </ul>
                </jsp:attribute>
            </form:viewcontainer>

        <form:view nodetype="images" relatedpage="../common/posrel_images" relationrole="posrel" sortable="true" name="gekoppelde plaatjes"/>

        <form:view nodetype="urls" relatedpage="../common/posrel_urls" relationrole="posrel" sortable="true" name="gekoppelde urls"/>

        <form:view nodetype="binders" relatedpage="../common/posrel_binders" relationrole="posrel" sortable="true" name="gekoppelde dossiers" edit="false" create="false" />

        <%--
        <p>opmerking voor de redactie: dit is een manier om 'audiobron' objecten te koppelen. het is nog niet helemaal duidelijk of dit de manier wordt om audiotours te linken, en in de website wordt dit nog niet gebruikt.</p>
        --%>
        <form:view nodetype="audiofragments" relatedpage="../plaats/audiotourrel_audiofragments" relationrole="audiotourrel" sortable="false" name="gekoppelde audio tours" edit="false" create="false"/>

        <form:view nodetype="attachments" relatedpage="../common/posrel_attachments" relationrole="posrel" sortable="true" name="gekoppelde bijlagen" edit="false" create="false" />

        <form:view nodetype="audiofragments" relatedpage="../common/posrel_audiofragments" relationrole="posrel" sortable="true" name="gekoppelde audio fragmenten" edit="false" create="false" />

        <form:view nodetype="videofragments"  relatedpage="../common/posrel_videofragments" openwizard="plaats_videofragments.jsp" relationrole="posrel" sortable="true" name="gekoppelde video fragmenten" edit="false" create="false" />

        <form:view nodetype="episodes" relatedpage="../common/posrel_episodes" relationrole="posrel" sortable="true" name="gekoppelde afleveringen" edit="false" create="false" />

        <form:view nodetype="items" relatedpage="../common/posrel_items" relationrole="posrel" sortable="true" name="gekoppelde items" edit="false" create="false" />

        <form:view nodetype="news" relatedpage="../common/posrel_news" relationrole="posrel" sortable="true" name="gekoppelde nieuws items" edit="false" create="false" />

        <form:view nodetype="shopproducts" relatedpage="../common/posrel_shopproducts" relationrole="posrel" sortable="true" name="gekoppelde winkel producten" edit="false" create="false" />

    </form:related>
    </mm:cloud>
</form:wizard>



