<select name="<%=property%>" >
<% if (value!=null) { %>
<option selected="selected"><%=value%></option>
<% } %>
<option>persistent</option>
<option>virtual</option>
<option>system</option>
</select>
