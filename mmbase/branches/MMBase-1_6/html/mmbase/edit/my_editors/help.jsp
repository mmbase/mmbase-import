<% String title = "Help"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<mm:import jspvar="type" externid="type" />
<% String path1 = type;		// Eerst stukje van kruimelpad %>
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


<p>You can reach some of the my_editors pages by node id. You can put for example the small edit 
icon near editable nodes in your web pages to give people a quick way to edit a web page.</p>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
