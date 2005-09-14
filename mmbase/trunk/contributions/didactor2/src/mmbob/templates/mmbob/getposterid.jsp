<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:import id="posterid">-1</mm:import>
<mm:node number="$user" notfound="skip">
    <mm:import id="dummy" reset="true"><mm:field name="username"/></mm:import>
    <mm:relatednodes type="posters">
        <mm:import id="haveposter" reset="true">yes</mm:import>
    </mm:relatednodes>
        <mm:import id="account"><mm:field name="username" /></mm:import>
        <mm:import id="password">blank</mm:import>
        <mm:import id="firstname"><mm:field name="firstname" /></mm:import>
        <mm:import id="lastname"><mm:field name="lastname" /></mm:import>
        <mm:import id="email"><mm:field name="email" /></mm:import>
        <mm:import id="location"><mm:field name="city" /></mm:import>
        <mm:import id="gender">male</mm:import>
        <mm:import id="feedback" reset="true"><mm:function set="mmbob" name="createPoster" referids="forumid,account,password,firstname,lastname,email,gender,location" /></mm:import>
        <mm:remove referid="account" />
        <mm:remove referid="password" />
        <mm:remove referid="firstname" />
        <mm:remove referid="lastname" />
        <mm:remove referid="email" />
        <mm:remove referid="location" />
        <mm:remove referid="gender" />
        <mm:list path="posters" constraints="posters.account='$dummy'">
            <mm:remove referid="posterid" />
            <mm:import id="posterid"><mm:field name="posters.number"/></mm:import>
        </mm:list>
        <mm:notpresent referid="haveposter">
            <mm:createrelation role="related" source="user" destination="posterid" />
        </mm:notpresent>

</mm:node>

<mm:compare referid="posterid" value="">
    <mm:remove referid="posterid" />
    <mm:import id="posterid">-1</mm:import>
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