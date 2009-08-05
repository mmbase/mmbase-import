<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.cmsc.navigation.*" %>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="stacked.title">
   <script src="content.js" type="text/javascript"></script>
   <script src="search.js" type="text/javascript"></script>
   <c:if test="${not empty requestScope.refreshChannels}">
      <script type="text/javascript">
         refreshFrame('pages');
      </script>
   </c:if>
</cmscedit:head>
<body>

<c:set var="hasWorkflow" value="false"/>
<mm:haspage page="/editors/workflow">
    <c:set var="hasWorkflow" value="true"/>
</mm:haspage>

<cmsc:rights nodeNumber="${param.parent}" var="rights"/>
<c:set var="orderby" value="${(param.orderby == null)?'title':param.orderby}"/>
<c:set var="orderdir" value="${(param.orderdir == null)?'up':param.orderdir}"/>
<c:set var="hasRights" value="${(rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster')}"/>

<div class="tabs">
    <!-- active TAB -->
    <div class="tab_active">
        <div class="body">
            <div>
                <a name="activetab"><fmt:message key="stacked.title" /></a>
            </div>
        </div>
    </div>
</div>
<div class="editor">
   <div class="body">
   <br />
	<mm:cloud>
		<c:if test="${hasRights}">
			<img src="../gfx/icons/new.png" alt="<fmt:message key="stacked.icon.new" />" title="<fmt:message key="stacked.icon.new" />" />
			<a href="AliasCreate.do?parentpage=${param.parent}&stacked=true"><fmt:message key="stacked.new.link" /></a>
		</c:if>
		
		<mm:node number="${param.parent}">
			<mm:relatednodes type="pagealias" role="navrel" orderby="${orderby}" directions="${orderdir}" searchdir="destination">
		      <mm:first>
		          <table>
		            <thead>
		               <tr>
		                  <th width="76"></th>
		                  <th><a href="?parent=${param.parent}&orderby=title&orderdir=${(orderby == 'title' && orderdir == 'up')?'down':'up'}"><fmt:message key="stacked.header.title" /></a></th>
		                  <th><a href="?parent=${param.parent}&orderby=urlfragment&orderdir=${(orderby == 'urlfragment' && orderdir == 'up')?'down':'up'}" class="headerlink" onclick="orderBy('urlfragment');" ><fmt:message key="stacked.header.urlfragment" /></a></th>
		                  <th><fmt:message key="stacked.header.target"/></th>
		               </tr>
		            </thead>
		            <tbody class="hover">
		      </mm:first>

				<c:set var="targetUrl" value=""/>
				<mm:relatednodes type="urls" role="related" searchdir="destination" >
					<c:set var="targetName"><mm:field name="name"/></c:set>
					<c:set var="targetUrl"><mm:field name="url"/></c:set>
					<c:set var="targetType" value="url"/>
					<c:set var="targetIcon" value="urls.png"/>
				</mm:relatednodes>
				<mm:relatednodes type="page" role="related" searchdir="destination" jspvar="node">
					<mm:import id="pagepath">../../<%= NavigationUtil.getPathToRootString(node, !ServerUtil.useServerName()) %></mm:import>
					<c:set var="targetName"><mm:field name="title"/></c:set>
					<c:set var="targetUrl"><mm:write referid="pagepath"/></c:set>
					<c:set var="targetType" value="page"/>
					<c:set var="targetIcon" value="type/page_none.png"/>
				</mm:relatednodes>	
      
      			<tr <mm:even inverse="true">class="swap"</mm:even>>
      				<td>
						<c:if test="${hasRights}">
      					<a href="AliasEdit.do?number=<mm:field name="number"/>&stacked=true&parentpage=${param.parent}">
		                   <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="stacked.icon.edit" />" title="<fmt:message key="stacked.icon.edit" />" /></a>
      					<a href="AliasDelete.do?number=<mm:field name="number"/>&stacked=true&parentpage=${param.parent}">
							<img src="../gfx/icons/delete.png" title="<fmt:message key="stacked.icon.delete" />" alt="<fmt:message key="stacked.icon.delete" />"/></a>
                     <c:if test="${hasWorkflow}">
                           <a href="../workflow/publish.jsp?number=<mm:field name="number"/>">
                           <img src="../gfx/icons/publish.png" title="<fmt:message key="stacked.icon.publish" />" alt="<fmt:message key="stacked.icon.publish" />"/></a>
                     </c:if>
						</c:if>
						<c:if test="${!empty targetUrl}">
		      				<a href="${targetUrl}">
								<img src="../gfx/icons/preview.png" title="<fmt:message key="stacked.icon.view" />" alt="<fmt:message key="stacked.icon.view" />"/></a>
						</c:if>
					</td>
					<td>
						<mm:field name="title"/>
					</td>
					<td>
						<mm:field name="urlfragment"/>
					</td>
					<td>
						<c:if test="${!empty targetUrl}">
							<img src="../gfx/icons/${targetIcon}" title="<fmt:message key="stacked.type.${targetType}" />" alt="<fmt:message key="stacked.type.${targetType}" />"/>
							${targetName} (${targetUrl})
						</c:if>
					</td>
					<%-- Turned off, because the workflow on Aliases don't work yet! Jj and Nico. 
					<c:if test="${hasWorkflow}">
					    <td width="10" onMouseDown="objClick(this);">
					        <c:set var="status" value="waiting"/>
					        <mm:relatednodes type="workflowitem" role="workflowrel" searchdir="source">
					            <c:set var="status"><mm:field name="status"/></c:set>
					        </mm:relatednodes>
					        <c:if test="${status == 'waiting'}">
					        	<c:set var="number"><mm:field name="number"/></c:set>
					            <mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
					                <c:set var="status" value="onlive"/>
					            </mm:listnodes>
					        </c:if>
					        <img src="../gfx/icons/status_${status}.png"
					             alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />"
					             title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />"/>
					    </td>
					</c:if>
					--%>
				</tr>				
		      <mm:last>
		            </tbody>
		         </table>
		      </mm:last>
			</mm:relatednodes>
		</mm:node>
	</mm:cloud>
	
	</div>	
</div>
   </body>
 
</html:html>
</mm:content>