<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator">
  <mm:listnodes type="classes" constraints="name = 'Docentencorps'">
    <mm:node id="teacherclass" />
  </mm:listnodes>
  <mm:notpresent referid="teacherclass">
    <mm:createnode type="classes" id="teacherclass">
      creating class with name "Docentencorps"<br/>
      <mm:setfield name="name">Docentencorps</mm:setfield>   
    </mm:createnode>
  </mm:notpresent>
  <mm:list path="people,related,roles" fields="people.number" distinct="true" constraints="roles.name = 'teacher'">
    <mm:node element="people" id="teacher">
      <mm:remove referid="hasclass" />
      <mm:related path="classrel,classes" max="1"><mm:import id="hasclass" /></mm:related>
      <mm:notpresent referid="hasclass">
        putting <mm:field name="firstname" /> <mm:field name="suffix" /> <mm:field name="lastname" /> (<mm:field name="username" />) in class "Docentencorps"<br/>
        <mm:createrelation source="teacher" destination="teacherclass" role="classrel" />
      </mm:notpresent>
    </mm:node>
  </mm:list>
</mm:cloud>

