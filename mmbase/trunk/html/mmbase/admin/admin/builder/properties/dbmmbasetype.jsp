<%
   String typebuilder = request.getParameter("builder");
   String typefield = request.getParameter("field");
   DataType dt = typebuilder != null && typefield != null ? DataTypes.getDataType(org.mmbase.module.core.MMBase.getMMBase().getBuilder(typebuilder).getField(typefield).getType()) : null;
%>
<select name="mmbasetype" id="mmbasetype" onChange="getDataTypes(); validate();">
  <%
  Iterator i = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
  while (i.hasNext()) {
  DataType root = (DataType) i.next();
  %>
  <option <%= dt != null && root.getName().equals(dt.getName()) ? "selected='selected'" : "" %> value="<%=root.getName()%>"><%=root.getLocalizedGUIName().get(Locale.US)%></option>
  <%}%>
</select>
<%
i = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
while (i.hasNext()) {
DataType root = (DataType) i.next();
%>
<span style="display: none;" class="description" id="description_<%=root.getName()%>">
<%=root.getLocalizedDescription().get(Locale.US)%>
</span>
<%
if (root instanceof LengthDataType) {
%>
<span id="haslength_<%=root.getName()%>" style="display: none;"> </span>
<%
}
}%>
