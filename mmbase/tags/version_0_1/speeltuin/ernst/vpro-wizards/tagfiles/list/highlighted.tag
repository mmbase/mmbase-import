<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="sectionnr" required="true" %>
<%@ attribute name="type" required="true" %>

<c:set var="highlightnr" scope="request">${sectionnr}</c:set>

<div class="highlighted">
    <div class="header">
        uitgelicht
        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/lightbulb_green.png" class="icon" border="0" />
    </div>
    <table cellspacing="0" cellpadding="0" border="0">
    <tbody>
        <mm:cloud method="asis">
            <mm:list nodes="${sectionnr}" path="sections,posrel,${type}" fields="posrel.pos" orderby="posrel.pos">
                <c:set var="_nodenr"><mm:field name="${type}.number" /></c:set>
                <c:set var="_relationnr"><mm:field name="posrel.number" /></c:set>
                <mm:odd><c:set var="_class" value="odd" /></mm:odd>
                <mm:even><c:set var="_class" value="even" /></mm:even>
                <tr class="${_class}">
                    <td class="move">
                        <c:set var="urlup">
                            <mm:url page="/wizard/post">
                                <mm:param name="posrelSortActions[${_relationnr}].number" value="${_nodenr}" />
                                <mm:param name="posrelSortActions[${_relationnr}].containerNode" value="${sectionnr}" />
                                <mm:param name="posrelSortActions[${_relationnr}].direction" value="up" />
                                <mm:param name="flushname" value="${flushname}" />
                            </mm:url>
                        </c:set>
                        <c:set var="urldown">
                            <mm:url page="/wizard/post">
                                <mm:param name="posrelSortActions[${_relationnr}].number" value="${_nodenr}" />
                                <mm:param name="posrelSortActions[${_relationnr}].containerNode" value="${sectionnr}" />
                                <mm:param name="posrelSortActions[${_relationnr}].direction" value="down" />
                                <mm:param name="flushname" value="${flushname}" />
                            </mm:url>
                        </c:set>
                        <mm:last inverse="true">
                            <%--
                            <a style="text-decoration:none" href="/mmbase/vpro-wizards/system/changeposrelnew.jsp?container=${sectionnr}&node=${_nodenr}&direction=up" class="movedown" onclick="return checkSearch(this);">
                            --%>
                            <a style="text-decoration:none" href="${urlup}" class="movedown" onclick="return checkSearch(this);">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_down_green.png" class="icon" border="0" alt="" title="Sortering aanpassen"/>
                            </a>
                        </mm:last>
                        <mm:first inverse="true">
                            <%--
                            <a style="text-decoration:none" href="/mmbase/vpro-wizards/system/changeposrelnew.jsp?container=${sectionnr}&node=${_nodenr}&direction=down" class="moveup" onclick="return checkSearch(this);">
                            --%>
                            <a style="text-decoration:none" href="${urldown}" class="moveup" onclick="return checkSearch(this);">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_up_green.png" class="icon" border="0" alt="" title="Sortering aanpassen"/>
                            </a>
                        </mm:first>
                    </td>
                    <td class="icons">
                        <c:if test="${not empty path_url}">
                            <c:set var="params">&path_url=${path_url}&path_name=${path_name}</c:set>
                        </c:if>
                        <a onclick="return checkSearch(this);" href="/mmbase/vpro-wizards/system/unhighlight.jsp?object=${_relationnr}&sectienr=${sectionnr}" class="unhighlight">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/lightbulb_off_green.png" class="icon" border="0" alt="" title="Niet meer uitlichten"/>
                        </a>
                        <a href="${wizardfile}.jsp?nodenr=${_nodenr}${params}" class="edit">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit_green.png" class="icon" border="0" title="Aanpassen" >
                        </a>
                    </td>
                    <td>
                        <a href="${wizardfile}.jsp?nodenr=${_nodenr}${params}" class="edit" title="Aanpassen"><mm:field name="${type}.gui()"/></a>
                    </td>
                </tr>
            </mm:list>
        </mm:cloud>
    </tbody>
    </table>
</div>