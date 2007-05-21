<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    <html:html xhtml="true">
    <head>
    <title><fmt:message key="dashboard.title" /></title>
    <link href="../css/main.css" type="text/css" rel="stylesheet" />
    </head>
    <body>

    <c:choose>
        <c:when test="${empty param.submit}">
            <div class="side_block">
                <!-- bovenste balkje -->
                <div class="header">
                    <div class="title"><fmt:message key="resetcache.title" /></div>
                    <div class="header_end"></div>
                </div>
                <div>
                    <div class="body">
	                    <p><fmt:message key="resetcache.help" /></p>
	                    <form>
	                        <input type="submit" name="submit" value="<fmt:message key="resetcache.submit" />" />
	                    </form>
	                 </div>
                </div>
                <!-- einde block -->
                <div class="side_block_end"></div>
            </div>
        </c:when>
        <c:otherwise>
        		<%com.finalist.cmsc.services.sitemanagement.SiteManagement.resetSiteCache();%>
            <h2><fmt:message key="resetcache.success" /></h2>
        </c:otherwise>
    </c:choose>

    </body>
    </html:html>
</mm:content>
