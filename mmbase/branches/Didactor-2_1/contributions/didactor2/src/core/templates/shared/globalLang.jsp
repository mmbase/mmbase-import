<%// Global language code %>
<mm:present referid="provider">
  <mm:node number="$provider">
    <mm:field name="locale">
      <mm:isnotempty>
        <mm:field name="path">
          <mm:isnotempty>
            <mm:import jspvar="locale"><mm:field name="locale" />_<mm:field name="path" /></mm:import>
            <mm:import id="language" reset="true"><mm:field name="locale" /></mm:import>
            <mm:present referid="education">
              <mm:node number="$education">
                <mm:field jspvar="edupath" vartype="String" write="false" name="path">
                  <% if (edupath != null && !"".equals(edupath)) {
                       locale += "_" + edupath;
                     }
                  %>
                </mm:field>
              </mm:node>
            </mm:present>
            <di:translate locale="<%=locale%>" debug="true" />
          </mm:isnotempty>
          <mm:isempty>
            <mm:import jspvar="locale"><mm:field name="locale" /></mm:import>
            <mm:import id="language" reset="true"><mm:field name="locale" /></mm:import>
            <di:translate locale="<%=locale%>" debug="true" />
          </mm:isempty>
        </mm:field>
      </mm:isnotempty>
      <mm:isempty>
        <di:translate locale="" debug="true" />
        <mm:import id="language" reset="true">en</mm:import>
      </mm:isempty>
    </mm:field>
  </mm:node>
</mm:present>

<%-- fall back to the default: english (no locale specified) --%>
<mm:notpresent referid="provider">
  <mm:import id="language" reset="true">en</mm:import>
  <di:translate locale="" debug="true" />
</mm:notpresent>
