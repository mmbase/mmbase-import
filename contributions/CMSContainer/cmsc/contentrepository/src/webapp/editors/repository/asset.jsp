<%@ page language="java" contentType="text/html;charset=utf-8" 
%><%@ include file="globals.jsp" 
%><%@ page import="com.finalist.cmsc.repository.RepositoryUtil" 
%><%@ page import="com.finalist.cmsc.security.*" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
<mm:import jspvar="returnurl" id="returnurl">/editors/repository/Asset.do?type=asset&parentchannel=<mm:write
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
<p>
    <fmt:message key="asset.channel">
        <fmt:param><mm:field name="path"/></fmt:param>
    </fmt:message>
</p>
<%@ include file="assetupload.jsp" %>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<ul class="shortcuts">
    <li class="new" style="text-decoration: none;"><fmt:message key="asset.new"/>
        <form name="initForm" action="../AssetInitAction.do" method="post" style="display:inline;text-decoration:none">
            <input type="hidden" name="action" value="create"/>
            <input type="hidden" name="creation" value="<mm:write referid="parentchannel" />"/>
            <input type="hidden" name="returnurl" value="<%= returnurl %>"/>
            <input type="hidden" name="order" value="${orderby}" />
            <input type="hidden" name="direction" value="${direction}"/>
            <input type="hidden" name="offset" value="${param.offset}"/>
            <select name="assettype">
                <c:forEach var="type" items="${typesList}">
                    <option value="${type.value}">${type.label}</option>
                </c:forEach>
            </select>
            <input type="submit" name="submitButton" value="<fmt:message key="asset.create" />" class="button"/>
        </form>
    </li>
</ul>
<% } %>
</div>

<div class="ruler_green">
    <div><fmt:message key="asset.asset"/></div>
</div>
<div class="body">
<mm:import externid="elements" from="request" required="true"/>
<mm:import externid="elementCount" from="request" vartype="Integer">0</mm:import>
<mm:import externid="resultsPerPage" from="request" vartype="Integer">25</mm:import>
<c:set var="listSize" value="${elementCount}"/>
<c:set var="offset" value="${param.offset}"/>
<c:set var="extraparams" value="&direction=${param.direction}&parentchannel=${param.parentchannel}"/>
<c:set var="orderby" value="${param.orderby}" scope="page" />
<c:set var="type" value="asset" scope="page" />
<%@ include file="../pages.jsp" %>

<form action="assetMassDelete.do" name="assetForm">
<input type="hidden" name="offset" value="${param.offset}"/>
<input type="hidden" name="orderby" value="${orderby}" />
<input type="hidden" name="direction" value="${direction}"/>
<input type="hidden" name="channelnumber" value="<mm:write referid="parentchannel" />"/>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<c:if test="${fn:length(elements) >1}">
<input type="submit" class="button" value="<fmt:message key="asset.delete.massdelete" />"/>
</c:if>
<% } %>
<table>
<thead>
    <tr>
        <th><% if (role != null && SecurityUtil.isWriter(role)) { %>
        <c:if test="${fn:length(elements) >1}">
        <input type="checkbox"  name="selectall"  onclick="selectAll(this.checked, 'assetForm', 'chk_');" value="on"/>
        </c:if>
        <% } %>
        </th>
        <th><a href="javascript:sortBy('Asset', 'otype','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="asset.typecolumn"/></a></th>
        <th><a href="javascript:sortBy('Asset', 'title','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="asset.titlecolumn"/></a></th>
        <th><a href="javascript:sortBy('Asset', 'creator','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="asset.authorcolumn"/></a></th>
        <th><a href="javascript:sortBy('Asset', 'lastmodifieddate','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="asset.lastmodifiedcolumn"/></a></th>
        <th><a href="javascript:sortBy('Asset', 'number','<mm:write referid="parentchannel" />')" class="headerlink">
        <fmt:message key="asset.numbercolumn"/></a></th>
        <th></th>
    </tr>
</thead>

<script src="../repository/asset.js" language="JavaScript" type="text/javascript"></script>

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
      <input type="checkbox"  name="chk_<mm:field name="number" />" value="<mm:field name="number" />" onClick="document.forms['contentForm'].elements.selectall.checked=false;"/>
      </c:if>
    <% } %>
     <a href="javascript:callEditWizard('<mm:field name="number" />');"
       title="<fmt:message key="asset.edit" />"><img src="../gfx/icons/edit.png" width="16" height="16"
                                                       title="<fmt:message key="asset.edit" />"
                                                       alt="<fmt:message key="asset.edit" />"/></a>
    <a href="<cmsc:contenturl number="${number}"/>" target="_blank"><img src="../gfx/icons/preview.png"
                                                                         alt="<fmt:message key="asset.preview.title" />"
                                                                         title="<fmt:message key="asset.preview.title" />"/></a>
    <a href="javascript:showInfo('<mm:nodeinfo type="guitype"/>', '<mm:field name="number" />');">
                              <img src="../gfx/icons/info.png" title="<fmt:message key="asset.info"/>" alt="<fmt:message key="asset.info"/>"/></a>
    <mm:haspage page="/editors/versioning">
        <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
            <c:param name="nodenumber"><mm:field name="number"/></c:param>
        </c:url>
        <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')"><img
                src="../gfx/icons/versioning.png" title="<fmt:message key="asset.icon.versioning.title" />"
                alt="<fmt:message key="asset.icon.versioning.title" />"/></a>
    </mm:haspage>
    <% if (role != null && SecurityUtil.isWriter(role)) { %>
    <a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');"
       title="<fmt:message key="asset.delete" />"><img src="../gfx/icons/delete.png" width="16" height="16"
                                                         title="<fmt:message key="asset.delete" />"
                                                         alt="<fmt:message key="asset.delete" />"/></a>
    <% } %>

    <cmsc:hasfeature name="savedformmodule">
        <c:set var="typeval">
            <mm:nodeinfo type="type"/>
        </c:set>
        <c:if test="${typeval == 'responseform'}">
            <mm:url page="/editors/savedform/ShowSavedForm.do" id="showSavedForms" write="false">
                <mm:param name="nodenumber"><mm:field name="number"/></mm:param>
                <mm:param name="initreturnurl" value="${returnurl}"/>
            </mm:url>
            <a href="<mm:write referid="showSavedForms"/>"><img src="../gfx/icons/application_form_magnify.png"
                                                                title="<fmt:message key="asset.icon.savedform.title" />"
                                                                alt="<fmt:message key="asset.icon.savedform.title" />"/></a>
        </c:if>
    </cmsc:hasfeature>
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
</td>
<c:if test="${hasWorkflow}">
    <td width="10" onMouseDown="objClick(this);">
        <c:set var="status" value="waiting"/>
        <mm:relatednodes type="workflowitem" constraints="type='asset'">
            <c:set var="status"><mm:field name="status"/></c:set>
        </mm:relatednodes>
        <c:if test="${status == 'waiting'}">
            <mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
                <c:set var="status" value="onlive"/>
            </mm:listnodes>
        </c:if>
        <img src="../gfx/icons/status_${status}.png"
             alt="<fmt:message key="asset.status" />: <fmt:message key="asset.status.${status}" />"
             title="<fmt:message key="asset.status" />: <fmt:message key="asset.status.${status}" />"/>
    </td>
</c:if>
</tr>
</mm:listnodes>
</tbody>
</table>
<% if (role != null && SecurityUtil.isWriter(role)) { %>
<c:if test="${fn:length(elements) >1}">
<input type="submit" class="button" value="<fmt:message key="asset.delete.massdelete" />"/>
</c:if>
<% } %>
</form>
<%@ include file="../pages.jsp" %>
</div>
</div>
</mm:node>
</mm:cloud>
</mm:content>