<div class="group">
<div class="environment">
 <p class="current">
    Current group: <mm:nodeinfo type="gui" />
 </p>
</div>

<div class="users">
   <mm:relatednodes id="user" searchdir="destination" role="contains" type="mmbaseusers">
     <mm:first>Users: </mm:first>
     <a href="<mm:url page="edit_user.jsp" referids="user" />"><mm:nodeinfo type="gui" /></a>
     <mm:last inverse="true">, </mm:last>
   </mm:relatednodes>
</div>


<div class="rights">
   <form method="post" action="commit_group.jsp">    
   <input type="hidden" name="group" value="<mm:field name="number" />" />
   <table>
      <mm:fieldlist type="edit">
    <tr><td><mm:fieldinfo type="guiname" /></td><td colspan="3"><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
    <tr>
     <td>Parent groups</td>
     <td>
      <select name="_parentgroups"  size="15" multiple="multiple">
        <mm:relatednodes id="ingroups" type="mmbasegroups" searchdir="source">
         <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>       
        </mm:relatednodes>
        <mm:unrelatednodes type="mmbasegroups">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:unrelatednodes>
      </select>
      <a href="<mm:url page="index_groups.jsp">
      <mm:relatednodes referid="ingroups">
        <mm:param name="group"><mm:field name="number" /></mm:param>
       </mm:relatednodes>
      </mm:url>">View groups</a>
     </td>
     <td>Child groups</td>
     <td>
      <select name="_childgroups"  size="15" multiple="multiple">
        <mm:relatednodes id="fromgroups" type="mmbasegroups" searchdir="destination">
         <option selected="selected" value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>       
        </mm:relatednodes>
        <mm:unrelatednodes type="mmbasegroups">
         <option value="<mm:field name="number" />"><mm:nodeinfo type="gui" /></option>
        </mm:unrelatednodes>
      </select>
      <a href="<mm:url page="index_groups.jsp">
      <mm:relatednodes referid="fromgroups">
        <mm:param name="group"><mm:field name="number" /></mm:param>
       </mm:relatednodes>
      </mm:url>">View groups</a>
     </td>
     </tr>
   </table>
   <table class="rights">
    <tr><th>Contexts</th><th>Create</th><th>Read</th><th>Write</th><th>Delete</th></tr>
    <mm:functioncontainer argumentsdefinition="org.mmbase.security.implementation.cloudcontext.builders.Groups.GRANT_ARGUMENTS">
    <mm:listnodes id="thiscontext" type="mmbasecontexts">
      <mm:param name="context"><mm:field name="name" /></mm:param>
      <tr><td><a href="<mm:url referids="thiscontext@context" page="index_contexts.jsp" />"><mm:nodeinfo type="gui" /><mm:field name="description"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field></a></td>
        <mm:import id="operations" vartype="list">create,read,write,delete</mm:import>
        <mm:stringlist referid="operations">
        <mm:param name="operation"><mm:write /></mm:param>
        <td <mm:booleanfunction node="currentgroup" name="parentsallow">
               class="parent"
           </mm:booleanfunction>
          >
          <mm:booleanfunction node="currentgroup" name="maygrant">
          <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
             <mm:booleanfunction node="currentgroup" name="allows">
               checked="checked"
             </mm:booleanfunction>
          />
          </mm:booleanfunction>
          <mm:booleanfunction node="currentgroup" name="maygrant" inverse="true">
            X
          </mm:booleanfunction>
        </td>
        </mm:stringlist>
      </tr>
    </mm:listnodes>
    </mm:functioncontainer> 
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
   </table>
   </form>
</div>


</div>