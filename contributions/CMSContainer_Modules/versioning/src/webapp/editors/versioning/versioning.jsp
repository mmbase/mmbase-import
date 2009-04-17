<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="versioning.title.content" />
<body>
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<mm:import externid="archiveNodes" jspvar="nodeList" vartype="List" />

   <div class="content">
      <div class="tabs">
          <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="versioning.title.content" /></a>
               </div>
            </div>
         </div>
      </div>
   </div>
   <div class="editor">
      <div class="body">
         <mm:node number="${param.nodenumber}" notfound="skipbody">
            <p>
               <fmt:message key="versioning.versions.of" />
               <b><mm:nodeinfo type="guitype" /></b>:
               <mm:field name="title"/>
            </p>
         </mm:node>
         <c:if test="${not empty error}">
            <p class="error"><img src="../gfx/icons/error.png" alt="!">${error}</p>
         </c:if>
         <c:if test="${not empty message}">
            <p>${message}</p>
         </c:if>
         <c:if test="${not isAllowed}">
            <p><fmt:message key="versioning.not.allowed"/></p>
         </c:if>
         <c:if test="${empty archiveNodes}">
            <p><fmt:message key="versioning.no.versions"/></p>
         </c:if>
         </div>
         <div class="ruler_green">
            <div><fmt:message key="versioning.title.content" /></div>
         </div>
		<div class="body">
         <mm:list referid="archiveNodes">
            <mm:first>
               <table>
                  <thead>
                     <tr>
                        <th nowrap="true"><fmt:message key="versioning.date"/></th>
                        <th><fmt:message key="versioning.author"/></th>
                        <th><fmt:message key="versioning.publish"/></th>
                        <th>&nbsp;</th>
                     </tr>
                  </thead>
                  <tbody class="hover">
            </mm:first>
            <mm:even inverse="true"><c:set var="class">class="swap"</c:set></mm:even>
            <mm:even><c:set var="class"></c:set></mm:even>
            <tr ${class}>
               <td>
                  <mm:field name="date"><cmsc:dateformat displaytime="true" /></mm:field>
               </td>
               <td>
                  <mm:field name="author_number" id="author_number" write="false"/>
                  <mm:hasnode number="${author_number}" inverse="true">
                     <fmt:message key="versioning.no.author"/>
                  </mm:hasnode>
                  <mm:node number="${author_number}" notfound="skipbody">
                     <mm:field name="firstname"/> <mm:field name="prefix"/>  <mm:field name="surname"/> (<mm:field name="username"/>)
                  </mm:node>
               </td>
               <td><mm:field name="publish"/>
               </td>
               <td>
                  <c:url value="/editors/versioning/RestoreAction.do" var="restoreUrl">
                     <c:param name="archivenumber"><mm:field name="number"/></c:param>
                     <c:param name="nodenumber"><mm:field name="original_node"/></c:param>
                  </c:url>

                  <c:if test="${isAllowed}">
                  <a href="${restoreUrl}" class="button">
                     <fmt:message key="versioning.restore"/>
                  </a>
                  </c:if>
               </td>
            </tr>
            <mm:last>
                  </tbody>
               </table>
            </mm:last>
         </mm:list>
      </div>
   </div>
</mm:cloud>
</body>
</html:html>
</mm:content>