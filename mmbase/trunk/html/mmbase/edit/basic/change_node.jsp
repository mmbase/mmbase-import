<%@ include file="page_base.jsp"
%><mm:content language="$config.lang" country="$config.country" expires="0" type="text/html" postprocessor="none">
<mm:cloud loginpage="login.jsp" sessionname="$config.session" jspvar="cloud" rank="$rank">
  <mm:param name="org.mmbase.xml-mode" value="$config.xmlmode" />
  <mm:param name="org.mmbase.richtext.wiki.show_broken"    value="true" />
  <mm:write referid="style" escape="none" />
  <title><%= m.getString("change_node.change")%></title>




<mm:context id="change_node">
<mm:import externid="node_number" required="true" from="parameters" escape="trimmer" />
<!-- We use two forms to avoid uploading stuff when not needed, because we cancel or only delete.-->

<mm:url page="change_node.jsp" id="purl" write="false" referids="node_number" />

<mm:node id="this_node" referid="node_number" notfound="skipbody" jspvar="thisNode">

<%

   if (urlStack.size() == 0) {
      push(urlStack, "home", "search_node.jsp?node_type=" + thisNode.getNodeManager().getName());
   }
   if (urlStack.size() == 1) {
       push(urlStack, "" + thisNode.getNumber(), "change_node.jsp?node_number=" + thisNode.getNumber());
   }
 %>
  <meta name="MMBase-NodeType"     content="<%=thisNode.getNodeManager().getName()%>" />
  <meta name="MMBase-SessionName"     content="${config.session}" />
</head>
<body class="basic" onLoad="document.change.elements[0].focus();">
<p class="crumbpath"><%= toHtml(urlStack, request) %></p>

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
   <form name="change" enctype="multipart/form-data" method="post" action='<mm:url referids="this_node@node_number" page="commit_node.jsp?pop=1" ><mm:param name="node_type"><mm:nodeinfo type="nodemanager" /></mm:param></mm:url>'>
  <table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
  <tr><th colspan="3">

    <div style="width: 30em; overflow: hidden;"><mm:nodeinfo type="gui" /></div>:
  <%=m.getString("Node")%><mm:link page="change_node.jsp" referids="_node@node_number">
       <a href="${_}"><mm:field name="number" /></a>
     </mm:link>  <%=m.getString("oftype")%> <mm:nodeinfo type="guinodemanager"  />
  ( <mm:nodeinfo type="nodemanager" /> )

    <a href="<mm:url page="navigate.jsp" referids="this_node@node_number" />">
      <span class="tree"><!-- needed for IE --></span><span class="alt">[tree]</span>
     </a>
     <a href="<mm:url page="functions.jsp" referids="this_node@node_number" />">
       Functions-view.
     </a>
     <mm:present referid="hasmmxf">
       <mm:write cookie="mmjspeditors_xmlmode"       referid="config.xmlmode"      />
       <mm:import id="xmlmodes" vartype="list">wiki,xml,prettyxml,kupu,docbook</mm:import>
       <mm:stringlist referid="xmlmodes">
         <mm:compare referid2="config.xmlmode" inverse="true">
           <a href="<mm:url referids="this_node@node_number">
             <mm:param name="mmjspeditors_xmlmode"><mm:write /></mm:param>
             </mm:url>"><mm:write /></a>
           </mm:compare>
           <mm:compare referid2="config.xmlmode">
             <mm:write />
           </mm:compare>
           <mm:last inverse="true"> | </mm:last>
         </mm:stringlist>
         | <a target="_new" href="<mm:url referids="node_number" page="mmxf_preview.jsp" />">preview</a>
       </mm:present>
   </th></tr>
   <mm:fieldlist id="my_form" type="edit" fields="owner">
     <tr>
       <td class="data" title="<mm:fieldinfo type="description"  escape="text/html/attribute" /> (<mm:fieldinfo type="datatype" />: <mm:fieldinfo type="datatypedescription" escape="text/html/attribute" />)"><em><mm:fieldinfo type="guiname" /></em> <small>(<mm:fieldinfo type="name" />)</small></td>
       <td class="listdata" colspan="2">
         <mm:fieldinfo type="$showtype" />
         <mm:isnull>(null)</mm:isnull>
         <mm:fieldinfo type="errors" />
       </td>
     </tr>
   </mm:fieldlist>
<tr>
<td colspan="3" class="buttons">
<p>
<input class="submit"  id="okbutton" type ="submit" name="ok" value="<%=m.getString("ok")%>" />
<input class="submit"  id="savebutton" type ="submit" name="save" value="save" />
<input class="submit"   type ="submit" name="cancel" value="<%=m.getString("cancel")%>" />
<mm:maydelete>
   <!-- input class="submit"   type ="submit" name="delete" value="<%=m.getString("delete")%>" /-->
   <input onclick="return confirm('Are you sure? This wil delete 1 node and <mm:countrelations /> relations!');" class="submit"   type ="submit" name="deleterelations"   value="<%=m.getString("change_node.deletewith")%>" />
</mm:maydelete>
</p>
</td>
</tr>
<tr><td colspan="3" class="search"><hr /></td></tr>
<tr>
  <th><%=m.getString("change_node.aliases")%></th>

  <td class="data" width="90%"><mm:aliaslist><mm:write /><mm:last inverse="true">, </mm:last></mm:aliaslist></td>
  <td class="navigate" width="0%">
 <mm:maywrite>
 <a  href="<mm:url referids="this_node@node_number"  page="edit_aliases.jsp" />">
       <span class="select"><!-- needed for IE --></span><span class="alt">[edit aliases]</span>
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
