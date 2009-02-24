<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<fmt:setBundle basename="cmsc-community" scope="request" />
<cmscedit:head title="community.data.title">
   <script type="text/javascript">
      function commitToXml(){
         document.getElementById("fileType").value="xml";
         docummnt.forms[0].submit();
      }
      function commitToCsv(){
         document.getElementById("fileType").value="csv";
         docummnt.forms[0].submit();
      }
      function commitImport(){
         var groupId = document.getElementById("imgroups").value;
         document.getElementById("imGroupId").value = groupId;
      }
   </script>

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
   <div class="ruler_green">
      <div>&nbsp;<fmt:message key="community.data.export.title"/>&nbsp;</div>
   </div>
   <div class="body">
      <html:form action="/editors/community/ReferenceImportExportAction.do?action=export">
         <input type="hidden" id="fileType" name="fileType"/>
         <p><input type="submit" onclick="javascript:commitToXml()" value="<fmt:message key="community.data.export.xml"/>"/></p>
         <p><input type="submit" onclick="javascript:commitToCsv()" value="<fmt:message key="community.data.export.csv"/>"/></p>
         <p><b><fmt:message key="community.data.options"/></b></p>
        <p><fmt:message key="community.data.export.option.title"/>  <select property="groups" style="width:150px" name="groups">
            <option value="0"><fmt:message key="community.data.export.allgroup"/></option>
            <c:if test="${not empty requestScope.groups}">
               <c:forEach items="${requestScope.groups}" var="groupItem">
                  <option value="${groupItem.id}">${groupItem.name}</option>
               </c:forEach>
            </c:if>
         </select></p>
      </html:form>
   </div>
   <div class="ruler_green">
      <div>&nbsp;<fmt:message key="community.data.import.title"/>&nbsp;</div>
   </div>
   
   <div class="body">
      <html:form action="/editors/community/ReferenceImportExportAction.do?action=showImportPage">
         <input type="hidden" name="imGroupId" id="imGroupId"/>
         <p><input type="submit" onclick="javascript:commitImport()" value="<fmt:message key="community.data.import.from"/>"/></p>
         <p> <b><fmt:message key="community.data.options"/></b></p>
         <p> <fmt:message key="community.data.import.option.title"/>   <select property="groups" style="width:150px" name="groups" id="imgroups">
            <option value="0"><fmt:message key="community.data.inport.nogroup"/></option>
            <c:if test="${not empty requestScope.groups}">
               <c:forEach items="${requestScope.groups}" var="groupItem">
                  <option value="${groupItem.id}">${groupItem.name}</option>
               </c:forEach>
            </c:if>
         </select></p>
      </html:form>
   </div>
</div>
</mm:content>