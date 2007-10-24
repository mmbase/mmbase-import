<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">--%>
<html:html xhtml="true">
<head>
   <title><fmt:message key="workflow.admin.title" /></title>
   <link rel="stylesheet" type="text/css" href="../../css/main.css" />
</head>
<body>
   <div class="side_block">
      <!-- bovenste balkje -->
      <div class="header">
         <div class="title"><fmt:message key="workflow.admin.title" /></div>
         <div class="header_end"></div>
      </div>
      <div class="body">
          <p>
              <fmt:message key="workflow.admin.help" />

              <c:if test="${not empty errors}">
                 <c:forEach var="error" items="${errors}">
                    <p><img src="../gfx/icons/error.png" alt="!"/> ${error}</p>
                 </c:forEach>
              </c:if>
          </p>
         <c:choose>
            <c:when test="${param.action ne 'start'}">
               <c:url var="actionUrl" value="/editors/workflow/admin/WorkflowAdminAction.do"/>
               <form action="${actionUrl}" method="post">
                  <input type="hidden" name="action" value="start"/>
                  <fmt:message key="workflow.admin.form.submit" var="inputValue"/>
                  <input type="submit" value="${inputValue}"/>
               </form>
            </c:when>
            <c:otherwise>
               <fmt:message key="workflow.admin.started" />
            </c:otherwise>
         </c:choose>
      </div>
      <!-- einde block -->
      <div class="side_block_end"></div>
   </div>

</body>
</html:html>
</mm:content>
