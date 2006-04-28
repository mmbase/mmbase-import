<select name="<%=property%>" >
<% if (value!=null) { %>
<option selected="selected"><%=value%></option>
<% } %>
<option>STRING</option>
<option>INTEGER</option>
<option>NODE</option>
<option>LONG</option>
<option>FLOAT</option>
<option>DOUBLE</option>
<option>BYTE</option>
</select>
