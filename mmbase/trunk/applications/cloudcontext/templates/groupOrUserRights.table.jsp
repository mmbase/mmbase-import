
   <mm:import id="operations" vartype="list">create,read,write,delete,change context</mm:import>
   <table class="rights">
    <tr><th>Contexts</th><mm:stringlist referid="operations"><th><mm:write /></th></mm:stringlist></tr>
    <mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Contexts.GRANT_ARGUMENTS">
      <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
    <mm:listnodes id="thiscontext" type="mmbasecontexts">
      <tr><td><a href="<mm:url referids="thiscontext@context" page="index_contexts.jsp" />"><mm:nodeinfo type="gui" /><mm:field name="description"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field></a></td>
        <mm:stringlist referid="operations">
        <mm:param name="operation"><mm:write /></mm:param>
        <td <mm:booleanfunction name="parentsallow">
               class="parent"
           </mm:booleanfunction>
          >
          <mm:booleanfunction name="maygrant">
          <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
             <mm:booleanfunction name="allows">
               checked="checked"
             </mm:booleanfunction>
          />
          </mm:booleanfunction>
          <mm:booleanfunction name="maygrant" inverse="true">
            <mm:booleanfunction name="allows">
            X
             </mm:booleanfunction>
          </mm:booleanfunction>
        </td>
        </mm:stringlist>
      </tr>
    </mm:listnodes>
    </mm:functioncontainer> 
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
   </table>