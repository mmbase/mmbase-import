<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud method="asis"
>  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>
<mm:import externid="bugreport" required="true"/>

<form action="<mm:url referids="parameters,$parameters"><mm:param name="btemplate">fullview.jsp</mm:param><mm:param name="flap">change</mm:param></mm:url>" method="POST">
<table cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list" width="70%">
<tr>
	<th>current Maintainer</th>
	<th>New Maintainer</th>
	<th>Action</th>
</tr>
<tr>
  <td>
    <mm:list path="bugreports,rolerel,users" nodes="$bugreport" constraints="rolerel.role='maintainer'">
      <mm:field name="users.firstname" /> <mm:field name="users.lastname" /><br />
    </mm:list>
    &nbsp;
  </td>
  <td>
    <select name="maintainer">      
      <mm:list path="users,groups"  constraints="groups.name='BugTrackerCommitors'">
        <option value="<mm:field name="users.number" />"><mm:field name="users.firstname" /> <mm:field name="users.lastname" /></option>
		  </mm:list>
      &nbsp;
	   </select>
	</td>
  <td>
    <input type="hidden" name="action" value="addmaintainer" />
    <input type="hidden" name="bugreport" value="<mm:write referid="bugreport"/>" />
    <input type="submit" value="SAVE" />
	</td>
</tr>
</table>
</form>
</mm:cloud>