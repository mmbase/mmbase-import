<mm:node element="people">
  <mm:compare referid="user" referid2="_node" inverse="true">
    <mm:compare referid="mode" value="students">
      <di:hasrole referid="_node" role="student" education="edu">
        <mm:import id="show_this_item" reset="true">true</mm:import>
      </di:hasrole>
    </mm:compare>
    <mm:compare referid="mode" value="teachers">
      <di:hasrole referid="_node" role="teacher" education="edu">
        <mm:import id="show_this_item" reset="true">true</mm:import>
      </di:hasrole>
    </mm:compare>
  </mm:compare>
  <mm:remove referid="isonline"/>
</mm:node>
