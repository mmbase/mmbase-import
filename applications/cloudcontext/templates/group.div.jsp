<div class="group">
<div class="environment">
 <p class="current">
    Current group: <mm:nodeinfo type="gui" />
 </p>
 <p class="parents">
   <mm:relatednodes id="parentgroup" searchdir="source" role="contains" type="mmbasegroups">
     <a href="<mm:url referids="parentgroup@group" />"><mm:nodeinfo type="gui" /></a><br />
   </mm:relatednodes>
 </p>
 <p class="childs">
   <mm:relatednodes id="childgroup" searchdir="destination" role="contains" type="mmbasegroups">
    <a href="<mm:url referids="childgroup@group" />"><mm:nodeinfo type="gui" /></a><br />
  </mm:relatednodes>
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
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    </mm:fieldlist>
   </table>
   </table>
   <table class="rights">
    <tr><th /><th>C</th><th>R</th><th>W</th><th>D</th></tr>
    <mm:listnodes id="thiscontext" type="mmbasecontexts">
      <mm:field id="context" name="name" write="false" />
      <tr><td><mm:nodeinfo type="gui" /></td>
        <mm:maycreaterelation source="thiscontext" destination="currentgroup" role="grants">
        <mm:import id="operations" vartype="list">create,read,write,delete</mm:import>
        <mm:stringlist referid="operations">
        <td <mm:booleanfunction node="currentgroup" name="parentsallow" parameters="$context,$_">
               class="parent"
           </mm:booleanfunction>
          >
          <input type="checkbox" name="<mm:write />:<mm:field name="number" />"
             <mm:booleanfunction node="currentgroup" name="allows" parameters="$context,$_">
               checked="checked"
             </mm:booleanfunction>
          />
        </td>
        </mm:stringlist>
        </mm:maycreaterelation>
      </tr>
    </mm:listnodes>
    <tr><td><input type="submit"  name="submit" value="submit" /></td></tr>
   </table>
   </form>
</div>


</div>