<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newsletter.bounce.title">
<c:url var="actionUrl" value="/editors/newsletter/module/NewsletterBounceAction.do"/>
<script type="text/javascript" src="../newsletter.js"></script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
<div class="tabs">
<div class="tab_active">
<div class="body">
   <div>
      <a href="#"><fmt:message key="newsletter.bounce.title" /></a>
   </div>
</div>
</div>
</div>
<div class="editor" style="height:500px">
<div class="ruler_green"><div><fmt:message key="newsletter.term.search.result" /></div></div>
<div class="body">
<form action="${actionUrl}" name="termForm" method="post">
<input type="hidden" name="method" value="list"/>
<input type="hidden" name="offset" value="${offset}"/>
<input type="hidden" name="direction" value="${direction}"/>
<input type="hidden" name="order" value="${order}"/>

<mm:import jspvar="resultCount" vartype="Integer">${resultCount}</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">${offset}</mm:import>
<mm:import externid="direction" jspvar="direction" vartype="String">${direction}</mm:import>
<mm:import externid="order" jspvar="order" vartype="String">${order}</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../../repository/searchpages.jsp" %>
   <table>
      <tr class="listheader">
         <th><a href="javascript:sortBy('userName')"> <fmt:message key="newsletter.bounce.subscriber" /></a> </th>
         <th><a href="javascript:sortBy('newsLetterTitle')"><fmt:message key="newsletter.bounce.newsletter" /></a></th>
         <th><a href="javascript:sortBy('bouncedate')"><fmt:message key="newsletter.bounce.bouncedate" /></a></th>
         <th><a href="javascript:sortBy('content')"><fmt:message key="newsletter.bounce.bouncecontent" /></a></th>
      </tr>
      <tbody class="hover">
                <c:set var="useSwapStyle">true</c:set>
                <c:forEach var="bounce" items="${resultList}" >
               <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
                   <td >
                   <c:out  value="${bounce.userName}"/> 
                   </td>
                   <td>
                   <c:out  value="${bounce.newsLetterTitle}"/>
                   </td>
                   <td >
                   <c:out  value="${bounce.bounceDate}"/> 
                   </td>
                   <td>
                      <a href="javascript:showItem('${bounce.id}')">
                         <c:out  value="${fn:length(bounce.bounceContent) >50?fn:substring(bounce.bounceContent,0,60):bounce.bounceContent}"/>
                      </a>
                   </td>
               </tr>
           <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
        	</c:forEach>
      </tbody>
   </table>
</c:if>
</form>
</div>
<c:if test="${resultCount == 0}">
<fmt:message key="newsletter.bounce.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
<%@include file="../../repository/searchpages.jsp" %>
</c:if>
</mm:cloud>
</body>
</html:html>
</mm:content>