<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <mm:content type="text/html" encoding="UTF-8" expires="0">
      <cmscedit:head title="community.title"/>
      <body>
         <edit:ui-tabs>
               <edit:ui-tab key="community.search.users">
                  ${pageContext.request.contextPath }/editors/community/SearchConditionalUser.do
               </edit:ui-tab>      
               <edit:ui-tab key="community.search.groups" active="true">
                  ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
               </edit:ui-tab>
         </edit:ui-tabs>
         <div class="editor">
            <div class="body">
               <ul class="shortcuts">
                  <li class="new" style="text-decoration: none;">
                     <a href="${pageContext.request.contextPath }/editors/community/groupInitAction.do"><fmt:message key="community.search.newGroup"/></a>
                  </li>
               </ul>
               <html:form action="/editors/community/searchConditionalGroupAction.do" method="post">
                   <table border="0">
                      <tbody>
                          <tr>
                             <td style="width:116px"><fmt:message key="community.search.groupname"/></td>
                             <td><html:text style="width: 200px" property="groupname"/></td>
                          </tr>
                          <tr>
                             <td style="width:116px"><fmt:message key="community.search.member"/></td>
                             <td><input type="text" style="width:200px" value="" name="member"></td>
                          </tr>
                          <tr>
                             <td style="width:116px">&nbsp;</td>
                             <td><input type="submit" name="submit" value="<fmt:message key="community.search.searchbatton"/>"> </td>
                          </tr>
                       </tbody>
                    </table>
                 </html:form>
            </div>
         </div>
         
         <div class="editor">
            <div class="ruler_green"><div>&nbsp; <fmt:message key="community.preference.result" /> &nbsp;</div></div>
            <div class="body">
               <form action="">
                  <%@ include file="grouplist_table.jspf"%> 
               </form>      
            </div>
         </div>
      </body>
   </mm:content>
</html>