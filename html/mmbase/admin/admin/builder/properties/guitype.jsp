<select name="<%=property%>" >
<% if (value!=null) { %>
<option selected="selected"><%=value%></option>
<% } %>
<option>string</option>
<option>field</option>
<option>integer</option>
<option>long</option>
<option>float</option>
<option>double</option>
<option>byte</option>
<option>eventtime</option>
<option>newfile</option>
<option>newimage</option>
</select>
