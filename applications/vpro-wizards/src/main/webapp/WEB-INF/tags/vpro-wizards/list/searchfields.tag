<%--
    This tag shows search input fields and a search button. it also alows you to set
    min and max age for objects to find.
    you can use the extrafields fragment attribute to add some custom fields 
    (outside the fields of the type you are searching for). For
    this you should use the list:field tag.
    These custom fields are not handled by default, but can be used together with the
    querysearchlist to create custom search constraints.
--%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ attribute name="fields" description="the fields you want to show search input elements for."  %>
<%@ attribute name="extrafields" fragment="true" %>
<%@ attribute name="defaultminage" description="Defaults to zero days."%>
<%@ attribute name="defaultmaxage" description="Defaults to seven days."  %>

<c:if test="${empty fields && empty extrafields}">
    <util:throw message="searchfiels tag: You have to set either the 'fields' or the 'extrafields' attributes (or both)."/>
</c:if>


<%-- default searchall checkbox laten zien --%>
<c:set var="searchfields" scope="request">${fields}</c:set>

<%--set the default minage and maxage--%>
<c:choose>
    <c:when test="${empty defaultminage}">
        <c:set var="defaultminage" scope="request" value="0"/>
    </c:when>
    <c:otherwise>
        <c:set var="defaultminage" scope="request" value="${defaultminage}"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${empty defaultmaxage}">
        <c:set var="defaultmaxage" scope="request" value="7"/>
    </c:when>
    <c:otherwise>
        <c:set var="defaultmaxage" scope="request" value="${defaultmaxage}"/>
    </c:otherwise>
</c:choose>


    <mm:cloud>
    <form id="searchfieldform">
        <%--add the extra fields if there are any--%>
        <jsp:invoke fragment="extrafields"/>
        
        <%--the input for the searchfields--%>
        <c:if test="${not empty fields}">
            <mm:fieldlist nodetype="${searchtype}" fields="${searchfields}">
                <div class="inputBlock">
                    <div class="fieldName"><mm:fieldinfo type="guiname" /></div>
                    <div class="fieldValue"><mm:fieldinfo type="searchinput" options="date" /></div>
                </div>
            </mm:fieldlist>
        </c:if>
        
        <%-- add the extra params--%>
        <c:forEach var="p" items="${___params}" >
            <input type="hidden" name="${p.key}" value="${p.value}" />
        </c:forEach>
        <input type="hidden" name="search" value="zoeken" />

        <%--min and max age--%>
        <div class="inputBlock">
            <div class="fieldName">Tussen</div>
            <div class="fieldValue">
                max. <select class="narrow" name="_maxage"> <list:optionlist age="${param._maxage}" defaultage="${defaultmaxage}"/></select>
                en min. <select class="narrow" name="_minage"> <list:optionlist age="${param._minage}" defaultage="${defaultminage}"/></select>
            </div>
        </div>

        <%-- the searchall radio input --%>
            <div class="inputBlock" id="searchallblock">
                <div class="fieldName">Zoek</div>
                <%--  we don't define the value of the searchall input thingies. becouse 'searchall' is a property of the searchlist, and this tag is not
                used inside the search list. It is awkward that this input field is here at all, but it must be. possible solution: extend this tag with a
                fragment attribute that can contain 'extra' input fields for the search form that has nothing to do with the 'core' search form.
                the value of this input field wil be set through javascript in the parentsearchlist tag.
                --%>
                <div class="fieldValue">
                    <input type="radio" onChange="document.getElementById('searchfieldform').submit()" id="searchalloff" class="radio" name="searchall" value="false">gekoppelde objecten van type '${typeguiname}'
                </div>
                <div class="fieldValue">
                    <input type="radio" onChange="document.getElementById('searchfieldform').submit()" id="searchallon" class="radio" name="searchall" value="true">alles
                </div>
            </div>

        <div class="formButtons">
            <input class="submit" type="submit" value="zoeken">
        </div>
    </form>
    </mm:cloud>
