<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,
           com.finalist.cmsc.mmbase.PropertiesUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newsletter.bounce.title">
<c:url var="actionUrl" value="/editors/newsletter/module/NewsletterBounceAction.do"/>
<script type="text/javascript">
 function setOffset(offset) {
    document.forms[0].offset.value = offset;
    document.forms[0].submit();
 }
 function showItem(objectnumber) {
    openPopupWindow("showItem", 500, 500, 'NewsletterBounceAction.do?method=getItem&objectnumber=' + objectnumber);
}
</script>
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

<mm:import jspvar="resultCount" vartype="Integer">${resultCount}</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">${offset}</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../../repository/searchpages.jsp" %>
   <table>
      <tr class="listheader">
         <th> <fmt:message key="newsletter.bounce.subscriber" /> </th>
         <th><fmt:message key="newsletter.bounce.newsletter" /></th>
         <th><fmt:message key="newsletter.bounce.bouncedate" /></th>
         <th><fmt:message key="newsletter.bounce.bouncecontent" /></th>
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