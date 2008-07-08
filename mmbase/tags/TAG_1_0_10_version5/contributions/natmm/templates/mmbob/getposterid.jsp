<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import id="posterid" externid="pid$forumid" from="session">-1</mm:import>
<mm:compare referid="posterid" value="-1">
    <mm:import id="cw" externid="cwf$forumid" from="cookie" />
    <mm:import id="ca" externid="caf$forumid" from="cookie" />
    <mm:compare referid="ca" value="" inverse="true">
        <mm:node number="$forumid">
        <mm:import id="loginfailed">true</mm:import>
        <mm:relatednodes type="posters" constraints="(account='$ca' AND password='$cw')">
            <mm:remove referid="posterid" />
            <mm:remove referid="loginfailed" />
            <mm:import id="posterid"><mm:field name="number"/></mm:import>
            <mm:write referid="posterid" session="pid$forumid" /> 
        </mm:relatednodes>
        </mm:node>
    </mm:compare>
</mm:compare>

<mm:compare referid="posterid" value="">
    <mm:remove referid="posterid" />
    <mm:import id="posterid">-1</mm:import>
    <mm:write referid="posterid" session="pid$forumid" />
</mm:compare>


<mm:import externid="lang" />
<mm:present referid="forumid">
<mm:node number="$forumid">
    <mm:present referid="lang" inverse="true">
        <mm:remove referid="lang" />
        <mm:import id="lang"><mm:field name="language" /></mm:import>
    </mm:present>
</mm:node>
</mm:present>
