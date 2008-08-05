<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc"%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">

<cmscedit:head title="reactions.title"/>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="community.data.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <div class="body">
     
      <div>
         

         <p>
            <a href="ReferenceImportExportAction.do?action=export">
               <fmt:message key="community.data.export"/>
            </a>
         </p>

         <p>
            <a href="${pageContext.request.contextPath }/editors/community/import.jsp">
               <fmt:message key="community.data.import"/>
            </a>
         </p>
         
      </div>
   </div>
</div>
</mm:content>