<%@ include file="page_base.jsp"
%><mm:cloud method="http" sessionname="${SESSION}" jspvar="cloud">
<mm:write referid="style" />
<title>Create a node</title>
</head>
<mm:context id="create_node">
<mm:import externid="node_type" required="true" />

<body class="basic" onLoad="document.create.elements[3].focus();">


<form name="create" enctype="multipart/form-data" method="post" action='<mm:url referids="node_type" page="commit_node.jsp" />'>
<input type="hidden" name="new" value="new" />
<input type="hidden" name="backpage_cancel" value="<mm:url page="search_node.jsp" referids="node_type" />" />
<input type="hidden" name="backpage_ok" value="<mm:url page="change_node.jsp" referids="" />" />
<table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
<tr><th colspan="2">New node of type <mm:write referid="node_type" /></th></tr>
    <mm:fieldlist id="my_form" type="edit" nodetype="${node_type}" >
       <tr>
         <td class="data"><em><mm:fieldinfo type="guiname" /></em> <small>(<mm:fieldinfo type="name" />)</small></td>
         <td class="listdata"><mm:fieldinfo type="input" /></td>
       </tr>
    </mm:fieldlist>
        <tr>
	 <td class="data"><em>alias</em></td>
	 <td class="listdata"><input type="text" name="alias_name" /></td>
        </tr>
<tr>
<td colspan="2" class="buttons">
<input class="submit"   type ="submit" name="cancel" value="cancel" />
<input class="submit"   type ="submit" name="ok" value="ok" />
</td>
</tr>
</table>
</form>
</mm:context>
<%@ include file="foot.jsp" %>
</mm:cloud>