<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="dump.title" />
<body>
    <c:choose>
        <c:when test="${empty param.path}">
           <cmscedit:sideblock title="dump.title">
                <p><fmt:message key="dump.help.1" /></p>
                <p><fmt:message key="dump.help.2" /></p>
                <form>
                    <fmt:message key="dump.form.path" /> <br />
                    <input name="path" value="/temp/defaults" /><br />
                    <input type="submit" value="<fmt:message key="dump.form.submit" />" />
                </form>
           </cmscedit:sideblock>
        </c:when>
        <c:otherwise>
           <cmscedit:sideblock title="dump.success">
                <p><cmsc:dumpdefaults path="${param.path}" /></p>
                <br/>
           </cmscedit:sideblock>
        </c:otherwise>
    </c:choose>
</body>
</html:html>
</mm:content>