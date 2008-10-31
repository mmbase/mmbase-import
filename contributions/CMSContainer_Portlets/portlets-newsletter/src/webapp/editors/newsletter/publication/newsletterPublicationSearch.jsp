<%@include file="../module/globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
      <script src="../../../js/window.js"></script>
      <script type="text/javascript">
         function showInfo(objectnumber) {
            openPopupWindow('newsletterpublicationInfo', '500', '500', '../newsletter/publication/newspubinfo.jsp?objectnumber='+objectnumber);
            
        }
      </script>
      <link href="<c:url value="/editors/css/newsletter.css"/>" rel="stylesheet" type="text/css">
   </cmscedit:head>

   <body>
      <edit:ui-tabs>
         <edit:ui-tab key="newspubs.title" active="true">
            #
         </edit:ui-tab>
      </edit:ui-tabs>
      <div class="editor">
         <div class="body">
            <html:form action="/editors/newsletter/NewsletterPublicationAction.do">
               <input type="hidden" name="method" value="searchNewsletterPublication"/>
               <table border="0">
                  <tr>
                     <td style="width: 105px"><fmt:message key="newspubform.title"/></td>
                     <td><html:text style="width: 150px" property="title"/></td>
                  </tr>
                  <tr>
                     <td><fmt:message key="newspubform.description" /></td>
                     <td><html:text style="width: 150px" property="description"/></td>
                  </tr>
                  <tr>
                     <td><fmt:message key="newspubform.subject" /></td>
                     <td><html:text style="width: 150px" property="subject"/></td>
                  </tr>
                  <tr>
                     <td><fmt:message key="newspubform.intro" /></td>
                     <td><html:text style="width: 150px" property="intro"/></td>
                  </tr>
                  <tr>
                     <td></td>
                     <td><input type="submit" value="<fmt:message key="newspubform.submit" />"/>
                     </td>
                  </tr>
               </table>
            </html:form>
         </div>
         <div class="ruler_green">
            <div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div>
         </div>
         <div class="body">
            <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterPublicationAction.do">
               <edit:ui-tcolumn title="" width="5%">
                  <c:choose>
                     <c:when test="${result.status ne 'DELIVERED'}">
                        <a href="
                           <mm:url page="../WizardInitAction.do">
                              <mm:param name="objectnumber">${result.id}</mm:param>
                              <mm:param name="returnurl" value="/editors/newsletter/NewsletterPublicationAction.do" />
                           </mm:url>
                        "><img src="../gfx/icons/page_edit.png" alt="<fmt:message key='newspubsearch.icon.edit'/>" title="<fmt:message key='newspubsearch.icon.edit'/>"/></a>
                     </c:when>
                     <c:otherwise>
                        <img src="../gfx/icons/edit_gray.gif"/>
                     </c:otherwise>
                  </c:choose><a href="javascript:showInfo(${result.id})"><img src="../gfx/icons/info.png" title="<fmt:message key="newsletter.edition.info" />"/></a>
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.titlecolumn" sort="title" width="20%">
                  ${result.title}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.descripcolumn" sort="description" width="15%">
                  ${result.description}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.subjectcolumn" sort="subject" width="15%">
                  ${result.subject}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.introduction" sort="intro" width="15%">
                  ${result.intro}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.lastmodifiercolumn" sort="lastmodifier" width="15%">
                  ${result.lastmodifier}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="newspubsearch.lastmodifieddatecolumn" sort="lastmodifieddate" width="15%">
                  ${result.lastmodifieddate}
               </edit:ui-tcolumn>
            </edit:ui-table>
         </div>
      </div>
   </body>
</html>
</mm:content>