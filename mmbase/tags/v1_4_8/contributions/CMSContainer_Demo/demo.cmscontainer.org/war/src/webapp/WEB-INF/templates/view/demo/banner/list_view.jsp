<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
   <mm:node number="${elementId}">   
   
      <mm:remove referid="url"/>
      <mm:remove referid="target"/>
      
      <mm:relatednodes type="object" role="posrel" searchdir="destination" max="1">
         <mm:nodeinfo type="type" jspvar="enodetype2" write="false" />         
         <c:choose>
            <c:when test="${enodetype2 eq 'page'}">
               <mm:field name="number" id="nodeNumber" write="false"/>
               <mm:import id="url"><cmsc:link dest="${nodeNumber}"/></mm:import>
            </c:when>
            <c:when test="${enodetype2 eq 'urls'}">
               <mm:field name="url" id="url" write="false"/>
               <mm:import id="target">_blank</mm:import>
            </c:when>
            <c:otherwise>
               <mm:import id="url"><cmsc:contenturl number="${_node.number}"/></mm:import>
            </c:otherwise>
         </c:choose>
      </mm:relatednodes>

      <mm:present referid="url">
         <a href="<mm:write referid="url" />" 
            title="<mm:field name="description" escape="text/xml" />" 
            <mm:present referid="target">target="<mm:write referid="target" />"</mm:present>
         >
            <mm:relatednodes type="images" role="imagerel" searchdir="destination">
               <cmsc-bm:image width="170"/>
            </mm:relatednodes>
         </a>
      </mm:present>
         
      <div class="scheiding3"></div>    
   </mm:node>
</mm:cloud>
</mm:content>