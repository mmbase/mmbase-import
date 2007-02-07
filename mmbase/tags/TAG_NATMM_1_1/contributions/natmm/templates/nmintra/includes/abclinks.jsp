<% 
// show navigation to pages in terms-abc.
%><table cellpadding="0" cellspacing="0" border="0" align="center">
    <tr>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><img src="media/spacer.gif" width="1" height="1"></td>
        <td><div><%
            for(char i='A'; i<='Z'; i++) {
                String abcConstraints = "UPPER(terms.name) LIKE '" + i + "%'";
                %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,terms" constraints="<%= abcConstraints %>"
                    ><mm:first><%
						if(abcId.indexOf(i)!=-1) {
							%><%= i %>&nbsp;&nbsp;<%
						} else { 
							%><a href="terms.jsp<%= templateQueryString %>&abc=<%= i %>"><%= i 
	                        %></a>&nbsp;&nbsp;<%
						} 
					%></mm:first
				></mm:list><%
            } 
        %></div>
        </td>
    </tr>
    <tr>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
        <td><img src="media/spacer.gif" width="1" height="10"></td>
    </tr>
</table>