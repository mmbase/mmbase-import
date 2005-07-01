<mm:node element="people" jspvar="person">

   <mm:import id="personno"><mm:field name="number"/></mm:import>
   <mm:compare referid="user" referid2="personno" inverse="true">
      <%// check if the current person has the role student or teacher %>
      <mm:compare referid="mode" value="students">
         <di:hasrole referid="personno" role="student" education="edu">
            <mm:remove referid="itemno"/>
            <mm:import id="itemno" jspvar="itemno"><mm:field name="number" /></mm:import>
            <%
               String[] arrstrPerson = new String[3];
               arrstrPerson[0] = "" + person.getNumber();
               arrstrPerson[1] = (String) person.getValue("lastname");
               arrstrPerson[2] = (String) person.getValue("firstname");
               sortsetUsers.add(arrstrPerson);
            %>
         </di:hasrole>
      </mm:compare>
      <mm:compare referid="mode" value="teachers">
         <di:hasrole referid="personno" role="teacher" education="edu">
            <mm:remove referid="itemno"/>
            <mm:import id="itemno" jspvar="itemno"><mm:field name="number" /></mm:import>
            <%
               String[] arrstrPerson = new String[3];
               arrstrPerson[0] = "" + person.getNumber();
               arrstrPerson[1] = (String) person.getValue("lastname");
               arrstrPerson[2] = (String) person.getValue("firstname");
               sortsetUsers.add(arrstrPerson);
            %>
         </di:hasrole>
      </mm:compare>
   </mm:compare>
   <mm:remove referid="personno"/>

</mm:node>
