<%@include file="/education/tests/definitions.jsp" %>
<mm:node number="$user">
  <di:copybook>
    <mm:node id="copybookNo" />
  </di:copybook>
</mm:node>
<mm:field name="number" jspvar="this_test" vartype="String" write="false">
  <!-- get madetest -->
  <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
    <mm:constraint field="copybooks.number" referid="copybookNo"/>
    <mm:relatednodes>
      <mm:field name="number" jspvar="this_madetest" vartype="String" write="false">
        <mm:import id="madetest" reset="true"><%= this_madetest %></mm:import>
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
    <mm:remove referid="this_test"/>
  </mm:notpresent>

  <!-- rate answers -->
  <mm:compare referid="step" value="save">
    <mm:relatednodes type="questions" path="posrel,questions" orderby="posrel.pos">
      <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/rate<mm:nodeinfo type="type"/>.jsp</mm:import>
      <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
        <mm:param name="question"><mm:field name="number"/></mm:param>
        <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
      </mm:treeinclude>
    </mm:relatednodes>
  </mm:compare>
  <!-- state questions -->
  <form name="questionform" action="<mm:treefile page="/assessment/index.jsp" objectlist="$includePath"  referids="$referids"/>" method="post">
    <input type="hidden" name="madetest_n" value="<mm:write referid="madetest"/>" />
    <mm:relatednodes type="questions" path="posrel,questions" orderby="posrel.pos">
      <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>
      <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
        <mm:param name="question"><mm:field name="number"/></mm:param>
        <mm:param name="testnumber"><%= this_test %></mm:param>
        <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
      </mm:treeinclude>
    </mm:relatednodes>
    <input type="submit" class="formbutton" value="<di:translate key="assessment.save" />" />
    <input type="hidden" name="step" value="save" />
  </form>

</mm:field>
