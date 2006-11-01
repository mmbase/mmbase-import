<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
  <mm:cloud rank="basic user">
    <jsp:directive.include file="/shared/setImports.jsp" />
    <div class="columnRight">
      <div class="titlefield2">
        <di:translate key="core.news" />
      </div>
      <!-- IF THERE IS NO PORTALPAGES -->
      <mm:import externid="hasPortalPages" />
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
