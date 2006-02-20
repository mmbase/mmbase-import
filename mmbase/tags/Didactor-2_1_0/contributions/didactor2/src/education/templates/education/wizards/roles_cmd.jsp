<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>


<mm:import externid="command">-1</mm:import>
<mm:compare referid="command" value="deleterole">
  <mm:import id="editcontextname" reset="true">rollen</mm:import>
  <%@include file="/education/wizards/roles_chk.jsp" %>
  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">

    <mm:import externid="rolenumber"/>
    <mm:node number="$rolenumber" notfound="skip">
      <mm:deletenode deleterelations="true"/>
    </mm:node>

  </mm:islessthan>
</mm:compare>
<mm:compare referid="command" value="accept">
  <mm:import id="editcontextname" reset="true">rollen</mm:import>
  <%@include file="/education/wizards/roles_chk.jsp" %>
  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">

    <% String sSelectName = ""; %>
    <mm:listnodes type="editcontexts" orderby="number">
      <mm:field name="number" id="this_editcontext" jspvar="dummy" vartype="String">
        <% sSelectName = "select_" + dummy + "_"; %>
      </mm:field>
      <mm:listnodes type="roles" orderby="number">
        <% String sSelectFullName = ""; %>
        <mm:remove referid="old_rel"/>
        <mm:field name="number" id="this_role" jspvar="dummy" vartype="String">
          <% sSelectFullName = sSelectName + dummy; %>
        </mm:field>
        <mm:import externid="<%= sSelectFullName %>">0</mm:import>
        <mm:related path="posrel,editcontexts" constraints="editcontexts.number='$this_editcontext'">
          <mm:node element="posrel" id="old_rel">
            <mm:setfield name="pos"><mm:write referid="<%= sSelectFullName %>"/></mm:setfield>
          </mm:node>
        </mm:related>
        <mm:notpresent referid="old_rel">
          <mm:createrelation role="posrel" source="this_role" destination="this_editcontext">
            <mm:setfield name="pos"><mm:write referid="<%= sSelectFullName %>"/></mm:setfield>
          </mm:createrelation>
        </mm:notpresent>
      </mm:listnodes>
    </mm:listnodes>

  </mm:islessthan>
</mm:compare>


<jsp:forward page="roles.jsp"/>
</mm:cloud>
</mm:content>
