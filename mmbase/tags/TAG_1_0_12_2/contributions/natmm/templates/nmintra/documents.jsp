<%@page import="com.finalist.tree.*,nl.leocms.util.tools.documents.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/check_documents_root.jsp" %>
<% // ** if no documents of type "file" are related to this page: add all documents under the documents_root to this page %>
<mm:node number="<%= paginaID %>" jspvar="thisPage">
   <mm:related path="posrel,documents" max="1" constraints="documents.type='file'">
      <mm:import id="page_contains_file" />
   </mm:related>
   <mm:notpresent referid="page_contains_file">
      <mm:node number="documents_root" jspvar="subtreeDoc">
      	<% DirReader.mergeSubtree(cloud,thisPage,subtreeDoc); %>
      </mm:node>
   </mm:notpresent> 
</mm:node>
<%@include file="includes/header.jsp" %>
   <td>
      <%@include file="includes/pagetitle.jsp" %>
   </td>
   <td><%
      String rightBarTitle = "";
      %><%@include file="includes/rightbartitle.jsp" %>
   </td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage" style="padding-right:10px;padding-left:10px;padding-top:20px;">
<%@include file="includes/relatedteaser.jsp" %>
<% DocumentsTreeModel model = new DocumentsTreeModel(cloud);
   DocumentsHTMLTree t = new DocumentsHTMLTree(model,"documents");
   t.setCellRenderer(new DocumentsRenderer(cloud,paginaID));
   t.setExpandAll(false);
   t.setImgBaseUrl("media/");
   t.render(out);
%>
<script language="Javascript1.2">restoreTree();</script>
</div>
</td><%

// *************************************** right bar *******************************
%><td>&nbsp;</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
