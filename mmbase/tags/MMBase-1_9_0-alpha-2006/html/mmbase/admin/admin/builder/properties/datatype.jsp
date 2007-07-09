<% Iterator rootIterator = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
while(rootIterator.hasNext()) {
DataType rootType = (DataType) rootIterator.next();
%>      
<select onChange="getDataTypes()" class="datatype" style="display: none;" name="datatype_off" id="datatype_<%=rootType.getName()%>">
<option value="">--</option>
<% Iterator j = org.mmbase.datatypes.DataTypes.getSystemCollector().getAllSpecializations(rootType.getName());
while (j.hasNext()) {
DataType dataType = (DataType) j.next();
%>
<option value="<%=dataType.getName()%>"><%=dataType.getLocalizedGUIName().get(Locale.US)%></option>
<%}%>
</select>
<%  j = org.mmbase.datatypes.DataTypes.getSystemCollector().getAllSpecializations(rootType.getName());
while (j.hasNext()) {
DataType dataType = (DataType) j.next();
%>
<span class="description" style="display: none;" id="description_<%=dataType.getName()%>">
<%=dataType.getLocalizedDescription().get(Locale.US)%>
</span>
<%}
}%>