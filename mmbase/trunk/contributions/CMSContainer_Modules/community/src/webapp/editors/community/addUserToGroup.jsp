<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <mm:content type="text/html" encoding="UTF-8" expires="0">
      <cmscedit:head title="community.title">
         <script type="text/javascript">
               function addToGroup(){
               var checkboxs = document.forms[1].getElementsByTagName("input");
               var objectnumbers = '';
               for(i = 0; i < checkboxs.length; i++) {
                  if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_group') == 0 && checkboxs[i].checked) {
                     objectnumbers += checkboxs[i].value;
                  }
               }
               if(objectnumbers == ''){
                  alert("<fmt:message key="community.search.promptgroup"/>");
                  return false;
               }
               return true;
            }
         </script> 
      </cmscedit:head>
      
      <body>
         <edit:ui-tabs>
            <edit:ui-tab key="community.search.users" active="true">
               #
            </edit:ui-tab>
            <edit:ui-tab key="community.search.groups">
               ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
            </edit:ui-tab>
         </edit:ui-tabs>
         
         <div class="editor">
            <div class="body" >
               <html:form action="editors/community/AddUserToGroupInit.do" method="post">
                  <table border="0">
                     <tbody>
                        <tr>
                           <td style="width:105px"><fmt:message key="community.search.groupname"/></td>
                           <td style="width:150px"><html:text style="width: 200px" property="group"/></td>
                           <td><input type="submit" name="submitButton" value="<fmt:message key="community.search.searchbatton"/>"> </td>
                        </tr>
                     </tbody>
                  </table>
                  <p>&nbsp;</p>
               </html:form>
            </div>
         </div>
         
         <div class="editor">
            <div class="ruler_green"><div>&nbsp; <fmt:message key="community.search.selectGroup"/> &nbsp;</div></div>
            <div class="body">
               <c:url var="addtoGroup" value="/editors/community/AddUserToGroup.do?users=${users}"/>
               <form action="${addtoGroup}" method="post">
                  <p><input type="submit" value="<fmt:message key="community.search.addUser"/>" name="submitButton" onclick="return addToGroup()"/></p>
                  <%@ include file="grouplist_addusertogroup.jspf" %>
               </form>
            </div>
         </div>
      </body>
   </mm:content>
</html>