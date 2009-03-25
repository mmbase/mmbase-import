<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0"
><%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">--%>
<html:html xhtml="true">
<cmscedit:head title="dashboard.title">
   <script type="text/javascript">
        function info(objectNumber) {
      openPopupWindow("info", 500, 500, "repository/showitem.jsp?objectnumber=" + objectNumber);
   }

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
<mm:cloudinfo type="user" id="cloudusername" write="false" />

<c:set var="dashboardRepositorySize" value="10"/>
	<mm:haspage page="/editors/repository/">
    <c:set var="dashboardRepositoryTitle"><fmt:message key="dashboard.repository.header"><fmt:param>${dashboardRepositorySize}</fmt:param></fmt:message></c:set>
	<cmscedit:contentblock title="${dashboardRepositoryTitle}" titleMode="plain"
		titleClass="content_block_pink" bodyClass="body_table">

      <mm:listnodescontainer type="contentelement">
         <mm:constraint field="lastmodifier" operator="EQUAL" referid="cloudusername" />
         <mm:maxnumber value="${dashboardRepositorySize}" />
         <mm:sortorder field="lastmodifieddate" direction="down" />
      <table>
         <thead>
            <tr>
               <th style="width: 80px;"></th>
               <th style="width: 68px;"><fmt:message key="dashboard.repository.element" /></th>
               <th><fmt:message key="dashboard.repository.title" /></th>
               <th style="width: 150px;"><fmt:message key="dashboard.repository.date" /></th>
               <th style="width: 80px;"><fmt:message key="dashboard.repository.number" /></th>
            </tr>
         </thead>
         <tbody class="hover">
            <mm:listnodes>
              <mm:field name="number" write="false" id="number"/>
               <tr <mm:even inverse="true">class="swap"</mm:even> href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');">
			   <td>
            	<a href="javascript:window.top.openRepositoryWithContent('<mm:write referid="number"/>');"><img src="gfx/icons/edit.png" alt="<fmt:message key="dashboard.content.edit" />" title="<fmt:message key="dashboard.content.edit" />" /></a>
            	<a href="<cmsc:contenturl number="${number}"/>" target="_blank"><img src="gfx/icons/preview.png" alt="<fmt:message key="dashboard.content.preview.title" />" title="<fmt:message key="dashboard.content.preview.title" />" /></a>
					<a href="javascript:info('${number}')"><img src="gfx/icons/info.png" title="<fmt:message key="dashboard.content.info" />" alt="<fmt:message key="dashboard.content.info" />"/></a>
					<mm:haspage page="/editors/versioning">
					<c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
					   <c:param name="nodenumber">${number}</c:param>
					</c:url>
					<a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')"><img src="gfx/icons/versioning.png" title="<fmt:message key="dashboard.content.icon.versioning.title" />" alt="<fmt:message key="dashboard.content.icon.versioning.title" />"/></a>
					</mm:haspage>
				</td>
                  <td onMouseDown="objClick(this);"><mm:nodeinfo type="guitype"/></td>
                  <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                  <td onMouseDown="objClick(this);"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
                  <td onMouseDown="objClick(this);"><mm:field name="number"/></td>
               </tr>
            </mm:listnodes>
         </tbody>
      </table>
      </mm:listnodescontainer>
	</cmscedit:contentblock>
   </mm:haspage>


    <c:set var="dashboardTaskSize" value="5"/>
    <c:set var="tmpRole" value="assignedrel"/>
    <mm:haspage page="/editors/taskmanagement/">
    <c:set var="dashboardTaskTitle"><fmt:message key="dashboard.task.header"><fmt:param>${dashboardTaskSize}</fmt:param></fmt:message></c:set>
    <cmscedit:contentblock title="${dashboardTaskTitle}" titleMode="plain"
      titleClass="content_block_pink" bodyClass="body_table">

     <mm:listnodescontainer type="user">
        <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
        <mm:maxnumber value="10" />
        <mm:listnodes>
           <mm:relatednodescontainer type="task" role="${tmpRole}" searchdirs="source">
              <table>
                 <thead>
                    <tr>
                      <th><fmt:message key="dashboard.task.created" /></th>
                      <th><fmt:message key="dashboard.task.deadline" /></th>
                      <th><fmt:message key="dashboard.task.title" /></th>
                      <th><fmt:message key="dashboard.task.status" /></th>
                      <th><fmt:message key="dashboard.task.contenttitle" /></th>
                      <th><fmt:message key="dashboard.task.nodetype" /></th>
                      <th><fmt:message key="dashboard.task.description" /></th>
                    </tr>
                 </thead>
                 <tbody class="hover">
                    <c:set var="taskList" value="" /><c:set var="isSwapClass" value="true"/>
                    <mm:relatednodes comparator="com.finalist.cmsc.tasks.TaskUrgencyComparator" max="${dashboardTaskSize}">
                       <c:set var="taskId"><mm:field name="number"/></c:set>
                       <c:if test="${not fn:contains(taskList, taskId)}">
                       <tr <c:if test="${isSwapClass}">class="swap"</c:if>>
                          <td><mm:field name="creationdate" id="created"><mm:time time="${created}" format="d/M/yyyy HH:mm" /></mm:field></td>
                          <td><mm:field name="deadline" id="deadl"><mm:time time="${deadl}" format="d/M/yyyy HH:mm"/></mm:field></td>
                          <td><mm:field name="title"/></td>
                          <c:set var="elementtitel"><mm:field name="title"/></c:set>
                          <c:set var="elementnumber"/>
                          <c:set var="elementtype"/>
                          <mm:relatednodescontainer type="contentelement" role="taskrel" searchdirs="destination">
                             <mm:maxnumber value="1" />
                             <mm:relatednodes>
                                <c:set var="elementtitel"><mm:field name="title"/></c:set>
                                <c:set var="elementnumber"><mm:field name="number"/></c:set>
                                <c:set var="elementtype"><mm:field name="number"><mm:isnotempty><mm:nodeinfo type="guitype"/></mm:isnotempty></mm:field></c:set>
                             </mm:relatednodes>
                          </mm:relatednodescontainer>
                          <c:set var="status"><mm:field name="status" /></c:set>
                          <td><fmt:message key="${status}" /></td>
                          <td>
                             <c:choose>
                                <c:when test="${empty elementnumber}">
                                   <fmt:message key="dashboard.task.noelement"/>
                                </c:when>
                                <c:otherwise>
                                   <mm:hasrank minvalue="basic user">
                                      <a href="javascript:window.top.openRepositoryWithContent('<mm:write referid="elementnumber"/>');"><img src="gfx/icons/edit.png" align="top" alt="<fmt:message key="dashboard.task.editelement"/>" title="<fmt:message key="dashboard.task.editelement"/>"/></a> ${elementtitel}
                                   </mm:hasrank>
                                </c:otherwise>
                             </c:choose>
                          </td>
                          <td>${elementtype}</td>
                          <td>
                             <mm:hasrank minvalue="basic user">
                                <mm:field name="number" jspvar="number" write="false"/>
                                <mm:url page="/editors/taskmanagement/tasklist.jsp" id="returnTaskedit" write="false" />
                                <a href="javascript:window.top.openTasksWithTask('<mm:write referid="number"/>');"><img src="gfx/icons/edit2.png" align="top" alt="<fmt:message key="dashboard.task.edit"/>" title="<fmt:message key="dashboard.task.edit"/>"/></a> <mm:field name="description" />
                             </mm:hasrank>
                          </td>
                       </tr>
                       <c:set var="taskList">${taskList},${taskId}</c:set>
                       <c:choose>
                          <c:when test="${isSwapClass eq 'false'}"><c:set var="isSwapClass" value="true"/></c:when>
                          <c:when test="${isSwapClass eq 'true'}"><c:set var="isSwapClass" value="false"/></c:when>
                       </c:choose>
                       </c:if>
                    </mm:relatednodes>
                 </tbody>
              </table>
           </mm:relatednodescontainer>
        </mm:listnodes>
     </mm:listnodescontainer>
   </cmscedit:contentblock>
   </mm:haspage>

</mm:cloud>
	</div>
</body>
</html:html>
</mm:content>