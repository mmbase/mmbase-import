<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<cmsc:location var="currentPage"/>
<div class="block">
   <form action="<cmsc:renderURL page="${SearchResultPage}" window="${SearchResultPortlet}"/>" name="simplesearchform" method="post">
      <input type="hidden" id="searchCategory" name="searchCategory" value="${currentPage.id}" />
      <div class="content">
         <div class="item">
            <h1><fmt:message key="search.title" /></h1>
         </div>
         <div class="divider_white"></div>
         <div class="item">
            <div class="searchheaders"><fmt:message key="search.keyword" /></div>
            <input type="text" width="10" name="searchText">
               <br>
                  <div class="whiteline"></div>
                  <div class="linkcontainer">
                     <ul class="checklist">
                        <li>
                           <label for="o1">
                              <input id="o1" name="searchwhere" value="current" checked="checked" type="radio" />
                              <fmt:message key="search.news" />
                           </label>
                        </li>
                        <li>
                           <label for="o2">
                              <input id="o2" name="searchwhere" value="all" type="radio" />
                              <fmt:message key="search.all" />
                           </label>
                        </li>
                     </ul>
                  </div>
         </div>
      </div>
      <div class="divider3"></div>
      <div class="bottom">
         <div class="center">
            <a href="javascript:document.simplesearchform.submit()"><fmt:message key="search.submit" /></a>
         </div>
      </div>
   </form>
</div>