<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="com.finalist.cmsc.repository.AssetElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@ page import="com.finalist.cmsc.security.UserRole"
%><%@ page import="com.finalist.cmsc.security.SecurityUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">

<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="returnurl"/>
<mm:import externid="parentchannel" jspvar="parentchannel"/>
<mm:import externid="assettypes" jspvar="assettypes"><%= AssetElementUtil.ASSETELEMENT %></mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>

<cmscedit:head title="search.title">
      <link rel="stylesheet" href="<cmsc:staticurl page='../css/thumbnail.css'/>" type="text/css">
      <script src="../repository/asset.js" language="JavaScript" type="text/javascript"></script>
      <script src="search.js" type="text/javascript"></script>
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
                   if(offset==null){offset=0;}
                   var assetsMode = document.getElementsByTagName("option");3
                   for(i = 0; i < assetsMode.length; i++){
                      if(assetsMode[i].selected & assetsMode[i].id=="a_list"){
                          document.forms[0].searchShow.value = 'list';
                          document.forms[0].submit();
                          //document.location.href = 'AssetSearchAction.do?type=asset&direction=down&searchShow=list&offset='+offset;
                      }else if(assetsMode[i].selected & assetsMode[i].id=="a_thumbnail"){
                          document.forms[0].searchShow.value = 'thumbnail';
                          document.forms[0].submit();
                         //document.location.href = 'AssetSearchAction.do?type=asset&direction=down&searchShow=thumbnail&offset='+offset;
                      }
                   }
                }
               function selectElement(element, title, src) {
                   if(window.top.opener != undefined) {
                      window.top.opener.selectElement(element, title, src);
                      window.top.close();
                   }
                }
            </script>
    <c:if test="${not empty requestScope.refreshChannels}">
        <script>
        refreshFrame('channels');
        </script>
    </c:if>
</cmscedit:head>
<body>
<mm:import id="assetsearchinit"><c:url value='/editors/repository/AssetSearchInitAction.do'/></mm:import>

<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<c:if test="${empty strict}">
   <div class="tabs">
    <!-- active TAB -->
 <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="asset.search.title" /></a>
         </div>
      </div>
   </div>
    <div class="tab">
        <div class="body">
            <div>
               <a href="../resources/attachmentupload.jsp?uploadAction=${action}&parentchannel=${parentchannel}&insertAsset=insertAsset" ><fmt:message key="asset.upload.title" /></a>
            </div>
        </div>
    </div>
</c:if>
</div>
   <div class="editor">
   <br />
      <div class="body">
         <html:form action="/editors/repository/InsertAssetSearchAction?insertAsset=insertAsset" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="mode"/>
            <html:hidden property="search" value="true"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="searchShow" value="${searchShow}"/>
            <html:hidden property="insertAsset" value="${insertAsset}"/>
            <html:hidden property="direction"/>
            <input type="hidden" name="deleteAssetRequest"/>
            <c:if test="${not empty strict}">
               <input type="hidden" name="assettypes" value="${strict}"/>
               <input type="hidden" name="strict" value="${strict}"/>
            </c:if>
            <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
            <table>
               <tr>
                  <td style="width:105px"><fmt:message key="searchform.title" /></td>
                  <td colspan="5"><html:text property="title" style="width:200px"/></td>
               </tr>
               <tr>
                     <td></td>
                     <td>
                        <input type="submit" class="button" name="submitButton" onclick="setOffset(0);" value="<fmt:message key="searchform.submit" />"/>
                     </td>
               </tr>
            </table>
         </html:form>
      </div>
   </div>

   <div class="editor" style="height:500px">
   <div class="ruler_green"><div><fmt:message key="searchform.results" /></div></div>

   <div class="body">
   <div style="padding-left:11px">
      <select name="assesMode" onchange="javascript:changeMode(${param.offset})">
         <c:if test="${empty searchShow || searchShow eq 'list'}">
            <option id="a_list" selected="selected">list</option>
            <option id = "a_thumbnail" >thumbnail</option>
         </c:if>
         <c:if test="${searchShow eq 'thumbnail'}">
            <option id="a_list">list</option>
            <option id = "a_thumbnail" selected="selected" >thumbnail</option>
         </c:if>
      </select>
   </div>

<!-- we check to see if we have workflow, this is done by looking if the editors for the workflow are on the HD -->
<c:set var="hasWorkflow" value="false"/>
<mm:haspage page="/editors/workflow">
   <c:set var="hasWorkflow" value="true"/>
</mm:haspage>

   <%-- Now print if no results --%>
   <mm:isempty referid="results">
      <fmt:message key="searchform.searchpages.nonefound" />
   </mm:isempty>

   <%-- Now print the results --%>
   <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
      <mm:field id="trashnumber" name="number" write="false"/>
   </mm:node>

<c:if test="${searchShow eq 'list'}">
   <mm:list referid="results">
      <mm:first>
         <%@include file="../repository/searchpages.jsp" %>
            <mm:hasrank minvalue="siteadmin">
               <c:if test="${fn:length(results) >1}">
               <div align="left">
                  <input type="submit" class="button" name="massdelete" 
                        onclick="javascript:deleteAsset('massdelete','<fmt:message key="recyclebin.massremoveconfirm"/>')"
                        value="<fmt:message key="asset.delete.massdelete" />"/>
               </div>
               </c:if>
            </mm:hasrank>
         <form action="" name="linkForm" method="post">
            <table>
            <thead>
               <tr>
                  <th>
                     <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
                     <c:if test="${fn:length(results) >1}">
                        <input type="checkbox" onclick="selectAll(this.checked, 'linkForm', 'chk_');" value="on" name="selectall" />
                     </c:if>
                  </th>
                  <th><a href="javascript:orderBy('otype')" class="headerlink" ><fmt:message key="locate.typecolumn" /></a></th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a></th>
                  <th><fmt:message key="locate.creationchannelcolumn" /></th>
                  <th><a href="javascript:orderBy('creator')" class="headerlink" ><fmt:message key="locate.authorcolumn" /></th>
                  <th><a href="javascript:orderBy('lastmodifieddate')" class="headerlink" ><fmt:message key="locate.lastmodifiedcolumn" /></th>
                  <th><a href="javascript:orderBy('number')" class="headerlink" ><fmt:message key="locate.numbercolumn" /></th>
               </tr>
            </thead>

            <tbody class="hover">
      </mm:first>

   <mm:field name="${assettypes}.number" id="number" write="false">
      <mm:node number="${number}">
         <c:set var="useSwapStyle">true</c:set>
         <mm:relatednodes role="creationrel" type="contentchannel">
            <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
            <mm:compare referid="trashnumber" referid2="creationnumber">
                <c:set var="channelName"><fmt:message key="search.trash" /></c:set>
                <c:set var="channelIcon" value="/editors/gfx/icons/trashbin.png"/>
                <c:set var="channelIconMessage"><fmt:message key="search.trash" /></c:set>
                <c:set var="channelUrl" value="../recyclebin/assettrash.jsp"/>
            </mm:compare>
            <mm:field name="number" jspvar="channelNumber" write="false"/>
            <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
            <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
                <mm:field name="name" jspvar="channelName" write="false"/>
                <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
                <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
                <c:set var="channelUrl" value="Asset.do?type=asset&parentchannel=${channelNumber}&direction=down"/>
            </mm:compare>
         </mm:relatednodes>
<c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
<c:if test="${assettype == 'attachments'}">
         <mm:import id="url">
            javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>',
                                       '<mm:attachment escape="js-single-quotes"/>');
         </mm:import>
</c:if>
         <tr <mm:even inverse="true">class="swap"</mm:even> href="<mm:write referid="url"/>">
            <td style="white-space: nowrap;">
                  <c:if test="${creationRelNumber == trashnumber && rights == 'webmaster' && fn:length(results) >1}">
                      <input type="checkbox" value="permanentDelete:<mm:field name="number" />" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
                  </c:if>
                  <c:if test="${creationRelNumber != trashnumber && (rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster') && fn:length(results) >1}">
                    <input type="checkbox" value="moveToRecyclebin:<mm:field name="number" />" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
                  </c:if>
               <%@ include file="../repository/searchIconsBar.jspf" %>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
               <mm:nodeinfo type="guitype"/>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
               <c:set var="assettype" ><mm:nodeinfo type="type"/></c:set>
               <mm:field id="title" write="false" name="title"/>
               <c:if test="${assettype == 'urls'}">
                  <c:set var="title" ><mm:field name="name"/></c:set>
               </c:if>
               <c:if test="${fn:length(title) > 50}">
                  <c:set var="title">${fn:substring(title,0,49)}...</c:set>
               </c:if>
               ${title}
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);">
                <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                  <mm:compare referid="action" value="search">
                     <a href="${channelUrl}">${channelName}</a>
                  </mm:compare>
                  <mm:compare referid="action" value="search" inverse="true">
                     ${channelName}
                  </mm:compare>
            </td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="creator" /></td>
            <td style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
            <td  style="white-space: nowrap;" onMouseDown="objClick(this);"><mm:field name="number"/></td>
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
      </mm:node>
   </mm:field>
   <mm:last>
   </tbody>
   </table>
   </form>
   <mm:hasrank minvalue="siteadmin">
      <c:if test="${fn:length(results) >1}">
      <div align="left">
         <input type="submit" class="button" name="massdelete" 
               onclick="javascript:deleteAsset('massdelete','<fmt:message key="recyclebin.massremoveconfirm"/>')"
               value="<fmt:message key="asset.delete.massdelete" />"/>
      </div>
      </c:if>
   </mm:hasrank>
         <%@include file="../repository/searchpages.jsp" %>
   </mm:last>
   </mm:list>
</c:if>

<c:if test="${searchShow eq 'thumbnail'}">
   <mm:list referid="results">
      <mm:first>
         <%@include file="../repository/searchpages.jsp" %>
      </mm:first>

   <mm:field name="${assettypes}.number" id="number" write="false">
      <mm:node number="${number}">

         <mm:relatednodes role="creationrel" type="contentchannel">
            <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
            <mm:field name="number" jspvar="channelNumber" write="false"/>
            <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
         </mm:relatednodes>

         <div class="thumbnail_show" onMouseOut="javascript:hideEditItems(<mm:field name='number'/>)" onMouseOver="showEditItems(<mm:field name='number'/>)">
            <div class="thumbnail_operation">
               <div class="asset-info" id="asset-info-<mm:field name='number'/>" style="display: none; position: relative; border: 1px solid #eaedff" >
               <%@ include file="../repository/searchIconsBar.jspf" %>
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
                              <c:if test="${assettype == 'urls'}">
                                 <c:set var="title" ><mm:field name="name"/></c:set>
                              </c:if>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <c:if test="${ assettype == 'images'}">
                              <br/><mm:field name="itype" />
                              </c:if>
               </div>
            </div>
            </div>
      </mm:node>
   </mm:field>
   </mm:list>
   <div style="clear:both;"></div>
         <%@include file="../repository/searchpages.jsp" %>
</c:if>
</div>
   </div>
</mm:cloud>

   </body>
</html:html>
</mm:content>