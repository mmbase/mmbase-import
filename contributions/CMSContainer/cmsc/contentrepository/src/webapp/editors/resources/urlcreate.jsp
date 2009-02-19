<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" %><%@page import="java.util.List"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="urls.create.title">
  <script src="../repository/search.js" type="text/javascript"></script>
  <script type="text/javascript">
    function create() {
        setTimeout('sayWait();',0);
    }

    function sayWait() {
        document.getElementById("busy").style.visibility="visible";
        document.getElementById("notbusy").style.visibility="hidden";
    }


   function showInfo(objectnumber) {
      openPopupWindow('urlinfo', '900', '500', 'urlinfo.jsp?objectnumber='+objectnumber);
    }

   function unpublish(parentchannel, objectnumber) {
       var url = "../repository/UrlDeleteAction.do";
       url += "?channelnumber=" + parentchannel;
       url += "&objectnumber=" + objectnumber;
       url += "&strict=${param.strict}";
       document.location.href = url;
   }

    var blockSelect = false;
  </script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="editor" style="height:580px">
          <div class="body">
              <html:form action="/editors/repository/UrlCreateAction.do" method="post">
                  <input type="hidden" id="parentchannel" name="parentchannel" value="${param.channelid}"/>
                  <html:hidden property="strict" value="${param.strict}"/>
                  <table border="0">
                  <tr>
                     <td style="width: 150px"><fmt:message key="urls.create.titlefield" /></td>
                     <td><html:text property="title" style="width: 350px" value=""/></td>
                  </tr>
                  <tr>
                     <td style="width: 150px"><fmt:message key="urls.create.urlfield" /></td>
                     <td><html:text property="url" style="width: 350px" value=""/></td>
                  </tr>
                  <tr>
                     <td style="width: 150px"><fmt:message key="urls.create.description" /></td>
                     <td><html:text property="description" style="width: 350px; height:75px" value=""/></td>
                  </tr>
                  <tr>
                     <td><html:submit property="createButton" onclick="create();">
                     <fmt:message key='urls.create.submit' /></html:submit></td>
                  </tr>
                  </table>
               </html:form>
          </div>
          <div class="ruler_green"><div><fmt:message key="urls.create.results" /></div></div>

          <div class="body">
            <div id="busy">
                <fmt:message key="uploading.message.wait"/><br />
            </div>
<c:if test="${param.createdNode != 0}">
<table>
   <tr class="listheader">
      <th></th>
      <th nowrap="true"><fmt:message key="urlsearch.titlecolumn" /></th>
      <th><fmt:message key="urlsearch.urlcolumn" /></th>
      <th><fmt:message key="urlsearch.validcolumn" /></th>
   </tr>
   <tbody class="hover">
      <c:set var="useSwapStyle">true</c:set>
      <mm:node number="${param.createdNode}">

            <c:if test="${param.strict == 'urls'}">
               <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
            </c:if>
            <c:if test="${ empty param.strict}">
               <mm:import id="url">javascript:selectElement('<mm:field name="number" />', '<mm:field name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</mm:import>
            </c:if>

            <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
               <td >
<%-- use createdNode in return url --%>
                  <c:set var="returnUrl">/editors/resources/urlcreate.jsp?createdNode=${param.createdNode}&uploadAction=${param.uploadAction}&strict=${param.strict}</c:set>
                  <c:choose>
                     <c:when test="${param.uploadAction == 'select'}">
                        <a href="<mm:url page="SecondaryEditAction.do">
                           <mm:param name="action" value="init"/>
                           <mm:param name="number"><mm:field name="number" /></mm:param>
                           <mm:param name="returnUrl" value="${returnUrl}"/>
                           </mm:url>" onclick="blockSelect = true">
                     </c:when>
                     <c:otherwise>
                        <a href="<mm:url page="../WizardInitAction.do">
                           <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                           <mm:param name="returnurl" value="${returnUrl }" />
                           </mm:url>">
                     </c:otherwise>
                  </c:choose>
                           <img src="../gfx/icons/page_edit.png" title="<fmt:message key="urls.create.edit"/>" alt="<fmt:message key="urls.create.edit"/>"/></a>
                        <a href="javascript:showInfo(<mm:field name="number" />);" onclick="blockSelect = true;">
                           <img src="../gfx/icons/info.png" title="<fmt:message key="urls.create.info"/>" alt="<fmt:message key="urls.create.info"/>"/></a>
                        <a href="javascript:unpublish('${sessionScope.creation}','${param.createdNode}');"
                           title="<fmt:message key="asset.delete" />"><img src="../gfx/icons/delete.png" width="16" height="16"
                           alt="<fmt:message key="asset.delete" />"/></a>
               </td>
              <mm:field name="title" jspvar="title" write="false"/>
              <td onMouseDown="objClick(this);">${fn:substring(title, 0, 40)}<c:if test="${fn:length(title) > 40}">...</c:if></td>
              <mm:field name="url" jspvar="url" write="false"/>
              <td onMouseDown="objClick(this);">${fn:substring(url, 0, 40)}<c:if test="${fn:length(url) > 40}">...</c:if></td>
              <mm:field name="valid" write="false" jspvar="isValidUrl"/>
              <td>
                  <c:choose>
                      <c:when test="${empty isValidUrl}">
                          <fmt:message key="urlsearch.validurl.unknown" />
                      </c:when>
                      <c:when test="${isValidUrl eq false}">
                          <fmt:message key="urlsearch.validurl.invalid" />
                      </c:when>
                      <c:when test="${isValidUrl eq true}">
                          <fmt:message key="urlsearch.validurl.valid" />
                      </c:when>
                      <c:otherwise>
                          <fmt:message key="urlsearch.validurl.unknown" />
                      </c:otherwise>
                  </c:choose>
              </td>
            </tr>
            <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
      </mm:node>
   </tbody>
</table>
</c:if>
         </div>
      </div>
</mm:cloud>
</body>
</html:html>
</mm:content>