<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>

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
