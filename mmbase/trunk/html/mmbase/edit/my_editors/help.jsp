<% String title = "Help"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<mm:import jspvar="ntype" externid="ntype" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<h2>Help</h2>

<p>
<img src="img/mmbase-new.gif" alt="new" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>New</b> object or new relation between two objects
</p>

<p>
<img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Delete</b> object or delete relation between two objects
</p>

<p>
<img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Edit</b> object or edit relation between two objects
</p>

<p>
<img src="img/mmbase-search.gif" alt="search" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Search</b> object
</p>


<p>
<img src="img/mmbase-down.gif" alt="down" width="21" height="20" hspace="4" vspace="0" border="0" />
<img src="img/mmbase-left.gif" alt="left" width="21" height="20" hspace="4" vspace="0" border="0" />
<img src="img/mmbase-up.gif" alt="up" width="21" height="20" hspace="4" vspace="0" border="0" />
<img src="img/mmbase-right.gif" alt="right" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Relate</b> object or <b>move</b> to next or previous page
</p>

<p>
<img src="img/mmbase-cancel.gif" alt="cancel" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Cancel</b> action
</p>


<p><b>Quick way to access my_editors from within your MMBase site</b><br />
You can reach some of the my_editors pages - like edit node and delete node - by node id. 
You could put for example the small edit icon near editable nodes in your web pages 
to give people a quick way to edit a web page. Of course you would need to hide the 
icons for people who are not logged in into your editor environment.</p>

<p><b>Version history</b><br />
27-11-2002: Fixed a bug with content-type in 'inc_top.jsp' and 'inc_head.jsp'. Default now is UTF-8.<br />
27-01-2003: Some pages still used the parameter 'type' in stead of 'ntype'. Fixed that. 
And changed a few little things in the lay-out. 
</p>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
