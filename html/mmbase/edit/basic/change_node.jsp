<%@ include file="page_base.jsp"
%><mm:cloud method="http" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>Change a node</title>
</head>
<body class="basic" onLoad="document.change.elements[0].focus();">
<mm:context id="change_node">
<mm:import externid="node_number" required="true" from="parameters"/>
<!-- We use two forms to avoid uploading stuff when not needed, because we cancel or only delete.-->
<mm:import externid="backpage_cancel"><mm:url referids="node_number"/>#relations</mm:import>
<mm:import externid="backpage_ok"><mm:url referids="node_number" />#relations</mm:import>

<mm:node id="this_node" referid="node_number"> 

<mm:maywrite>
  <mm:import id="showtype">input</mm:import>
</mm:maywrite>
<mm:maywrite inverse="true">
 <mm:import id="showtype">guivalue</mm:import>
 You may not edit this node.
</mm:maywrite>
<%-- create the form
     by the way, it is not necessary to indicate that
     enctype="multipart/form-data", this will be automatic if there is
     a input type="file". But lynx will also work like this (except for images) --%>
<form name="change" enctype="multipart/form-data" method="post" action='<mm:url referids="node_number" page="commit_node.jsp" ><mm:param name="node_type"><mm:nodeinfo type="nodemanager" /></mm:param></mm:url>'>
  <table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
  <tr><th colspan="3">
  <mm:field name="gui()" />:
  Node <mm:field name="number" /> of type <mm:nodeinfo type="guinodemanager"  />
  ( <mm:nodeinfo type="nodemanager" /> )
  </th></tr>
    <mm:fieldlist id="my_form" type="edit">
      <tr>
        <td class="data"><em><mm:fieldinfo type="guiname" /></em> <small>(<mm:fieldinfo type="name" />)</small></td>
        <td class="listdata" colspan="2"><mm:fieldinfo type="${showtype}" />&nbsp;</td>
      </tr>
    </mm:fieldlist>
    <mm:maychangecontext>
	<mm:write referid="this_node" vartype="Node" jspvar="node1">
    <tr>
      <td class="data"><em>Context</em> <small>(<%= node1.getContext() %>)</small></td>
      <td class="listdata" colspan="2">
      <input type="checkbox" name="_my_form_change_context" />change
      <select name="_my_form_context"> <%
	 try{
	 String context = node1.getContext();
	 StringIterator possibleContexts = node1.getPossibleContexts().stringIterator();
	 while (possibleContexts.hasNext()) {
	     String listContext = possibleContexts.nextString();
	     if (context.equals(listContext)){
		 out.println("       <option selected=\"selected\">" + listContext + "</option>");
	     } else {
		 out.println("       <option>" + listContext + "</option>");
	     }
	 }
	 } catch (Exception e) {}
%>    </select>
     </td></tr>
     </mm:write>
	 </mm:maychangecontext>
<tr>
<td colspan="3" class="buttons">
<mm:maydelete>
   <input class="submit"   type ="submit" name="deleterelations"   value="delete with relations" />
   <input class="submit"   type ="submit" name="delete" value="delete" />
</mm:maydelete>
<input class="submit"   type ="submit" name="cancel" value="cancel" />
<input class="submit"   type ="submit" name="ok" value="ok" />
</td>
</tr>
<tr><td colspan="3" class="search"><hr /></td></tr>
<tr>
  <th>aliases</th>
  <td class="data" width="90%"><mm:aliaslist><mm:write /><mm:last inverse="true">, </mm:last></mm:aliaslist></td>
  <td class="navigate" width="0%">
 <a href="<mm:url referids="node_number" page="edit_aliases.jsp" />"><img src="images/select.gif" alt="[edit aliases]" width="20" height="20" border="0" align="right"/></a>     
 </td>
</tr>
</table>
</form>

<!-- list relations: -->
<hr />
<a name="relations" />
<%@ include file="relations.jsp"%>

</mm:node>
</mm:context>
<%@ include file="foot.jsp"  %>
</mm:cloud>