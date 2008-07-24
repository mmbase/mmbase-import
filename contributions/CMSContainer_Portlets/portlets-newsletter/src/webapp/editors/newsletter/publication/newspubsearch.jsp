<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<%@page import="java.util.Iterator, com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:import externid="mode" id="mode">search</mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <cmscedit:head title="newspubs.title">
      <script src="<cmsc:staticurl page='/editors/repository/search.js'/>" type="text/javascript"></script>
      <script src="<cmsc:staticurl page='/editors/repository/content.js'/>" type="text/javascript"></script>
      <script src="<cmsc:staticurl page='/js/window.js'/>" type="text/javascript"></script>
      <script>
         function showInfo(objectnumber,returnUrl) {
            openPopupWindow('newsletterpublication Info', '500', '500', '../newsletter/publication/newspubinfo.jsp?objectnumber='+objectnumber);
         }
      </script>
      <link href="<cmsc:staticurl page='/editors/css/main.css'/>" rel="stylesheet" type="text/css" />
   </cmscedit:head>
   
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../../editors/login.jsp">
         <mm:import externid="action">search</mm:import><%-- either: search of select --%>
         <div class="tabs">
            <div class="tab_active">
               <div class="body">
                  <div>
                     <a href="#"><fmt:message key="newspubs.title" /></a>
                  </div>
               </div>
            </div>
         </div>
         
         <div class="editor" style="height:500px">
            <div class="body">
               <mm:import id="searchinit">
                  <c:url value='/editors/newsletter/NewletterPublicationInitAction.do'/>
               </mm:import>
               <html:form action="/editors/newsletter/NewletterPublicationAction" method="post">
                  <html:hidden property="action" value="${action}"/>
                  <html:hidden property="offset"/>
                  <html:hidden property="order"/>
                  <html:hidden property="direction"/>
                  <mm:import id="contenttypes" jspvar="contenttypes">newsletterpublication</mm:import>
                  <%@include file="newspubform.jsp" %>
               </html:form>
            </div>
            <div class="ruler_green">
               <div>
                  <fmt:message key="newspubs.results" />
               </div>
            </div>
            <div class="body"> 
               <mm:import externid="results" jspvar="nodeList" vartype="List" />
               <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
               <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
               <c:if test="${resultCount > 0}">
                  <%@include file="../../repository/searchpages.jsp" %>
                  <table>
                     <tr class="listheader">
                        <th> </th>
                        <th nowrap="true">
                           <a href="#" class="headerlink" onClick="orderBy('title');">
                              <fmt:message key="newspubsearch.titlecolumn" />
                           </a>
                        </th>
                        <th><fmt:message key="newspubsearch.descripcolumn" /></th>
                        <th><fmt:message key="newspubsearch.subjectcolumn" /></th>
                        <th><fmt:message key="newspubsearch.lastmodifiercolumn" /></th>
                        <th><fmt:message key="newspubsearch.lastmodifieddatecolumn" /></th>
                     </tr>
                     <tbody class="hover">                        
                        <c:set var="useSwapStyle">true</c:set>
                        <mm:listnodes referid="results">
                           <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
                              <td style="white-space:nowrap;">
                                 <mm:field name="status" id="status" write="false"/>
                                 <c:choose>
                                    <c:when test="${status ne 'DELIVERED'}">
                                       <a href="<mm:url page="../WizardInitAction.do">
                                          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                          <mm:param name="returnurl" value="<%="/editors/newsletter/NewletterPublicationAction.do" + request.getAttribute("geturl")%>" /> </mm:url>">
                                          <img src="../gfx/icons/page_edit.png" alt="<fmt:message key="newspubsearch.icon.edit" />" title="<fmt:message key="newspubsearch.icon.edit" />" />
                                       </a>
                                    </c:when>
                                    <c:otherwise>
                                       <img src="../gfx/icons/edit_gray.gif"/>
                                    </c:otherwise>
                                 </c:choose>
                                 <a href="javascript:showInfo(<mm:field name='number'/>,'<%="/editors/newsletter/NewletterPublicationAction.do" + request.getAttribute("geturl")%>')">
                                    <img src="../gfx/icons/info.png" title="<fmt:message key="urlsearch.icon.info" />" />
                                 </a>
                              </td>
                              <td onMouseDown="objClick(this);"><mm:field name="title" /></td>
                              <td onMouseDown="objClick(this);"><mm:field name="description" /></td>
                              <td onMouseDown="objClick(this);"><mm:field name="subject" /></td>
                              <td onMouseDown="objClick(this);"><mm:field name="lastmodifier" /></td>
                              <td onMouseDown="objClick(this);">
                                 <mm:field name="lastmodifieddate" id="lastmodifieddate" write="false"/>
                                    <mm:write referid="lastmodifieddate">
                                       <mm:time format="dd-MM-yyyy hh:mm"/>
                                    </mm:write>
                              </td>
                           </tr>
                           <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
                        </mm:listnodes>
                     </tbody>
                  </table>
               </c:if>
               <c:if test="${resultCount == 0 && param.title != null}">
                  <fmt:message key="newspubsearch.noresult" />
               </c:if>
               <c:if test="${resultCount > 0}">
                  <%@include file="../../repository/searchpages.jsp" %>
               </c:if>
            </div>
         </div>
      </mm:cloud>
   </body>
</html:html>
</mm:content>