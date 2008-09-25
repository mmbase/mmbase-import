<%@include file="taglib.tagf" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="titleMode" required="false" %>
<%@ attribute name="ajax" required="false" %>
<head>
<title><c:choose>
	<c:when test="${not empty titleMode}"><c:out value="${title}" /></c:when>
	<c:otherwise><fmt:message key="${title}" /></c:otherwise>
</c:choose></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
<!--[if IE]>
<style type="text/css" xml:space="preserve">
   body { behavior: url(<cmsc:staticurl page='/editors/css/hover.htc)'/>;}
</style>
<![endif]-->
<script src="<cmsc:staticurl page='/editors/utils/rowhover.js'/>" type="text/javascript"></script>
<script src="<cmsc:staticurl page='/js/window.js'/>" type="text/javascript"></script>
<script src="<cmsc:staticurl page='/js/transparent_png.js'/>" type="text/javascript"></script>
<c:if test="${not empty ajax and ajax eq 'true'}">
	<script type="text/javascript" src="<cmsc:staticurl page='/js/prototype.js' />"></script>
	<script type="text/javascript" src="<cmsc:staticurl page='/js/scriptaculous/scriptaculous.js' />"></script>
</c:if>
<jsp:doBody />
</head>