<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<cmscedit:head title="reactions.title">
   <script type="text/javascript">
      function addToSubscribe(){
         var checkboxs = document.forms[1].getElementsByTagName("input");
         var objectnumbers = '';
            for (i = 0; i < checkboxs.length; i++) {
               if (checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
                  objectnumbers += checkboxs[i].value;
               }
            }
            if (objectnumbers == '') {
               alert("<fmt:message key="community.search.promptuser"/>");
               return false;
            }
            return true;
      }
   </script>
</cmscedit:head>
<body>
   <edit:ui-tabs>
      <edit:ui-tab key="community.search.users" active="true"/>
   </edit:ui-tabs>

   <div class="editor"><br/>
      <div style="padding-left:10px;">
         <html:form action="/editors/community/SearchConditionalUser.do" method="post">
            <%@include file="search_user_form_table.jspf" %>
            <input type="hidden" name="method" value="searchCandidateSubscriber"/>
            <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
         </html:form>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green">
         <div>&nbsp;<fmt:message key="community.search.result"/>&nbsp;</div>
      </div>
      <div class="body">
         <c:url var="addSubscribe" value="/editors/newsletter/NewsletterSubscriptionAddRelAction.do">
            <c:param name="method" value="subscribeNewsletters"/>
            <c:param name="newsletterId" value="${requestScope.newsletterId}"/>
         </c:url>
         <form action="${addSubscribe}" method="post" name="selectform" id="selectform">
            <input type="submit" value="<fmt:message key='community.search.addToSubscribe'/>" name="submitButton" onclick="return addToSubscribe()"/>
            <c:set var="hiddenOperation" value="true"/>
            <%@ include file="userlist_table.jspf" %>
         </form>
      </div>
   </div>
</body>
</mm:content>
