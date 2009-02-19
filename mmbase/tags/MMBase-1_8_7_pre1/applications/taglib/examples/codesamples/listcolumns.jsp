<% int cols = 4; %>
<table>
  <tr>
    <mm:listnodes type="typedef">
      <td><mm:field name="name" /></td>
      <mm:index jspvar="i" write="false">
        <% if (i.intValue() % cols == 0) { %>
        <mm:last inverse="true"></tr><tr></mm:last>
        <% } else { %>
        <mm:last><td colspan="<%= cols - i.intValue() % cols%>" /></mm:last>
        <% } %>
      </mm:index>
    </mm:listnodes>
  </tr>
</table>