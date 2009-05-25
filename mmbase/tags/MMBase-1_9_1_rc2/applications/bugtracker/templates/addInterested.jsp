<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="parameters.jsp" 
%><%@include file="login.jsp" %>
<mm:import externid="bugreport" />

<form action="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate" value="addInterested.jsp" /></mm:url>" method="post">
<table>
<tr><th>Currently Interested</th><th>New Interested</th><th>Action</th></tr>

<tr>
	<td>
    <mm:listnodescontainer path="bugreports,rolerel,users" nodes="$bugreport" element="users">
      <mm:constraint field="rolerel.role" value="interested" />
      <mm:listnodes>
        <p><mm:field name="firstname" /> <mm:field name="lastname" /></p>
			</mm:listnodes>
    </mm:listnodescontainer>
	</td>
	<td>
	  <select name="interested">
			<mm:listnodescontainer path="users,groups" element="users">
         <mm:constraint field="groups.name" value="BugTrackerInterested" />
         <mm:listnodes>       
    				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /></option>
				</mm:listnodes>
      </mm:listnodescontainer>
		</select>
	</td>
  <td>
		<input type="submit" value="save" />
  </td>
</tr>
</table>
</form>
</mm:cloud>

