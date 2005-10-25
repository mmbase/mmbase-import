<%// Global language code %>
<mm:present referid="provider">
  <mm:node number="$provider">
    <mm:field name="locale">
      <mm:isnotempty>
        <mm:field name="path">
          <mm:isnotempty>
            <mm:import jspvar="locale"><mm:field name="locale" />_<mm:field name="path" /></mm:import>
            <di:translate locale="<%=locale%>" debug="true" />
          </mm:isnotempty>
          <mm:isempty>
            <mm:import jspvar="locale"><mm:field name="locale" /></mm:import>
            <di:translate locale="<%=locale%>" debug="true" />
          </mm:isempty>
        </mm:field>
      </mm:isnotempty>
      <mm:isempty>
        <di:translate locale="" debug="true" />
      </mm:isempty>
    </mm:field>
  </mm:node>
</mm:present>

<%-- fall back to the default: english (no locale specified) --%>
<mm:notpresent referid="provider">
  <di:translate locale="" debug="true" />
</mm:notpresent>
