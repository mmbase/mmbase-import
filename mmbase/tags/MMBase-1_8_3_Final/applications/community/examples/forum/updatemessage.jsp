<%@ page errorPage="posterror.jsp" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="thread" from="parameters" />
<mm:import externid="username" from="parameters" />
<mm:import externid="subject" from="parameters" />
<mm:import externid="body" from="parameters" />
<mm:import externid="channel" from="parameters" />
<mmcommunity:update message="${thread}">
    <mm:setfield name="username"><mm:write referid="username" /></mm:setfield>
    <mm:setfield name="subject"><mm:write referid="subject" /></mm:setfield>
    <mm:setfield name="body"><mm:write referid="body" /></mm:setfield>
</mmcommunity:update>
<mm:node number="${thread}">
  <h2><mm:field name="html(subject)" /></h2>
     <mmcommunity:getinfo key="name">
     <mm:isnotempty>
     <p><em>Posted by <mm:write /></em> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></p>
     </mm:isnotempty>
     </mmcommunity:getinfo>
     <p><mm:field name="html(body)" /></p>
     &nbsp;
</mm:node>
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>          
