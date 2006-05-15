   <%// Direct relation people->classrel->education %>
   <mm:compare referid="class" value="null">

      <mm:import id="copybookNo"/>
      <mm:relatedcontainer path="classrel,educations">
         <mm:constraint field="educations.number" value="$education"/>

         <mm:related>
            <mm:node element="classrel">
               <mm:relatednodes type="copybooks">
                  <mm:remove referid="copybookNo"/>
                  <mm:field id="copybookNo" name="number" write="false"/>
               </mm:relatednodes>
            </mm:node>
         </mm:related>
      </mm:relatedcontainer>
   </mm:compare>



   <%// people->classrel->class->related->education %>
   <mm:compare referid="class" value="null" inverse="true">

      <mm:import id="copybookNo"/>
      <mm:relatedcontainer path="classrel,classes">
         <mm:constraint field="classes.number" value="$class"/>

         <mm:related>
            <mm:node element="classrel">
               <mm:relatednodes type="copybooks">
                  <mm:remove referid="copybookNo"/>
                  <mm:field id="copybookNo" name="number" write="false"/>
               </mm:relatednodes>
            </mm:node>
         </mm:related>
      </mm:relatedcontainer>
   </mm:compare>


