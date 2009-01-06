<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@ page import="com.finalist.cmsc.security.UserRole" 
%><%@ page import="com.finalist.cmsc.security.SecurityUtil" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="search.title">
      <script src="content.js" type="text/javascript"></script>
      <script src="search.js" type="text/javascript"></script>
    <c:if test="${not empty requestScope.refreshChannels}">
        <script>
        refreshFrame('channels');
        </script>
    </c:if>
</cmscedit:head>
<body>
<mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="returnurl"/>
<mm:import externid="linktochannel"/>
<mm:import externid="parentchannel" jspvar="parentchannel"/>
<mm:import externid="contenttypes" jspvar="contenttypes"><%= ContentElementUtil.CONTENTELEMENT %></mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
<c:set var="returnurl" value="${fn:replace(returnurl,'&amp;','&')}"/>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
   <div class="tabs">
<c:if test="${param.index eq 'yes'}">
    <!-- active TAB -->
    <div class="${(contenttypes == 'contentelement' || contenttypes == null)?'tab_active':'tab'}">
        <div class="body">
            <div>
                <a href="SearchInitAction.do?index=yes"><fmt:message key="content.search.title"/></a>
            </div>
        </div>
    </div>
    <div class="${contenttypes == 'asset'?'tab_active':'tab'}">
      <div class="body">
         <div>
            <a href="AssetSearchInitAction.do"><fmt:message key="asset.search.title" /></a>
         </div>
      </div>
   </div>
</c:if>
<c:if test="${empty param.index}">
   <div class="tabs">
    <div class="${(contenttypes == 'contentelement' || contenttypes == null)?'tab_active':'tab'}">
        <div class="body">
            <div>
                <a href="SearchInitAction.do"><fmt:message key="content.search.title"/></a>
            </div>
        </div>
    </div>
</c:if>
</div>
   <div class="editor">
   <br />
      <div class="body">
         <%-- If we want to link content: --%>
         <mm:compare referid="action" value="link">
            <div class="ruler_green"><div><fmt:message key="searchform.link.title"/></div></div>
            <mm:notpresent referid="results">
               <fmt:message key="searchform.link.text.step1" ><fmt:param ><mm:node number="${linktochannel}"> <mm:field name="name"/></mm:node></fmt:param></fmt:message>
            </mm:notpresent>
            <mm:present referid="results">
               <fmt:message key="searchform.link.text.step2" ><fmt:param ><mm:node number="${linktochannel}"> <mm:field name="name"/></mm:node></fmt:param></fmt:message>
            </mm:present>
            <br />
            <br />
            <mm:present referid="returnurl">
               <a href="<mm:url page="${returnurl}"/>" title="<fmt:message key="locate.back" />" class="button"><fmt:message key="locate.back" /></a>
            </mm:present>
            <br />
            <br />
            <hr />
         </mm:compare>

         <html:form action="/editors/repository/ContentSearchAction" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="mode"/>
            <html:hidden property="search" value="true"/>
            <html:hidden property="linktochannel"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <html:hidden property="index" value="${param.index}"/>
            <input type="hidden" name="deleteContentRequest"/>
            <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
                     <mm:compare referid="mode" value="advanced" >
                        <a href="#" onclick="selectTab('basic');"><input type="button" class="button" value="<fmt:message key="search.simple.search" />"/></a>
                     </mm:compare>
                <mm:compare referid="mode" value="basic" >
                        <a href="#" onclick="selectTab('advanced');"><input type="button" class="button" value="<fmt:message key="search.advanced.search" />"/></a>
                     </mm:compare> 
            <table>
               <tr>
                  <td style="width:105px"><fmt:message key="searchform.title" /></td>
                  <td colspan="5"><html:text property="title" style="width:200px"/></td>
               </tr>
             <mm:compare referid="mode" value= "advanced">
               <tr>
                  <td><fmt:message key="searchform.keywords" /></td>
                  <td  colspan="3"><html:text property="keywords" style="width:200px"/></td>
                  <td style="width:105px"><fmt:message key="searchform.contenttype" /></td>
                  <td>
                     <html:select property="contenttypes" onchange="selectContenttype('${searchinit}');" >
                        <html:option value="contentelement">&lt;<fmt:message key="searchform.contenttypes.all" />&gt;</html:option>
                        <html:optionsCollection name="typesList" value="value" label="label"/>
                     </html:select>                     
                  </td>
               </tr>              
                  <tr>
                     <td></td>
                     <td><b><fmt:message key="searchform.dates" /></b></td>
                     <td></td>
                     <td><b><fmt:message key="searchform.users" /></b></td>
                     <td></td>
                     <td>
                        <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                           <fmt:message key="searchform.searchfor">
                              <fmt:param><mm:nodeinfo nodetype="${contenttypes}" type="guitype"/></fmt:param>
                           </fmt:message>
                        </mm:compare>
                     </td>
                  </tr>
                  <tr valign="top">
                     <td><fmt:message key="searchform.creationdate" /></td>
                     <td>
                        <html:select property="creationdate" size="1">
                           <html:option value="0"> - </html:option>
                           <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                           <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                           <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                           <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                           <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                        </html:select>
                     </td>
                     <td><fmt:message key="searchform.personal" /></td>
                     <td>
                        <html:select property="personal" size="1">
                           <html:option value=""> - </html:option>
                           <html:option value="lastmodifier"><fmt:message key="searchform.personal.lastmodifier" /></html:option>
                           <html:option value="author"><fmt:message key="searchform.personal.author" /></html:option>
                        </html:select>
                     </td>
                     <td rowspan="5">
                     <% ArrayList fields = new ArrayList(); %>
                        <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                           <table>
                              <mm:fieldlist nodetype="${contenttypes}">
                                 <%-- check if the field is from contentelement --%>
                                 <% boolean showField = true; %>
                                 <mm:fieldinfo type="name" id="fname">
                                     <mm:fieldlist nodetype="contentelement">
                                         <mm:fieldinfo type="name" id="cefname">
                                            <mm:compare referid="fname" referid2="cefname">
                                               <% showField=false; %>
                                            </mm:compare>
                                         </mm:fieldinfo>
                                     </mm:fieldlist>
                                 </mm:fieldinfo>
                                 <% if (showField) { %>
                                    <tr rowspan="5">
                                       <td height="22">
                                          <mm:fieldinfo type="guiname" jspvar="guiname"/>:
                                          <mm:fieldinfo type="name" jspvar="name" write="false">
                                             <% fields.add(contenttypes + "." + name); %>
                                          </mm:fieldinfo>
                                    </tr>
                                 <% } %>
                              </mm:fieldlist>
                           </table>
                        </mm:compare>
                     </td>
                     <td rowspan="5">
                        <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                           <table>
                              <% for (int i = 0; i < fields.size(); i++) {
                                 String field = (String) fields.get(i); %>
                                 <tr>
                                    <td>
                                       <input type="text" name="<%= field %>" value="<%= (request.getParameter(field) == null)? "" :request.getParameter(field) %>" />
                                    </td>
                                 </tr>
                              <% } %>
                           </table>
                        </mm:compare>
                     </td>
                  </tr>
                  <tr>
                     <td><fmt:message key="searchform.lastmodifieddate" /></td>
                     <td>
                        <html:select property="lastmodifieddate" size="1">
                           <html:option value="0"> - </html:option>
                           <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                           <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                           <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                           <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                           <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                        </html:select>
                     </td>
                     <td>
                        <mm:hasrank minvalue="siteadmin">
                           <fmt:message key="searchform.useraccount" />
                        </mm:hasrank>
                     </td>
                     <td>
                        <mm:hasrank minvalue="siteadmin">
                           <html:select property="useraccount" size="1">
                              <html:option value=""> - </html:option>
                               <mm:listnodes type='user' orderby='username'>
                                   <mm:field name="username" id="useraccount" write="false"/>
                                  <html:option value="${useraccount}"> <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> </html:option>
                               </mm:listnodes>
                           </html:select>
                        </mm:hasrank>
                     </td>
                  </tr>
                  <tr>
                     <td><fmt:message key="searchform.publishdate" /></td>
                     <td>
                        <html:select property="publishdate" size="1">
                           <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                           <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                           <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                           <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                           <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                           <html:option value="0"> - </html:option>
                           <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                           <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                           <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                           <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                           <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                        </html:select>
                     </td>
                     <td></td>
                     <td><b><fmt:message key="searchform.other" /></b></td>
                  </tr>
                  <tr>
                     <td><fmt:message key="searchform.expiredate" /></td>
                     <td>
                        <html:select property="expiredate" size="1">
                           <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                           <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                           <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                           <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                           <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                           <html:option value="0"> - </html:option>
                           <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                           <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                           <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                           <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                           <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                        </html:select>
                     </td>
                     <td><fmt:message key="searchform.number" /></td>
                     <td><html:text property="objectid"/></td>
                  </tr>
                  <tr>
                     <td>
                     </td>
                     <td></td>
                     <td nowrap>
                        <mm:compare referid="action" value="link">
                           <mm:write write="false" id="showTreeOption" value="true" />
                        </mm:compare>

                        <mm:compare referid="action" value="selectforwizard">
                           <mm:write write="false" id="showTreeOption" value="true" />
                        </mm:compare>
                        <mm:present referid="showTreeOption">
                           <fmt:message key="searchform.select.channel" />

                     <a href="<c:url value='/editors/repository/select/SelectorChannel.do' />"
                        target="selectChannel" onclick="openPopupWindow('selectChannel', 340, 400)">
                           <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="searchform.select.channel" />"/></a>
                           <a href="#" onClick="selectChannel('', '');" ><img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="searchform.clear.channel.button" />" /></a>
                        </mm:present>
                     </td>
                     <td>
                        <mm:present referid="showTreeOption">
                        <html:hidden property="parentchannel" />
                          <html:hidden property="parentchannelpath"/>
                        <input type="text" name="parentchannelpathdisplay" disabled value="${SearchForm.parentchannelpath}"/><br />
                        </mm:present>
                     </td>
                  </tr>
                  
               </mm:compare>
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
   <mm:list referid="results">
      <mm:first>
         <%@include file="searchpages.jsp" %>

         <form action="LinkToChannelAction.do" name="linkForm">
         <mm:compare referid="action" value="link" inverse="true">
             <mm:hasrank minvalue="siteadmin">
               <c:if test="${fn:length(results) >1}">
               <div align="left"> <input type="button" class="button" name="massdelete" onclick="javascript:deleteContent('massdelete','<fmt:message key="recyclebin.massremoveconfirm"/>')" value="<fmt:message key="content.delete.massdelete" />"/></div>
               </c:if>
              </mm:hasrank> 
         </mm:compare>
          <mm:compare referid="action" value="link" >
             <input type="submit" class="button" value="<fmt:message key="searchform.link.submit" />"/>
          </mm:compare>
          <table>
            <thead>
               <tr>
                  <th>
                     <mm:compare referid="action" value="link" >
                        <input type="hidden" name="channelnumber" value="<mm:write referid="linktochannel"/>" />
                        <input type="hidden" name="channel" value="<mm:write referid="linktochannel"/>" />
                        <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>  
                         <input type="checkbox" onclick="selectAll(this.checked, 'linkForm', 'chk_');" value="on" name="selectall" />
                     </mm:compare>
                     <mm:compare referid="action" value="link" inverse="true">
                     <c:if test="${fn:length(results) >1}">
                      <input type="checkbox" onclick="selectAll(this.checked, 'linkForm', 'chk_');" value="on" name="selectall" />
                     </c:if>
                     </mm:compare>
                  </th>
                  <th><a href="javascript:orderBy('otype')" class="headerlink" ><fmt:message key="locate.typecolumn" /></a></th>
                  <th><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="locate.titlecolumn" /></a></th>
                  <th><fmt:message key="locate.creationchannelcolumn" /></th>
                  <th><a href="javascript:orderBy('creator')" class="headerlink" ><fmt:message key="locate.authorcolumn" /></th>
                  <th><a href="javascript:orderBy('lastmodifieddate')" class="headerlink" ><fmt:message key="locate.lastmodifiedcolumn" /></th>
                  <th><a href="javascript:orderBy('number')" class="headerlink" ><fmt:message key="locate.numbercolumn" /></th>
                  <th></th>
               </tr>
            </thead>
            <tbody class="hover">
      </mm:first>


      <mm:field name="${contenttypes}.number" id="number">
         <mm:node number="${number}">

            <mm:relatednodes role="creationrel" type="contentchannel">
               <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
               <mm:compare referid="trashnumber" referid2="creationnumber">
                   <c:set var="channelName"><fmt:message key="search.trash" /></c:set>
                   <c:set var="channelIcon" value="/editors/gfx/icons/trashbin.png"/>
                   <c:set var="channelIconMessage"><fmt:message key="search.trash" /></c:set>
                   <c:set var="channelUrl" value="../recyclebin/contenttrash.jsp"/>
               </mm:compare>
               <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
                   <mm:field name="number" jspvar="channelNumber" write="false"/>
                   <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
                   <mm:field name="name" jspvar="channelName" write="false"/>

                   <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
                   <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
                   <c:set var="channelUrl" value="Content.do?type=content&parentchannel=${channelNumber}&direction=down"/>
               </mm:compare>
            </mm:relatednodes>



            <tr <mm:even inverse="true">class="swap"</mm:even>>
               <td style="white-space: nowrap;">
               <cmsc:rights nodeNumber="${creationRelNumber}" var="rights"/>
               <mm:compare referid="action" value="link">
                   <input type="checkbox" value="<mm:field name="number" />" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
               </mm:compare>
               <mm:compare referid="action" value="link" inverse="true">
                  <c:if test="${creationRelNumber == trashnumber && rights == 'webmaster' && fn:length(results) >1}">
                      <input type="checkbox" value="permanentDelete:<mm:field name="number" />" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
                  </c:if>
                  <c:if test="${creationRelNumber != trashnumber && (rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster') && fn:length(results) >1}">
                    <input type="checkbox" value="moveToRecyclebin:<mm:field name="number" />" name="chk_<mm:field name="number" />" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
                  </c:if>
               </mm:compare>    
              
                
                 <%-- also show the edit icon when we return from an edit wizard! --%>
                  <mm:write referid="action" jspvar="action" write="false"/>
                  <c:if test="${action == 'search' || action == 'save' || action == 'cancel'}">
                      <a href="<mm:url page="../WizardInitAction.do">
                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                          <mm:param name="returnurl" value="/editors/repository/ContentSearchAction.do${geturl}" />
                      </mm:url>">
                         <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="searchform.icon.edit.title" />" title="<fmt:message key="searchform.icon.edit.title" />" /></a>
               </c:if>
                
               <mm:compare referid="action" value="select">
                     <script>
                        function link<mm:field name="number"/>() {
                           selectElement('<mm:field name="number" />',
                                    '<mm:field name="title" escape="js-single-quotes"/>',
                                    '<cmsc:staticurl page="/content/" /><mm:field name="number"/>')
                        }
                     </script>

                     <a href="#" onClick="link<mm:field name="number" />();">
                         <img src="../gfx/icons/link.png" title="<fmt:message key="searchform.icon.select.title" />" /></a>
                  </mm:compare>
                  <mm:compare referid="action" value="selectforwizard">
                     <a href="#" onClick="top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();">
                         <img src="../gfx/icons/link.png" title="<fmt:message key="searchform.icon.select.title" />" /></a>
                  </mm:compare>
                  <mm:field name="number"  write="false" id="nodenumber">
                     <a href="<cmsc:contenturl number="${nodenumber}"/>" target="_blank"><img src="../gfx/icons/preview.png" alt="<fmt:message key="searchform.icon.preview.title" />" title="<fmt:message key="searchform.icon.preview.title" />" /></a>
                  </mm:field>
               <a href="#" onclick="showItem(<mm:field name="number"/>);"><img src="../gfx/icons/info.png" alt="<fmt:message key="searchform.icon.info.title" />" title="<fmt:message key="searchform.icon.info.title" />" /></a>
                  <mm:compare referid="action" value="search">
                     <mm:haspage page="/editors/versioning">
                        <c:url value="/editors/versioning/ShowVersions.do" var="showVersions">
                           <c:param name="nodenumber"><mm:field name="number" /></c:param>
                        </c:url>
                        <a href="#" onclick="openPopupWindow('versioning', 750, 550, '${showVersions}')"><img src="../gfx/icons/versioning.png" alt="<fmt:message key="searchform.icon.versioning.title" />" title="<fmt:message key="searchform.icon.versioning.title" />" /></a>
                     </mm:haspage>
                     <cmsc:hasfeature name="responseform">
                        <c:set var="typeval">
                               <mm:nodeinfo type="type" />
                            </c:set>
                            <c:if test="${typeval == 'responseform'}">
                               <mm:url page="/editors/savedform/ShowSavedForm.do" id="showSavedForms" write="false">
                                 <mm:param name="nodenumber"><mm:field name="number" /></mm:param>
                                        <mm:param name="initreturnurl" value="/editors/repository/ContentSearchAction.do${geturl}" />
                               </mm:url>
                               <a href="<mm:write referid="showSavedForms"/>"><img src="../gfx/icons/application_form_magnify.png" title="<fmt:message key="content.icon.savedform.title" />" alt="<fmt:message key="content.icon.savedform.title" />"/></a>
                             </c:if>
                     </cmsc:hasfeature>
                  </mm:compare>
            <c:if test="${creationRelNumber == trashnumber && rights == 'webmaster'}">
               <a href="javascript:deleteContent('<mm:field name='number'/>','<fmt:message key="recyclebin.removeconfirm"/>')">
                  <img src="../gfx/icons/delete.png" title="<fmt:message key="searchform.icon.delete.recyclebin" />" alt="<fmt:message key="searchform.icon.delete.recyclebin" />"/>
               </a>
            </c:if>
            <c:if test="${creationRelNumber != trashnumber && (rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster')}">
               <a href="javascript:deleteContent('<mm:field name='number'/>')"><img src="../gfx/icons/delete.png" title="<fmt:message key="searchform.icon.delete.channel" />" alt="<fmt:message key="searchform.icon.delete.channel" />"/></a>
            </c:if>

               </td>
               <td style="white-space: nowrap;">
                 <mm:nodeinfo type="guitype"/>
               </td>
                 <td>
                  <mm:field jspvar="title" write="false" name="title" />
                  <c:if test="${fn:length(title) > 50}">
                     <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                  </c:if>
                  ${title}
               </td>
               <td style="white-space: nowrap;">
              <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                  <mm:compare referid="action" value="search">
                     <a href="${channelUrl}">${channelName}</a>
                  </mm:compare>
                  <mm:compare referid="action" value="search" inverse="true">
                     ${channelName}
                  </mm:compare>
               </td>
               <td style="white-space: nowrap;">
                  <mm:field name="lastmodifier" jspvar="lastmodifier" write="false"/>
                  <mm:listnodes type="user" constraints="username = '${lastmodifier}'">
                     <c:set var="lastmodifierFull"><mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /></c:set>
                     <c:if test="${lastmodifierFull != ''}"><c:set var="lastmodifier" value="${lastmodifierFull}"/></c:if>
                  </mm:listnodes>
                  ${lastmodifier}
               </td>
               <td style="white-space: nowrap;"><mm:field name="lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
               <td width="60"><mm:field name="number"/></td>
               <c:if test="${hasWorkflow}">
                  <td width="10" style="white-space: nowrap;">
                     <c:set var="status" value="waiting"/>
                     <mm:relatednodes type="workflowitem">
                        <c:set var="status"><mm:field name="status"/></c:set>
                     </mm:relatednodes>
                     <c:if test="${status == 'waiting'}">
                        <mm:listnodes type="remotenodes" constraints="sourcenumber=${number}">
                           <c:set var="status" value="onlive"/>
                        </mm:listnodes>
                     </c:if>
                     <img src="../gfx/icons/status_${status}.png" alt="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" title="<fmt:message key="content.status" />: <fmt:message key="content.status.${status}" />" />
                  </td>
                </c:if>
            </tr>

         </mm:node>
      </mm:field>

      <mm:last>
            </tbody>
         </table>
          <mm:compare referid="action" value="link" inverse="true">
             <c:if test="${fn:length(results) >1}">
             <input type="submit" class="button" name="massdelete" onclick="javascript:deleteContent('massdelete','<fmt:message key="recyclebin.removeconfirm"/>')" value="<fmt:message key="content.delete.massdelete" />"/>
             </c:if>
         </mm:compare>
            <mm:compare referid="linktochannel" value="" inverse="true">
                     <input type="submit" class="button" value="<fmt:message key="searchform.link.submit" />"/>

            </mm:compare>

          </form>
         <%@include file="searchpages.jsp" %>
      </mm:last>
   </mm:list>
   </div>
   </div>
</mm:cloud>

   </body>
</html:html>
</mm:content>