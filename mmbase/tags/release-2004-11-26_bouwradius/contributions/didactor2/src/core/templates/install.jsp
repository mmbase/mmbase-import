<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud" name="mmbase">
  <mm:createnode type="people">
    <mm:setfield name="username">admin</mm:setfield>
    <mm:setfield name="password">admin2k</mm:setfield>
  </mm:createnode>
  <mm:createnode type="providers" id="provider">
    <mm:setfield name="name">provider</mm:setfield>
  </mm:createnode>
  <mm:listnodes type="components">
    <mm:node id="comp" />
    <mm:createrelation source="provider" destination="comp" role="settingrel" />
    <mm:remove referid="comp" />
  </mm:listnodes>
</mm:cloud>
