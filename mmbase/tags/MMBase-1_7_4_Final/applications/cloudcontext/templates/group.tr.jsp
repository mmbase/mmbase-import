<tr>
 <td>   
   <mm:relatednodes id="parentgroup" searchdir="source" role="contains" type="mmbasegroups">
     <a href="<mm:url referids="parentgroup@group" />"><mm:nodeinfo type="gui" /></a><br />
   </mm:relatednodes>
 </td>
 <td />
 <td rowspan="10">
   <mm:relatednodes id="user" searchdir="destination" role="contains" type="mmbaseusers">
     <a href="<mm:url page="edit_user.jsp" referids="user" />"><mm:nodeinfo type="gui" /></a><br />
   </mm:relatednodes>
 </td>
 <td rowspan="10">
   <table class="rights">
    <tr><th /><th>R</th><th>W</th><th>D</th></tr>
    <mm:listnodes type="mmbasecontexts">
      <tr><td><mm:nodeinfo type="gui" /></td>
          <td class="direct">
  <%--
           <mm:functioncontainer>
             <mm:function node="currentgroup" name="maydo">

             <mm:param name="context"><mm:field name="name" /></mm:param> 
             </mm:function>
             <mm:write>
               <mm:compare value="direct">
               </mm:compare>
             </mm:write>
           </mm:functioncontainer>
--%>
          <td class="yes"><input type="checkbox"    checked="checked" /></td>
          <td class="no"><input type="checkbox" /></td></tr>
    </mm:listnodes>
   </table>
 </td>
</tr>
<tr>
 <td />
 <td>
   <mm:nodeinfo type="gui" />
 </td>
</tr>
<tr>
 <td>   
   <mm:relatednodes id="childgroup" searchdir="destination" role="contains" type="mmbasegroups">
     <a href="<mm:url referids="childgroup@group" />"><mm:nodeinfo type="gui" /></a><br />
   </mm:relatednodes>
 </td>
 <td />
</tr>
