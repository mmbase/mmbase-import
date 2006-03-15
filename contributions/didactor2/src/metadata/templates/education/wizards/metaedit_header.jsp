<font style="font-family:arial; font-size:20px; font-weight:normal"><mm:node number="<%= sNode %>"><mm:nodeinfo type="guinodemanager" /></mm:node> Metadata</font>
<hr style="width:99%; height:1px; color:#CCCCCC">
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
      <td><font style="color:009900; font-weight:bold; font-size:12px">Naam</font></td>
      <td>&nbsp;</td>
      <td>
         <mm:node number="<%= sNode %>" jspvar="nodeHeader">
            <mm:node number="$user" jspvar="nodeUser">
               <mm:import id="header_name"><%= MetaDataHelper.getAliasForObject(cloud, nodeHeader.getNumber(), nodeUser.getNumber()) %></mm:import>

               <mm:write referid="header_name">
                  <mm:isempty><mm:field name="title" /></mm:isempty>
                  <mm:isnotempty><mm:write /></mm:isnotempty>
               </mm:write>
            </mm:node>
         </mm:node>
      </td>
   </tr>
</table>
