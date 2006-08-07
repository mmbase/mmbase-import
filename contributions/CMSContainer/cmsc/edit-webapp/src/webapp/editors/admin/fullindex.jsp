<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/luceusmodule" prefix="luceusmodule"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="dashboard.title" /></title>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
</head>
<body>

<c:choose>
	<c:when test="${empty param.doit}">
		<div class="side_block">
			<!-- bovenste balkje -->
			<div class="header">
				<div class="title"><fmt:message key="fullindex.title" /></div>
				<div class="header_end"></div>
			</div>
			<div>
                <p>
                    <fmt:message key="fullindex.help.1" />
                </p>
				<form>
				<input type="hidden" name="doit" value="yes"/>
				<input type="submit" value="<fmt:message key="fullindex.form.submit" />"/>
				</form>
			<!-- einde block -->
			<div class="side_block_end"></div>
		</div>
	</c:when>
	<c:otherwise>
		<h2><fmt:message key="fullindex.busy" /></h2>
		<luceusmodule:fullindex />
	</c:otherwise>
</c:choose>

</body>
</html:html>
</mm:content>