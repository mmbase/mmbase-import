<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/luceusmodule" prefix="luceusmodule"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="fullindex.title" />
<body>
<cmscedit:sideblock title="fullindex.title">
<c:choose>
	<c:when test="${empty param.doit}">
        <p>
            <fmt:message key="fullindex.help.1" />
        </p>
		<form>
		<input type="hidden" name="doit" value="yes"/>
        <input type="checkbox" name="doerase" value="true" /><fmt:message key="fullindex.erase" />
        <p/>
		<input type="submit" value="<fmt:message key="fullindex.form.submit" />"/>
		</form>
	</c:when>
	<c:otherwise>
		<p><fmt:message key="fullindex.busy" /></p>
		<luceusmodule:fullindex erase="${param.doerase}" />
	</c:otherwise>
</c:choose>
</cmscedit:sideblock>

</body>
</html:html>
</mm:content>