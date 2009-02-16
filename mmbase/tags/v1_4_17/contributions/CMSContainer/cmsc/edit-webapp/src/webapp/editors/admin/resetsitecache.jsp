<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="resetcache.title" />
<body>
    <c:choose>
        <c:when test="${empty param.submit}">
            <cmscedit:sideblock title="resetcache.title">
                <p><fmt:message key="resetcache.help" /></p>
                <form>
                    <input type="submit" name="submit" value="<fmt:message key="resetcache.submit" />" />
                </form>
            </cmscedit:sideblock>
        </c:when>
        <c:otherwise>
    		<%com.finalist.cmsc.services.sitemanagement.SiteManagement.resetSiteCache();%>
            <cmscedit:sideblock title="resetcache.title">
	            <h2><fmt:message key="resetcache.success" /></h2>
            </cmscedit:sideblock>
        </c:otherwise>
    </c:choose>
</body>
</html:html>
</mm:content>