<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">--%>
<html:html xhtml="true">
<head>
   <title><fmt:message key="publish.title" /></title>
   <link rel="stylesheet" type="text/css" href="../css/main.css" />
</head>
<body>
   <div class="side_block">
      <!-- bovenste balkje -->
      <div class="header">
         <div class="title"><fmt:message key="publish.title" /></div>
         <div class="header_end"></div>
      </div>
      <div class="body">
          <p>
              <fmt:message key="publish.help" />

              <c:if test="${not empty errors}">

                 <c:forEach var="error" items="${errors}">
                    <p><img src="../gfx/icons/error.png" alt="!"/> ${error}</p>
                 </c:forEach>

              </c:if>
          </p>
         <c:choose>
            <c:when test="${(empty param.doit) or (not empty errors)}">
               <c:url var="actionUrl" value="/editors/publish-remote/PublishBuilderAction.do"/>
         <form action="${actionUrl}" method="post">
            <table>
               <tr>
                  <th>&nbsp;</th>
                  <th><fmt:message key="publish.builders.header" /></th>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_properties"/></td>
                  <td><fmt:message key="publish.builder.properties" /></td>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_portletdefinition"/></td>
                  <td><fmt:message key="publish.builder.portletdefinition" /></td>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_layout"/></td>
                  <td><fmt:message key="publish.builder.layout" /></td>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_view"/></td>
                  <td><fmt:message key="publish.builder.view" /></td>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_stylesheet"/></td>
                  <td><fmt:message key="publish.builder.stylesheet" /></td>
               </tr>
               <tr>
                  <td><input type="checkbox" name="builder_contentchannel"/></td>
                  <td><fmt:message key="publish.builder.contentchannel" /></td>
               </tr>
            </table>
            <input type="hidden" name="doit" value="yes"/>
            <fmt:message key="publish.form.submit" var="inputValue"/>
            <input type="submit" value="${inputValue}"/>
         </form>
            </c:when>
            <c:otherwise>
               <fmt:message key="publish.succesfull"/>
            </c:otherwise>
            </c:choose>
         </div>
      <!-- einde block -->
      <div class="side_block_end"></div>
   </div>

</body>
</html:html>
</mm:content>
