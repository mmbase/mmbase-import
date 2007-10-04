<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<div 
  class="mm_c mm_c_core mm_c_b_builders ${requestScope.componentClassName}"
  id="${requestScope.componentId}">
  
  <h3>Builder Overview <mm:cloudinfo type="rank" /></h3>
  <table summary="builders" border="0" cellspacing="0" cellpadding="3">
	<caption>
	  This overview lists all known builders.<br />
	  The first list contains all builders that are currently 'active' (accessible through MMBase).
	  The second list (if available) lists all builders for which the definition is known, but which are currently inactive
	  (and thus inaccessible).
	</caption>
	<tr>
	  <th>Name</th>
	  <th>Version</th>
	  <th>Installed</th>
	  <th>Maintainer</th>
	  <th class="center">View</th>
	</tr>
	<mm:nodelistfunction id="builders" module="mmadmin" name="BUILDERS">
	  <mm:field name="item3">
		<mm:compare value="no" inverse="true">
		  <tr>
			<td>
			  <mm:import id="builder" reset="true"><mm:field name="item1" /></mm:import>
			  <mm:link page="builders-actions" referids="builder">
				<a href="${_}">${builder}</a>
			  </mm:link>
			</td>
			<td><mm:field name="item2" /></td>
			<td><mm:field name="item3" /></td>
			<td><mm:field name="item4" /></td>
			<td class="center">
			  <mm:link page="builders-actions" referids="builder">
				<a href="${_}"><img src="<mm:url page="/mmbase/style/images/search.png" />" alt="view" /></a>
			  </mm:link>
			</td>
		  </tr>
		</mm:compare>
	  </mm:field>
	</mm:nodelistfunction>

	<tr><th colspan="5">Not installed</th></tr>
	<mm:listnodes referid="builders">
	  <mm:field name="item3">
		<mm:compare value="no">
		  <tr>
			<td>
			  <mm:import id="builder" reset="true"><mm:field name="item1" /></mm:import>
			  <mm:write referid="builder" />
			</td>
			<td><mm:field name="item2" /></td>
			<td><mm:field name="item3" /></td>
			<td><mm:field name="item4" /></td>
			<td class="center">
			  <mm:link referids="builder" page="builder/actions.jsp">
			    <a href="${_}"><img src="<mm:url page="/mmbase/style/images/search.png" />" alt="view" /></a>
			  </mm:link>
			</td>
		  </tr>
		</mm:compare>
	  </mm:field>
	</mm:listnodes>
  </table>

</div>
</mm:cloud>
