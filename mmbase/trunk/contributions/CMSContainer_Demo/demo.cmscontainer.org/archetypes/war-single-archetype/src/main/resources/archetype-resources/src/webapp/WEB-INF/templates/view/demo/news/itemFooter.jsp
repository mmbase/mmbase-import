<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
#set( $dollar = "$" )
<c:if test="${dollar}{not empty showExpanded and elementLast and elementIndex ge showExpanded}">
      </ul>
   </div>
</c:if>