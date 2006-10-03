<form action="<mm:url referids="parameters,$parameters,url" />" method="post">
<table summary="<mm:nodeinfo nodetype="$nodetype" type="guitype" />">
  <mm:fieldlist nodetype="$nodetype" fields="$fields">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="searchinput" /></td</tr>
   </mm:fieldlist>
   <tr><td colspan="2"><input type="submit" value="<%=getPrompt(m,"search")%>" name="search" /></td></tr>
</table>
</form>

