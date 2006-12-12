<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="recyclebin.title" /></title>
  <link href="../css/main.css" type="text/css" rel="stylesheet" />
  <script src="recyclebin.js" type="text/javascript"></script>
	<script type="text/javascript" src="../utils/window.js"></script>
	<script type="text/javascript">
		function refreshChannels() {
			refreshFrame('channels');
			if (window.opener) {
				window.close();
			}
		}
	</script>
</head>
<body onload="refreshChannels();">
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="recyclebin.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
	<div class="body"
		<mm:cloud jspvar="cloud" rank="administrator" method='http'>
			<mm:import id="parentchannel" jspvar="parentchannel"><%= RepositoryUtil.ALIAS_TRASH %></mm:import>
			<mm:import jspvar="returnurl" id="returnurl">/editors/recyclebin/index.jsp</mm:import>
                <p>
                    <fmt:message key="recyclebin.channel" />
                </p>
				<form name="deleteForm" action="DeleteAction.do" method="post">
					<input type="hidden" name="action" value="deleteall" />
					<ul class="shortcuts">
		            	<li class="trashbinempty">
							<a href="javascript:deleteAll('<fmt:message key="recyclebin.removeallconfirm" />');"><fmt:message key="recyclebin.clear" /></a>
						</li>
					</ul>
				</form>
				<div style="clear:both; height:10px;"></div>
	</div>
                <div class="ruler_green"><div><fmt:message key="recyclebin.content" /></div></div>
   
	     		<div class="body">	
				<mm:node number="$parentchannel">
					<mm:relatednodescontainer path="contentrel,contentelement" searchdirs="destination" element="contentelement">
						<mm:sortorder field="contentelement.title" direction="up" />

						<c:set var="listSize"><mm:size/></c:set>
						<c:set var="resultsPerPage" value="50"/>
						<c:set var="offset" value="${param.offset}"/>
						
						<mm:listnodes jspvar="node" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
					      <mm:first>
 					         <%@include file="../pages.jsp" %>
					          <table>
					            <thead>
					               <tr>
					                  <th>
					                  </th>
					                  <th><fmt:message key="locate.typecolumn" /></th>
					                  <th><fmt:message key="locate.titlecolumn" /></th>
					                  <th><fmt:message key="locate.authorcolumn" /></th>
					                  <th><fmt:message key="locate.lastmodifiedcolumn" /></th>
					                  <th><fmt:message key="locate.numbercolumn" /></th>
					               </tr>
					            </thead>
					            <tbody class="hover">
					      </mm:first>
					
					      <tr <mm:even inverse="true">class="swap"</mm:even>>
					         <td nowrap width="80">
					        	<a href="javascript:info('<mm:field name="number" />')"><img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="recyclebin.info" />"/></a>
					        	<a href="javascript:permanentDelete('<mm:field name="number" />', '<fmt:message key="recyclebin.removeconfirm" />', '${offset}');"><img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="recyclebin.remove" />"/></a>
							  <% if (RepositoryUtil.hasDeletionChannels(node)) { %>
						      	<a href="javascript:restore('<mm:field name="number" />', '${offset}');"><img src="../gfx/icons/restore.png" width="16" height="16" alt="<fmt:message key="recyclebin.restore" />"/></a>
					          <% } %>
					         </td>
				               <td>
				            	  <mm:nodeinfo type="guitype"/>
				               </td>
				               <td><mm:field name="title"/></td>
				               <td width="50"><mm:field name="lastmodifier" /></td>
					         <td width="120" nowrap><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
					         <td width="60"><mm:field name="number"/></td>
					      </tr>
					
					      <mm:last>
					            </tbody>
					         </table>
 					         <%@include file="../pages.jsp" %>
					      </mm:last>
					  </mm:listnodes>
					</mm:relatednodescontainer>
				</mm:node>

			</mm:cloud>

		</div>
		<div class="side_block_end"></div>
	</div>	

</body>
</html:html>
</mm:content>