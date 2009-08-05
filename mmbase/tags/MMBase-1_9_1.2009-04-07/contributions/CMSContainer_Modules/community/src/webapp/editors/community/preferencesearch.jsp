<%@page language="java" contentType="text/html;charset=utf-8" 
%><%@include file="globals.jsp" 
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="community.preference.title">
   <c:url var="actionUrl" value="/editors/community/PreferenceAction.do"/>
   <c:url var="userActionUrl" value="/editors/community/userAddInitAction.do"/>
   <link href="community.css" type="text/css" rel="stylesheet" />
   <script src="../../js/prototype.js" type="text/javascript"></script>
   <script src="js/formcheck.js" type="text/javascript"></script>
   <script type="text/javascript">
      function update(number) {
         var myAjax = new Ajax.Request(
            '${actionUrl}',
            {   parameters:"method=modify&id=" + number + "&key=" + $("key_" + number).value + "&value=" + $("value_" + number).value,
              onComplete: postUpdate
            }
         );
      }

      function deleteInfo(number, offset, resultLength) {
         if (confirm('<fmt:message key="community.preference.delete.conform" />')) {
            if (resultLength == "1") {
               offset = eval(offset - 1);
            }
            if(offset < 0) {
               offset = 0;
            }
            document.forms[0].method.value = "delete";
            $("id").value = number;
            $("page").value = offset;
            document.forms[0].submit();
         }
      }

      function postUpdate() {
         alert('<fmt:message key="community.preference.update.success" />');
      }

   </script>
</cmscedit:head>

<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

   <mm:import externid="action">search</mm:import><%-- either: search of select --%>
   <edit:ui-singletab key="community.preference.title" action="${actionUrl}?method=list&reload=true"/>
      <div class="editor">
         <div class="body">
            <html:form action="/editors/community/PreferenceAction" method="post">
               <html:hidden property="method" value="list"/>
               <html:hidden property="order"/>
               <html:hidden property="direction"/>
               <input type="hidden" name="page" id="page" value="${page}"/>
               <input type="hidden" name="id" id="id" value=""/>
               <%@include file="preferenceform.jsp" %>
            </html:form>
         </div>

      <div class="ruler_green">
         <div><fmt:message key="community.preference.result"/></div>
      </div>
      <div class="body">
         <edit:ui-table items="${results}" var="preference" size="${totalCount}"
                        requestURI="/editors/community/PreferenceAction.do">
         <edit:ui-tcolumn title="">
         <mm:hasrank minvalue="siteadmin">
            <a href="javascript:deleteInfo('${preference.id}','${page}',${fn:length(results)})">
               <img src="../gfx/icons/delete.png" title="<fmt:message key="community.preference.delete" />"/></a>
         </mm:hasrank>
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="community.preference.user.upper" sort="userId">
         <a href="${userActionUrl}?authid=${preference.authenticationId}&forward=communitypreference&path=${forward}">${preference.userId}</a>
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="community.preference.module.upper" sort="module">
            ${preference.module}
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="community.preference.key.upper" sort="key">
         <input type="text" name="key" id="key_${preference.id}" value="${preference.key}"/>
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="community.preference.value.upper" sort="value">
         <input type="text" name="value" id="value_${preference.id}" value="${preference.value}"/>
         </edit:ui-tcolumn>
         <edit:ui-tcolumn titlekey="community.preference.action.upper">
         <mm:hasrank minvalue="siteadmin">
            <a href="javascript:update('${preference.id}')"><fmt:message key="view.submit"/></a>
         </mm:hasrank>
         </edit:ui-tcolumn>
         </edit:ui-table>
</mm:cloud>
</body>
</html:html>
</mm:content>