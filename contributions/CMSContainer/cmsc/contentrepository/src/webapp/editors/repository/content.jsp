<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<%@page import="com.finalist.cmsc.security.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="content.title" /></title>
		<link rel="stylesheet" type="text/css" href="../css/main.css" />
		<script src="content.js" type="text/javascript"></script>
		<script src="../utils/window.js" type="text/javascript"></script>
		<script src="../utils/rowhover.js" type="text/javascript"></script>
	    <script type="text/javascript" src="../utils/transparent_png.js" ></script>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
	  <mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
      <mm:import jspvar="returnurl" id="returnurl">/editors/repository/Content.do?parentchannel=<mm:write referid="parentchannel"/>&direction=down</mm:import>

      <div class="tabs">
         <!-- actieve TAB -->
         <div class="tab_active">
            <div class="body">
               <div>
                  <a name="activetab"><fmt:message key="content.title" /></a>
               </div>
            </div>
         </div>
      </div>

    <div class="editor">
      <div class="body">


<mm:node number="$parentchannel" jspvar="parentchannelnode">
<% UserRole role = RepositoryUtil.getRole(cloud, parentchannelnode, false); %>
   <p>
   <fmt:message key="content.channel" >
      <fmt:param ><mm:field name="path"/></fmt:param>
   </fmt:message>
   </p>
		<% if (role != null && SecurityUtil.isWriter(role)) { %>
			<ul class="shortcuts">
			   <li class="new" style="text-decoration: none;"><fmt:message key="content.new" />
				  <form action="../WizardInitAction.do" method="post" style="display:inline;text-decoration:none">
					 <input type="hidden" name="action" value="create" />
					 <input type="hidden" name="creation" value="<mm:write referid="parentchannel" />" />
					 <input type="hidden" name="returnurl" value="<%= returnurl %>" />


					 <select name="contenttype">
						<c:forEach var="type" items="${typesList}">
	                        <option value="${type.value}">${type.label}</option>
						</c:forEach>
					 </select>
					 <input type="submit" name="submitButton" value="<fmt:message key="content.create" />" class="button" />
				  </form>
			   </li>
			   <li class="link">
				  <a href="<mm:url page="SearchInitAction.do">
							  <mm:param name="linktochannel" value="$parentchannel" />
							  <mm:param name="returnurl" value="${returnurl}" />
                       <mm:param name="mode" value="advanced" />
                       <mm:param name="action" value="link" />
						   </mm:url>">
					 <fmt:message key="content.existing" />
				  </a>
			   </li>
				<% if (SecurityUtil.isEditor(role)) { %>
			   <li class="reorder">
				  <a href="<mm:url page="ReorderAction.do">
							  <mm:param name="parent" value="$parentchannel" />
						   </mm:url>">
					 <fmt:message key="content.reorder" />
				  </a>
			   </li>
				<% } %>
			</ul>
			<% } %>

   <div class="ruler_green"><div><fmt:message key="content.content" /></div></div>

<mm:import externid="elements" from="request" required="true"/>

   <table>
   <thead>
      <tr>
         <th></th>
         <th><fmt:message key="content.typecolumn" /></th>
         <th><fmt:message key="content.titlecolumn" /></th>
         <th><fmt:message key="content.authorcolumn" /></th>
         <th><fmt:message key="content.lastmodifiedcolumn" /></th>
         <th><fmt:message key="content.numbercolumn" /></th>
         <th><fmt:message key="content.creationchannelcolumn" /></th>
      </tr>
   </thead>
   <tbody class="hover">
   <mm:listnodes referid="elements" jspvar="node">
		<mm:field name="number" write="false" id="number" vartype="String"/>
		<mm:field name="number" write="false" id="relnumber"/>

		<mm:url page="../WizardInitAction.do" id="url" write="false" >
		   <mm:param name="objectnumber" value="$number"/>
		   <mm:param name="returnurl" value="$returnurl" />
		</mm:url>
      <tr <mm:even inverse="true">class="swap"</mm:even> href="<mm:write referid="url"/>">
		<td nowrap>
        	<a href="javascript:info('<mm:field name="number" />')"><img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="content.info" />"/></a>
			<a href="javascript:callEditWizard('<mm:field name="number" />');"  title="<fmt:message key="content.edit" />"><img src="../gfx/icons/edit.png" width="16" height="16" alt="<fmt:message key="content.edit" />"/></a>
			<% if (role != null && SecurityUtil.isWriter(role)) { %>
				<a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');" title="<fmt:message key="content.unlink" />"><img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="content.unlink" />"/></a>
			<% } %>
			<mm:last inverse="true">
	        	<a href="javascript:moveDown('<mm:field name="number" />','<mm:write referid="parentchannel" />')"><img src="../gfx/icons/down.png" width="16" height="16" alt="<fmt:message key="content.move.down" />"/></a>
        	</mm:last>
			<mm:first inverse="true">
		        <mm:last><img src="../gfx/icons/spacer.png" width="16" height="16" alt=""/></mm:last>
	        	<a href="javascript:moveUp('<mm:field name="number" />','<mm:write referid="parentchannel" />')"><img src="../gfx/icons/up.png" width="16" height="16" alt="<fmt:message key="content.move.up" />"/></a>
	        </mm:first>
		</td>
      <td onMouseDown="objClick(this);">
		   <mm:nodeinfo type="guitype"/>
		</td>
		<td  onMouseDown="objClick(this);">
		   <mm:field name="title"/>
		</td>
		<td onMouseDown="objClick(this);"><mm:field name="lastmodifier" /></td>
        <td nowrap><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
        <td><mm:field name="number"/></td>
		<td width="50" onMouseDown="objClick(this);">
			<c:choose>
				<c:when test="${not empty createdNumbers[number]}">
					<fmt:message key="content.yes" />
				</c:when>
				<c:otherwise>
					<fmt:message key="content.no" />
				</c:otherwise>
			</c:choose>
		</td>

         </tr>
   </mm:listnodes>
      </tbody>
   </table>
      </div>
   </div>
   </mm:node>
</mm:cloud>
	</body>
</html:html>
</mm:content>