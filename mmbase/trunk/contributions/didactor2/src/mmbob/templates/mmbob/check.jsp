<mm:listnodes type="posters" constraints="account='admin' AND password='admin2k'">
    <mm:import id="adminposter" reset="true"><mm:field name="number"/></mm:import>
</mm:listnodes>
<mm:import id="classforum" reset="true">-1</mm:import>
<mm:node number="$class" notfound="skip">
    <mm:relatednodes type="forums">
        <mm:import id="classforum" reset="true"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
    <mm:compare referid="classforum" value="-1">
	<mm:import id="dummy" reset="true"><mm:field name="name"/></mm:import>

	<mm:import id="name" reset="true"><mm:write referid="class"/></mm:import>
	<mm:import id="description" reset="true">Forum for class '<mm:field name="name"/>'</mm:import>
	<mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
	<mm:listnodes type="posters" constraints="account='admin' AND password='admin2k'">
	    <mm:import id="account" reset="true"><mm:field name="account"/></mm:import>
	    <mm:import id="password" reset="true"><mm:field name="password"/></mm:import>
	</mm:listnodes>
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,account,password">
	</mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$class'">
            <mm:import id="classforum" reset="true"><mm:field name="number"/></mm:import>
            <mm:setfield name="name"><mm:write referid="dummy"/></mm:setfield>
        </mm:listnodes>
        <mm:createrelation role="related" source="class" destination="classforum" />
    </mm:compare>
    <di:hasrole role="teacher">
        <mm:import id="forumid" reset="true"><mm:write referid="classforum"/></mm:import>
        <mm:remove referid="posterid"/>
        <mm:remove referid="lang"/>
       
        <%@ include file="getposterid.jsp" %>
        <mm:import id="dummy" reset="true"><mm:write referid="posterid"/></mm:import>
        <mm:import id="posterid" reset="true"><mm:write referid="adminposter"/></mm:import>
        <mm:import id="newadministrator" reset="true"><mm:write referid="dummy"/></mm:import>
        <mm:nodefunction set="mmbob" name="newAdministrator" referids="forumid,posterid,newadministrator">
	</mm:nodefunction>
        <mm:import id="posterid" reset="true"><mm:write referid="dummy"/></mm:import>
    </di:hasrole>
</mm:node>

<mm:import id="educationforum" reset="true">-1</mm:import>
<mm:node number="$education" notfound="skip">
    <mm:relatednodes type="forums">
        <mm:import id="educationforum" reset="true"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
    <mm:compare referid="educationforum" value="-1">
	<mm:import id="dummy" reset="true"><mm:field name="name"/></mm:import>

	<mm:import id="name" reset="true"><mm:write referid="education"/></mm:import>
	<mm:import id="description" reset="true">Forum for education '<mm:field name="name"/>'</mm:import>
	<mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
	<mm:listnodes type="posters" constraints="account='admin' AND password='admin2k'">
	    <mm:import id="account" reset="true"><mm:field name="account"/></mm:import>
	    <mm:import id="password" reset="true"><mm:field name="password"/></mm:import>
	</mm:listnodes>
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,account,password">
	</mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$education'">
            <mm:import id="educationforum" reset="true"><mm:field name="number"/></mm:import>
            <mm:setfield name="name"><mm:write referid="dummy"/></mm:setfield>
        </mm:listnodes>
        <mm:createrelation role="related" source="education" destination="educationforum" />
    </mm:compare>
<di:hasrole role="teacher">
        <mm:import id="forumid" reset="true"><mm:write referid="educationforum"/></mm:import>
        <mm:remove referid="posterid"/>
        <mm:remove referid="lang"/>
        <%@ include file="getposterid.jsp" %>
        <mm:import id="dummy" reset="true"><mm:write referid="posterid"/></mm:import>
        <mm:import id="posterid" reset="true"><mm:write referid="adminposter"/></mm:import>
	<mm:import id="newadministrator" reset="true"><mm:write referid="dummy"/></mm:import>
	<mm:nodefunction set="mmbob" name="newAdministrator" referids="forumid,posterid,newadministrator">
	</mm:nodefunction>
        <mm:import id="posterid" reset="true"><mm:write referid="dummy"/></mm:import>
</di:hasrole>
</mm:node>

