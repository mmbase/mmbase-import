<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../../globals.jsp" %>
<fmt:setBundle basename="cmsc-repository" scope="request" />
<%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <head>
      <link href="../style.css" type="text/css" rel="stylesheet"/>
      <title><fmt:message key="urls.title" /></title>
      <script src="../repository/search.js"type="text/javascript" ></script>
      <script src="../repository/content.js"type="text/javascript" ></script>
      <script src="../utils/window.js" type="text/javascript"></script>
      <script src="../utils/rowhover.js" type="text/javascript"></script>
		<script type="text/javascript">
			function selectElement(element, title, src) {
				window.top.opener.selectElement(element, title, src);
				window.top.close();
			}
		</script>
   </head>
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
         <table style="width: 100%; vertical-alignment: top;">
            <tr>
               <td>
                  <h3>
                       <fmt:message key="urls.title" />
                  </h3>
               </td>
            </tr>
         </table>
         <mm:import id="searchinit"><c:url value='/editors/resources/UrlInitAction.do'/></mm:import>
         <html:form action="/editors/resources/UrlAction" method="post">
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>

<mm:import id="contenttypes" jspvar="contenttypes">urls</mm:import>
<%@include file="urlform.jsp" %>

         </html:form>

<mm:import externid="results" jspvar="nodeList" vartype="List" />
<%@include file="../repository/searchpages.jsp" %>

         <table border="0" width="100%" class="listcontent">
            <tr class="listheader">
               <th>                                    <a href="#" class="headerlink" onclick="orderBy('number');"><fmt:message key="urlsearch.numbercolumn" /></a></th>
               <th style="width: 100px;" nowrap="true"><a href="#" class="headerlink" onclick="orderBy('name');"><fmt:message key="urlsearch.namecolumn" /></a></th>
               <th style="width: 110px;"><fmt:message key="urlsearch.urlcolumn" /></th>
            </tr>
           	<mm:list referid="results">
           	   <mm:node element="${contenttypes}" jspvar="node">
                  <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title"/>','<mm:field name="url" />');</mm:import>
                  <tr onMouseOver="objMouseOver(this);"
                      onMouseOut="objMouseOut(this);"
                      href="<mm:write referid="url"/>">
                     <td onMouseDown="objClick(this);"><mm:field name="number"/></td>
                     <td onMouseDown="objClick(this);"><mm:field name="name"/></td>
                     <td onMouseDown="objClick(this);"><mm:field name="url"/></td>
                  </tr>
               </mm:node>
            </mm:list>
         </table>
            <% } %> <%-- Close searchpages --%>
      </mm:cloud>
   </body>
</html:html>
</mm:content>