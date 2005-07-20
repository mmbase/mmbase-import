<%-- in: user, editcontextname --%>
<%-- out: rights, forbidtemplate (for EW) --%>
<%-- need: definition of RIGHTS_NO --%>

<mm:import id="rights" reset="true"><mm:write referid="RIGHTS_NO"/></mm:import>
<mm:list nodes="$user" path="people,related,roles,posrel,editcontexts" 
    constraints="editcontexts.name='$editcontextname'">
  <mm:field name="posrel.pos">
    <mm:isgreaterthan referid2="rights">
      <mm:import id="rights" reset="true"><mm:write/></mm:import>
    </mm:isgreaterthan>
  </mm:field>
</mm:list>
<mm:import id="forbidtemplate" reset="true">&forbiddelete=yes</mm:import>
<mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
  <mm:import id="forbidtemplate" reset="true"></mm:import>
</mm:islessthan>
