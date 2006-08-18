<mm:import id="list_of_coaches" reset="true"></mm:import>

<mm:node number="$education" notfound="skip">
   <mm:related path="classrel,people">
      <mm:node element="people">
         <mm:remove referid="to" />
         <mm:import id="to"><mm:field name="email"/></mm:import>

         <mm:compare referid="to" value="" inverse="true">
            <mm:compare referid="list_of_coaches" value="">
               <mm:import id="tmp" reset="true"><mm:field name="number"/></mm:import>
            </mm:compare>
            <mm:compare referid="list_of_coaches" value="" inverse="true">
               <mm:import id="tmp" reset="true"><mm:write referid="list_of_coaches"/>,<mm:field name="number"/></mm:import>
            </mm:compare>
         
            <mm:related path="related,roles" constraints="roles.name='teacher'">
               <mm:import id="list_of_coaches" reset="true"><mm:write referid="tmp"/></mm:import>
            </mm:related>
         </mm:compare>
         
      </mm:node>
   </mm:related>

   <mm:related path="classrel,classes,classrel,people">
      <mm:node element="people">
         <mm:remove referid="to" />
         <mm:import id="to"><mm:field name="email"/></mm:import>

         <mm:compare referid="to" value="" inverse="true">
            <mm:compare referid="list_of_coaches" value="">
               <mm:import id="tmp" reset="true"><mm:field name="number"/></mm:import>
            </mm:compare>
            <mm:compare referid="list_of_coaches" value="" inverse="true">
               <mm:import id="tmp" reset="true"><mm:write referid="list_of_coaches"/>,<mm:field name="number"/></mm:import>
            </mm:compare>
         
            <mm:related path="related,roles" constraints="roles.name='teacher'">
               <mm:import id="list_of_coaches" reset="true"><mm:write referid="tmp"/></mm:import>
            </mm:related>
         </mm:compare>

      </mm:node>
   </mm:related>


</mm:node>
