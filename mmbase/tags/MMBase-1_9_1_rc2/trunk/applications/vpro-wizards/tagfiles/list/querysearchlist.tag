<%--
    This is a search list based on an external query container.
    It has to be an ListNodesContainer. You can write the reference to the container to the request like:

    <mm:write write="false" request="[listnodescontainerid param]" referid="[actual listnodescontainer id]"/>
--%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ attribute name="listnodescontainerid" %>
<%@ attribute name="numberofitems" description="this could be set in the listnodescontainer that drives this list, but this attribute has a defautl value (of 20)." %>
<%@ attribute name="parentnodenr" %>
<%@ attribute name="searchdir" description="either 'source' or 'destination'. defaults to 'destination'" %>
<%@ attribute name="relationrole"%>

<c:if test="${empty searchdir and not empty requestScope.searchdir}"><c:set var="searchdir" value="${requestScope.searchdir}" /></c:if>
<c:if test="${empty searchdir and not empty param.searchdir}"><c:set var="searchdir" value="${param.searchdir}" /></c:if>
<c:if test="${empty searchdir}"><c:set var="searchdir" value="destination" /></c:if>
<c:set var="searchdir" value="${searchdir}" scope="request" />

<c:if test="${empty relationrole and not empty param.relationrole}"><c:set var="relationrole" value="${param.relationrole}" /></c:if>
<c:if test="${empty relationrole and not empty requestScope.relationrole}"><c:set var="relationrole" value="${requestScope.relationrole}" /></c:if>
<c:if test="${empty relationrole}"><c:set var="relationrole" value="related" /></c:if>
<c:set var="relationrole" value="${relationrole}" scope="request" />

    <%--
        when there is a parentnodenr there must also be a relationrole.
        the parent nodetype must be derived, and all three values must be set
        into the request.
    --%>
    <c:if test="${not empty parentnodenr}">
        <%--check on parent and relation role--%>
        <c:if test="${empty relationrole}">
            <c:set var="relationrole" value="related" />
        </c:if>

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

<script language="javascript">
    document.getElementById('searchallblock').style.display='none';
</script>

<%-- the maximun number of items to show--%>
<c:if test="${empty numberofitems}"> <c:set var="numberofitems"  value="20" /></c:if>

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

<mm:cloud >
    <mm:import from="request" externid="${listnodescontainerid}" id="hola"/>
    <div class="itemlist">
        <mm:listnodescontainer referid="hola" >

            <mm:ageconstraint minage="${_minage}" maxage="${_maxage}"/>
            <mm:size id="maxsize" write="false"/>
            <mm:maxnumber value="${numberofitems}"/>
            <mm:offset value="${param.offset}" />

            <c:if test="${not empty searchfields}">
                <mm:fieldlist nodetype="${searchtype}" fields="${searchfields}">
                    <mm:fieldinfo type="usesearchinput" />
                </mm:fieldlist>
            </c:if>

            <%@ include file="paging.tagf" %>
                <table cellspacing="0" cellpadding="0" border="0" class="list">
                    <!--make the searchrow tag create the header of the result table-->
                    <c:set var="mode" value="header" scope="request" />
                    <jsp:doBody />

                    <!--now do the table body-->
                    <c:set var="mode" value="row" scope="request"/>
                    <tbody>
                        <mm:listnodes>
                            <c:set var="nodenrrow" scope="request"><mm:field name="number"/></c:set>
                            <mm:log>** in de lijst: ${nodenrrow}</mm:log>
                            <c:set var="index" scope="request"><mm:index/></c:set>
                            <mm:last inverse="true"><c:set var="last" scope="request" value="false" /></mm:last>
                            <mm:last><c:set var="last" scope="request" value="true" /></mm:last>
                            <jsp:doBody />
                        </mm:listnodes>
                    </tbody>
                </table>
                <c:set var="skippaginginfo" scope="request">true</c:set>

                <%@ include file="paging.tagf" %>

                <mm:size>
                    <mm:compare value="0">
                        <p>Er zijn geen zoekresultaten gevonden.</p>
                    </mm:compare>
                </mm:size>
        </mm:listnodescontainer>
    </div>
</mm:cloud>