<div class="group">
<div class="environment">
 <p class="current">
    Current context: <mm:nodeinfo type="gui" />
 </p>

<div class="rights">
   <form method="post" action="commit_context.jsp">    
   <input type="hidden" name="context" value="<mm:field name="number" />" />
   <table>
      <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
   </table>
   </table>
   <table class="rights">
    <tr><th>Group</th><th>Create</th><th>Read</th><th>Write</th><th>Delete</th></tr>
    <mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Groups.GRANT_ARGUMENTS">
    <mm:param name="context"><mm:field name="name" /></mm:param>
    <mm:listnodes id="thisgroup" type="mmbasegroups">
      <tr><td><a href="<mm:url referids="thisgroup@group" page="index_groups.jsp" />"><mm:nodeinfo type="gui" /><mm:field name="description"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field></a></td>
        <mm:import id="operations" vartype="list">create,read,write,delete</mm:import>
        <mm:stringlist referid="operations">
        <mm:param name="operation"><mm:write /></mm:param>
        <td <mm:booleanfunction name="parentsallow">
               class="parent"
           </mm:booleanfunction>
          >
          <mm:booleanfunction  name="maygrant">
          <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
             <mm:booleanfunction name="allows">
               checked="checked"
             </mm:booleanfunction>
          />
          </mm:booleanfunction>
          <mm:booleanfunction name="maygrant" inverse="true">
            X
          </mm:booleanfunction>
        </td>
        </mm:stringlist>
      </tr>
    </mm:listnodes>
    </mm:functioncontainer> 
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
    <tr><td><a href="index_contexts.jsp">back</a></td></tr>
   </table>
   </form>
</div>


</div>