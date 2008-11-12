<%@ taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ attribute name="relate" type="java.lang.Boolean" description="wether you can create relations (when there is a parent node). defalts to true." %>
<%@ attribute name="edit" type="java.lang.Boolean" description="wether you can edit the listed objects (when you are authorized and there is a wizardfile). defaults to true." %>
<%@ attribute name="delete" type="java.lang.Boolean" description="wether you can delete relations (unlink) objects (when there is a parent and you are authorized). Defaults to true."%>
<%@ attribute name="confirmdelete" type="java.lang.Boolean" description="wether you must confirm deleting nodes. default is true"%>
<%@ attribute name="harddelete" type="java.lang.Boolean" description="wether you can delete nodes in the list (when you are authorized). defautls to false."%>
<%@ attribute name="fields" required="true" description="the fields to show"%>
<%--
<%@ attribute name="parenttype" description="when this is not empty, the $nodenr (attribute on list:wizard or list:searchlist) is taken as parent for nodes in the list, if it is set, the first node of given type that is related to a node in the list is taken as parent, so it translates into :'has any connections to..'. Don't quite see the use of it..." %>
--%>

<%--
    the following attributes are fragments that are included in three places:
    1 in the search result table header (at the start)
    2 in the search result action button section. (at the end)
    3 in the search result table row (at the start)

    using these you can add columns to the result table with some specialized extra data.
    and you can add action buttons for specialized actions
    this way you can extend this tag without changing it.

    you can access the number of the node of the row through the 'nodenrrow' variable
    you can access th url to the editor for that row through the 'wizardurl' variable
--%>
<%@ attribute name="headerfragment" fragment="true" %>
<%@ attribute name="rowfragment" fragment="true" %>
<%@ attribute name="buttonfragment" fragment="true" %>

<%--  preconditions  --%>
<c:if test="${empty confirmdelete}"><c:set var="confirmdelete" value="true"/></c:if>
<c:if test="${empty relate}"><c:set var="relate" value="true"/></c:if>
<c:if test="${empty edit}"><c:set var="edit" value="true"/></c:if>
<c:if test="${empty delete}"><c:set var="delete" value="true"/></c:if>
<c:if test="${empty harddelete}"><c:set var="harddelete" value="false"/></c:if>

<c:if test="${(not empty headerfragment and empty rowfragment) || (empty headerfragment and not empty rowfragment)}">
    <util:throw message="list:searchrow tag: you can not set only a headerfragment or a row fragment. set both and make them matching."/>
</c:if>

<%--
    the mode must be set in the searchlist tag. modes are 'header' or 'row'
--%>
<c:if test="${empty mode}">
    <util:throw message="list:searchrow tag: can not use the searchrow tag without a searchlist tag"/>
</c:if>

<c:set var="searchdir" value="${requestScope.searchdir}" />
<c:if test="${empty searchdir}">
    <c:set var="searchdir" value="destination" />
</c:if>


<%--
<% try { %>
--%>
<mm:cloud method="asis">
    <%--table headers (only at first row--%>
    <c:if test="${mode == 'header'}">
        <thead>
            <tr>
                <mm:log>searchrow: Header mode</mm:log>
                    <td></td>
                    <%--TODO: this stuff should be taken away from this tag. it is too specific.
                    <c:if test="${not empty time}"><td>Datum</td></c:if>
                    <c:if test="${not empty status}"><td>Status</td></c:if>
                    --%>
                    <%--
                        here we invoke the fragment that lets you add columns to the header of the tabel
                        match it with the rowfragment
                    --%>
                    <jsp:invoke fragment="headerfragment"/>

                    <%--the searchfields--%>
                    <mm:fieldlist fields="${fields}" nodetype="${searchtype}">
                        <td><mm:fieldinfo type="guiname"/></td>
                    </mm:fieldlist>
            </tr>
        </thead>
    </c:if>

    <%--
        do a single row of the result
    --%>
    <c:if test="${mode == 'row'}">
        <mm:log>searchrow: row mode</mm:log>
        <c:if test="${not empty param.nodenr}"> <util:removeParam name="nodenr"/></c:if>
        <util:getParams var="extraparams"/>

        <c:choose>
            <c:when test="${index % 2 == 0}"><c:set var="rowClass">odd</c:set></c:when>
            <c:otherwise><c:set var="rowClass">even</c:set></c:otherwise>
        </c:choose>

        <%--  add the node number to the relevant templates in the flushname string--%>
        <c:if test="${not empty flushname}">
            <util:flushnamefilter nodenr="${nodenrrow}" flushname="${flushname}" type="${nodetype}"/>
            <c:set var="flushnameparam" value="flushname=${result}" scope="request"/>
            <c:set var="flushname" value="${result}" scope="request"/>
        </c:if>


        <tr class="${rowClass}">
            <mm:node number="${nodenrrow}" id="currentnode">

                <%--
                    do the buttons for the actions for this row.
                    actions:
                    -link/unlink
                    -delete
                    -edit
                --%>
                <td class="icons">

                    <%--delete: if you may--%>
                    <c:if test="${harddelete == true}">
                        <mm:node number="${nodenrrow}"><mm:maydelete><c:set var="maydelete" value="true"/></mm:maydelete></mm:node>
                        <c:if test="${not empty maydelete}">
                            <c:set var="url" >
                                    <mm:url page="/wizard/post">
                                        <mm:param name="actions[deleteNode][1].number" value="${nodenrrow}" />
                                        <c:if test="${not empty flushname}">
                                            <mm:param name="flushname" value="${flushname}" />
                                        </c:if>
                                </mm:url>
                            </c:set>
                            <a class="delete" href="${url}" onClick="return doConfirm(true, 'Weet je zeker dat je dit wilt verwijderen? Deze actie is niet ongedaan te maken.')">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/delete.png" class="icon" border="0" alt="" title="Verwijder">
                            </a>
                        </c:if>
                        <c:if test="${empty maydelete}">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/delete.png" class="icon" border="0" alt="" title="Verwijder"></a>
                        </c:if>
                    </c:if>
                    <c:remove var="maydelete"/>


                    <%--
                        link or unlink:
                            only when there is a parentnodenr
                        unlink:
                            when delete == true
                            the user is allowed to delete the relation
                            there is a relation between the current node and the parent node.
                        link:
                            relate == true
                            when  searchall == 'on' and
                            the current node has no relation to the parent node
                    --%>

                    <c:if test="${not empty parentnodenr}">
                        <%-- check if the current node is connected, and store the relation number --%>
                        <c:set var="_path" value="${parentnodetype},${relationrole},${searchtype}" />
                        <mm:log>test: path="${_path}" searchdir="${searchdir}"</mm:log>

                        <c:remove var="link"/>
                        <mm:list path="${_path}"
                                searchdir="${searchdir}"
                                constraints="${parentnodetype}.number=${parentnodenr} and ${searchtype}.number=${nodenrrow}"
                                fields="${relationrole}.number" >
                            <c:set var="link" scope="request"><mm:field name="${relationrole}.number" /></c:set>
                            <mm:log>node ${nodenrrow} has a relation to parent ${parentnodenr}</mm:log>
                        </mm:list>



                        <c:choose>
                            <c:when test="${not empty link}">
                                <mm:log>delete link. link: ${link}</mm:log>
                                <%--unlink the node if we can--%>
                                <mm:node number="${link}"><mm:maydelete><c:set var="maydelete" value="true"/></mm:maydelete></mm:node>
                                <c:if test="${delete == 'true'}">
                                    <c:if test="${not empty maydelete}">
                                        <c:set var="url" >
                                            <mm:url page="/wizard/post">
                                                <mm:param name="actions[deleteNode][1].number" value="${link}"/>
                                                <c:if test="${not empty flushname}">
                                                    <mm:param name="flushname" value="${flushname}" />
                                                </c:if>
                                            </mm:url>
                                        </c:set>
                                        <a class="delete" href="${url}" onclick="return doConfirm(${confirmdelete}, 'Weet je zeker dat je dit object wilt loskoppelen?')">
                                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/unlink.png" class="icon" border="0" alt="" title="Koppel los"/>
                                        </a>
                                    </c:if>
                                    <c:if test="${empty maydelete}">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/unlink_soft.png" class="icon" border="0" alt="" title="Koppel los"/>
                                    </c:if>
                                </c:if>
                                <c:remove var="maydelete"/>
                            </c:when>


                            <c:otherwise>
                                <%--link to the parent if we can--%>
                                <c:if test="${relate == 'true'}">
                                    <mm:log>create link</mm:log>
                                    <mm:node number="${parentnodenr}" id="parentnode"/>

                                    <c:remove var="maycreate" />
                                    <c:if test="${searchdir == 'destination'}">
                                        <mm:maycreaterelation role="${relationrole}" source="parentnode" destination="currentnode" >
                                            <c:set var="maycreate" value="true" />
                                        </mm:maycreaterelation>
                                    </c:if>
                                    <c:if test="${searchdir == 'source'}">
                                        <mm:maycreaterelation role="${relationrole}" source="currentnode" destination="parentnode" >
                                            <c:set var="maycreate" value="true" />
                                        </mm:maycreaterelation>
                                    </c:if>

                                    <c:if test="${not empty maycreate}">
                                        <c:set var="url" >
                                            <mm:url page="/wizard/post">
                                                <mm:param name="actions[createRelation][${nodenrrow}].sourceNodeNumber" value="${parentnodenr}" />
                                                <mm:param name="actions[createRelation][${nodenrrow}].destinationNodeNumber" value="${nodenrrow}" />
                                                
                                                <mm:param name="actions[createRelation][${nodenrrow}].role" value="${relationrole}" />
                                                <mm:param name="actions[createRelation][${nodenrrow}].sortPosition" value="end" />
                                                <c:if test="${not empty flushname}">
                                                    <mm:param name="flushname" value="${flushname}" />
                                                </c:if>

                                            </mm:url>
                                        </c:set>
                                        <a class="link" href="${url}">
                                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/link.png" class="icon" border="0" alt="" title="Koppel vast">
                                        </a>
                                    </c:if>
                                    <c:if test="${empty maycreate}">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/link_soft.png" class="icon" border="0" alt="" title="Koppel vast">
                                    </c:if>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <%--edit--%>
                    <c:if test="${not empty wizardfile && edit == true}">
                        <mm:maywrite>
                            <c:set var="maywrite" value="true" />
                            <c:set var="url" >
                                <mm:url page="${wizardfile}.jsp">
                                    <mm:param name="nodenr" value="${nodenrrow}" />
                                </mm:url>
                            </c:set>
                            <c:if test="${not empty flushnameparam}"><c:set var="flushnameparam" value="&${flushnameparam}"/></c:if>
                            <a class="edit" href="${url}&${extraparams}${flushnameparam}">
                                <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit.png" class="icon" border="0" alt="" title="Aanpassen"/>
                            </a>
                        </mm:maywrite>
                        <mm:maywrite inverse="true">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/edit_soft.png" class="icon" border="0" alt="" title="Aanpassen"/>
                        </mm:maywrite>
                     </c:if>

                     <%-- now do the fragment for adding button actions--%>
                     <jsp:invoke fragment="buttonfragment"/>
                </td>


                <%--**
                **  now show the fields
                ** --%>

                 <%--  now invoke the fragment for the table row --%>
                 <c:choose>
                     <c:when test="${not empty wizardfile && maywrite}">
                        <c:set var="wizardurl" value="${url}&${extraparams}${flushnameparam}" scope="request" />
                     </c:when>
                     <c:otherwise>
                        <c:remove var="wizardurl" />
                     </c:otherwise>
                 </c:choose>
                 <jsp:invoke fragment="rowfragment"/>

                <%--show the fields--%>
                <mm:fieldlist fields="${fields}">
                    <td>
                        <c:choose>
                            <c:when test="${not empty wizardfile && maywrite}">
                                <a class="edit" title="Aanpassen" href="${url}&${extraparams}${flushnameparam}">
                                    <span name="searchresults"><mm:fieldinfo type="guivalue" />&nbsp;</span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span name="searchresults"><mm:fieldinfo type="guivalue" />&nbsp;</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </mm:fieldlist>
            </mm:node>
        </tr>
        <c:if test="${not empty param.nodenr}"><util:addParam name="nodenr" value="${param.nodenr}"/></c:if>
    </c:if>

</mm:cloud>
<%--
<% } catch (Exception e) {out.println(e);} %>
--%>
