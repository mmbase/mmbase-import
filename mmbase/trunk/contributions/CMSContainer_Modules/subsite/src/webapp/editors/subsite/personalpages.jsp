<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@ page import="com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
      <script src="content.js" type="text/javascript"></script>
      <script src="search.js" type="text/javascript"></script>
</cmscedit:head>
<body>
<mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<!--
<mm:import externid="returnurl"/>
<mm:import externid="linktochannel"/>
<mm:import externid="parentchannel" jspvar="parentchannel"/>
<mm:import externid="contenttypes" jspvar="contenttypes"><%= ContentElementUtil.CONTENTELEMENT %></mm:import>
-->

<mm:import externid="pageNodes" jspvar="nodeList" vartype="List" />
<!--
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
-->

<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

<b>Pages</b><br>
<c:set var="pagesElements" value="${pagesElements}" scope="request"/>
List of pagesElements: ${pagesElements}<br><br>

<!-- 
<c:forEach var="pageNodes" items="${pageNodes}">
  <b>${pageNodes}</b><br>
</c:forEach>
 -->

<a href="../subsite/PersonalPageCreate.do?parentpage=">aanmaken nieuwe persoonlijke pagina</a>

<table>
<mm:listnodes referid="pageNodes">
<tr>
<td>
   <b><mm:field name="title" /></b>
</td>
<td>
   <a href="../subsite/SubSiteDelete.do?number=<mm:field name="number" />">verwijderen pagina</a>
</td>
<td>
   <a href="../subsite/SubSiteEdit.do?number=<mm:field name="number" />">edit page</a>
</td>
<td>
edit artikelen (geeft overzicht lijst artikelen)
</td>
</tr>
</mm:listnodes>
</table>


   <div class="editor">
   <br />

   <%-- Now print if no results --%>
   <mm:isempty referid="pageNodes">
      <fmt:message key="searchform.searchpages.nonefound" />
   </mm:isempty>

    </div>
</mm:cloud>

   </body>
</html:html>
</mm:content>