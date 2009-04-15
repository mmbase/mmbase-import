<%@ page import="org.apache.commons.lang.RandomStringUtils"
 %><%@ page import="com.finalist.cmsc.paging.PagingUtils" 
 %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
 %><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
 %><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<!--todo : get default page size from system properties.-->
<c:set var="currentPage" value="${empty param.offset ? 1 : param.offset +1 }"/>
<c:set var="pagessize" value="${ cmsc:ceil(listSize/resultsPerPage)}"/>

<fmt:bundle basename="cmsc">
<fmt:message key="pages.message" var="error"/>
<fmt:message key="pages.go" var="go"/>
<fmt:message key="searchpages.showresults" var="searchresult">
   <fmt:param>${(currentPage-1) * resultsPerPage +1 }</fmt:param>
   <fmt:param>${(listSize>currentPage*resultsPerPage)?(currentPage*resultsPerPage):listSize }</fmt:param>
   <fmt:param>${listSize}</fmt:param>
</fmt:message>


<script type="text/javascript">

   function gotopage(targetfield) {

      var inputValue = document.getElementById(targetfield).value;
      var re = new RegExp("^[1-9][0-9]*$");
      if (re.test(inputValue) && inputValue <= Math.ceil(${pagessize})) {
	   setOffset(inputValue - 1);
      } else {
         alert("${error}");
      }
   }

   function enterto(event, targetfield) {
      if (event.keyCode == 13) {
         gotopage(targetfield)
      }
   }
</script>


<table border="0" width="100%">
   <tr>
      <td style="width:50%;">
            ${searchresult}
      </td>
      <c:if test="${pagessize>1}">

         <td style="text-align:right;width:50%;">
            <fmt:message key="searchpages.page"/>:
            <c:if test="${currentPage > 1}">
               <a href="javascript:setOffset('${currentPage - 2}');" class="page_list_navtrue">&lt;&lt;<fmt:message key="pages.previous"/></a>
            </c:if>


            <c:choose>
               <c:when test="${pagessize>0&&pagessize<14}">
                  <c:forEach var="count" begin="1" end="${pagessize}">
                     <a href="javascript:setOffset('${count-1}');" class="${count == currentPage ? 'page_list_navfalse':'page_list_navtrue'}"><c:out value="${count}"/></a>
                     <c:if test="${count ne pagessize}"> | </c:if>
                  </c:forEach>
               </c:when>

               <c:when test="${pagessize>13}">
                  <c:set var="length_of_prefix" value="${currentPage <7 ? currentPage+2 : 3}"/>
                  <c:forEach var="count" begin="1" end="${length_of_prefix}">
                      <a href="javascript:setOffset('${count-1}');" class="${count == currentPage ? 'page_list_navfalse':'page_list_navtrue'}"><c:out value="${count}"/></a>
                     |
                  </c:forEach>
                  ...

                  <c:if test="${6<currentPage&&currentPage <pagessize-5}">
                     <c:forEach var="count" begin="${currentPage-2}" end="${currentPage+2}">
                      <a href="javascript:setOffset('${count-1}');" class="${count == currentPage ? 'page_list_navfalse':'page_list_navtrue'}"><c:out value="${count}"/></a>
                        |
                     </c:forEach>
                     ...
                  </c:if>

                  <c:set var="length_of_postfix" value="${currentPage>(pagessize-6) ? currentPage-2 : (pagessize-2)}"/>
                  <c:forEach var="count" begin="${length_of_postfix}" end="${pagessize}">
                     <a href="javascript:setOffset('${count-1}');" class="${count == currentPage ? 'page_list_navfalse':'page_list_navtrue'}"><c:out value="${count}"/></a>
                     |
                  </c:forEach>
               </c:when>

               <c:otherwise>
                  The page you requested does not exist. ${pagessize}
               </c:otherwise>

            </c:choose>

            <c:set var="targetfield">
               <%=RandomStringUtils.randomAlphabetic(5)%>
            </c:set>
            <c:if test="${currentPage < pagessize}">
               <a href="javascript:setOffset('${currentPage}');" class="page_list_navtrue"><fmt:message key="pages.next"/>&gt;&gt;</a>
            </c:if>
            <c:if test="${pagessize>13}">
               <input type="text" name="targetpage" id="${targetfield}" size="4"
                      onKeyPress="enterto(event,'${targetfield}')"/>

               <input type="button" id="goto" value="${go}"
                      onclick="gotopage('${targetfield}')"/>
            </c:if>
         </td>
      </c:if>
   </tr>

</table>
</fmt:bundle>