<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

Removing old agendas and workspaces...
<mm:listnodescontainer type="agendas">
  <mm:constraint field="owner" value="admin" />
  <mm:listnodes id="old_agenda">
    <mm:related path="object" searchdir="source">
      <mm:node element="object" id="parent_node">
        <mm:related path="agendas" constraints="agendas.number != $old_agenda">
          <mm:field name="agendas.number" id="new_agenda" write="false" />
        </mm:related>
      </mm:node>
    </mm:related>

    <mm:present referid="new_agenda">
      <mm:related path="eventrel,items">
        <mm:node element="items" id="item">
          <mm:related path="agendas" constraints="agendas.number = $new_agenda">
            <mm:import id="alreadyrelated" />
          </mm:related>
        </mm:node>
        <mm:notpresent referid="alreadyrelated">
          <mm:field name="eventrel.start" id="rel_start" write="false" />
          <mm:field name="eventrel.stop" id="rel_stop" write="false" />
          <mm:createrelation role="eventrel" source="new_agenda" destination="item">
            <mm:setfield name="start"><mm:write referid="rel_start" /></mm:setfield>
            <mm:setfield name="stop"><mm:write referid="rel_stop" /></mm:setfield>
          </mm:createrelation>
        </mm:notpresent>
        <mm:remove referid="alreadyrelated" />
        <mm:node element="eventrel">
          <mm:deletenode deleterelations="true" />
        </mm:node>
      </mm:related>
      <mm:node number="$old_agenda">
        <mm:deletenode deleterelations="true" />
      </mm:node>
    </mm:present>
  </mm:listnodes>
</mm:listnodescontainer>

<mm:remove referid="parent_node" />

<mm:listnodescontainer type="workspaces">
  <mm:constraint field="owner" value="admin" />
  <mm:listnodes id="old_ws">
    <mm:related path="object" searchdir="source">
      <mm:node element="object" id="parent_node">
        <mm:related path="workspaces" constraints="workspaces.number != $old_ws">
          <mm:field name="workspaces.number" id="new_ws" write="false" />
        </mm:related>
      </mm:node>
    </mm:related>

    <mm:present referid="new_ws">
      <mm:related path="related,folders">
        <mm:node element="folders" id="folder">
          <mm:related path="workspaces" constraints="workspaces.number = $new_ws">
            <mm:import id="alreadyrelated" />
          </mm:related>
        </mm:node>
        <mm:notpresent referid="alreadyrelated">
          <mm:createrelation role="related" source="new_ws" destination="folder" />
        </mm:notpresent>
        <mm:remove referid="alreadyrelated" />
        <mm:node element="related">
          <mm:deletenode deleterelations="true" />
        </mm:node>
      </mm:related>
      <mm:node number="$old_ws">
        <mm:deletenode deleterelations="true" />
      </mm:node>
    </mm:present>
  </mm:listnodes>
</mm:listnodescontainer>

done. <br/>

</mm:cloud>
</mm:content>
</html>


