<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

creating workspaces for workgroups...<br/>
<mm:listnodes type="workgroups">
    <mm:remove referid="workgroup"/>
    <mm:field name="number" id="workgroup">
        <mm:field name="name" id="workgroupname">
            <mm:countrelations type="workspaces">
                <mm:islessthan value="1">
                    <mm:createnode id="workspace" type="workspaces">
                        <mm:setfield name="name">Werkruimte van werkgroep <mm:write referid="workgroupname"/></mm:setfield>
                    </mm:createnode>
                    <mm:createrelation source="workgroup" destination="workspace" role="related"/>.
                </mm:islessthan>
            </mm:countrelations>
        </mm:field>
    </mm:field>
</mm:listnodes>

Done.
</mm:cloud>
</mm:content>
</html>

