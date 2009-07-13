<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:cloud  method="delegate" authenticate="class">
  <mm:import externid="email">bla@gmail.com</mm:import>
  <!--  create the email node -->
  <mm:createnode id="mail1" type="email">
    <mm:setfield name="from">${email}</mm:setfield>
    <mm:setfield name="to">${email}</mm:setfield>
    <mm:setfield name="subject">my first mmbase mail !!</mm:setfield>
    <mm:setfield name="body">Just testing email</mm:setfield>
  </mm:createnode>

  <!-- send the email node  (default type is oneshot) -->
  <mm:node referid="mail1">
    <mm:function name="mail" />
    <p>Mail ${mail1} was sent with status <mm:field name="mailstatus"><mm:fieldinfo type="guivalue" /></mm:field></p>
  </mm:node>

</mm:cloud>
