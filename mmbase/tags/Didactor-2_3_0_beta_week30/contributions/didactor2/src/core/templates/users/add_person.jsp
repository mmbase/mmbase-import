<mm:node element="people">
   <mm:compare referid="user" referid2="_node" inverse="true">
     <%// check if the current person has the role student or teacher %>
     <mm:compare referid="mode" value="students">
       <di:hasrole referid="_node" role="student" education="edu">
         <mm:remove referid="itemno"/>
         <mm:field id="itemno" name="number" write="false"/>
         &nbsp;&nbsp;
         <jsp:directive.include file="show_person.jsp" />
         <br />
       </di:hasrole>
      </mm:compare>
      <mm:compare referid="mode" value="teachers">
        <di:hasrole referid="_node" role="teacher" education="edu">
          <mm:remove referid="itemno"/>
          <mm:field id="itemno" name="number" write="false"/>
          &nbsp;&nbsp;
          <jsp:directive.include file="show_person.jsp" />
          <br />
        </di:hasrole>
      </mm:compare>
   </mm:compare>
</mm:node>
