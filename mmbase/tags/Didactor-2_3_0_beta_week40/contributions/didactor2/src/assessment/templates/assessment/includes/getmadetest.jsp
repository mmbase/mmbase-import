          <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
            <mm:constraint field="copybooks.number" referid="copybookNo"/>
            <mm:relatednodes>
              <mm:field name="number" jspvar="this_madetest" vartype="String" write="false">
                <mm:related path="related,problems" constraints="problems.number=$problem_n">
                  <mm:import id="madetest" reset="true"><%= this_madetest %></mm:import>
                </mm:related>
              </mm:field>
            </mm:relatednodes>
          </mm:relatednodescontainer>
          <mm:notpresent referid="madetest">
            <mm:createnode type="madetests" id="madetest">
              <% long currentDate = System.currentTimeMillis() / 1000; %>
              <mm:setfield name="date"><%=currentDate%></mm:setfield>
              <mm:setfield name="testpath"><%= getTestpath(cloud,this_test) %></mm:setfield>
              <mm:setfield name="score"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:setfield>
            </mm:createnode>
            <mm:node number="<%= this_test %>" id="this_test"/>
            <mm:createrelation role="related" source="this_test" destination="madetest"/>
            <mm:createrelation role="related" source="copybookNo" destination="madetest"/>
            <mm:node number="$problem_n" notfound="skip">
              <mm:createrelation role="related" source="problem_n" destination="madetest"/>
            </mm:node>
            <mm:remove referid="this_test"/>
          </mm:notpresent>
