<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<cmsc:location var="cur" sitevar="site" />
<cmsc:list-pages var="channels" origin="${site}"/>
<ul class="treemenu singleopen keepopen">
   <c:forEach var="chan" items="${channels}">
         <li class="treenodeshow">
         	<a class="mainlevel" href="<cmsc:link dest="${chan.id}"/>">${chan.title}</a>
            <cmsc:list-pages var="subchannels" origin="${chan}"/>
            <c:if test="${not empty subchannels}">
            <ul>
               <c:forEach var="subchan" items="${subchannels}">
                     <li><a class="sublevel" href="<cmsc:link dest="${subchan.id}"/>">${subchan.title}</a></li>
               </c:forEach>
            </ul>
            </c:if>
         </li>
   </c:forEach>
</ul>
<script type="text/javascript">initMenus()</script>