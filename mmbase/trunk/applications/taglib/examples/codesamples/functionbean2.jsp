<mm:functioncontainer>
   <mm:param name="Parameter1">Kloink</mm:param>
   <mm:param name="Parameter2">5</mm:param>
   <p>
     Some function of the given bean: 
     <mm:function classname="org.mmbase.util.functions.ExampleBean" 
                  name="stringFunction" />
   </p>
   <p>
     Another function of the same bean:
     <mm:function classname="org.mmbase.util.functions.ExampleBean" 
                  name="integerFunction">
       <mm:isgreaterthan value="9">
         <mm:write /> is greater than 9!
       </mm:isgreaterthan>
     </mm:function>
   </p>
</mm:functioncontainer>