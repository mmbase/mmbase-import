<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../../globals.jsp"%>
<fmt:setBundle basename="cmsc-repository" scope="request" />
<%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	<html:html xhtml="true">
	<head>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
	<title><fmt:message key="urls.title" /></title>
	<script src="../repository/search.js" type="text/javascript"></script>
	<script src="../repository/content.js" type="text/javascript"></script>
	<script src="../utils/window.js" type="text/javascript"></script>
	<script src="../utils/rowhover.js" type="text/javascript"></script>
	<script type="text/javascript">
			function selectElement(element, title, src) {
				if(window.top.opener != undefined) {
					window.top.opener.selectElement(element, title, src);
					window.top.close();
				}
			}
		</script>
	</head>
	<body>
	<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
		<mm:import externid="action">search</mm:import>
		<%-- either: search of select --%>
		<div class="tabs"><!-- actieve TAB -->
		<div class="tab_active">
		<div class="body">
		<div><a><fmt:message key="urls.title" /></a></div>
		</div>
		</div>
		</div>

		<div class="editor" style="height:500px">
		<div class="body"><mm:import id="searchinit">
			<c:url value='/editors/resources/UrlInitAction.do' />
		</mm:import> <html:form action="/editors/resources/UrlAction" method="post">
			<html:hidden property="action" value="${action}"/>
			<html:hidden property="offset" />
			<html:hidden property="order" />
			<html:hidden property="direction" />

			<mm:import id="contenttypes" jspvar="contenttypes">urls</mm:import>
			<%@include file="urlform.jsp"%>

		</html:form>

		<div class="ruler_green">
		<div><fmt:message key="urls.results" /></div>
		</div>

		<mm:import externid="results" jspvar="nodeList" vartype="List" /> <mm:import
			externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import> <mm:import
			externid="offset" jspvar="offset" vartype="Integer">0</mm:import> <c:if test="${resultCount > 0}">
			<%@include file="../repository/searchpages.jsp"%>

			<table border="0" width="100%" class="listcontent">
				<tr class="listheader">
					<th></th>
					<th nowrap="true"><a href="#" class="headerlink" onclick="orderBy('name');"><fmt:message
						key="urlsearch.namecolumn" /></a></th>
					<th><fmt:message key="urlsearch.urlcolumn" /></th>
				</tr>
				<tbody class="hover">
					<c:set var="useSwapStyle">true</c:set>
					<mm:listnodes referid="results">
						<mm:import id="url">javascript:selectElement('<mm:field name="number" />', '<mm:field
								name="name" />','<mm:field name="url" />');</mm:import>
						<tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
							<td style="white-space:nowrap;">
							  <mm:compare referid="action" value="search">
								<a href="<mm:url page="../WizardInitAction.do">
                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                     <mm:param name="returnurl" value="<%="../editors/resources/UrlAction.do" + request.getAttribute("geturl")%>" />
                                                  </mm:url>">
								<img src="../gfx/icons/page_edit.png" title="<fmt:message key="urlsearch.icon.edit" />" /></a>
								<mm:hasrank minvalue="administrator">
									<a href="<mm:url page="DeleteSecondaryContentAction.do" >
	                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
	                                                     <mm:param name="returnurl" value="<%="/editors/resources/UrlAction.do" + request.getAttribute("geturl")%>" />
	                                                  </mm:url>">
									<img src="../gfx/icons/delete.png" title="<fmt:message key="urlsearch.icon.delete" />" /></a>
								</mm:hasrank>
							  </mm:compare>
							</td>
							<mm:field name="name" jspvar="name" write="false"/>
							<td onMouseDown="objClick(this);">${fn:substring(name, 0, 40)}<c:if test="${fn:length(name) > 40}">...</c:if></td>
							<mm:field name="url" jspvar="url" write="false"/>
							<td onMouseDown="objClick(this);">${fn:substring(url, 0, 40)}<c:if test="${fn:length(url) > 40}">...</c:if></td>
						</tr>
						<c:set var="useSwapStyle">${!useSwapStyle}</c:set>
					</mm:listnodes>
				</tbody>
			</table>
		</c:if>
<c:if test="${resultCount == 0 && param.name != null}">
	<fmt:message key="urlsearch.noresult" />
</c:if>
	</mm:cloud>
	</body>
	</html:html>
</mm:content>
