<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
   <mm:compare referid="user" referid2="_node" inverse="true">
     <!-- check if the current person has the role student or teacher -->
     <mm:compare referid="mode" value="students">
       <di:hasrole referid="_node" role="student" education="edu">
         <mm:remove referid="itemno"/>
         <mm:field id="itemno" name="number" write="false"/>
         &amp;nbsp;&amp;nbsp; <!-- there exists a nice technique named CSS, why not using that? -->
         <jsp:directive.include file="show_person.jsp" />
         <br />
       </di:hasrole>
      </mm:compare>
      <mm:compare referid="mode" value="teachers">
        <di:hasrole referid="_node" role="teacher" education="edu">
          <mm:remove referid="itemno"/>
          <mm:field id="itemno" name="number" write="false"/>
          &amp;nbsp;&amp;nbsp;
          <jsp:directive.include file="show_person.jsp" />
          <br />
        </di:hasrole>
      </mm:compare>
   </mm:compare>
</jsp:root>

