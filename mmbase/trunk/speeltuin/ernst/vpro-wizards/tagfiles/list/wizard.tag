<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="title" description="the title of the page" %>
<%@ attribute name="flushname" %>
<%@ attribute name="noreferrer" type="java.lang.Boolean" description="by default a referer cookie is set to indicate the current page, and it is used by other pages to return to it. if you include an editor, you may not want to set this here"%>
<%@ attribute name="header" fragment="true" description="include some stuff in the html header element"  %>

<%-- add the parameters that must be passed on with every url in this wizard--%>
<%--
--%>
<c:if test="${not empty param.nodenr}">
    <util:addParam name="nodenr" parameter="nodenr"/>
    <c:if test="${not empty param.relationrole}"><util:addParam name="relationrole" parameter="relationrole"/></c:if>
</c:if>



<%-- set the current url in the referrer cookie--%>
<c:if test="${empty noreferrer}">
    <util:setreferrer/>
</c:if>

<%--if flushname is not set, perhaps there is a parameter--%>
<util:flushname  value="${flushname}"/>

<%--TODO: depricated. is replaced by util:...params tags.--%>
<c:if test="${not empty param.path_url}">
    <c:set var="params" scope="request">&path_url=${param.path_url}&path_name=${param.path_name}</c:set>
</c:if>

<mm:content expires="0" type="text/html"  encoding="utf-8">
    <mm:cloud jspvar="cloud" rank="basic user" method="loginpage" loginpage="/mmbase/vpro-wizards/system/login.jsp" >
    <c:set var="requestcloud" scope="request" value="${cloud}"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
        <html>
        <head>
            <title>${title}</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/mmbase/vpro-wizards/stylesheets/edit.css"/>
             <script>
                var contextPath='${pageContext.request.contextPath}';
            </script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/javascript.js"></script>
            <script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/jquery/jquery.js"></script>
            <jsp:invoke fragment="header"/>
        </head>
            <body>
                <util:header/>
                <jsp:doBody/>
                <!--[if lt IE 7]>
                    <script defer type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/pngfix.js"></script>
                <![endif]-->
            </body>
        </html>
    </mm:cloud>
</mm:content>