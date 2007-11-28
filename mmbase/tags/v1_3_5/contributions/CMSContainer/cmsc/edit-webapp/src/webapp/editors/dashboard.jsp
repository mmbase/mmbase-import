<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">--%>
<html:html xhtml="true">
<cmscedit:head title="dashboard.title">
   <script type="text/javascript">
      function clearDefaultSearchText(defaultText) {
      	var searchField = document.forms["searchForm"]["title"];
      	if(searchField.value == defaultText) {
	      	searchField.value = "";
      	}
      }
   </script>
</cmscedit:head>
<body>
		<div id="left">
			<!-- Zoek block -->
			<cmscedit:sideblock title="dashboard.search.header">
				<form action="repository/index.jsp" name="searchForm" method="post">
  							<div class="search_form"><input type="text" name="title" value="<fmt:message key="dashboard.search.term" />" onfocus="clearDefaultSearchText('<fmt:message key="dashboard.search.term" />');"/></div>

					<div class="search_form_options">
 								<a href="javascript:document.forms['searchForm'].submit()" class="button"><fmt:message key="dashboard.search.search" /></a>
					</div>
				</form>
			</cmscedit:sideblock>

            <c:set var="message"><cmsc:property key="dashboard.welcome.message"/></c:set>
            <c:if test="${fn:length(message) gt 0}">
	            <cmscedit:sideblock title="dashboard.welcome.header" titleClass="side_block_gray">
					<br />
					${message}
				</cmscedit:sideblock>
            </c:if>
	</div>
	<div id="content">

<mm:cloud jspvar="cloud" loginpage="login.jsp">

<c:set var="dashboardRepositorySize" value="10"/>
	<mm:haspage page="/editors/repository/">
    <c:set var="dashboardRepositoryTitle"><fmt:message key="dashboard.repository.header"><fmt:param>${dashboardRepositorySize}</fmt:param></fmt:message></c:set>
	<cmscedit:contentblock title="${dashboardRepositoryTitle}" titleMode="plain"
		titleClass="content_block_pink" bodyClass="body_table">
        <br />
		<mm:cloudinfo type="user" id="cloudusername" write="false" />

      <mm:listnodescontainer type="contentelement">
			<mm:constraint field="lastmodifier" operator="EQUAL" referid="cloudusername" />
         <mm:maxnumber value="${dashboardRepositorySize}" />
         <mm:sortorder field="lastmodifieddate" direction="down" />

		<table>
         <thead>
            <tr>
               <th><fmt:message key="dashboard.repository.date" /></th>
               <th><fmt:message key="dashboard.repository.element" /></th>
               <th><fmt:message key="dashboard.repository.title" /></th>
               <th><fmt:message key="dashboard.repository.number" /></th>
            </tr>
         </thead>
         <tbody class="hover">
            <mm:listnodes>
              <mm:field name="number" write="false" id="number"/>
               <tr <mm:even inverse="true">class="swap"</mm:even> href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');">
                  <td onMouseDown="objClick(this);"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
                  <td onMouseDown="objClick(this);"><mm:nodeinfo type="guitype"/></td>
                  <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                  <td onMouseDown="objClick(this);"><mm:field name="number"/></td>
               </tr>
            </mm:listnodes>
         </tbody>
      </table>
      </mm:listnodescontainer>
	</cmscedit:contentblock>

   </mm:haspage>
</mm:cloud>
	</div>
</body>
</html:html>
</mm:content>