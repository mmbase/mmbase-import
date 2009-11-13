<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="title" description="is the page title" %>
<%@ attribute name="name" description="i don't think this is used. what's more: it interferes with the name field of the form:related tag if you don't specifically set the name on that one." %>
<%@ attribute name="wizardfile" description="is being used by the form:view tag to create the url to the related pages." %>
<%@ attribute name="nodenr" description="sets it in the request, and also calls javascript (disablerelations) when it is not empty. This is specific for the form, and dous not work if the form is not there (javascript error)." %>
<%@ attribute name="flushname" description="handeled by the server"%>
<%@ attribute name="noreferrer" type="java.lang.Boolean" description="by default a referer cookie is set to indicate the current page, and it is used by other pages to return to it. if you include an editor, you may not want to set this here"%>
<%@ attribute name="header" fragment="true" description="include some stuff in the html header element"  %>

<%--
    Deze tag vormt de root van het hoofdformulier. De huidige pagina url wordt in een cookie gezet.
    De flush parameters worden in de request gezet
    De css en javascript wordt geinclude


--%>

<%-- set the current url in the referrer cookie--%>
<c:if test="${empty noreferrer}">
    <util:setreferrer/>
</c:if>

<c:set var="name" scope="request">${name}</c:set>
<c:set var="wizardfile" scope="request">${wizardfile}</c:set>
<c:set var="nodenr" scope="request">
    <c:choose>
        <c:when test="${not empty param.nodenr}">${param.nodenr}</c:when>
        <c:otherwise>${nodenr}</c:otherwise>
    </c:choose>
</c:set>

<%--if flushname is not set, perhaps there is a parameter--%>
<util:flushname  value="${flushname}"/>

<c:if test="${not empty param.path_url}">
    <c:set var="params" scope="request">&path_url=${param.path_url}&path_name=${param.path_name}</c:set>
</c:if>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<mm:cloud jspvar="cloud" rank="basic user" method="loginpage" loginpage="/mmbase/vpro-wizards/system/login.jsp" >
    <c:set var="requestcloud" scope="request" value="${cloud}"/>
    <mm:content expires="0" type="text/html" encoding="utf-8" >
        <html>
            <head>
                <title>${title}</title>
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/mmbase/vpro-wizards/stylesheets/edit.css"/>
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/calendar/calendar.css">
                 <script>
                    var contextPath='${pageContext.request.contextPath}';
                </script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/javascript.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/calendar/calendar.js" ></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/calendar/clock.js" ></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/fckeditor/fckeditor.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/jquery/jquery.js"></script>
                <jsp:invoke fragment="header"/>
              </head>
            <body>

                <util:header/>

                <%-- these twoo are used to position over and mask --%>
                <div id="disableRelated" onclick="alert('Bewaar of Annuleer de aanpassingen eerst.');"></div>
                <div id="enableButtons" onclick="alert('Informatie is niet aangepast.');"></div>

                <jsp:doBody/>

                <c:if test="${empty nodenr}">
                    <script type="text/javascript">
                        disableRelated();
                    </script>
                </c:if>
                 <!--[if lt IE 7]>
                    <script defer type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/pngfix.js"></script>
                <![endif]-->
            </body>
        </html>
    </mm:content>
</mm:cloud>