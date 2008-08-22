<%@page import = "nl.didactor.metadata.util.MetaDataMigrate" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" method="delegate">
   <%@include file="/shared/setImports.jsp" %>
   <% MetaDataMigrate.convert(cloud); %>
   Done.
</mm:cloud>


