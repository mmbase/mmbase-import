<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>

<%@ attribute name="relationrole" %>
<%@ attribute name="multipart" type="java.lang.Boolean" description="don't set this if you use a fielfield in the body. In that case it's the default"  %>
<%@ attribute name="sortfield" description="this is the field in the relation that a sorting value will be inserted in. (like posrel.pos, although for posrel this field is set by default)"  %>
<%@ attribute name="sortposition" description="[begin|end] where to insert the new relation in the sorted list of existing relations (between given source and new node). defaults to 'end'"  %>

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
<util:getParams var="params"/>

<c:set var="actiontype" scope="request" value="new"/>

<div class="addFields">
    <div id="addclosed" <c:if test="${param.create eq true}"> style="display:none;"</c:if>>
        <a href="javascript:hideshow('addclosed','addopen')" class="header">
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/fold_closed.png" class="icon fold" border="0"/>
            nieuw
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new.png" class="icon" border="0"/></a>
    </div>
    <div id="addopen" <c:if test="${param.create ne true}"> style="display:none;"</c:if>>
        <a href="javascript:hideshow('addopen','addclosed')" class="header">
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/fold_open.png" class="icon fold" border="0"/>
            nieuw
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/new.png" class="icon" border="0" />
        </a>

        <jsp:doBody var="body"/>


        <c:set var="enctype"><c:if test="${multipart == true || not empty requestScope.filefield}">enctype="multipart/form-data"</c:if></c:set>
        <c:remove var="requestScope.filefield" />
        <form action="${pageContext.request.contextPath}/wizard/post" method="post" id="formcontainer" ${enctype}>
            <related:createrelation referDestination="new" source="${param.nodenr}" role="${relationrole}" sortfield="${sortfield}" sortposition="${sortposition}"/>
            <util:flushname/>
            <c:if test="${not empty flushname}">
                <input type="hidden" name="flushname" value="${flushname}" />
            </c:if>
            <input type="hidden" name="actions[createNode][].id" value="new"/>
            <input type="hidden" name="actions[createNode][].nodeType" value="${nodetype}"/>
            <%--
            <jsp:doBody/>
            --%>
            <c:out value="${body}" escapeXml="false"/>
            <div class="formButtons">
                <input class="save" type="submit" value="bewaar" />
                <input class="cancel" type="reset" value="annuleer" />
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
    </div>
</div>