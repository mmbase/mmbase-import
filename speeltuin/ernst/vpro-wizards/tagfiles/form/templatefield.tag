<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="container" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="guifield" required="true" %>
<%@ attribute name="defaultnode" %>
<%@ attribute name="relationtype" %>
<%@ attribute name="fieldname" required="true"%>
<%@ attribute name="emptyselection" type="java.lang.Boolean" %>

<c:if test="${empty relationtype}"><c:set var="relationtype" value="related"/></c:if>
<c:if test="${empty templateid}"><c:set var="templateid" scope="request" value="100"/></c:if>
<c:set var="templateid" scope="request" value="${templateid+1}"/>

<%--
  Deze tag maakt het mogelijk om een bepaalde template te selecteren.
  Welke templates gekozen kunnen worden wordt bepaald door het koppelen
  van deze templates aan een bepaald object. Het objectnr van dat object
  wordt meegegeven onder de naam 'container'.
--%>

<mm:cloud method="asis">
    <%-- Zet oude waarde als er al een selectie is gemaakt ooit. --%>
    <c:if test="${not empty nodenr}">
    <mm:node number="${nodenr}">
        <mm:relatednodes path="${type}" max="1">
            <mm:import id="oldoption"><mm:field name="number"/></mm:import>
            <input type="hidden" name="checkRelationActions[${templateid}].fields[olddestination]" value="<mm:write referid="oldoption"/>"/>
        </mm:relatednodes>
    </mm:node>
    </c:if>
    <%-- Zet defaultwaarde als er nog niets gezet is. --%>
    <mm:notpresent referid="oldoption">
        <c:if test="${not empty defaultnode}">
            <mm:import id="oldoption">${defaultnode}</mm:import>
        </c:if>
    </mm:notpresent>
    <div class="inputBlock">
        <div class="fieldName">${fieldname}</div>
        <div class="fieldValue">
        <mm:node number="${container}" notfound="skipbody">
            <mm:import id="nodefound"/>
            <c:choose>
            <c:when test="${not empty nodenr}">
                <input type="hidden" name="checkRelationActions[${templateid}].fields[relationtype]" value="${relationtype}"/>
                <input type="hidden" name="checkRelationActions[${templateid}].fields[source]" value="${nodenr}"/>
                <select onchange="disableRelated();" name="checkRelationActions[${templateid}].fields[newdestination]">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="createRelationActions[${templateid}].referSource" value="new"/>
                <input type="hidden" name="createRelationActions[${templateid}].role" value="${relationtype}" />
                <select onchange="disableRelated();" name="createRelationActions[${templateid}].destination" />
            </c:otherwise>
            </c:choose>

            <c:if test="${emptyselection}">
                <option value="">--Maak een keus--</option>
            </c:if>

            <mm:relatednodes path="${type}">
                <option value="<mm:field id="option" name="number"/>"<mm:present referid="oldoption"><mm:compare referid="oldoption" referid2="option">selected</mm:compare></mm:present>><mm:field name="${guifield}"/></option>
            </mm:relatednodes>
            </select>
        </mm:node>
        <mm:notpresent referid="nodefound">
            Er is geen 'container' beschikbaar met nr. ${container}
        </mm:notpresent>
    </div>
    </div>
</mm:cloud>