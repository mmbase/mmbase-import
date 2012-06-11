<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html><jsp:directive.include file="page_base_functionality.jsp"
/><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<mm:cloud  loginpage="login.jsp" sessionname="$config.session" rank="$rank" uri="$config.uri" jspvar="cloud">
  <mm:param name="org.mmbase.xml-mode" value="$config.xmlmode" />
  <mm:import externid="node_type" required="true" />
  <mm:formatter xslt="xslt/framework/head.xslt" escape="none">
    <head>
      <title>Create a node</title>
      <jsp:directive.include file="head.entries.jsp" />
      <jsp:directive.include file="editor.head.jsp" />
      <meta name="MMBase-NodeType"     content="${node_type}" />
    </head>
  </mm:formatter>
<mm:context id="create_node">



<body class="basic" onLoad="document.create.elements[3].focus();">


<form name="create" enctype="multipart/form-data" method="post" action='<mm:url referids="node_type" page="commit_node.jsp" />'>
<input type="hidden" name="new" value="new" />
<mm:import externid="node" />
<mm:present referid="node">
    <mm:import externid="role_name" />
    <mm:import externid="direction" />
    <input type="hidden" name="node" value="<mm:write referid="node" />" />
    <input type="hidden" name="role_name" value="<mm:write referid="role_name" />" />
    <input type="hidden" name="direction" value="<mm:write referid="direction" />" />
</mm:present>
<table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
<tr><th colspan="2"><%=m.getString("create_node.new")%> <mm:write referid="node_type" /></th></tr>
    <mm:fieldlist id="my_form" type="edit" nodetype="$node_type" >
       <tr>
         <td class="data"><em><mm:fieldinfo type="guiname" /></em> <small>(<mm:fieldinfo type="name" />)</small></td>
         <td class="listdata">
           <mm:fieldinfo type="input" />
           <mm:fieldinfo type="errors" />
         </td>
       </tr>
    </mm:fieldlist>
<%--tr><td class="data"><em><%=m.getString("change_node.context")%></em></td>
    <td class="listdata"><%=((org.mmbase.mode.core.MMBase.getMMBaseCop().getAuthentication().log %></td>
</tr--%>

        <tr>
     <td class="data"><em><%= m.getString("alias")%></em></td>
     <td class="listdata"><input type="text" name="alias_name" /></td>
        </tr>
 <tr>
<td colspan="2" class="buttons">
<input class="submit"  id="okbutton" type ="submit" name="ok" value="<%=m.getString("ok")%>" />
<input class="submit"   type ="submit" name="cancel" value="<%=m.getString("cancel")%>" />
</td>
</tr>
</table>
</form>
</mm:context>
<%@ include file="foot.jsp" %>
</mm:cloud>
</mm:content>
