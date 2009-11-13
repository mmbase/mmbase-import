<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="title" description="the title of the page, also used by the related:view tag" %>
<%--
<%@ attribute name="name" description="i don't think this is used anymore" %>
--%>
<%@ attribute name="nodenr" description="this is the parent node for all the related stuff on the page." %>
<%--
<%@ attribute name="wizardfile" description="used only for the back button. is not put in the request. Should go, the url is also in the cookie, and that one includes the params." %>
--%>
<%@ attribute name="nodetype" required="true" description="used by the related:view and related:add tags. can be set on view but not on ad. " %>
<%@ attribute name="relationrole" description="the type of relation between ${nodenr} and the related nodes. is also used in the related:add and related:view tags" %>
<%@ attribute name="flushname" description="is handled by the server" %>
<%@ attribute name="showback" type="java.lang.Boolean" description="show the big arrow back? use this when you don't use the path tags to create return navigation"  %>
<%@ attribute name="noreferrer" type="java.lang.Boolean" description="by default a referer cookie is set to indicate the current page, and it is used by other pages to return to it. if you include an editor, you may not want to set this here"%>
<%@ attribute name="header" fragment="true" description="include some stuff in the html header element"  %>

<%-- add the parameters that must be passed on with every url in this wizard--%>
<util:addParam name="nodenr" parameter="nodenr"/>
<c:if test="${not empty param.parentnodenr}"><util:addParam name="parentnodenr" parameter="parentnodenr"/></c:if>
<c:if test="${not empty param.relationrole}"> <util:addParam name="relationrole" parameter="relationrole"/> </c:if>
<c:if test="${not empty param.searchdir}"><util:addParam name="searchdir" parameter="searchdir"/></c:if>
<c:if test="${not empty param.edit}"><util:addParam name="edit" parameter="edit"/></c:if>
<c:if test="${not empty param.create}"><util:addParam name="create" parameter="create"/></c:if>

<%--
<util:addParam name="editnodenr" parameter="editnodenr"/>
--%>

<mm:content type="text/html"  expires="0" encoding="utf-8" >
    <c:set var="relationrole" scope="request" value="${relationrole}" />

  <%-- set the current url in the referrer cookie--%>
    <c:set var="back" value="${cookie.referer.value}" scope="request"/>
    <c:if test="${empty noreferrer}">
        <util:setreferrer/>
    </c:if>

    <c:set var="nodetype" scope="request" value="${nodetype}"/>
    <%--
        the default modifier is create. this is save for field tags, cose if it is update, there muste
        be a currentnode.
    --%>
    <c:set var="modifier" scope="request" value="create" />
    <c:set var="title" scope="request">${title}</c:set>
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
    <c:if test="${empty showback}"><c:set var="showback" value="false" /></c:if>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
        <body class="related">
            <mm:cloud jspvar="cloud" rank="basic user" method="loginpage" loginpage="/mmbase/vpro-wizards/system/login.jsp" >
                <c:set var="requestcloud" scope="request" value="${cloud}"/>
                <util:header/>

                <c:if test="${showback}">
                    <div class="terug">
                        <a href="${back}">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/arrow_back.png" class="icon" border="0" alt="0"/>
                            terug
                        </a>
                    </div>
                </c:if>
    
                <%--do the body, and catch an exception if there is one--%>
                <div class="relatedview">
                    <jsp:doBody/>
                </div>
    
                <script type="text/javascript">
                    //dummy implementation of this function. it is used in the form page, so the
                    //field tags extept it.
                    function disableRelated(){ }
                </script>
                <!--[if lt IE 7]>
                    <script defer type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/pngfix.js"></script>
                <![endif]-->
            </mm:cloud>
        </body>
    </html>
</mm:content>