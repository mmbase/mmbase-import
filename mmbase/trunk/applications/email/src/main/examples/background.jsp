<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud  method="delegate" authenticate="class">
  <mm:import externid="email" />

  <!--  create the email node -->
  <mm:createnode id="mail1" type="email">
    <mm:setfield name="from">${email}</mm:setfield>
    <mm:setfield name="to">${email}</mm:setfield>
    <mm:setfield name="subject">test mmbase email</mm:setfield>
    <mm:setfield name="body">Hi this mail was sent in the background !</mm:setfield>
  </mm:createnode>

  <!-- start the mailer but return directly (background mailing) -->
  <mm:node referid="mail1">
    <mm:functioncontainer>
      <mm:param name="type" value="oneshot" />
      <mm:function name="startmail" />
    </mm:functioncontainer>
  </mm:node>

  <p>Mail ${mail1} was sent, in the background</p>

</mm:cloud>
