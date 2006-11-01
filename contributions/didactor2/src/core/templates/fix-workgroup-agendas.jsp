<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<html>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

Fixing workgroup agendas...<br/>
<mm:listnodes type="workgroups" id="workgroup">
    <mm:field name="name" id="workgroupname"/>...
    <mm:countrelations type="agendas" id="numagendas">
        <mm:compare referid="numagendas" value="0">
            <mm:createnode type="agendas" id="agenda">
                <mm:setfield name="name">Agenda van werkgroep '<mm:write referid="workgroupname"/>'</mm:setfield>
            </mm:createnode>
            <mm:createrelation role="related" source="workgroup" destination="agenda"/>
            Created.
        </mm:compare>
        <br/>
    </mm:countrelations>
</mm:listnodes>

Done!
</mm:cloud>
</mm:content>
</html>
