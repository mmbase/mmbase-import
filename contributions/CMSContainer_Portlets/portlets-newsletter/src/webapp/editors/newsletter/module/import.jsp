<%@include file="globals.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>

<cmscedit:head title="ewsletter.subscription.manage.newsletteroverview">
</cmscedit:head>
<script>
   function formSubmit(){
      if(${requestScope.importType eq 'importCSV'}){
         document.getElementsByName('action')[0].value="importUserSubScription";
      }
      else{
         document.getElementsByName('action')[0].value="importsubscription";
      }
      document.forms[0].submit();
   }
</script>
<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="newsletteroverview.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <div class="body">
      <html:form action="/editors/newsletter/SubscriptionImportExportAction" enctype="multipart/form-data">
         <html:file property="datafile"/>
         <input type="hidden" name="action" value="importsubscription"/>
         <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
         <input type="button" value="Import" onclick="formSubmit()"/>
      </html:form>
      <div style="margin:4px;color:red;">
         <html:messages id="file" message="true" bundle="newsletter">
            <bean:write name="file"/><br>
         </html:messages>
      </div>
   </div>

</div>
</div>


