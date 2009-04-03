<%--
    This is horrible
in: user, editcontextname --%>
<%-- out: rights, forbidtemplate (for EW) --%>
<%-- need: definition of RIGHTS_NO --%>

<mm:import externid="requestrights" from="request" />
<mm:present referid="requestrights">
  <mm:import id="rights" externid="requestrights" from="this" />
</mm:present>
<mm:present referid="requestrights" inverse="true">
  <mm:import id="rights" reset="true"><mm:write referid="RIGHTS_NO"/></mm:import>
  <mm:node referid="user" notfound="skip">
    <mm:relatedcontainer path="related,roles,posrel,editcontexts" fields="posrel.pos">
      <mm:constraint field="editcontexts.name" value="$editcontextname" />
      <mm:related>
         <mm:field name="posrel.pos">
          <mm:isgreaterthan referid2="rights">
            <mm:import id="rights" reset="true"><mm:write/></mm:import>
          </mm:isgreaterthan>
        </mm:field>
      </mm:related>
    </mm:relatedcontainer>
  </mm:node>

  <%-- should this not be handled by mmbase security? --%>
  <mm:import id="forbidtemplate" reset="true">&amp;forbiddelete=yes</mm:import>
  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
    <mm:import id="forbidtemplate" reset="true"></mm:import>
  </mm:islessthan>
</mm:present>
