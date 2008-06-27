<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://finalist.com/cmsc-directreaction" prefix="cmsc-dr"%>

<%@include file="globals.jsp" %>
<fmt:setBundle basename="cmsc-reactions" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="reactioninfo.title" />

<cmsc-dr:getreaction number="${param.objectnumber}" var="reactionObject"/>
<body>
   <div class="side_block_green" style="width: 100%">
      <div class="header">
         <div class="title">
            <fmt:message key="reactioninfo.title" />: ${reactionObject.title}
         </div>
      <div class="header_end"></div>
   </div>
   <div class="body" >
   
      <table class="listcontent">
         <tr>
            <td><fmt:message key="reactioninfo.number" />:</td>
            <td>${reactionObject.number}</td>
         </tr>
         <tr>
            <td><fmt:message key="secondaryinfo.creationdate" />:</td>
            <td><fmt:formatDate value="${reactionObject.creationdate}" pattern="dd MMMMM yyyy hh:mm:ss"/></td>
         </tr>
         <tr>
            <td><fmt:message key="secondaryinfo.creator" />:</td>
            <td>${reactionObject.name}</td>
         </tr>
         <tr>
            <td><fmt:message key="reactioninfo.titlefield" />:</td>
            <td>${reactionObject.title}</td>
         </tr>
         <tr>
            <td><fmt:message key="reactioninfo.bodyfield" />:</td>
            <td>${reactionObject.body}</td>
         </tr>            
         <tr>
            <td><fmt:message key="reactioninfo.emailfield" />:</td>
            <td>${reactionObject.email}</td>
         </tr>         
      </table>
      <table class="listcontent">
         <tr>
            <td>
               <hr/>
               <fmt:message key="reactioninfo.related" />
               <table>
                  <tr>
                     <td>
                        <b>${reactionObject.contentTitle}</b>
                     </td>
                  </tr>
               </table>
            </td>
         </tr>
      </table>
      <br />
      <ul class="shortcuts">
         <li class="close">
            <a href="#" onClick="window.close()"><fmt:message key="reactioninfo.close" /></a>
         </li>
      </ul>
   </div>
   <div class="side_block_end"></div>
   </div>            
</body>

</html:html>
</mm:content>              