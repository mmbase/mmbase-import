<%@ page language="java" contentType="text/html;charset=utf-8"
%><%@ include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.repository.RepositoryUtil"
%><%@ page import="com.finalist.cmsc.security.*"
%><mm:content type="text/html" encoding="UTF-8" expires="0">

   <mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
      <html:html xhtml="true">
         <mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>
         <mm:import jspvar="returnurl" id="returnurl">/editors/repository/Asset.do?type=asset&parentchannel=<mm:write
         referid="parentchannel"/>&direction=up</mm:import>

         <cmscedit:head title="images.title">
            <link rel="stylesheet" href="<cmsc:staticurl page='../css/thumbnail.css'/>" type="text/css">
            <script type="text/javascript">
               function showEditItems(id){
                  document.getElementById('asset-info-'+id).style.display = 'block';
                  document.getElementById('asset-info-'+id).style.zIndex = 2001;
               }
               function hideEditItems(id){
                  document.getElementById('asset-info-'+id).style.display = 'none';
                  document.getElementById('asset-info-'+id).style.zIndex = 2000;
               }
               function changeMode(offset){
                  if(offset==null){
                      offset=0;
                  }
                  var assetsMode = document.getElementsByTagName("option");
                  for(i = 0; i < assetsMode.length; i++){
                     if(assetsMode[i].selected & assetsMode[i].id=="a_list"){
                         document.location.href = 'Asset.do?type=asset&parentchannel=<mm:write referid="parentchannel"/>&direction=up&show=list&offset='+offset+'&imageOnly=no';
                     }else if(assetsMode[i].selected & assetsMode[i].id=="a_thumbnail"){
                            document.location.href = 'Asset.do?type=asset&parentchannel=<mm:write referid="parentchannel"/>&direction=up&show=thumbnail&offset='+offset;
                     }
                  }
               }
               function showImageOnly(){
                   var offset=0;
                   var assetsMode = document.getElementById("assetMode");
                   assetsMode.selectedIndex=1;
                   if(document.getElementById("chk_showImageOnly").checked == true){
                     document.location.href = 'Asset.do?type=asset&parentchannel=<mm:write referid="parentchannel"/>&direction=up&show=thumbnail&offset='+offset+'&imageOnly=yes'
                  }else{
                     document.location.href = 'Asset.do?type=asset&parentchannel=<mm:write referid="parentchannel"/>&direction=up&show=thumbnail&offset='+offset+'&imageOnly=no';
                   }
               }
            </script>
            <script src="../repository/asset.js" language="JavaScript" type="text/javascript"></script>
         </cmscedit:head>

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
               <%@ include file="assetupload.jsp" %>
                  <ul class="shortcuts">
                     <li class="new" style="text-decoration: none;"><fmt:message key="asset.new"/>
                        <form name="initForm" action="../WizardInitAction.do" method="post" style="display:inline;text-decoration:none">
                           <input type="hidden" name="action" value="create"/>
                           <input type="hidden" name="creation" value="<mm:write referid="parentchannel" />"/>
                           <input type="hidden" name="returnurl" value="<%= returnurl %>"/>
                           <input type="hidden" name="order" value="${orderby}" />
                           <input type="hidden" name="direction" value="${direction}"/>
                           <input type="hidden" name="offset" value="${param.offset}"/>
                           <input type="hidden" name="assettype" value="urls"/>
                           <input type="submit" name="submitButton" value="<fmt:message key="asset.create" />" class="button"/>
                        </form>
                     </li>
                  </ul>
               <% } %>
            </div>

            <div class="ruler_green">
               <div>
                  <fmt:message key="asset.asset">
                     <fmt:param><mm:field name="path"/></fmt:param>
                  </fmt:message>
               </div>
            </div>

            <div class="body" >
               <div style="padding-left:11px">
                  <select name="assesMode" id="assetMode" onchange="javascript:changeMode(${param.offset})">
                     <c:if test="${show eq 'list'}">
                        <option id="a_list" selected="selected"><fmt:message key="asset.image.list"/></option>
                        <option id = "a_thumbnail" ><fmt:message key="asset.image.thumbnail"/></option>
                     </c:if>
                     <c:if test="${show eq 'thumbnail'}">
                        <option id="a_list"><fmt:message key="asset.image.list"/></option>
                        <option id = "a_thumbnail" selected="selected" ><fmt:message key="asset.image.thumbnail"/></option>
                     </c:if>
                  </select>
                  <div style="padding-left:100px;display:inline;font-size:12px;">
                     <c:if test="${show eq 'thumbnail'}">
                        <input type="checkbox" name="showImageOnly" class="checkbox" id="chk_showImageOnly" <c:if test="${imageOnly eq 'yes'}">checked="checked"</c:if> onclick="javascript:showImageOnly()"/><fmt:message key="asset.image.show"/>
                     </c:if>
                  </div>
               </div>

            <mm:import externid="elements" from="request" required="true"/>
            <mm:import externid="elementCount" from="request" vartype="Integer">0</mm:import>
            <mm:import externid="resultsPerPage" from="request" vartype="Integer">25</mm:import>
            <c:set var="listSize" value="${elementCount}"/>
            <c:set var="offset" value="${param.offset}"/>
            <c:set var="extraparams" value="&direction=${param.direction}&parentchannel=${param.parentchannel}&show=${show}"/>
            <c:set var="orderby" value="${param.orderby}" scope="page" />
            <c:set var="type" value="asset" scope="page" />

            <%@ include file="../pages.jsp" %>

            <c:if test="${show eq 'list'}">
               <form action="AssetMassDeleteAction.do" name="assetForm">
                  <input type="hidden" name="offset" value="${param.offset}"/>
                  <input type="hidden" name="orderby" value="${orderby}" />
                  <input type="hidden" name="direction" value="${direction}"/>
                  <input type="hidden" name="channelnumber" value="<mm:write referid="parentchannel" />"/>
                  <% if (role != null && SecurityUtil.isWriter(role)) { %>
                     <c:if test="${fn:length(elements) >1}">
                        <input type="button" class="button" value="<fmt:message key="asset.delete.massdelete" />"
                              onclick="massDelete('<fmt:message key="asset.delete.massdeleteconfirm" />', 'assetForm')"/>
                        <input type="button" class="button" value="<fmt:message key="content.delete.massmove" />" 
                              onclick="massMove('${parentchannel}','<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
                     </c:if>
                  <% } %>
                  <table>
                     <thead>
                        <tr>
                           <th><% if (role != null && SecurityUtil.isWriter(role)) { %>
                                 <c:if test="${fn:length(elements) >1}">
                                  <input type="checkbox"  name="selectall" class="checkbox"  onclick="selectAll(this.checked, 'assetForm', 'chk_');" value="on"/></c:if> <% } %></th>
                           <th><a href="javascript:sortBy('Asset', 'otype','<mm:write referid="parentchannel" />')" class="headerlink">
                                 <fmt:message key="asset.typecolumn"/></a></th>
                           <th><a href="javascript:sortBy('Asset', 'title','<mm:write referid="parentchannel" />')" class="headerlink">
                                 <fmt:message key="asset.titlecolumn"/></a></th>
                           <th><a href="javascript:sortBy('Asset', 'lastmodifier','<mm:write referid="parentchannel" />')" class="headerlink">
                                 <fmt:message key="asset.authorcolumn"/></a> </th>
                           <th><a href="javascript:sortBy('Asset', 'lastmodifieddate','<mm:write referid="parentchannel" />')" class="headerlink">
                                 <fmt:message key="asset.lastmodifiedcolumn"/></a></th>
                           <th><a href="javascript:sortBy('Asset', 'number','<mm:write referid="parentchannel" />')" class="headerlink">
                                 <fmt:message key="asset.numbercolumn"/></a></th>
                           <th>&nbsp;</th>
                        </tr>
                     </thead>

                     <tbody class="hover">
                        <mm:listnodes referid="elements" jspvar="node">
                           <mm:field name="number" write="false" id="number"vartype="String"/>
                           <mm:field name="number" write="false" id="relnumber"/>

                           <mm:url page="../WizardInitAction.do" id="url" write="false">
                              <mm:param name="objectnumber" value="$number"/>
                              <mm:param name="returnurl" value="$returnurl"/>
                           </mm:url>
                           <tr <mm:even inverse="true">class="swap"</mm:even> href="<mm:write referid="url"/>">
                           <td style="white-space: nowrap;">
                              <% if (role != null && SecurityUtil.isWriter(role)) { %>
                                 <c:if test="${fn:length(elements) >1}">
                                    <input type="checkbox"  name="chk_<mm:field name="number" />" class="checkbox" value="<mm:field name="number" />" onClick="document.forms['contentForm'].elements.selectall.checked=false;"/>
                                 </c:if>
                              <% } %>
                              <%@ include file="icons.jspf"%>
                           </td>
                           <td onMouseDown="objClick(this);">
                              <mm:nodeinfo type="guitype"/></td>
                           <td onMouseDown="objClick(this);">
                           <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 50}">
                                 <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                              </c:if>${title}</td>
                           <td onMouseDown="objClick(this);" style="white-space: nowrap;">
                              <mm:field name="lastmodifier" id="lastmodifier" write="false"/>
                              <mm:listnodes type="user" constraints="username = '${lastmodifier}'">
                                 <c:set var="lastmodifierFull"><mm:field name="firstname"/> <mm:field name="prefix"/> <mm:field
                                 name="surname"/></c:set>
                                 <c:if test="${lastmodifierFull != ''}"><c:set var="lastmodifier" value="${lastmodifierFull}"/></c:if>
                              </mm:listnodes>
                              ${lastmodifier}</td>
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
                     <input type="button" class="button" value="<fmt:message key="content.delete.massmove" />" 
                           onclick="massMove('${parentchannel}','<c:url value='/editors/repository/select/SelectorChannel.do?role=writer' />')"/>
                  </c:if>
                  <% } %>
               </form>
            </c:if>

            <c:if test="${show eq 'thumbnail'}">
               <c:if test="${imageOnly eq 'no'}">
                  <c:set var="extraparams" value="&direction=${param.direction}&parentchannel=${param.parentchannel}&show=thumbnail&imageOnly=no"/>
               </c:if>
               <c:if test="${imageOnly eq 'yes'}">
                  <c:set var="extraparams" value="&direction=${param.direction}&parentchannel=${param.parentchannel}&show=thumbnail&imageOnly=yes"/>
               </c:if>
               <div width="100%;float:left;">
                  <mm:listnodes referid="elements">
                     <div class="thumbnail_show" onMouseOut="javascript:hideEditItems(<mm:field name='number'/>)" onMouseOver="showEditItems(<mm:field name='number'/>)">
                        <div class="thumbnail_operation">
                           <div class="asset-info" id="asset-info-<mm:field name='number'/>" style="display: none; position: relative; border: 1px solid #eaedff" >
                              <%@ include file="icons.jspf" %>
                           </div>
                        </div>
                        <div class="thumbnail_body" >
                           <div class="thumbnail_img" onMouseOver="this.style.background = 'yellow';" onMouseOut="this.style.background = 'white';">
                              <a href="javascript:showInfo('<mm:nodeinfo type="type"/>', '<mm:field name="number" />')">
                                 <c:set var="typedef" ><mm:nodeinfo type="type"/></c:set>
                                 <c:if test="${typedef eq 'images'}">
                                    <img src="<mm:image template="s(120x100)"/>" alt=""/>
                                 </c:if> 
                                 <c:if test="${typedef eq 'attachments'}">
                                    <c:set var="filename"><mm:field name="filename"/></c:set>
                                    <c:set var="subfix">${fn:substringAfter(filename, '.')}</c:set>
                                    <mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="false">
                                       <img src="../gfx/${subfix}${'.gif'}" alt=""/>
                                    </mm:haspage> 
                                    <mm:haspage page="../gfx/${subfix}${'.gif'}" inverse="true">
                                       <img src="../gfx/otherAttach.gif" alt=""/>
                                    </mm:haspage>
                                 </c:if>
                                 <c:if test="${typedef eq 'urls'}">
                                    <img src="../gfx/url.gif" alt=""/>
                                 </c:if>
                              </a>
                           </div>
                           <div class="thumnail_info">
                              <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <c:if test="${ assettype == 'images'}">
                              <br/><mm:field name="itype" />
                              </c:if>
                           </div>
                        </div>
                     </div>
                  </mm:listnodes>
                  <div style="clear:both;"></div>
               </div>
         </c:if>
         <%@ include file="../pages.jsp" %>
         </div>
      </mm:node>
   </div>

      </html:html>
   </mm:cloud>
</mm:content>