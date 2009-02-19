<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:cloud  method="delegate" authenticate="class">
  <mm:import externid="email" />
  <!--  create the email node -->
  <mm:createnode id="mail1" type="email">
    <mm:setfield name="from">${email}</mm:setfield>
    <!-- WTF -->
    <mm:setfield name="subject"><mm:url absolute="true" page="example5_subject.jsp" /></mm:setfield>
    <mm:setfield name="body"><mm:url absolute="true" page="example5_body.jsp" /></mm:setfield>
  </mm:createnode>

  <mm:node id="group1" number="groups.testgroep" />

  <mm:createrelation source="mail1" destination="group1" role="related" />

  <!-- start the mailer but return directly (background mailing) -->
  <mm:node referid="mail1">
    <mm:functioncontainer>
      <mm:param name="type" value="oneshot" />
      <mm:function name="startmail" />
    </mm:functioncontainer>
  </mm:node>

  <p>Mail ${mail1} was sent (to group ${group1}), in the background, using http functionality.</p>
</mm:cloud>
