<% int cols = 4; %>
<table>
<tr>
    <mm:listnodes type="typedef">
      <td><mm:field name="name" /></td>
      <mm:index jspvar="i">
       <% if (i.intValue() % cols == 0) { %>
          </tr><tr>
       <% } %>
      </mm:index>
      <mm:last>
        <mm:index jspvar="i">
            <td colspan="<%= cols - i.intValue() % cols%>" />
        </mm:index>        
      </mm:last>     
    </mm:listnodes>
</tr>
</table>