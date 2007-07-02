<div class="row">
  <label><strong>Aliases</strong></label>
  <div class="aliasses">
  <mm:aliaslist id="alias">
	<mm:import id="aliasconstr" reset="true">name='<mm:write referid="alias" />'</mm:import>
	<mm:listnodes type="oalias" constraints="$aliasconstr">
	  <mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete alias"><img src="img/mmbase-delete.png" alt="delete" width="21" height="20" /></a></mm:maydelete>
	  <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit alias"></mm:maywrite><mm:write referid="alias" /><mm:maywrite></a></mm:maywrite>
	</mm:listnodes> 
	<mm:last inverse="true"><br /></mm:last>
  </mm:aliaslist>
  </div>
</div>
<div class="row">
  <label for="new_alias">
	<strong>New alias</strong>
	<a onmouseover="showBox('descr_alias',event);return false;" onmouseout="showBox('descr_alias',event);return false;">alias</a>
  </label>
  <input type="text" id="new_alias" name="new_alias" size="80" maxlength="255" class="small" /><br />
  <div class="description" id="descr_alias"><mm:nodeinfo type="description" nodetype="oalias" /></div>
</div>
