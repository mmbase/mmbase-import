<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" 
%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@include file="globals.jsp" %>

<html>
	<head>
		<title><fmt:message key="site.title" /></title>
		<script type="text/javascript" src="portalcontrols.js.jsp"></script>
      <script type="text/javascript">
    function preview() {
        var contentLocation = this.frames["pcontent"].location.href;
        if (contentLocation.indexOf("mode=preview") > -1) {
            setPortletMode(false, false, false);
            setElementStyleByClassName('portlet-mode-spacer', 'display', 'none');
            setElementStyleByClassName('portlet-header-canvas', 'display', 'none');
            setElementStyleByClassName('portlet-canvas', 'borderWidth', '0px');
            setElementStyleByClassName('portlet-mode-canvas portlet-mode-type-view', 'display', 'none');
        }
    }

    function setPortletMode(admin, edit, view) {
        setElementStyleByClassName('portlet-mode-type-admin', 'display', admin ? '' : 'none');
        setElementStyleByClassName('portlet-mode-type-edit', 'display', edit ? '' : 'none');
        setElementStyleByClassName('portlet-mode-type-view', 'display', view ? '' : 'none');
    }

    function setElementStyleByClassName(cl, propertyName, propertyValue) {
        if (!pcontent.document.getElementsByTagName) return;
        var re = new RegExp("(^| )" + cl + "( |$)");
        var el = pcontent.document.all ? pcontent.document.all : pcontent.document.getElementsByTagName("body")[0].getElementsByTagName("*"); // fix for IE5.x
        for (var i = 0; i < el.length; i++) {
            if (el[i].className && el[i].className.match(re)) {
                el[i].style[propertyName] = propertyValue;
            }
        }
    }

		function reload(){
			preview();
			<c:if test="${not empty requestScope.fresh}">
				parent.frames['pages'].location.reload();
			</c:if>
		}
</script>
	</head>

<c:set var="str">${requestScope.toolbar}?number=${requestScope.nodeId}&pagepath=${requestScope.pathofpage}</c:set>
<frameset rows="*" framespacing="0" border="0">
 <%--
<frameset rows="35,*" framespacing="0" border="0">
 	CMSC-764, disabled this, because we do not want these tabs yet
 
     <frame frameborder="0" src="${str}" name="toolbar" scrolling="no"/>
	    <frame frameborder="0" src="${requestScope.pathofpage}" onload="reload()" name="pcontent"/>
     
     
 --%>
	    <frame frameborder="0" src="${requestScope.pathofpage}" onload="frameLoaded()" name="pcontent"/>
</frameset>

</html>