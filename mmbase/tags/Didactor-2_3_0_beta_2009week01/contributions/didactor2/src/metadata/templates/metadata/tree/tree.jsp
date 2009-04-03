<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@page import="nl.didactor.tree.*,nl.didactor.metadata.tree.*" %>
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>
   <mm:import externid="wizardjsp" jspvar="wizardjsp" />
   <mm:import externid="listjsp" jspvar="listjsp" />
   <mm:import externid="locale" jspvar="locale" vartype="object" />
   <% MetadataTreeModel model = new MetadataTreeModel(cloud);
      HTMLTree t = new HTMLTree(model,"metadata");
      t.setCellRenderer(new MetadataRenderer(cloud, wizardjsp, listjsp, locale.toString()));
      t.setExpandAll(false);
      t.setImgBaseUrl("gfx/");
      t.render(out);
   %>
   <script language="Javascript1.2">restoreNavTree();</script>
</mm:cloud>
