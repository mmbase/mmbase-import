<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>

<%@ attribute name="title" %>
<%@ attribute name="relationrole" %>
<%@ attribute name="nodetype" description="is used by the related:view tag "  %>
<%@ attribute name="edit" type="java.lang.Boolean" %>
<%@ attribute name="delete" type="java.lang.Boolean" %>
<%@ attribute name="confirmdelete" type="java.lang.Boolean" description="wether you must confirm deleting nodes. default is true"%>
<%@ attribute name="sortable" type="java.lang.Boolean" %>
<%@ attribute name="multipart" type="java.lang.Boolean"  description="don't set this if you use a fielfield in the body. In that case it's the default" %>

<%--for overriding the gui for this node--%>
<%@ attribute name="display" fragment="true" description="when this is set, the fragment is evaluated for each node. the fragment must render the gui for this node. use fields '_nodenr', '_relationnr' and 'nodetype'" %>
<%@ variable name-given="_nodenr" scope="AT_BEGIN" %>
<%@ variable name-given="_relationnr"  scope="AT_BEGIN"%>

<%-- this parameter is used to communicate the editor to open the records with (when edit is true). perhaps it should be a tag parameter--%>
<c:if test="${not empty param.openwizard}">
    <util:addParam name="openwizard" parameter="openwizard"/>
</c:if>

<util:getParams var="params"/>


<%-- Standaard moeten deze variablen aan staan. --%>
<c:if test="${empty confirmdelete}"><c:set var="confirmdelete" value="true"/></c:if>
<c:if test="${empty edit}"><c:set var="edit" value="true"/></c:if>
<c:if test="${empty delete}"><c:set var="delete" value="true"/></c:if>

<%-- default relatie insrel --%>
<c:if test="${empty relationrole}">
    <c:choose>
        <c:when test="${empty requestScope.relationRole}">
            <c:set var="relationrole">related</c:set>
        </c:when>
        <c:otherwise>
            <c:set var="relationrole" value="${requestScope.relationrole}"/>
        </c:otherwise>
    </c:choose>
</c:if>
<%-- Als er gesorteerd kan worden dan nemen we aan dat de relatie een posrel relatie is --%>
<c:if test="${sortable == true}"><c:set var="relationrole" value="posrel"/></c:if>
<c:if test="${relationrole == 'posrel'}"> <c:set var="sortable" value="true" /> </c:if>


<%-- Als het om een posrel relatie gaat sorteer deze dan ook goed --%>
<c:choose>
    <c:when test="${relationrole eq 'posrel'}"> <c:set var="orderby" value="posrel.pos"/> </c:when>
    <c:otherwise> <c:set var="orderby" value="${nodetype}.number"/> </c:otherwise>
</c:choose>

<div class="relatedItem">
    <%-- try {--%>
    <mm:cloud method="asis">
        <h5>
            <c:choose>
                <c:when test="${empty title}"><mm:nodeinfo nodetype="${nodetype}" type="guitype"/></c:when>
                <c:otherwise>${title}</c:otherwise>
            </c:choose>
        </h5>
        <mm:log>test related:view relationrole:${relationrole}, nodetype:${nodetype}, orderby: ${orderby}</mm:log>
        <c:if test="${not empty nodenr}">
            <mm:node number="${nodenr}">
                <mm:related  path="${relationrole},${nodetype}" orderby="${orderby}" fields="${orderby}">
                    <mm:first>
                        <ul>
                    </mm:first>
                    <c:set var="_relationnr"><mm:field name="${relationrole}.number" /></c:set>
                    <c:set var="_nodenr" ><mm:field name="${nodetype}.number"/></c:set>
                    <c:choose>
                        <%--  deze node moet je kunnen editen--%>
                        <c:when test="${param.editnodenr eq _nodenr}">
                            <c:set var="modifier" scope="request" value="update" />
                            <jsp:doBody var="body"/>
                            <li>
                                <c:set var="enctype"><c:if test="${multipart == true || not empty requestScope.filefield}">enctype="multipart/form-data"</c:if></c:set>
                                <c:remove var="requestScope.filefield" />
                                <form action="${pageContext.request.contextPath}/wizard/post" ${enctype} method="post" id="formcontainer">

                                <!-- http://jira.vpro.nl//browse/DRIE-1409  -->
                                <util:flushname/>
                                <c:if test="${not empty flushname}">
                                    <input type="hidden" name="flushname" value="${flushname}" />
                                </c:if>
                                <%--do we need this?
                                <input type="hidden" name="nodenr" value="${_nodenr}" />
                                --%>

                                <input type="hidden" name="actions[updateNode][${_nodenr}].nodeType" value="${nodetype}"/>
                                <input type="hidden" name="actions[updateNode][${_nodenr}].number" value="${_nodenr}"/>

                                <%--toon de velden die geedit kunnen worden--%>
                                <%--
                                <c:set var="modifier" scope="request" value="update" />
                                <jsp:doBody/>
                                --%>
                                <c:out value="${body}" escapeXml="false"/>

                                <c:set var="modifier" scope="request" value="create" />

                                <div class="formButtons">
                                    <input class="save" type="submit" value="bewaar" />
                                    <script type="text/javascript">
                                        function annuleer() {
                                            var url = document.location.href;
                                            startEditnodenrIndex = url.indexOf('&editnodenr');
                                            url = url.substring(0,startEditnodenrIndex);
                                            location = url;
                                        }
                                    </script>
                                    <input class="cancel" onclick="annuleer()" type="reset" value="annuleer" />
                                </div>
                                </form>
                                <%--
                                <c:if test="${not empty requestScope.filefield}">
                                    <script language="javascript">
                                    $(function(){
                                            var encType="multipart/form-data";
                                            if( $('form#formcontainer').attr('enctype') != encType){
                                                $('form#formcontainer').attr('enctype', encType);
                                            }
                                    });
                                    </script>
                                </c:if>
                                --%>
                            </li>
                        </c:when>

                        <%--deze node hoef je niet te kunnen editen--%>
                        <c:otherwise>
                            <li>
                                <div class="move">
                                    <c:if test="${sortable == true}">
                                        <c:set var="urlup">
                                            <mm:url page="/wizard/post">
                                                <mm:param name="actions[sortRelation][${_relationnr}].destinationNodeNumber" value="${_nodenr}" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].sourceNodeNumber" value="${nodenr}" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].direction" value="up" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].role" value="posrel" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].sortField" value="pos" />
                                                <mm:param name="flushname" value="${flushname}" />
                                            </mm:url>
                                        </c:set>
                                        <c:set var="urldown">
                                            <mm:url page="/wizard/post">
                                                <mm:param name="actions[sortRelation][${_relationnr}].destinationNodeNumber" value="${_nodenr}" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].sourceNodeNumber" value="${nodenr}" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].direction" value="down" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].role" value="posrel" />
                                                <mm:param name="actions[sortRelation][${_relationnr}].sortField" value="pos" />
                                                <mm:param name="flushname" value="${flushname}" />
                                            </mm:url>
                                        </c:set>
                                        <%--arrow down--%>
                                        <mm:last inverse="true">
                                            <a style="text-decoration:none" href="${urldown}" class="movedown" onclick="return checkSearch(this);">
                                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_down.png" class="icon" border="0" title="Sorteer"/>
                                            </a>
                                        </mm:last>

                                        <%--arrow up--%>
                                        <mm:first inverse="true">
                                            <a style="text-decoration:none" href="${urlup}" class="moveup" onclick="return checkSearch(this);">
                                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_up.png" class="icon" border="0" title="Sorteer"/>
                                            </a>
                                        </mm:first>
                                    </c:if>
                                </div>

                                <div class="icons">
                                    <%--delete button--%>
                                    <mm:node number="${_relationnr}"><mm:maydelete><c:set var="maydelete" value="true"/></mm:maydelete></mm:node>
                                    <c:if test="${delete && not empty maydelete}">
                                        <c:set var="url" >
                                            <mm:url page="/wizard/post">
                                                <mm:param name="actions[deleteNode][1].number" value="${_relationnr}"/>
                                                <mm:param name="flushname" value="${flushname}" />
                                            </mm:url>
                                        </c:set>
                                        <a href="${url}" class="delete" onclick="return doConfirm(${confirmdelete}, 'Weet je zeker dat je dit object wilt loskoppelen?')"><img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/unlink.png" class="icon" border="0" title="Koppel los"/></a>
                                    </c:if>
                                    <c:remove var="maydelete"/>
                                    <%--edit button--%>
                                    <%--
                                    <c:if test="${edit}">
                                        <a href="${pageContext.request.contextPath}${pageContext.request.servletPath}?${params}&editnodenr=${_nodenr}" class="edit"><img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" title="Aanpassen!!"/></a>
                                    </c:if>
                                    <c:if test="${not empty param.openwizard}">
                                        <a href="${param.openwizard}?nodenr=${_nodenr}&${params}" class="edit"><img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" title="Aanpassen!!"/></a>
                                    </c:if>
                                    --%>
                                    
                                    <%--edit button--%>
                                    <c:if test="${edit}">
                                        <c:choose>
                                            <c:when test="${not empty param.openwizard}">
                                                <a href="${param.openwizard}?nodenr=${_nodenr}&${params}" class="edit">
                                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" title="Aanpassen!!"/>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}${pageContext.request.servletPath}?${params}&editnodenr=${_nodenr}" class="edit">
                                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" title="Aanpassen!!"/>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    
                                </div>

                                <%--
                                <c:choose>
                                    <c:when test="${nodetype eq 'images'}">
                                        <util:image nodenr="${_nodenr}" urlvar="url"/>
                                        <util:image nodenr="${_nodenr}" template="+s(40)+part(0x0x40x40)+s(!40x!40)" urlvar="previewurl"/>
                                        <a target="image" href="${url}">
                                            <img src="${previewurl}" class="image" border="0"/>
                                        </a>
                                    </c:when>

                                    <c:when test="${nodetype eq 'attachments'}">
                                        <mm:node element="${nodetype}">
                                            <c:set var="number" ><mm:field name="number" /></c:set>
                                            <util:attachment nodenr="${number}" urlvar="url"/>
                                            <a href="${url}"><mm:field name="title" /></a>
                                        </mm:node>
                                    </c:when>

                                    <c:otherwise>
                                        <mm:node element="${nodetype}">
                                            <mm:field name="gui()"/>
                                        </mm:node>
                                    </c:otherwise>
                                </c:choose>
                                --%>

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
                                                            <mm:field name="gui()" escape="none" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <mm:field name="${field}" escape="none" />
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
                        </c:otherwise>
                    </c:choose>
                    <mm:last>
                        </ul>
                    </mm:last>
                </mm:related>
            </mm:node>
        </c:if>
    </mm:cloud>
    <%-- } catch (Exception e) { out.println("<div class=\"error\">Fout in view "+nodetype+". reden: "+e.getMessage()+"</div>"); }--%>
</div>