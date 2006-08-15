    <mm:related path="posrel,tests" constraints="posrel.pos=$testpos">
      <mm:field name="tests.number" jspvar="this_test" vartype="String" write="false">
        <mm:node element="tests" id="this_test">
          <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
            <mm:constraint field="copybooks.number" referid="copybookNo"/>
            <mm:relatednodes>
              <mm:import id="madetest" reset="true"><mm:field name="number"/></mm:import>
            </mm:relatednodes>
          </mm:relatednodescontainer>
          <mm:notpresent referid="madetest">
            <mm:createnode type="madetests" id="madetest">
              <% long currentDate = System.currentTimeMillis() / 1000; %>
              <mm:setfield name="date"><%=currentDate%></mm:setfield>
              <mm:setfield name="testpath"><%= getTestpath(cloud,this_test) %></mm:setfield>
              <mm:setfield name="score"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:setfield>
            </mm:createnode>
            <mm:createrelation role="related" source="this_test" destination="madetest"/>
            <mm:createrelation role="related" source="copybookNo" destination="madetest"/>
          </mm:notpresent>
        </mm:node>
      </mm:field>
    </mm:related>
