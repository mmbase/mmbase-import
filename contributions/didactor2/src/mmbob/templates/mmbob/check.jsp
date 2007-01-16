
<mm:listnodes type="posters" constraints="account='admin'" max="1">
  <mm:import id="adminposter" reset="true"><mm:field name="number"/></mm:import>
</mm:listnodes>
<mm:node number="${user}">
  <mm:relatednodes type="posters" max="1">
    <mm:node id="usersposter" />
  </mm:relatednodes>
</mm:node>
<mm:import id="classforum" reset="true">-1</mm:import>
<mm:import id="educationforum" reset="true">-1</mm:import>
<mm:present referid="class">
  <mm:node number="$class" notfound="skip">
    <mm:relatedcontainer path="settingrel,components">
      <mm:constraint field="components.name" value="mmbob"/>
      <mm:related>
        <mm:import id="hasmmbob" reset="true">true</mm:import>
      </mm:related>
    </mm:relatedcontainer>
    <mm:present referid="hasmmbob">
      <mm:relatednodes type="forums">
        <mm:import id="classforum" reset="true"><mm:field name="number"/></mm:import>
      </mm:relatednodes>
      <mm:compare referid="classforum" value="-1">
        <mm:node number="$provider">
          <mm:import id="lang_code" reset="true"><mm:field name="locale" /></mm:import>
        </mm:node>
        <mm:import id="name" reset="true"><mm:write referid="class"/></mm:import>
        <mm:import id="description" reset="true"><di:translate key="mmbob.forumforclass" /> '<mm:field name="name"/>'</mm:import>
        <mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
        <mm:import id="account" reset="true">admin</mm:import>
        <mm:import id="password" reset="true">admin2k</mm:import><!-- sigh! -->
        <mm:log>Calling mmbob#newforum</mm:log>
        <mm:nodefunction set="mmbob" name="newForum" referids="name,description,account,password">         
          <mm:field name="forumid" id="forumid" write="false" />
          <mm:field name="adminid" id="adminid" write="false" />
        </mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$class'">
          <mm:import id="classforum" reset="true"><mm:write referid="forumid"/></mm:import>
        </mm:listnodes>
        <mm:import id="name" reset="true"><mm:field name="name"/></mm:import>
        <mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,description" >
        </mm:booleanfunction>
        <mm:createrelation role="related" source="class" destination="classforum" />
        <di:hasrole role="teacher">
          <mm:log>Assigning admin-rights to ${usersposter}, using ${adminid}</mm:log>
          <mm:voidfunction set="mmbob" name="newAdministrator"
                       referids="forumid,adminid@posterid,usersposter@newadministrator" />
        </di:hasrole>
      </mm:compare>
      <mm:remove referid="hasmmbob"/>
    </mm:present>
  </mm:node>
</mm:present>
<mm:present referid="education">
  <mm:node number="$education" notfound="skip">
    <mm:relatedcontainer path="settingrel,components">
      <mm:constraint field="components.name" value="mmbob"/>
      <mm:related>
        <mm:import id="hasmmbob" reset="true">true</mm:import>
      </mm:related>
    </mm:relatedcontainer>
    <mm:present referid="hasmmbob">
      <mm:relatednodes type="forums">
        <mm:import id="educationforum" reset="true"><mm:field name="number"/></mm:import>
      </mm:relatednodes>
      <mm:compare referid="educationforum" value="-1">
        <mm:import id="name" reset="true"><mm:write referid="education"/></mm:import>
        <mm:import id="description" reset="true"><di:translate key="mmbob.forumforeducation" /> '<mm:field name="name"/>'</mm:import>
        <mm:node number="$provider">
          <mm:import id="lang_code" reset="true"><mm:field name="locale" /></mm:import>
        </mm:node>
        <mm:import id="language" reset="true"><mm:write referid="lang_code"/></mm:import>
        <mm:import id="account" reset="true">admin</mm:import>
        <mm:import id="password" reset="true">admin2k</mm:import>
        <mm:nodefunction set="mmbob" name="newForum" referids="name,language?,description,account,password">
        </mm:nodefunction>
        <mm:listnodes type="forums" constraints="name='$education'">
          <mm:import id="forumid" reset="true"><mm:field name="number"/></mm:import>
          <mm:import id="educationforum" reset="true"><mm:write referid="forumid"/></mm:import>
        </mm:listnodes>
        <mm:import id="name" reset="true"><mm:field name="name"/></mm:import>
        <mm:booleanfunction set="mmbob" name="changeForum" referids="forumid,name,description" >
        </mm:booleanfunction>
        <mm:createrelation role="related" source="education" destination="educationforum" />
      </mm:compare>
      <mm:remove referid="hasmmbob"/>
    </mm:present>
  </mm:node>
</mm:present>
