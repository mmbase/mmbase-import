<% int pageCount = 0; %>
<mm:list nodes="<%= page_number %>" path="page,posrel,mmevents" fields="posrel.pos"
 	constraints="<%= timeConstraint %>" >
	<mm:field name="posrel.pos" jspvar="page_count" vartype="Integer" write="false">
		<% pageCount += page_count.intValue(); %>
	</mm:field>
</mm:list>
<%	boolean showPage = (selection==-1);
	if(!showPage) { // check if pages falls in selection
		Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCount));
		if(numberOfPages!=null) {
			showPage = true;
			if(numberOfPages.intValue()==1) {
				pageCounts.remove(new Integer(pageCount));
			} else {
				pageCounts.put(new Integer(pageCount),new Integer(numberOfPages.intValue()-1));
			}
		}
	} 
	if(showPage) { %>
	<tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
		<td>&nbsp;</td><td>&nbsp;</td><td><%= page_title %></td>
		<td>
			<img src="../media/bar-orange.gif" alt="" width="<%= (100*pageCount / maxPageCount) %>" height="5" border=0>&nbsp;(<%= pageCount %>)
		</td>
	</tr>
<% }
%>


