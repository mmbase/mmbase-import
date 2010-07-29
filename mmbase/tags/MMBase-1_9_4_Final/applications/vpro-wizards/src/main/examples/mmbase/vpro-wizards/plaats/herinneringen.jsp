<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<list:wizard  title="Plaats van Herinnering" >
    <c:choose>
        <c:when test="${param.status == 1}"><c:set var="status" value="niet afgemaakt" /></c:when>
        <c:when test="${param.status == 2}"><c:set var="status" value="afgemaakt, niet geactiveerd" /></c:when>
        <c:when test="${param.status == 3}"><c:set var="status" value="geactiveerd, niet door redactie gezien" /></c:when>
        <c:when test="${param.status == 4}"><c:set var="status" value="door de redactie gezien" /></c:when>
    </c:choose>

        <edit:path name="herinneringen: <em>${status}</em>" session="herinneringen.jsp?nodenr=${param.nodenr}&status=${param.status}"/>
        <edit:sessionpath/>

    <list:search nodetype="memoryresponse" wizardfile="herinnering" >
        <list:parentsearchlist parentnodenr="${param.nodenr}"
            relationrole="related"
            constraintfield="memoryresponse.status"
            constraintvalue="${param.status}"
            constraintoperator="="
            showsearchall="false"

            orderby="memoryresponse.number"
            direction="down">

            <list:searchrow  delete="false" fields="name,creationdate,title"   />
        </list:parentsearchlist>
    </list:search>
</list:wizard>

