<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="urls.title">
<script src="../repository/search.js" type="text/javascript"></script>
<script src="../repository/content.js" type="text/javascript"></script>
<script type="text/javascript">
   function selectElement(element, title, src) {
      if(window.top.opener != undefined) {
         window.top.opener.selectElement(element, title, src);
         window.top.close();
      }
   }
   function showInfo(objectnumber,returnUrl) {
      openPopupWindow('urlinfo', '500', '500', 'urlinfo.jsp?objectnumber='+objectnumber+'&returnUrl='+returnUrl);
     }
   function confirmDelete(){
      var checkboxs = document.getElementsByTagName("input");
      var num = 0;
      for (i = 0; i < checkboxs.length; i++) {
        if (checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
          num++;
        }
      }
      if(num > 0){
        del = confirm("<fmt:message key="secondaryedit.mass.sure"/> "+num+" <fmt:message key="secondaryedit.mass.elements"/> ?");
        if(del){
          document.forms['imageform'].submit();
        }
      }else{
             alert("<fmt:message key="secondaryedit.mass.atleast.delete"/> ");
         }
   }
</script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
   <mm:import externid="action">search</mm:import>
   <%-- either: search of select --%>
   <div class="tabs"><!-- actieve TAB -->
   <div class="tab_active">
   <div class="body">
   <div><a><fmt:message key="urls.title" /></a></div>
   </div>
   </div>
   </div>

   <div class="editor" style="height:500px">
   <div class="body"><mm:import id="searchinit">
      <c:url value='/editors/resources/UrlInitAction.do' />
   </mm:import> <html:form action="/editors/resources/UrlAction" method="post">
      <html:hidden property="action" value="${action}"/>
      <html:hidden property="offset" />
      <html:hidden property="order" />
      <html:hidden property="direction" />

      <mm:import id="contenttypes" jspvar="contenttypes">urls</mm:import>
      <%@include file="urlform.jsp"%>

   </html:form>
   </div>
   
   <div class="ruler_green">
   <div><fmt:message key="urls.results" /></div>
   </div>
   
   <div class="body">
   <mm:import externid="results" jspvar="nodeList" vartype="List" /> <mm:import
      externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import> <mm:import
      externid="offset" jspvar="offset" vartype="Integer">0</mm:import> <c:if test="${resultCount > 0}">
      <%@include file="../repository/searchpages.jsp"%>
      <form action="SecondaryContentMassDeleteAction.do?object_type=urls" method="post" name="urlform">
         <table border="0" width="100%" class="listcontent">
            <tr class="listheader">
               <th>
               </th>
               <th nowrap="true"><a href="javascript:orderBy('name')" class="headerlink"><fmt:message
                  key="urlsearch.namecolumn" /></a></th>
               <th nowrap="true"><a href="javascript:orderBy('url')" class="headerlink" ><fmt:message key="urlsearch.urlcolumn" /></a></th>
                    <th nowrap="true"><a href="javascript:orderBy('valid')" class="headerlink" ><fmt:message key="urlsearch.validcolumn" /></a></th>
            </tr>
            <tbody class="hover">
               <c:set var="useSwapStyle">true</c:set>
               <mm:listnodes referid="results">
                  <mm:import id="url">javascript:selectElement('<mm:field name="number" />', '<mm:field
                        name="title" escape="js-single-quotes"/>','<mm:field name="url" />');</mm:import>
                  <tr <c:if test="${useSwapStyle}">class="swap"</c:if> href="<mm:write referid="url"/>">
                     <td style="white-space:nowrap;">
                           <c:if test="${action != 'select'}">
                        <a href="<mm:url page="../WizardInitAction.do">
                                                     <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                     <mm:param name="returnurl" value='<%="../editors/resources/UrlAction.do" + request.getAttribute("geturl")%>' />
                                                  </mm:url>">
                        <img src="../gfx/icons/page_edit.png" title="<fmt:message key="urlsearch.icon.edit" />" /></a>
                         <a href="javascript:showInfo(<mm:field name="number" />,'<%="/editors/resources/UrlAction.do" + request.getAttribute("geturl")%>')">
                               <img src="../gfx/icons/info.png" title="<fmt:message key="urlsearch.icon.info" />" /></a>
                        <mm:hasrank minvalue="siteadmin">
                           <a href="<mm:url page="DeleteSecondaryContentAction.do" >
                                                        <mm:param name="object_type" value="urls"/>
                                                        <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
                                                        <mm:param name="returnurl" value='<%="/editors/resources/UrlAction.do" + request.getAttribute("geturl")%>' />
                                                     </mm:url>">
                           <img src="../gfx/icons/delete.png" title="<fmt:message key="urlsearch.icon.delete" />" /></a>
                        </mm:hasrank>
                       </c:if>

                     </td>
                     <mm:field name="title" jspvar="name" write="false"/>
                     <td onMouseDown="objClick(this);">${fn:substring(name, 0, 40)}<c:if test="${fn:length(name) > 40}">...</c:if></td>
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
               </mm:listnodes>
            </tbody>
         </table>
      </form>
   </c:if>
<c:if test="${resultCount == 0 && param.name != null}">
<fmt:message key="urlsearch.noresult" />
</c:if>
<c:if test="${resultCount > 0}">
<%@include file="../repository/searchpages.jsp" %>
</c:if>
</div>
</div>
</mm:cloud>
</body>
</html:html>
</mm:content>
