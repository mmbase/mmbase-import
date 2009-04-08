<%@ page language="java" contentType="text/html;charset=utf-8" 
%><%@ include file="globals.jsp" 
%><%@ page import="com.finalist.cmsc.repository.RepositoryUtil" 
%><%@ page import="com.finalist.cmsc.security.*" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
<mm:import jspvar="returnurl" id="returnurl">/editors/repository/Content.do?type=content&parentchannel=<mm:write
        referid="parentchannel"/>&direction=down</mm:import>

<div class="editor">
<div class="body">
<!-- check to see if we have workflow, this is done by looking if the editors for the workflow are on the HD -->
<c:set var="hasWorkflow" value="false"/>
<mm:haspage page="/editors/workflow">
    <c:set var="hasWorkflow" value="true"/>
</mm:haspage>


<mm:node number="$parentchannel" jspvar="parentchannelnode">
<% UserRole role = RepositoryUtil.getRole(cloud, parentchannelnode, false); %>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<ul class="shortcuts">
    <li class="new" style="text-decoration: none;"><fmt:message key="content.new"/>
        <form name="initForm" action="../WizardInitAction.do" method="post" style="display:inline;text-decoration:none">
            <input type="hidden" name="action" value="create"/>
            <input type="hidden" name="creation" value="<mm:write referid="parentchannel" />"/>
            <input type="hidden" name="returnurl" value="<%= returnurl %>"/>
            <input type="hidden" name="order" value="${orderby}" />
            <input type="hidden" name="direction" value="${direction}"/>
            <input type="hidden" name="offset" value="${param.offset}"/>
            <select name="contenttype">
                <c:forEach var="type" items="${typesList}">
                    <option value="${type.value}">${type.label}</option>
                </c:forEach>
            </select>
            <input type="submit" name="submitButton" value="<fmt:message key="content.create" />" class="button"/>
        </form>
    </li>
    <li class="link">
        <mm:url page="SearchInitAction.do" id="search_init_action_url" write="false">
            <mm:param name="linktochannel" value="$parentchannel"/>
            <mm:param name="returnurl" value="${returnurl}"/>
            <mm:param name="mode" value="advanced"/>
            <mm:param name="action" value="link"/>
        </mm:url>
        <a href="${search_init_action_url}">
            <fmt:message key="content.existing"/>
        </a>
    </li>
    <% if (SecurityUtil.isEditor(role)) { %>
    <mm:url page="ReorderAction.do" id="reorder_action_url" write="false">
        <mm:param name="parent" value="$parentchannel"/>
    </mm:url>
    <li class="reorder">
        <a href="${reorder_action_url}">
            <fmt:message key="content.reorder"/>
        </a>
    </li>
    <!--display openoffice file upload function enter point-->
    <mm:haspage page="/editors/repository/uploadodt.jsp">
        <mm:url page="/editors/upload/display_unsaved_files.do" id="display_unsaved_files_url" write="false">
            <mm:param name="parent" value="$parentchannel"/>
        </mm:url>
        <li class="reorder">
            <a href="${display_unsaved_files_url}">
                <fmt:message key="odtupload.entry"/>
            </a>
        </li>
    </mm:haspage>
    <% } %>
</ul>
<% } %>
</div>

<div class="ruler_green">
   <div>
   <fmt:message key="content.content">
      <fmt:param><mm:field name="path"/></fmt:param>
   </fmt:message>
   </div>
</div>
<div class="body">
<mm:import externid="elements" from="request" required="true"/>
<mm:import externid="elementCount" from="request" vartype="Integer">0</mm:import>
<mm:import externid="resultsPerPage" from="request" vartype="Integer">25</mm:import>

<c:set var="listSize" value="${elementCount}"/>
<c:set var="offset" value="${param.offset}"/>
<c:set var="extraparams" value="&direction=${param.direction}&parentchannel=${param.parentchannel}"/>
<c:set var="orderby" value="${param.orderby}" scope="page" />
<c:set var="type" value="content" scope="page" />
<%@ include file="../pages.jsp" %>

<form action="contentMassDelete.do" name="contentForm">
<input type="hidden" name="offset" value="${param.offset}"/>
<input type="hidden" name="orderby" value="${orderby}" />
<input type="hidden" name="direction" value="${direction}"/>
<input type="hidden" name="channelnumber" value="<mm:write referid="parentchannel" />"/>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<c:if test="${fn:length(elements) >1}">
<input type="button" class="button" value="<fmt:message key="content.delete.massdelete" />" onclick="massDelete('<fmt:message key="content.delete.massdeleteconfirm" />', 'contentForm')"/>
<input type="button" class="button" value="<fmt:message key="content.delete.massmove" />" onclick="massMove('${parentchannel}','<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
</c:if>
<% } %>
<table>
<thead>
    <tr>
        <th><% if (role != null && SecurityUtil.isWriter(role)) { %>
        <c:if test="${fn:length(elements) >1}">
        <input type="checkbox"  name="selectall" class="checkbox" onclick="selectAll(this.checked, 'contentForm', 'chk_');" value="on"/>
        </c:if>
        <% } %>
        </th>
        <th><a href="javascript:sortBy('Content','otype','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="content.typecolumn"/></a></th>
        <th><a href="javascript:sortBy('Content','title','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="content.titlecolumn"/></a></th>
        <th><a href="javascript:sortBy('Content','lastmodifier','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="content.authorcolumn"/></a></th>
        <th><a href="javascript:sortBy('Content','lastmodifieddate','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="content.lastmodifiedcolumn"/></a></th>
        <th><a href="javascript:sortBy('Content','number','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="content.numbercolumn"/></a></th>
        <th><fmt:message key="content.creationchannelcolumn"/></th>
        <th></th>
    </tr>
</thead>
<tbody class="hover">
<mm:listnodes referid="elements" jspvar="node">
<mm:field name="number" write="false" id="number" vartype="String"/>
<mm:field name="number" write="false" id="relnumber"/>

<mm:url page="../WizardInitAction.do" id="url" write="false">
    <mm:param name="objectnumber" value="$number"/>
    <mm:param name="returnurl" value="$returnurl"/>
</mm:url>
<tr   <mm:even inverse="true">class="swap"</mm:even> href="<mm:write referid="url"/>">
    <td style="white-space: nowrap;">
    <% if (role != null && SecurityUtil.isWriter(role)) { %>
      <c:if test="${fn:length(elements) >1}">
      <input type="checkbox"  name="chk_<mm:field name="number" />" class="checkbox" value="<mm:field name="number" />" onClick="document.forms['contentForm'].elements.selectall.checked=false;"/>
      </c:if>
    <% } %>
     <a href="javascript:callEditWizard('<mm:field name="number" />');"
       title="<fmt:message key="content.edit" />"><img src="../gfx/icons/edit.png" width="16" height="16"
                                                       title="<fmt:message key="content.edit" />"
                                                       alt="<fmt:message key="content.edit" />"/></a>
    <a href="<cmsc:contenturl number="${number}"/>" target="_blank"><img src="../gfx/icons/preview.png"
                                                                         alt="<fmt:message key="content.preview.title" />"
                                                                         title="<fmt:message key="content.preview.title" />"/></a>
    <a href="javascript:info('<mm:field name="number" />')"><img src="../gfx/icons/info.png" width="16" height="16"
                                                                 title="<fmt:message key="content.info" />"
                                                                 alt="<fmt:message key="content.info" />"/></a>

    <mm:haspage page="/editors/versioning">
        <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
            <c:param name="nodenumber"><mm:field name="number"/></c:param>
        </c:url>
        <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')"><img
                src="../gfx/icons/versioning.png" title="<fmt:message key="content.icon.versioning.title" />"
                alt="<fmt:message key="content.icon.versioning.title" />"/></a>
    </mm:haspage>
    <% if (role != null && SecurityUtil.isWriter(role)) { %>
    <a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');"
       title="<fmt:message key="content.unlink" />"><img src="../gfx/icons/delete.png" width="16" height="16"
                                                         title="<fmt:message key="content.unlink" />"
                                                         alt="<fmt:message key="content.unlink" />"/></a>
    <a href="<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />"
       target="selectchannel" onclick="moveContent(<mm:field name="number" />, ${parentchannel} )">
        <img src="../gfx/icons/page_move.png" title="<fmt:message key="searchform.icon.move.title" />"/></a>
    <% } %>

    <cmsc:hasfeature name="responseform">
        <c:set var="typeval">
            <mm:nodeinfo type="type"/>
        </c:set>
        <c:if test="${typeval == 'responseform'}">
            <mm:url page="/editors/savedform/ShowSavedForm.do" id="showSavedForms" write="false">
                <mm:param name="nodenumber"><mm:field name="number"/></mm:param>
                <mm:param name="initreturnurl" value="${returnurl}"/>
            </mm:url>
            <a href="<mm:write referid="showSavedForms"/>"><img src="../gfx/icons/application_form_magnify.png"
                                                                title="<fmt:message key="content.icon.savedform.title" />"
                                                                alt="<fmt:message key="content.icon.savedform.title" />"/></a>
        </c:if>
    </cmsc:hasfeature>
    
    <% if (role != null && SecurityUtil.isEditor(role)) { %>
      <mm:first inverse="true">

        <a href="javascript:moveUp('<mm:field name="number" />','<mm:write referid="parentchannel" />')"><img
                src="../gfx/icons/up.png" width="16" height="16" title="<fmt:message key="content.move.up" />"
                alt="<fmt:message key="content.move.up" />"/></a>
    </mm:first>
    <mm:last inverse="true">
        <mm:first><img src="../gfx/icons/spacer.png" width="16" height="16" alt=""/></mm:first>
        <a href="javascript:moveDown('<mm:field name="number" />','<mm:write referid="parentchannel" />')"><img
                src="../gfx/icons/down.png" width="16" height="16" title="<fmt:message key="content.move.down" />"
                alt="<fmt:message key="content.move.down" />"/></a>
    </mm:last>

    <% } %>

</td>
<td onMouseDown="objClick(this);">
    <mm:nodeinfo type="guitype"/>
</td>
<td onMouseDown="objClick(this);">
    <mm:field id="title" write="false" name="title"/>
    <c:if test="${fn:length(title) > 50}">
        <c:set var="title">${fn:substring(title,0,49)}...</c:set>
    </c:if>
        ${title}
</td>
<td onMouseDown="objClick(this);" style="white-space: nowrap;">
    <mm:field name="lastmodifier" id="lastmodifier" write="false"/>
    <mm:listnodes type="user" constraints="username = '${lastmodifier}'">
        <c:set var="lastmodifierFull"><mm:field name="firstname"/> <mm:field name="prefix"/> <mm:field
                name="surname"/></c:set>
        <c:if test="${lastmodifierFull != ''}"><c:set var="lastmodifier" value="${lastmodifierFull}"/></c:if>
    </mm:listnodes>
        ${lastmodifier}
</td>
<td style="white-space: nowrap;"><mm:field name="lastmodifieddate"><cmsc:dateformat
        displaytime="true"/></mm:field></td>
<td><mm:field name="number"/></td>
<td width="50" onMouseDown="objClick(this);" style="white-space: nowrap;">
    <c:choose>
    <c:when test="${not empty createdNumbers[number]}">
        <fmt:message key="content.yes"/>
    </c:when>
    <c:otherwise>
    <mm:relatednodes role="creationrel" type="contentchannel">
    <mm:field name="number" id="channelNumber" write="false"/>
    <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>

    <mm:field name="name" id="channelName" write="false"/>
    <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
    <c:set var="channelIconMessage"><fmt:message key="role.${rights}"/></c:set>
    <c:set var="channelUrl" value="Content.do?parentchannel=${channelNumber}"/>

    <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}"/>
    <a href="${channelUrl}">${channelName}</a>
</td>
</mm:relatednodes>
</c:otherwise>
</c:choose>
</td>
<c:if test="${hasWorkflow}">
    <td width="10" onMouseDown="objClick(this);">
        <c:set var="status" value="waiting"/>
        <mm:relatednodes type="workflowitem" constraints="type='content'">
            <c:set var="status"><mm:field name="status"/></c:set>
        </mm:relatednodes>
        <c:if test="${status == 'waiting'}">
            <mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
                <c:set var="status" value="onlive"/>
            </mm:listnodes>
        </c:if>
        <img src="../gfx/icons/status_${status}.png"
             alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />"
             title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />"/>
    </td>
</c:if>
</tr>
</mm:listnodes>
</tbody>
</table>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<c:if test="${fn:length(elements) >1}">
<input type="submit" class="button" value="<fmt:message key="content.delete.massdelete" />"/>
<input type="button" class="button" value="<fmt:message key="content.delete.massmove" />"  onclick="massMove('${parentchannel}','<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
</c:if>
<% } %>
</form>
<%@ include file="../pages.jsp" %>
</div>
</div>
</mm:node>
</mm:cloud>
</mm:content>