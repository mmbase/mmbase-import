<mm:maywrite>
  <mm:context>
    <mm:import id="cat"><mm:field name="number" /></mm:import>
    <li><a name="c<mm:write referid="cat" />"></a>
      <mm:import id="context"><mm:field name="owner" /></mm:import>
      <mm:nodeinfo id="actualtype" type="nodemanager" write="false" />
        <mm:field name="gui()" />&nbsp;
      <mm:relatednodes type="templates" max="1" role="related">
        <mm:field id="template" name="number" write="false" />
      </mm:relatednodes>
      <mm:present referid="template">
        <mm:node number="$template">
          <%@include file="cat.template.li.jsp" %>
        </mm:node>
      </mm:present>
      <mm:relatednodes type="templates" role="posrel" orderby="posrel.pos" >
        <mm:context>
          <mm:import id="template" ><mm:field name="number" /></mm:import>
          <%@include file="cat.template.li.jsp" %>
        </mm:context>
      </mm:relatednodes>
    </li>
  </mm:context>
</mm:maywrite>
<mm:maywrite inverse="true">
  <mm:depth>
    <mm:islessthan referid="depth" value="5">
    <li>
      <span class="editintro">
        <mm:field name="gui()" />
      </span>
    </li>
    </mm:islessthan>
  </mm:depth>
</mm:maywrite>

