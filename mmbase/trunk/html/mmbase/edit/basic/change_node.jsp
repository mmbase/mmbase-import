<%@ include file="page_base.jsp"
%><mm:content language="$config.lang" expires="0" type="text/html">
<mm:cloud method="$config.method" loginpage="login.jsp" sessionname="$config.session" jspvar="cloud">
<mm:write referid="style" escape="none" />
<title><%= m.getString("change_node.change")%></title>
</head>
<body class="basic" onLoad="document.change.elements[0].focus();">
<p class="crumbpath"><%= toHtml(urlStack, request) %></p>
<mm:context id="change_node">
<mm:import externid="node_number" required="true" from="parameters"/>
<!-- We use two forms to avoid uploading stuff when not needed, because we cancel or only delete.-->

<mm:url page="change_node.jsp" id="purl" write="false" referids="node_number" />

<mm:node id="this_node" referid="node_number" notfound="skipbody" jspvar="node">

<% if (urlStack.size() == 0) {
    push(urlStack, "home", "search_node.jsp?node_type=" + node.getNodeManager().getName());
   }
   if (urlStack.size() == 1) {
       push(urlStack, "" + node.getNumber(), request);
   }
 %>


<mm:import id="found" />

<mm:maywrite>
  <mm:import id="showtype">input</mm:import>
</mm:maywrite>
<mm:maywrite inverse="true">
 <mm:import id="showtype">guivalue</mm:import>
<h2><%= m.getString("change_node.maynotedit")%></h2>
</mm:maywrite>
<%-- create the form
     by the way, it is not necessary to indicate that
     enctype="multipart/form-data", this will be automatic if there is
     a input type="file". But lynx will also work like this (except for images) --%>
<form name="change" enctype="multipart/form-data" method="post" action='<mm:url referids="node_number" page="commit_node.jsp?pop=1" ><mm:param name="node_type"><mm:nodeinfo type="nodemanager" /></mm:param></mm:url>'>
  <table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
  <tr><th colspan="3">
  <mm:nodeinfo type="gui" />:
  <%=m.getString("Node")%> <mm:field name="number" /> <%=m.getString("oftype")%> <mm:nodeinfo type="guinodemanager"  />
  ( <mm:nodeinfo type="nodemanager" /> )

    <a href="<mm:url page="navigate.jsp" referids="node_number" />">
      <span class="tree"></span><span class="alt">[tree]</span>
     </a>
     </td>
  </th></tr>
    <mm:fieldlist id="my_form" type="edit" fields="owner">
      <tr>
        <td class="data"><em><mm:fieldinfo type="guiname" /></em> <small>(<mm:fieldinfo type="name" />)</small></td>
        <td class="listdata" colspan="2"><mm:fieldinfo type="$showtype" />&nbsp;</td>
      </tr>
    </mm:fieldlist>
<tr>
<td colspan="3" class="buttons">
<input class="submit"   type ="submit" name="ok" value="<%=m.getString("ok")%>" />
<input class="submit"   type ="submit" name="cancel" value="<%=m.getString("cancel")%>" />
<mm:maydelete>
   <!-- input class="submit"   type ="submit" name="delete" value="<%=m.getString("delete")%>" /-->
   <input class="submit"   type ="submit" name="deleterelations"   value="<%=m.getString("change_node.deletewith")%>" />
</mm:maydelete>
</td>
</tr>
<tr><td colspan="3" class="search"><hr /></td></tr>
<tr>
  <th><%=m.getString("change_node.aliases")%></th>
  <td class="data" width="90%"><mm:aliaslist><mm:write /><mm:last inverse="true">, </mm:last></mm:aliaslist></td>
  <td class="navigate" width="0%">
 <mm:maywrite>
 <a  href="<mm:url referids="node_number"  page="edit_aliases.jsp" />">
       <span class="select"></span><span class="alt">[edit aliases]</span>
</a>     
   </mm:maywrite>    
</tr>
</table>
</form>

<!-- list relations: -->
<hr />
<a name="relations"></a>
<%@ include file="relations.jsp"%>

</mm:node>
<mm:notpresent referid="found">
  <%=m.getString("change_node.notexists")%>: <mm:write referid="node_number" />
</mm:notpresent>
</mm:context>
<%@ include file="foot.jsp"  %>
</mm:cloud>
</mm:content>