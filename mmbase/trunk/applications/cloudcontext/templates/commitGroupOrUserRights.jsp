   <mm:import id="operations" vartype="list">create,read,write,delete,change context</mm:import>
<mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Contexts.GRANT_ARGUMENTS">
     <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
     <mm:listnodes id="thiscontext" type="mmbasecontexts">  
       <mm:stringlist referid="operations">
         <mm:param name="operation"><mm:write /></mm:param>
         <mm:import id="right" externid="$_:$thiscontext" />
         <mm:compare referid="right" value="on">
            <mm:function write="false" name="grant" />
         </mm:compare>
         <mm:compare referid="right" value="on" inverse="true">
             <mm:function write="false" name="revoke" />
         </mm:compare>
       </mm:stringlist>
   </mm:listnodes>
   </mm:functioncontainer>
