<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

migrating page layout

<mm:listnodes type="pages">
    <mm:import id="pagenum" reset="true"><mm:field name="number"/></mm:import>
    <% int count = 0; %>
      <mm:write referid="pagenum"/>,
      <mm:related path="related,images">
 
        <mm:import id="relnum" reset="true"><mm:field name="related.number"/></mm:import>
        <mm:import id="imagenum" reset="true"><mm:field name="images.number"/></mm:import> 
        <mm:createrelation role="sizerel" source="pagenum" destination="imagenum">
            <mm:setfield name="width">100</mm:setfield>
            <mm:setfield name="height">100</mm:setfield>
            <mm:setfield name="pos"><%= count++ %></mm:setfield>
        </mm:createrelation>
        <mm:deletenode number="$relnum"/>
      </mm:related>
    <mm:setfield name="layout">0</mm:setfield>
    <mm:setfield name="imagelayout">0</mm:setfield>
</mm:listnodes>

ok.

</mm:cloud>
</mm:content>
