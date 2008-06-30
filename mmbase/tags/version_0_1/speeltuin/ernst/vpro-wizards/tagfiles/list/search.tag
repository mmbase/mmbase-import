<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="nodetype" description="the node type this search will show" %>
<%@ attribute name="wizardfile" description="the name of the wizard that is called to edit the node (minus .jsp). when empyt editing is disabled"  %>
<%@ attribute name="collapsed" type="java.lang.Boolean" description="wether the search element is collapsed to a single bar. Default is false"  %>

<c:if test="${empty nodetype}">
    <util:throw message="list:search tag: nodetype attribute is empty, and not set in parent tag either."/>
</c:if>

<c:set var="searchtype" scope="request">${nodetype}</c:set>
<mm:cloud >
    <c:set var="typeguiname" scope="request"><mm:nodeinfo nodetype="${nodetype}" type="guitype"/></c:set>
</mm:cloud>
<c:if test="${empty collapsed}">
    <mm:log>**** collapsed is empty</mm:log>
    <c:set var="collapsed" value="false" />
</c:if>
<mm:log>collapsed: ${collapsed}</mm:log>

<c:set var="wizardfile" scope="request">${wizardfile}</c:set>

    <%-- Zet alle parameters klaar die we nodig hebben om te kunnen zoeken. --%>
    <c:set var="searchurl" scope="request"><%
        String searchstring = "";
        java.util.Enumeration parameternames = request.getParameterNames();
        while (parameternames.hasMoreElements()) {
            String param = (String)parameternames.nextElement();
            if(param.startsWith("_") || param.equals("searchall")) {
                searchstring+="&"+param;
                searchstring+="="+request.getParameter(param);
            }
        }
    %><%=searchstring%></c:set>

    <%-- these are all the search fields --%>
    <c:set var="searchkeys" scope="request"><%
        String searchstring = "";
        java.util.Enumeration parameternames = request.getParameterNames();
        while (parameternames.hasMoreElements()) {
            String param = (String)parameternames.nextElement();
            if(param.startsWith("_") && !param.equals("_maxage") && !param.equals("_minage") && !request.getParameter(param).equals("")) {
                searchstring+=request.getParameter(param)+"|";
            }
        }
    %><%=searchstring%></c:set>

<div class="searchFields">
    <c:if test="${collapsed == false}"><c:set var="_style" >display:none</c:set></c:if>
    <div id="searchclosed" style="${_style}">
        <a href="javascript:hideshow('searchclosed','searchopen')" class="header">
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/fold_closed.png" class="icon fold" border="0"/>
            zoeken
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/search.png" class="icon" border="0" alt=""></a>
    </div>

    <c:remove var="_style"/>
    <c:if test="${collapsed == true}"><c:set var="_style" >display:none</c:set></c:if>
    <div id="searchopen" style="${_style}" >
        <a href="javascript:hideshow('searchopen','searchclosed')" class="header">
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/fold_open.png" class="icon fold" border="0"/>
            zoeken
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/search.png" class="icon" border="0"/>
        </a>
        <jsp:doBody/>
    </div>
</div>

    <%--
    <script type="text/javascript">
        showSearchResults(${searchkeys});
    </script>
    --%>