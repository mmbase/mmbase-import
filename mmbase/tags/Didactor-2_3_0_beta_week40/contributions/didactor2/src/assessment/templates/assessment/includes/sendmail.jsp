<mm:remove referid="mail1" />

<mm:createnode type="emails" id="mail1">
   <mm:setfield name="from"><mm:write referid="from"/></mm:setfield>
   <mm:setfield name="to"><mm:write referid="to"/></mm:setfield>
   <mm:setfield name="subject"><mm:write referid="subject"/></mm:setfield>
   <mm:setfield name="body"><mm:write referid="body" escape="text/plain"/></mm:setfield>
   <mm:setfield name="type">0</mm:setfield>
   <mm:setfield name="date"><%=System.currentTimeMillis()/1000%></mm:setfield>

</mm:createnode>

<!-- send the email node (Didactor way) -->                
<mm:node referid="mail1">
   <mm:setfield name="type">1</mm:setfield>
</mm:node>
