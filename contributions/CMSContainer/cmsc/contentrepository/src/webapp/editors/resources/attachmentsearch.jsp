<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="com.finalist.cmsc.repository.AssetElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"
%><%@page import="java.util.Iterator,
   com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="attachments.title">
   <script src="../repository/search.js" type="text/javascript"></script>
   <script src="../repository/content.js" type="text/javascript"></script>
   <script type="text/javascript">
      function selectElement(element, title, src) {
         if(window.top.opener != undefined) {
            window.top.opener.selectElement(element, title, src);
            window.top.close();
         }
      }
      
      function showInfo(objectnumber) {
         openPopupWindow('attachmentinfo', '500', '500', 'attachmentinfo.jsp?objectnumber='+objectnumber);
        }
      function confirmDelete(){
         var checkboxs = document.getElementsByTagName("input");
         var num = 0;
         for (i = 0; i < checkboxs.length; i++) {
           if (checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
             num++;
           }
         }
         if(num > 0){
           del = confirm("<fmt:message key="secondaryedit.mass.sure"/> "+num+" <fmt:message key="secondaryedit.mass.elements"/> ?");
           if(del){
             document.forms['imageform'].submit();
           }
         }else{
             alert("<fmt:message key="secondaryedit.mass.atleast.delete"/> ");
         }
        }
   </script>
</cmscedit:head>
<body>
   <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
   <mm:import externid="action">search</mm:import><%-- either: search of select --%>

      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="attachments.title" /></a>
               </div>
            </div>
         </div>
         <div class="tab">
            <div class="body">
               <div>
                  <a href="attachmentupload.jsp?uploadAction=${action}&channelid=${channelid}"><fmt:message key="attachments.upload.title" /></a>
               </div>
            </div>
         </div>
      </div>
      <div class="editor" style="height:500px">
         <div class="body">
            <mm:import id="searchinit"><c:url value='/editors/resources/AttachmentInitAction.do'/></mm:import>
            <html:form action="/editors/resources/AttachmentAction" method="post">
               <html:hidden property="action" value="${action}"/>
               <html:hidden property="offset"/>
               <html:hidden property="order"/>
               <html:hidden property="direction"/>
               <mm:import id="contenttypes" jspvar="contenttypes">attachments</mm:import>
               <%@include file="attachmentform.jsp" %>
            </html:form>
         </div>

         <div class="ruler_green"><div><fmt:message key="attachments.results" /></div></div>

         <div class="body">
            <mm:import externid="results" jspvar="nodeList" vartype="List" />
            <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
            <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
            <c:if test="${resultCount > 0}">
               <%@include file="../repository/searchpages.jsp" %>
               <form action="SecondaryContentMassDeleteAction.do?object_type=attachments" method="post" name="attachform">
                  <table>
                     <tr>
                        <c:if test="${fn:length(results) >1}">
                           <th><input type="submit" onclick="confirmDelete();return false;" value="<fmt:message key="secondaryedit.mass.delete"/>"/></th>
                        </c:if>
                     </tr>
                     <tr class="listheader">
                        <th>
                           <c:if test="${fn:length(results) >1}">
                              <input type="checkbox"  name="selectall"  onclick="selectAll(this.checked, 'attachform', 'chk_');" value="on"/>
                           </c:if>
                        </th>
                        <th nowrap="true"><a href="javascript:orderBy('title')" class="headerlink" ><fmt:message key="attachmentsearch.titlecolumn" /></a></th>
                        <th nowrap="true"><a href="javascript:orderBy('filename')" class="headerlink" ><fmt:message key="attachmentsearch.filenamecolumn" /></a></th>
                        <th nowrap="true"><fmt:message key="locate.creationchannelcolumn" /></th>
                        <th nowrap="true"><a href="javascript:orderBy('size')"><fmt:message key="attachmentsearch.filesizecolumn" /></a></th>
                        <th nowrap="true"><a href="javascript:orderBy('mimetype')" class="headerlink" ><fmt:message key="attachmentsearch.mimetypecolumn" /></a></th>
                     </tr>
                     <tbody class="hover">
                     <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
                        <mm:field id="trashnumber" name="number" write="false"/>
                     </mm:node>
                        <c:set var="useSwapStyle">true</c:set>
                        <mm:listnodes referid="results">
                        <mm:field name="number" jspvar="channelNumber" write="false"/>
                        <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
                        <mm:relatednodes role="creationrel" type="contentchannel">
                           <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
                           <mm:compare referid="trashnumber" referid2="creationnumber">
                              <c:set var="channelName"><fmt:message key="search.trash" /></c:set>
                              <c:set var="channelIcon" value="/editors/gfx/icons/trashbin.png"/>
                              <c:set var="channelIconMessage"><fmt:message key="search.trash" /></c:set>
                           </mm:compare>
                           <mm:field name="number" jspvar="channelNumber" write="false"/>
                           <cmsc:rights nodeNumber="${channelNumber}" var="rights"/>
                           <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
                              <mm:field name="name" jspvar="channelName" write="false"/>
                              <c:set var="channelIcon" value="/editors/gfx/icons/type/contentchannel_${rights}.png"/>
                              <c:set var="channelIconMessage"><fmt:bundle basename="cmsc-security"><fmt:message key="role.${rights}" /></fmt:bundle></c:set>
                           </mm:compare>
                        </mm:relatednodes>
                           <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:attachment escape="js-single-quotes"/>');</mm:import>
                           <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                              <td style="white-space:nowrap;">
                                 <c:if test="${(rights == 'writer' || rights == 'chiefeditor' || rights == 'editor' || rights == 'webmaster') && fn:length(results) >1}">
                                    <input type="checkbox"  name="chk_<mm:field name="number" />" value="<mm:field name="number" />" onClick="document.forms['attachform'].elements.selectall.checked=false;"/>
                                 </c:if>
                                 <c:if test="${action != 'select'}">
                                    <a href="<mm:url page="../WizardInitAction.do">
                                       <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                       <mm:param name="returnurl" value='<%="../editors/resources/AttachmentAction.do" + request.getAttribute("geturl")%>' />
                                       </mm:url>"><img src="../gfx/icons/page_edit.png" alt="<fmt:message key="attachmentsearch.icon.edit" />" title="<fmt:message key="attachmentsearch.icon.edit" />"/></a>
                                    <a href="javascript:showInfo(<mm:field name="number" />)"><img src="../gfx/icons/info.png" alt="<fmt:message key="attachmentsearch.icon.info" />" title="<fmt:message key="attachmentsearch.icon.info" />" /></a>
                                    <mm:hasrank minvalue="administrator">
                                       <a href="<mm:url page="DeleteSecondaryContentAction.do" >
                                          <mm:param name="object_type" value="attachments"/>
                                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                          <mm:param name="returnurl" value='<%="/editors/resources/AttachmentAction.do" + request.getAttribute("geturl")%>' />
                                       </mm:url>"><img src="../gfx/icons/delete.png" alt="<fmt:message key="attachmentsearch.icon.delete" />" title="<fmt:message key="attachmentsearch.icon.delete" />"/></a>
                                    </mm:hasrank>
                                 </c:if>
                              </td>
                              <td onMouseDown="objClick(this);"><mm:field name="title"/></td>
                              <td onMouseDown="objClick(this);"><mm:field name="filename"/></td>
                              <td style="white-space: nowrap;" onMouseDown="objClick(this);">
                                 <img src="<cmsc:staticurl page="${channelIcon}"/>" align="top" alt="${channelIconMessage}" />
                                    ${channelName}</a>
                              </td>
                              <td onMouseDown="objClick(this);">
                                 <mm:field name="size" jspvar="filesize" write="false"/>
                                 <c:choose>
                                    <c:when test="${filesize lt 2048}">
                                       <fmt:formatNumber value="${filesize}" pattern=""/> byte 
                                    </c:when>
                                    <c:when test="${filesize ge 2048 && filesize le(2*1024*1024)}">
                                       <fmt:formatNumber value="${filesize div 1024}" pattern=".0"/> K 
                                    </c:when>
                                    <c:otherwise>
                                       <fmt:formatNumber value="${filesize div (1024 * 1024)}" pattern=".0"/> M 
                                    </c:otherwise>
                                 </c:choose>
                              </td>
                              <td onMouseDown="objClick(this);"><mm:field name="mimetype"/></td>
                           </tr>
                           <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
                        </mm:listnodes>
                     </tbody>
                     <tr>
                        <c:if test="${fn:length(results) >1}">
                           <th><input type="submit" onclick="confirmDelete();return false;" value="<fmt:message key="secondaryedit.mass.delete"/>"/></th>
                        </c:if>
                     </tr>
                  </table>
               </form>
            </c:if>
            <c:if test="${resultCount == 0 && param.title != null}">
               <fmt:message key="attachmentsearch.noresult" />
            </c:if>
            <c:if test="${resultCount > 0}">
               <%@include file="../repository/searchpages.jsp" %>
            </c:if>
         </div>
      </div>
   </mm:cloud>
</body>
</html:html>
</mm:content>