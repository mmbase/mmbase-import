<%@ include file="page_base.jsp"
%><mm:cloud method="http" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>Edit aliases</title>
</head>
<body class="basic" onLoad="document.add_alias.new_alias.focus();">
<mm:context id="edit_aliases">
<mm:import externid="node_number" required="true" />
<mm:import externid="new_alias" />
<mm:import externid="delete_alias" />

<mm:node number="${node_number}" jspvar="node">

<mm:present referid="new_alias">
  <mm:createalias name="${new_alias}" />
</mm:present>

<mm:present referid="delete_alias">
  <mm:deletealias name="${delete_alias}" />
</mm:present>

<form name="add_alias" enctype="multipart/form-data" method="post" action='<mm:url referids="node_number" />'>
<table class="edit" summary="alias editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
<tr><th>Aliases of node <mm:field name="gui()" /></th></tr>
<tr><td>
<table width="100%">
<mm:aliaslist id="alias"> 
    <tr><td><mm:write /></td><td><a href="<mm:url referids="node_number">
         <mm:param name="delete_alias"><mm:write referid="alias" /></mm:param>
	 </mm:url>"><img src="images/delete.gif" alt="[delete]" width="20" height="20" border="0" align="right"/></a></td></tr>
</mm:aliaslist>
</table>
</td></tr>
<tr><td>Add alias:<input type="text" size="30" name="new_alias" /> 
<input type="submit"  name="add" value="add" /></td>
</td></tr>
<tr><td><a href="<mm:url page="change_node.jsp" referids="node_number" />" >
         <img src="images/previous.gif" alt="[back to node]" width="20" height="20" border="0" align="left"/>
		 </a></td></tr>
</table>
</form>
</mm:node>
</mm:context>
<%@ include file="foot.jsp"  %>
</mm:cloud>
