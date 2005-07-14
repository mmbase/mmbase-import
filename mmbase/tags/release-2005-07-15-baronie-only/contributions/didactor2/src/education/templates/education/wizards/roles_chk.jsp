<%-- in: user, editcontextname --%>
<%-- out: rights --%>
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