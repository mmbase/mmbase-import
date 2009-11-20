<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>
<%@ tag body-content="empty"  %>

<%@ attribute name="nodetype" required="true"%>
<%@ attribute name="field" description="what field to show. default is the gui function of the node. You can also use a fragment in the body to build the output for each node" %>
<%@ attribute name="name"%>
<%@ attribute name="relationrole"%>
<%@ attribute name="constraints"%>
<%@ attribute name="searchdir" description="this is the mmbase searchdir, the direction of the relation. default is 'destination'" %>

<%@ attribute name="edit" type="java.lang.Boolean"%>
<%@ attribute name="delete" type="java.lang.Boolean" description="Make it possible to delete the relation node" %>
<%@ attribute name="harddelete" type="java.lang.Boolean" description="Make it possible to directly delete the related node" %>
<%@ attribute name="confirmdelete" type="java.lang.Boolean" description="wether the user must confirm the deletion of the relation node. defaults to true" %>
<%@ attribute name="search" type="java.lang.Boolean"%>
<%@ attribute name="create" type="java.lang.Boolean"%>
<%@ attribute name="sortable" type="java.lang.Boolean" description="When this is true arrows are shown to push the nodes up and down in the list." %>
<%@ attribute name="sortfield" description="when you want to sort on non-posrel relations. it defaults to 'pos'"%>
<%@ attribute name="newwizard" description="create a new object with a given wizard. If you dont give this a wizard name is created (assumed)."  %>
<%@ attribute name="openwizard" %>
<%@ attribute name="relatedpage" description="this value will override the generated url to the 'related' page. normally it is ${wizardfile}_$nodetype}. this forces you to put the reled page in the current dir, what prevents reuse."  %>

<%--for overriding the gui for this node--%>
<%@ attribute name="display" fragment="true" description="when this is set, the fragment is evaluated for each node. the fragment must render the gui for this node. use fields '_nodenr', '_relationnr' and 'nodetype'" %>
<%@ variable name-given="_nodenr" scope="AT_BEGIN" %>
<%@ variable name-given="_relationnr"  scope="AT_BEGIN"%>


<%--
    show a summery of the relations of a certain type in the 'form' page. You can click on them to
    open the 'related' page
--%>

<c:if test="${empty relatedpage}">
    <c:set var="relatedpage" value="${wizardfile}_${nodetype}"/>
</c:if>

<c:if test="${empty searchdir}">
    <c:set var="searchdir" value="destination" />
</c:if>

<c:set var="anchor" value="__view_${nodetype}" />
<%-- Standaard moeten deze variablen aan staan. --%>
<c:if test="${empty edit}"> <c:set var="edit" value="true" /> </c:if>
<c:if test="${empty delete}"> <c:set var="delete" value="true" /> </c:if>
<c:if test="${empty search}"> <c:set var="search" value="true" /> </c:if>
<c:if test="${empty create}"> <c:set var="create" value="true" /> </c:if>
<c:if test="${empty confirmdelete}"><c:set var="confirmdelete" value="true"/></c:if>



<%-- Als er gesorteerd kan worden dan nemen we aan dat de relatie een posrel relatie is --%>
<c:if test="${not empty sortfield}">
    <c:set var="sortable" value="true" />
</c:if>




<%--when only sortable is set, we assume a posrel relation (unless explicitely overruled)--%>
<c:if test="${sortable == true}">
    <c:if test="${empty sortfield}">
        <c:set var="sortfield" value="pos" />
    </c:if>
    <c:if test="${empty relationrole}">
        <c:set var="relationrole" value="posrel" />
    </c:if>
</c:if>


<%-- default relation: insrel, no sort --%>
<c:if test="${empty relationrole}">
    <c:set var="relationrole" value="related" />
    <c:set var="sortable" value="false" />
    <c:remove var="sortfield" />
</c:if>

<%-- set the orderby properly --%>
<c:choose>
    <c:when test="${sortable ==  true}">
        <c:set var="orderby" value="${relationrole}.${sortfield}" />
    </c:when>
    <c:otherwise>
        <c:set var="orderby" value="${nodetype}.number" />
    </c:otherwise>
</c:choose>

<a name="${anchor}"></a>
<div class="relatedItem">
    <%
    try {
    %>
    <mm:cloud method="asis">
        <%--display the container name--%>
        <h5>
            <c:choose>
                <c:when test="${empty name}">
                    <mm:nodeinfo nodetype="${nodetype}" type="guitype" />
                </c:when>
                <c:otherwise><c:out value="${name}" escapeXml="false"/> </c:otherwise>
            </c:choose>
        </h5>

        <%--show the top buttons--%>
        <util:getParams var="extra_params"/>
        <div class="addItem">
            <c:if test="${create}">
                <c:choose>
                    <c:when test="${not empty newwizard}">
                        <c:set var="newwizardparam" ><c:if test="${not empty relationrole}">&relationrole=${relationrole}</c:if></c:set>
                        <a href="${newwizard}?parentnodenr=${nodenr}${newwizardparam}&${extra_params}" class="addButton">nieuw
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new.png" class="icon" border="0" title="Nieuw">
                        </a>
                    </c:when>
                <c:otherwise>
                    <a href="${relatedpage}.jsp?nodenr=${nodenr}&amp;create=true&relationrole=${relationrole}&searchdir=${searchdir}&${extra_params}"
                    class="addButton">nieuw <img
                        src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new2.png"
                        class="icon" border="0" alt=""></a>
                </c:otherwise>
                </c:choose>
            </c:if>

            <c:if test="${search}">
                <%--the edit attribute has a different function when there is an openwizard attribute.
                - when there is no openwizard attribute the list page shows a simple editor for the node
                - when there is an openwizard attribute, the list page shows edit icons with links to the
                    openwizard.
                --%>
                <c:if test="${edit == true && empty openwizard}"><c:set var="editparam" value="&edit=true"/></c:if>
                <c:if test="${not empty openwizard}"><c:set var="editparam" value="&openwizard=${openwizard}"/></c:if>
                <a href="${relatedpage}.jsp?nodenr=${nodenr}&search=true${editparam}&relationrole=${relationrole}&searchdir=${searchdir}&${extra_params}" class="searchButton">zoeken
                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new2.png" class="icon" border="0" alt="">
                </a>
            </c:if>
        </div>

        <c:if test="${not empty nodenr}">
            <mm:node number="${nodenr}">
                <%--path: ${relationrole},${nodetype}--%>
                <mm:related path="${relationrole},${nodetype}" orderby="${orderby}" fields="${orderby}" constraints="${constraints}">
                    <c:set var="_nodenr" ><mm:field name="${nodetype}.number"/></c:set>
                    <c:set var="_relationnr" ><mm:field name="${relationrole}.number"/></c:set>
                    <mm:first>
                        <ul>
                    </mm:first>
                    <li <mm:last>class="last"</mm:last>>

                        <%-- show the sorting arrows --%>
                        <div class="move">
                            <c:if test="${sortable == true}">
                                <%--find the sort value--%>
                                <c:remove var="_pos" />
                                <c:choose>
                                    <c:when test="${relationrole eq 'posrel'}">
                                        <c:set var="_pos" ><mm:node number="${_relationnr}"><mm:field name="pos" /></mm:node></c:set>
                                    </c:when>
                                    <c:when test="${not empty sortfield}">
                                        <c:set var="_pos" ><mm:node number="${_relationnr}"><mm:field name="${sortfield}" /></mm:node></c:set>
                                    </c:when>
                                </c:choose>
                                
                                <c:set var="urlup">
                                    <mm:url page="/wizard/post">
                                        <mm:param name="actions[sortRelation][1].sourceNodeNumber" value="${nodenr}" />
                                        <mm:param name="actions[sortRelation][1].destinationNodeNumber" value="${_nodenr}" />
                                        <mm:param name="actions[sortRelation][1].direction" value="up" />
                                        <mm:param name="actions[sortRelation][1].role" value="${relationrole}" />
                                        <mm:param name="actions[sortRelation][1].sortField" value="${sortfield}" />
                                        <mm:param name="flushname" value="${flushname}" />
                                    </mm:url>
                                </c:set>
                                <c:set var="urldown">
                                    <mm:url page="/wizard/post">
                                        <mm:param name="actions[sortRelation][1].sourceNodeNumber" value="${nodenr}" />
                                        <mm:param name="actions[sortRelation][1].destinationNodeNumber" value="${_nodenr}" />
                                        <mm:param name="actions[sortRelation][1].direction" value="down" />
                                        <mm:param name="actions[sortRelation][1].role" value="${relationrole}" />
                                        <mm:param name="actions[sortRelation][1].sortField" value="${sortfield}" />
                                        <mm:param name="flushname" value="${flushname}" />
                                    </mm:url>
                                </c:set>
                                
                                <mm:last inverse="true">
                                    <a style="text-decoration:none" href="${urldown}" class="movedown" onclick="return checkSearch(this);">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_down.png" class="icon" border="0" />
                                    </a>
                                </mm:last>
                                
                               <mm:last>
                                   <%--placeholder to align te icons--%>
                                   <span style="float:left; width: 16px;">&nbsp;</span>
                                </mm:last>
                               
                                <c:if test="${not empty _pos}">
                                    <span class="position" style="float:left"><c:if test="${_pos lt 10}">0</c:if>${_pos}</span>
                                </c:if>
                                
                                <mm:first>
                                    <%--placeholder to align te icons--%>
                                   <span style="float:left; width: 18px;">&nbsp;</span>
                                </mm:first>
                                <mm:first inverse="true">
                                    <a style="text-decoration:none" href="${urlup}" class="moveup" onclick="return checkSearch(this);">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_up.png" class="icon" border="0" />
                                    </a>
                                </mm:first>
                                
                               

                            </c:if>
                        </div>

                        <%-- buttons--%>
                        <div class="icons">
                            <c:remove var="maydelete" />
                            <mm:node number="${_relationnr}"> <mm:maydelete><c:set var="maydelete" value="true"/></mm:maydelete> </mm:node>
                                
                            <c:remove var="mayharddelete" />
                            <mm:node number="${_nodenr}"> <mm:maydelete><c:set var="mayharddelete" value="true"/></mm:maydelete> </mm:node>

                            <c:if test="${delete && not empty maydelete}">
                                <mm:link page="/wizard/post">
                                    <mm:param name="actions[deleteNode][1].number" value="${_relationnr}" />
                                    <mm:param name="flushname" value="${flushname}" />
                                    <a href="${_}" class="delete"  onclick="return doConfirm(${confirmdelete}, 'Weet je zeker dat je dit object wilt loskoppelen?')">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/unlink.png" class="icon" border="0" alt="" title="Koppel los"/>
                                    </a>
                                </mm:link>
                            </c:if>
                            
                             <c:if test="${harddelete && not empty mayharddelete}">
                                <mm:link page="/wizard/post">
                                    <mm:param name="actions[deleteNode][1].number" value="${_nodenr}" />
                                    <mm:param name="flushname" value="${flushname}" />
                                    <a href="${_}" class="delete"  onclick="return doConfirm(${confirmdelete}, 'Weet je zeker dat je dit object wilt verwijderen? (kan niet hersteld worden)')">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/delete.png" class="icon" border="0" alt="" title="Verwijder"/>
                                    </a>
                                </mm:link>
                            </c:if>
                            <c:remove var="maydelete"/>

                            <mm:node element="${nodetype}" id="currentnode">
                                <mm:maywrite>
                                    <c:choose>
                                        <c:when test="${not empty openwizard}">
                                            <a href="${openwizard}?nodenr=${_nodenr}&parentnodenr=${param.nodenr}&relationrole=${relationrole}&searchdir=${searchdir}${extra_params}">
                                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" alt="" title="Aanpassen"/>
                                            </a>
                                        </c:when>
                                            <c:when test="${edit}">
                                                <c:remove var="editparam" />
                                                <c:if test="${empty openwizard}"><c:set var="editparam" value="&edit=true" /></c:if>
                                                <a href="${relatedpage}.jsp?nodenr=${nodenr}${editparam}&parentnodenr=${param.nodenr}&relationrole=${relationrole}&editnodenr=${currentnode.number}&searchdir=${searchdir}&${extra_params}" class="edit">
                                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" alt="" title="Aanpassen"/>
                                                </a>
                                            </c:when>
                                        <c:otherwise>
                                        </c:otherwise>
                                    </c:choose>
                                </mm:maywrite>
                            </mm:node>

                        </div>

                        <%--
                            show the gui value for this node.
                            when the 'display' fragment is set, use that
                        --%>
                        <c:choose>
                            <c:when test="${empty display}">
                                
                                <c:choose>
                                    <c:when test="${nodetype eq 'images'}">
                                        <util:image urlvar = "url" nodenr="${_nodenr}"/>
                                        <util:image urlvar = "previewurl" nodenr="${_nodenr}" template="+s(40)+part(0x0x40x40)+s(!40x!40)"/>
                                        <a target="image" href="${url}">
                                            <img alt="gerelateerde afbeelding" src="${previewurl}" class="image" border="0" />
                                        </a>
                                    </c:when>

                                    <c:when test="${nodetype eq 'audiosources' || nodetype eq 'videosources'}">

                                        <mm:node element="${nodetype}">
                                            <mm:field name="format" id="format">
                                                <mm:compare referid="format" value="12"><a href="<mm:node number="${nodenr}"><mm:functioncontainer><mm:param name="format">asf</mm:param><mm:function name="url" /></mm:functioncontainer></mm:node>"><mm:field name="gui()" /></a></mm:compare>
                                                <mm:compare referid="format" value="6"><a href="<mm:node number="${nodenr}"><mm:functioncontainer><mm:param name="format">rm</mm:param><mm:function name="url" /></mm:functioncontainer></mm:node>"><mm:field name="gui()" /></a></mm:compare>
                                                <mm:compare referid="format" value="61"><a href="<mm:node number="${nodenr}"><mm:functioncontainer><mm:param name="format">m4v</mm:param><mm:function name="url" /></mm:functioncontainer></mm:node>"><mm:field name="gui()" /></a></mm:compare>
                                                <mm:compare referid="format" valueset="6,12,61" inverse="true"><a href="<mm:node number="${nodenr}"><mm:functioncontainer><mm:function name="url" /></mm:functioncontainer></mm:node>"><mm:field name="gui()" /></a></mm:compare>
                                            </mm:field>

                                        </mm:node>
                                    </c:when>
                                    <c:otherwise>
                                        <mm:node element="${nodetype}">
                                            <c:choose>
                                                <c:when test="${empty field}">
                                                    <mm:field name="gui()" escape="none"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <mm:field name="${field}" escape="none"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </mm:node>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                        <c:otherwise>
                            <jsp:invoke fragment="display"/>
                        </c:otherwise>
                    </c:choose>
                    </li>
                    <mm:last>
                        </ul>
                    </mm:last>
                </mm:related>
            </mm:node>
        </c:if>
    </mm:cloud>
    <%
            } catch (Exception e) {
                out.println("<div class=\"error\">Fout in view " + nodetype + ". Reden: "+e.getMessage()+"</div>");
        }
    %>
</div>
