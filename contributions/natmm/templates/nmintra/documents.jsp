<%@page import="com.finalist.tree.*,nl.leocms.util.tools.documents.*" %>
<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%
// ** check whether documents root exists
// ** if there are no documents of type "file" related to this page: add all documents under the documents_root to this page
%>
<mm:node number="documents_root" notfound="skipbody">
   <mm:import id="root_document_exists" />
</mm:node>
<mm:notpresent referid="root_document_exists">
   <% (new DirReader()).run(); %>
</mm:notpresent>
<mm:node number="<%= pageId %>" jspvar="thisPage">
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
<div class="<%= infopageClass %>" style="padding-right:10px;padding-left:10px;padding-top:20px;">
<%@include file="includes/relatedteaser.jsp" %>
<% DocumentsTreeModel model = new DocumentsTreeModel(cloud);
   HTMLTree t = new HTMLTree(model,"documents");
   t.setCellRenderer(new DocumentsRenderer(cloud,pageId));
   t.setExpandAll(false);
   t.setImgBaseUrl("media/");
   t.render(out);
%>
<script language="Javascript1.2">restoreTree();</script>
</div>
</td><%-- 

*************************************** right bar *******************************
--%><td>&nbsp;</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
