<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<div class="columnRight">
 <div class="titlefield2">
   <di:translate key="core.news" />
 </div>
 <!-- IF THERE IS NO PORTALPAGES -->
  <mm:import id="hasPortalPages" externid="hasPortalPages" required="true"/>
  <mm:present referid="hasPortalPages" inverse="true">
    <div class="ListRight">
     <mm:node number="component.news" notfound="skipbody">
       <mm:treeinclude page="/news/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" />
     </mm:node>
    </div>
  </mm:present>
  <!-- IF THERE IS PORTALPAGES -->
  <mm:present referid="hasPortalPages">
    <div class="ListRightHalf">
      <mm:node number="component.news" notfound="skipbody">
         <mm:treeinclude page="/news/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" />
      </mm:node>
   </div>
   <div class="ListRight">
     <mm:include page="loginbox.jsp"/>
   </div>
  </mm:present>
</div>

</mm:cloud>
</mm:content>