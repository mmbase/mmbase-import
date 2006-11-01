<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
<% if (cloud.getUser().getIdentifier() != null && !"anonymous".equals(cloud.getUser().getIdentifier())) { %>
  <%@include file="/shared/setImports.jsp" %>
  <di:ifsetting component="core" setting="showtranslationbox">
    <di:hasrole role="systemadministrator">
      <mm:treeinclude page="/translation/render.jsp" objectlist="$includePath" referids="$referids" />
    </di:hasrole>
  </di:ifsetting>
<% } %>
</mm:cloud>
</mm:content>
</body>
</html>
