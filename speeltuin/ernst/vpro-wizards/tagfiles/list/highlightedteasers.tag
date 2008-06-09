<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="sectionnr" required="true" %>
<%@ attribute name="type" required="true" %>

<c:set var="highlightnr" scope="request">${sectionnr}</c:set>

<div class="highlighted">
    <div class="header">
        Teasers
        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/lightbulb_green.png" class="icon" border="0" />
    </div>

    <c:set var="teaserClass">eenTeaser</c:set>
    <mm:cloud method="asis">
        <mm:list nodes="${sectionnr}" path="sections,templates">
            <c:set var="teaserClass"><mm:field name="templates.filename"/></c:set>
        </mm:list>
    </mm:cloud method="asis">

    <div class="layout layout_${teaserClass}">
        <a class="eenteaser" href="/mmbase/vpro-wizards/system/teasertemplate.jsp?sectionnr=${sectionnr}&amp;template=eenTeaser">
            <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_1.png" border="0" title="1 teaserpositie" alt="1 teaserpositie"/>
        </a>
        <a class="vierteasers" href="/mmbase/vpro-wizards/system/teasertemplate.jsp?sectionnr=${sectionnr}&amp;template=vierTeasers">
            <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_2.png" border="0" title="4 teaserposities" alt="4 teaserpositie"/>
        </a>
    </div>

    <table id="tabel" class="teasers ${teaserClass}" cellspacing="0" cellpadding="0" border="0">
    <mm:cloud>
    <thead>
        <tr><td></td><td></td><td>Teasernaam</td><td>publiceren van</td><td>publiceren tot</td></tr>
    </thead>
    <tbody>
    <mm:list nodes="${sectionnr}" path="sections,posrel,${type},mmevents" fields="${type}.number" orderby="mmevents.stop">
        <c:set var="tstart"><mm:field name="mmevents.start" /></c:set>
        <c:set var="tnow"><mm:time time="now" /></c:set>
        <c:set var="tstop"><mm:field name="mmevents.stop" /></c:set>

        <c:set var="status">
            <c:choose>
                <c:when test="${tnow > tstop}">
                    passed
                </c:when>
                <c:when test="${tstart < tnow and tnow < tstop}">
                    online
                </c:when>
                <c:when test="${tnow < tstart}">
                    future
                </c:when>
            </c:choose>
        </c:set>

        <mm:even>
            <tr class="even ${status}">
        </mm:even>
        <mm:odd>
            <tr class="odd ${status}">
        </mm:odd>
            <td class="layout eenteaser">
                <c:set var="pos"><mm:field name="posrel.pos"/></c:set>
                <c:choose>
                    <c:when  test="${pos == 1}">
                        <a class="selected" href="/mmbase/vpro-wizards/system/computeteaser.jsp?sectionnr=${sectionnr}&amp;position=-1&amp;teasernr=<mm:field name="${type}.number"/>">
                            <img class="icon" alt="Teaser vrij laten rouleren" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_3.png" border="0" title="Teaser vrij laten rouleren"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="/mmbase/vpro-wizards/system/computeteaser.jsp?sectionnr=${sectionnr}&amp;position=1&amp;teasernr=<mm:field name="${type}.number"/>">
                            <img class="icon" alt="Zet teaser vast" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_3_soft.png" border="0" title="Zet teaser vast"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </td>
            <td class="layout vierteasers">
                <c:forEach begin="1" end="4" step="1" var="index">
                    <c:choose>
                        <c:when test="${index == pos}">
                            <a class="selected" href="/mmbase/vpro-wizards/system/computeteaser.jsp?sectionnr=${sectionnr}&amp;position=-1&amp;teasernr=<mm:field name="${type}.number"/>">
                            <c:choose>
                                <c:when test="${index == 1}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_up_left.png" border="0" title="Teaser vrij laten rouleren"/>
                                </c:when>
                                <c:when test="${index == 2}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_up_right.png" border="0" title="Teaser vrij laten rouleren"/>
                                </c:when>
                                <c:when test="${index == 3}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_down_left.png" border="0" title="Teaser vrij laten rouleren"/>
                                </c:when>
                                <c:when test="${index == 4}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_down_right.png" border="0" title="Teaser vrij laten rouleren"/>
                                </c:when>
                            </c:choose>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="unselected" href="/mmbase/vpro-wizards/system/computeteaser.jsp?sectionnr=${sectionnr}&amp;position=${index}&amp;teasernr=<mm:field name="${type}.number"/>">
                            <c:choose>
                                <c:when test="${index == 1}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_up_left_soft.png" border="0" title="Zet teaser vast"/>
                                </c:when>
                                <c:when test="${index == 2}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_up_right_soft.png" border="0" title="Zet teaser vast"/>
                                </c:when>
                                <c:when test="${index == 3}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_down_left_soft.png" border="0" title="Zet teaser vast"/>
                                </c:when>
                                <c:when test="${index == 4}">
                                    <img class="icon" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/box_down_right_soft.png" border="0" title="Zet teaser vast"/>
                                </c:when>
                            </c:choose>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </td>
            <td class="icons">
                <c:if test="${not empty path_url}">
                    <c:set var="params">&amp;path_url=${path_url}&amp;path_name=${path_name}</c:set>
                </c:if>
                <a onclick="return checkSearch(this);" href="/mmbase/vpro-wizards/system/unhighlight.jsp?object=<mm:field name="posrel.number"/>&amp;sectienr=${sectionnr}" class="unhighlight">
                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/lightbulb_off_green.png" class="icon" border="0" alt="" title="Niet meer uitlichten"/>
                </a>
                <c:set var="objectNumber"><mm:field name="${type}.number"/></c:set>
                <a href="${wizardfile}.jsp?nodenr=${objectNumber}${params}" class="edit">
                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit_green.png" class="icon" border="0" title="Aanpassen" >
                </a>
            </td>
            <td>
                <a href="${wizardfile}.jsp?nodenr=${objectNumber}${params}" class="edit" title="Aanpassen"><mm:field name="${type}.gui()"/></a>
            </td>
            <mm:node element="${type}">
                <mm:relatednodes type="mmevents" max="1">
                    <c:set var="start"><mm:field name="start"><mm:time format="dd-MM-yyyy HH:mm"/></mm:field></c:set>
                    <c:set var="stop" ><mm:field name="stop" ><mm:time format="dd-MM-yyyy HH:mm"/></mm:field></c:set>
                </mm:relatednodes>
            </mm:node>
            <td>
                <a href="${wizardfile}.jsp?nodenr=${objectNumber}${params}" class="edit" title="Aanpassen">${start}</a>
                </td>
            <td>
                <a href="${wizardfile}.jsp?nodenr=${objectNumber}${params}" class="edit" title="Aanpassen">${stop}</a>
            </td>
            <c:set var="start"/>
            <c:set var="stop"/>
    </mm:list>
    </tbody>
    </mm:cloud>
    </table>
</div>


