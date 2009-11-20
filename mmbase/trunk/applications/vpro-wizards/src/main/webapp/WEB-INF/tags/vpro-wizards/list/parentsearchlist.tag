<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>

<%@ attribute name="numberofitems" %>
<%@ attribute name="parentnodenr" %>
<%@ attribute name="searchdir" description="either 'source' or 'destination'. defaults to 'destination'" %>
<%@ attribute name="relationrole"%>
<%@ attribute name="showsearchall" description="show the control for toggeling searchall in the search fields form or not [true|false] default is true" %>
<%@ attribute name="searchall" description="should we only search for nodes related to the parent node, or should we search for all nodes [true|false]. default: false" %>
<%@ attribute name="orderby"%>
<%@ attribute name="direction"%>
<%@ attribute name="constraintfield" description="when there is a parent node, the fields must be preceded with the nodetypename and a dot. " %>
<%@ attribute name="constraintvalue" %>
<%@ attribute name="constraintoperator" %>


<c:if test="${empty searchdir and not empty requestScope.searchdir}"><c:set var="searchdir" value="${requestScope.searchdir}" /></c:if>
<c:if test="${empty searchdir and not empty param.searchdir}"><c:set var="searchdir" value="${param.searchdir}" /></c:if>
<c:if test="${empty searchdir}"><c:set var="searchdir" value="destination" /></c:if>
<c:set var="searchdir" value="${searchdir}" scope="request" />

<c:if test="${empty relationrole and not empty param.relationrole}"><c:set var="relationrole" value="${param.relationrole}" /></c:if>
<c:if test="${empty relationrole and not empty requestScope.relationrole}"><c:set var="relationrole" value="${requestScope.relationrole}" /></c:if>
<c:if test="${empty relationrole}"><c:set var="relationrole" value="related" /></c:if>
<c:set var="relationrole" value="${relationrole}" scope="request" />

<mm:cloud >

    <c:if test="${empty orderby}"><c:set var="orderby" value="number" /></c:if>
    <c:if test="${empty direction}"><c:set var="direction" value="down" /></c:if>

    <%--import the min and maxage from the parameters or defaults--%>
    <c:set var="_minage" >
        <c:choose>
            <c:when test="${not empty param._minage}">${param._minage}</c:when>
            <c:otherwise>${defaultminage}</c:otherwise>
        </c:choose>
    </c:set>
    <c:set var="_maxage" >
        <c:choose>
            <c:when test="${not empty param._maxage}">${param._maxage}</c:when>
            <c:otherwise>${defaultmaxage}</c:otherwise>
        </c:choose>
    </c:set>

    <%-- the maximun number of items to show--%>
    <c:choose>
        <c:when test="${empty numberofitems}">
            <c:set var="numberofitems" scope="request" value="20" />
        </c:when>
        <c:otherwise>
            <c:set var="numberofitems" scope="request" value="${numberofitems}" />
        </c:otherwise>
    </c:choose>

    <c:if test="${empty searchall}">
        <c:set var="searchall" value="false" />
    </c:if>

    <%--
        when there is a parentnodenr there must also be a relationrole.
        the parent nodetype must be derived, and all three values must be set
        into the request.
    --%>
    <c:if test="${not empty parentnodenr}">

        <%--derive the parent node type--%>
        <mm:cloud >
            <mm:node number="${parentnodenr}">
                <c:set var="parentnodetype" scope="request" ><mm:nodeinfo type="type"/></c:set>
            </mm:node>
        </mm:cloud>

        <%--set the other values into the request. for parentnodenumber: make shure we don't have an alias but a real node number--%>
        <c:set var="parentnodenr" scope="request" ><mm:node number="${parentnodenr}"><mm:field name="number" /></mm:node></c:set>
        <c:set var="relationrole" scope="request" value="${relationrole}" />
    </c:if>


    <%--
        when showsearchall is set to 'true' then see if there is a parameter for it first.
    --%>
    <c:if test="${not empty searchall}">
        <c:choose>
            <c:when test="${searchall != 'true' && searchall != 'false'}">
                <util:throw message="param 'searchall should have value: [true|false]"/>
            </c:when>
            <c:when test="${not empty param.searchall}">
                <c:set var="searchall" value="${param.searchall}" scope="request" />
            </c:when>
            <c:otherwise>
                <c:set var="searchall" value="${searchall}" scope="request" />
            </c:otherwise>
        </c:choose>
    </c:if>


    <%--
        the searchall block in the searchfields tag:
        when there is no parent: hide it.
        when there is a parent: give it the value of searchall
    --%>
    <c:choose>
        <c:when test="${empty parentnodenr || showsearchall == 'false'}">
            <script>
                var b = document.getElementById("searchallblock");
                if(b != null){
                    b.style.display='none';
                }
            </script>
        </c:when>
        <c:otherwise>
            <script language="javascript">
                searchall = '${requestScope.searchall}';
                if(searchall == 'true'){
                    document.getElementById("searchallon").checked = true;
                }else{
                    document.getElementById("searchalloff").checked=true;
                }
            </script>
        </c:otherwise>
    </c:choose>


    <%--
        when there is a parent node, the parentnodetype and the relationrole are added
        to the path, unless 'searchall' is on. then we show all the nodes of type 'searchtype'
    --%>
    <c:choose>
        <c:when test="${(not empty parentnodenr) && requestScope.searchall == 'false'}">
            <c:set var="_path" value="${parentnodetype},${relationrole},${searchtype}" />
            <c:set var="_nodes" value="${parentnodenr}"/>
        </c:when>
        <%--
            if the orderby value is a field of the relation step, but we have 'searchall', we have no relationstep in the query
            so we must check on and fix this.
        --%>
        <c:otherwise>
            <c:set var="_type" value="${searchtype}" />
            <c:if test="${fn:contains(orderby, '.')}">
                <c:set var="orderby" value="number" />
            </c:if>
        </c:otherwise>
    </c:choose>

    <div class="itemlist">
        <mm:log>searchall:${requestScope.searchall}, searchdir:${requestScope.searchdir} parentnodenr:${parentnodenr}, relation role:${relationrole} :: query: nodes:${_nodes}, path:${_path}, element:${searchtype}, type:${_type}</mm:log>
        <c:choose>
            <c:when test="${not empty _path}">
                <mm:listnodescontainer nodes="${_nodes}" path="${_path}" element="${searchtype}" searchdirs="${requestScope.searchdir}">
                    <%@include file="listbody.tagf"%>
                </mm:listnodescontainer>
            </c:when>
            <c:otherwise>
                <mm:listnodescontainer type="${_type}" >
                    <%@include file="listbody.tagf"%>
                </mm:listnodescontainer>
            </c:otherwise>
        </c:choose>
    </div>
</mm:cloud>