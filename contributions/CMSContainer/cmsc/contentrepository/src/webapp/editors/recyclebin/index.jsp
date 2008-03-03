<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="recyclebin.title">
  <script src="recyclebin.js" type="text/javascript"></script>
</cmscedit:head>
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
		<mm:cloud jspvar="cloud" rank="basic user" method='http'>
               
         <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
            <mm:field name="number" jspvar="trashNumber" vartype="Integer">
            
               <cmsc:rights nodeNumber="<%=trashNumber.intValue()%>" var="rolename"/>
               <c:choose>
                  <c:when test="${rolename eq 'webmaster'}">

                     <mm:import id="parentchannel" jspvar="parentchannel"><%= RepositoryUtil.ALIAS_TRASH %></mm:import>
                     <mm:import jspvar="returnurl" id="returnurl">/editors/recyclebin/index.jsp</mm:import>
                     
                     <div class="body">
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
                        
                     <div class="ruler_green">
                         <div><fmt:message key="recyclebin.content" /></div>
                     </div>
               
                     <div class="body">   
                        <mm:node number="$parentchannel">
                           <mm:relatednodescontainer path="contentrel,contentelement" searchdirs="destination" element="contentelement">
                              <mm:sortorder field="contentelement.title" direction="up" />
            
                              <c:set var="listSize"><mm:size/></c:set>
                              <c:set var="resultsPerPage" value="50"/>
                              <c:set var="offset" value="${not empty param.offset ? param.offset : '0'}"/>
                              
                              <mm:listnodes jspvar="node" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
                                 <mm:first>
                                    <%@include file="../pages.jsp" %>
                                     <table>
                                       <thead>
                                          <tr>
                                             <th style="width: 56px;"></th>
                                             <th style="width: 68px;"><fmt:message key="locate.typecolumn" /></th>
                                             <th><fmt:message key="locate.titlecolumn" /></th>
                                             <th style="width: 50px;"><fmt:message key="locate.authorcolumn" /></th>
                                             <th style="width: 120px;"><fmt:message key="locate.lastmodifiedcolumn" /></th>
                                             <th style="width: 60px;"><fmt:message key="locate.numbercolumn" /></th>
                                          </tr>
                                       </thead>
                                       <tbody class="hover">
                                 </mm:first>
                           
                                 <tr <mm:even inverse="true">class="swap"</mm:even>>
                                    <td nowrap>
                                    <a href="javascript:info('<mm:field name="number" />')"><img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="recyclebin.info" />" title="<fmt:message key="recyclebin.info" />"/></a>
                                    <a href="javascript:permanentDelete('<mm:field name="number" />', '<fmt:message key="recyclebin.removeconfirm" />', '${offset}');"><img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="recyclebin.remove" />" title="<fmt:message key="recyclebin.remove" />"/></a>
                                   <% if (RepositoryUtil.hasDeletionChannels(node)) { %>
                                       <a href="javascript:restore('<mm:field name="number" />', '${offset}');"><img src="../gfx/icons/restore.png" width="16" height="16" alt="<fmt:message key="recyclebin.restore" />" title="<fmt:message key="recyclebin.restore" />"/></a>
                                     <% } %>
                                    </td>
                                       <td>
                                         <mm:nodeinfo type="guitype"/>
                                       </td>
                                       <td><mm:field name="title"/></td>
                                       <td><mm:field name="lastmodifier" /></td>
                                    <td nowrap><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
                                    <td><mm:field name="number"/></td>
                                 </tr>
                           
                                 <mm:last>
                                       </tbody>
                                    </table>
                                    <%@include file="../pages.jsp" %>
                                 </mm:last>
                             </mm:listnodes>
                           </mm:relatednodescontainer>
                        </mm:node>

                     </div>

                  </c:when>
                  <c:otherwise>
                     <div class="body">
                        <fmt:message key="recyclebin.no.access" />
                     </div>

                  </c:otherwise>
               </c:choose>
               
            </mm:field>
         </mm:node>

      </mm:cloud>
		<div class="side_block_end"></div>
	</div>	

</body>
</html:html>
</mm:content>