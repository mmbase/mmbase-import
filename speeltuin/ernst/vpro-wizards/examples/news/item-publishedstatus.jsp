<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<mm:import externid="nodenr" required="true" />
<mm:import externid="detail">true</mm:import>

<c:if test="${empty requestScope.now}">
    <mm:import id="now" vartype="Long" ><mm:time time="now" /></mm:import>
    <c:set var="now" scope="request" value="${now}"/>
</c:if>

<mm:node number="${param.nodenr}">
    <mm:nodeinfo type="type">
        <mm:compare value="news">
            <mm:import id="online"><mm:field name="date">gepubliceerd op<mm:time format="dd MMMM yyyy"/> om <mm:time format="H:mm"/>.</mm:field></mm:import>
        
            <%--default--%>
            <c:set var="icon" value="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/star_empty.png" />
            <c:set var="pr" value="Wordt" />
            
            <%--override default when article is published--%>
            <mm:import id="ptime" reset="true" vartype="Long"><mm:field name="date" /></mm:import>
            <c:if test="${ptime lt now}">
                <c:set var="pr" value="Is" />
                <c:set var="icon" value="${pageContext.request.contextPath}/mmbase/vpro-wizards//system/img/star_yellow.png" />
            </c:if>
            <img src="${icon}" title="${pr} ${online}"/><c:if test="${detail == 'true'}">&nbsp;<mm:nodeinfo type="gui"/></c:if>
        </mm:compare>
        <mm:compare value="news" inverse="true">
            <p>Error! node type is niet 'news' maar: ${_}</p>
        </mm:compare>
     </mm:nodeinfo>
</mm:node>
