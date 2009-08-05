<%@include file="globals.jsp"
%><%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
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
   <body>
      <edit:ui-tabs>
         <edit:ui-tab key="newsletteroverview.title" active="true">
            #
         </edit:ui-tab>
      </edit:ui-tabs>
      <div class="editor">
         <div class="body">
            <html:form action="/editors/newsletter/SubscriptionImportExportAction" enctype="multipart/form-data">
               <html:file property="datafile"/>
               <input type="hidden" name="action" value="importsubscription"/>
               <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
               <input type="button" value="<fmt:message key='index.link.import'/>" onclick="formSubmit()"/>
            </html:form>
            <div style="margin:4px;color:red;">
               <html:messages id="file" message="true" bundle="newsletter">
                  <bean:write name="file"/><br>
               </html:messages>
            </div>
         </div>
      </div>
   </body>
</html>
</mm:content>
