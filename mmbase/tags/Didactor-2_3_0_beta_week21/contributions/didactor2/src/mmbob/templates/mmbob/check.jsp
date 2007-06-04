
<!-- WTF!! -->
<mm:listnodes type="posters" constraints="account='admin'" max="1">
  <mm:import id="adminposter" reset="true"><mm:field name="number"/></mm:import>
</mm:listnodes>
<mm:node number="${user}">
  <mm:relatednodes type="posters" max="1">
    <mm:node id="usersposter" />
  </mm:relatednodes>
</mm:node>

<mm:node number="$provider">
  <mm:import id="lang_code" reset="true"><mm:field name="locale" /></mm:import>
</mm:node>

<mm:present referid="class">
  <mm:node number="$class" notfound="skip">
    <di:hascomponent name="mmbob">
      <mm:relatednodes type="forums" role="related">
        <mm:field name="number" id="classforum" write="false" />
      </mm:relatednodes>

      <mm:notpresent referid="classforum">

        <mm:functioncontainer>
          <mm:param name="description"><di:translate key="mmbob.forumforclass" /> '<mm:field name="name"/>'</mm:param>
          <mm:param name="account">admin</mm:param>
          <mm:param name="password">admin2k</mm:param><!-- sigh!!! -->
          <mm:param name="name"><mm:field name="name" /></mm:param>
          
          <mm:log>Calling mmbob#newforum</mm:log>
          <mm:nodefunction set="mmbob" name="newForum" referids="lang_code@language">         
            <mm:field name="forumid" id="classforum" write="false" />
            <mm:field name="adminid" id="adminid" write="false" />
          </mm:nodefunction>
        </mm:functioncontainer>	
        
        <mm:createrelation role="related" source="class" destination="classforum" />
        
        <di:hasrole role="teacher">
          <mm:log>Assigning admin-rights to ${usersposter}, using ${adminid} for forum ${classforum}</mm:log>
          <mm:voidfunction set="mmbob" name="newAdministrator"
                           referids="classforum@forumid,adminid@posterid,usersposter@newadministrator" />
        </di:hasrole>
      </mm:notpresent>
      
    </di:hascomponent>
  </mm:node>
</mm:present>
<mm:present referid="education">
  <mm:node number="$education">
    <di:hascomponent name="mmbob">
      
      <di:component name="mmbob">
        <di:settingvalue number="${education}" name="students">
          <c:if test="${_ eq 'on'}">
            <mm:relatednodes type="forums" role="related">
              <mm:field name="number" id="educationforum" write="false" />
            </mm:relatednodes>
            
            <mm:notpresent referid="educationforum">
              
              <mm:functioncontainer>
                <mm:param name="name"><mm:field name="name" /></mm:param>
                <mm:param name="description"><di:translate key="mmbob.forumforeducation" /> '<mm:field name="name"/>'</mm:param>
                <mm:param name="account">admin</mm:param>
                <mm:param name="password">admin2k</mm:param><!-- sigh!!! -->
                
                <mm:log>Creating new forum for education ${education}</mm:log>
                
                <mm:nodefunction set="mmbob" name="newForum" referids="lang_code@language">
                  <mm:field name="forumid" id="educationforum" write="false" />
                  <mm:field name="adminid" id="adminid" write="false" />
                </mm:nodefunction>
                
                <mm:createrelation role="related" source="education" destination="educationforum" />
                
              </mm:functioncontainer>
            </mm:notpresent>
          </c:if>
        </di:settingvalue>
        
        <di:settingvalue number="${education}" name="coaches">
          <c:if test="${_ eq 'on'}">
            <mm:relatednodescontainer type="forums" role="rolerel">
              <mm:constraint field="rolerel.role" value="coaches" />
              <mm:relatednodes>
                <mm:field name="number" id="educationforum_coaches" write="false" />
              </mm:relatednodes>
            </mm:relatednodescontainer>
            
            <mm:notpresent referid="educationforum_coaches">
              
              <mm:functioncontainer>
                <mm:param name="name"><mm:field name="name" /></mm:param>
                <mm:param name="description"><di:translate key="mmbob.forumforeducation" /> '<mm:field name="name"/>'</mm:param>
                <mm:param name="account">admin</mm:param>
                <mm:param name="password">admin2k</mm:param><!-- sigh!!! -->
                
                <mm:log>Creating new forum for education ${education}</mm:log>
                
                <mm:nodefunction set="mmbob" name="newForum" referids="lang_code@language">
                  <mm:field name="forumid" id="educationforum_coaches" write="false" />
                  <mm:field name="adminid" id="adminid" write="false" />
                </mm:nodefunction>
                
                <mm:createrelation role="rolerel" source="education" destination="educationforum_coaches">
                  <mm:setfield name="role">coaches</mm:setfield>
                </mm:createrelation>
                
              </mm:functioncontainer>
            </mm:notpresent>
          </c:if>
        </di:settingvalue>
      </di:component>
      
    </di:hascomponent>	  
  </mm:node>
</mm:present>
