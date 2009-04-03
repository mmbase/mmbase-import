<mm:remove referid="mail1"/>
<mm:remove referid="attachment"/>
<mm:createnode type="emails" id="mail1">
  <mm:setfield name="from"><mm:write referid="from"/></mm:setfield>
  <mm:setfield name="to"><mm:write referid="to"/></mm:setfield>
  <mm:setfield name="subject"><mm:write referid="subject"/></mm:setfield>
  <mm:setfield name="body"><mm:write referid="body" escape="text/plain"/></mm:setfield>
  <mm:setfield name="type">0</mm:setfield>
  <mm:setfield name="date"><%=System.currentTimeMillis()/1000%></mm:setfield> 
</mm:createnode>
<mm:present referid="htmlbody">
  <mm:createnode type="attachments" id="attachment" jspvar="attachment">
    <mm:setfield name="title">title</mm:setfield>
    <mm:setfield name="filename">message.htm</mm:setfield>
    <mm:setfield name="mimetype">text/html</mm:setfield>
    <mm:write referid="htmlbody" jspvar="htmlbody" vartype="String" write="false">
<% 
      attachment.setByteValue("handle",htmlbody.getBytes("UTF-8"));
      attachment.setIntValue("size",htmlbody.length());
   
%>
    </mm:write>
  </mm:createnode>
  <mm:createrelation role="related" source="mail1" destination="attachment"/>
  <mm:remove referid="attachment"/>
</mm:present>

<mm:node referid="mail1">
  <mm:setfield name="type">1</mm:setfield>
</mm:node>
