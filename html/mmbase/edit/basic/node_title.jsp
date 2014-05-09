title="<mm:formatter escape="spaceremover" format="none">
  <mm:function name="age">    
    <mm:write /> 
    <mm:compare value="1">day</mm:compare>
    <mm:compare value="1" inverse="true">days</mm:compare> old,
  </mm:function> 
  <mm:countrelations>
    <mm:write /> 
    <mm:compare value="1">relation</mm:compare>
    <mm:compare value="1" inverse="true">relations</mm:compare>
  </mm:countrelations>
  <mm:aliaslist>
    <mm:first>, <%=m.getString("change_node.aliases")%>: </mm:first>
    <mm:write />
    <mm:last inverse="true">, </mm:last>
  </mm:aliaslist>
</mm:formatter>"