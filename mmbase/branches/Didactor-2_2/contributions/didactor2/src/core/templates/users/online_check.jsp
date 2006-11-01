            <mm:node element="people">
               <mm:import id="personno" reset="true"><mm:field name="number"/></mm:import>
               <mm:compare referid="user" referid2="personno" inverse="true">
                  <mm:compare referid="mode" value="students">
                     <di:hasrole referid="personno" role="student" education="edu">
                        <mm:import id="show_this_item" reset="true">true</mm:import>
                     </di:hasrole>
                  </mm:compare>
                  <mm:compare referid="mode" value="teachers">
                     <di:hasrole referid="personno" role="teacher" education="edu">
                        <mm:import id="show_this_item" reset="true">true</mm:import>
                     </di:hasrole>
                  </mm:compare>
               </mm:compare>
               <mm:remove referid="personno"/>
               <mm:remove referid="isonline"/>
            </mm:node>
