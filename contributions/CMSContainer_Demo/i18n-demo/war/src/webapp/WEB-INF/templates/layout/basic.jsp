<%@page language="java" contentType="text/html; charset=utf-8" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@taglib uri="http://finalist.com/csmc" prefix="cmsc" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%>
<%@ taglib tagdir="/WEB-INF/tags/" prefix="cmscf" %>

<mm:content type="text/html" encoding="UTF-8">

<c:set var="siteTitle" value="CMSContainer" />
<c:set var="siteSubtitle" value="Architecture for Repository-based Mmbase Applications"/>
<cmsc:screen>
<cmsc:location var="page" sitevar="site" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="nl" >
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title><c:out value="${siteTitle}"/> - <cmsc:title/></title>

		<cmsc:headercontent dublin="true"/>

		<link rel="home" href="<cmsc:staticurl page='/' />" title="<c:out value='${siteTitle}'/>" />
		<link rel="contents" href="<cmsc:staticurl page='/sitemap' />" title="Sitemap" />
		<link rel="icon" href="<cmsc:staticurl page='/mmbase/edit/basic/images/favicon.ico' />" type="image/x-icon" />
		<link rel="shortcut icon" href="<cmsc:staticurl page='/mmbase/edit/basic/images/favicon.ico' />" type="image/x-icon" />

		<style type="text/css" media="all">@import "<cmsc:staticurl page='/2col.css'/>";</style>
		<style type="text/css" media="all">@import "<cmsc:staticurl page='/menu.css'/>";</style>
		
		<cmsc:insert-stylesheet var="stylesheet"/>
		<c:forEach var="style" items="${stylesheet}">
			<link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/${style.resource}'/>" media="${style.media}"/>
		</c:forEach>
		<script src="<cmsc:staticurl page='/menu.js'/>" type="text/javascript"><!-- Browser --></script>
		
		<cmscf:editresources />
	</head>
	<body>
		<div class="Header">
			<h1 class="HeaderTitle"><c:out value="${siteTitle}"/></h1>
			<p class="HeaderSubtitle"><c:out value="${siteSubtitle}"/></p>
		</div>
		<div class="Content">
			<cmsc:insert-portlet layoutid="breadcrumb" />	
				
			<cmsc:insert-portlet layoutid="center1" />
			<cmsc:insert-portlet layoutid="center2" />
			<cmsc:insert-portlet layoutid="center3" />
			<cmsc:insert-portlet layoutid="center4" />
			<cmsc:insert-portlet layoutid="center5" />
		</div>
		<div class="Menu">
			<cmsc:insert-portlet layoutid="menu" />
			<cmsc:insert-portlet layoutid="left1" />
			<cmsc:insert-portlet layoutid="left2" />
			<cmsc:insert-portlet layoutid="left3" />
		</div>
		<div class="logo">
			<%--
			<a href="<cmsc:staticurl page='/'/>">
				<img class="imglink" alt="logo" src="<cmsc:staticurl page='/images/logo.gif'/>" />
			</a>
			--%>
		</div>
	</body>
</html>
</cmsc:screen>
</mm:content>