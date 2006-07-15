<% String title = "Help"; %>
<%@ include file="inc_top.jsp" %>

<mm:cloud method="loginpage" name="mmbase" loginpage="login.jsp" rank="basic user" jspvar="wolk">

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
<b>Edit</b> object
</p>

<p>
<img src="img/mmbase-relation-left.gif" alt="relation" width="21" height="20" hspace="4" vspace="0" border="0" />
<img src="img/mmbase-relation-right.gif" alt="relation" width="21" height="20" hspace="4" vspace="0" border="0" />
<b>Edit</b> or <b>create</b> relation between two objects: parent &amp; child
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
<b>Move</b> to next page etc.
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

<p><b>Preview your stylesheet in <a href="my_styles.html">my_styles.html</a></b><br />
You can edit <a href="my_editors.css">my_editors.css</a> stylesheet or replace it with your own. 
Use the HTML file <a href="my_styles.html">my_styles.html</a> to preview your changes or stylesheet.</p>

<p><b>Version history</b><br />
27-11-2002 (v0.5): Fixed a bug with content-type in 'inc_top.jsp' and 'inc_head.jsp'. Default now is UTF-8.<br />
27-01-2003 (v0.5.1): Some pages still used the parameter 'type' in stead of 'ntype'. Fixed that. 
And changed a few little things in the lay-out. <br />
02-05-2003 (v.0.6b): Changed lay-out.<br />
Fixed bug listing relations.<br />
Icons relations in 'Edit node' show directionality: child or parent.<br />
Added create new node and relate to this here option.<br />
07-05-2003 (v.0.6b2): Replaced new_object.jsp.<br />
General adjustments to lay-out and navigation.<br />
Added 'About the icons'.<br />
18-05-2003 (v.0.6b3): More adjustments to lay-out.<br />
Relations are created with the proper direction in mind. Relate buttons should point in the direction of the relation.<br />
26-05-2003 (v.0.6b4): Made includes: inc_search.jsp &amp; inc_relate.jsp for searching and relating nodes.<br />
06-08-2003 (v.0.6r1): Fixed bug #5998.<br/>Some lay-out fixes.<br />
12-12-2003 (v.0.7b): Made non-editable nodetypes (nodetypes for relations) hidden in listing on homepage. 
Can be configured using <a href="config.jsp">config.jsp</a>.<br />
Implemented security tags. Buttons giving access to nodes and relation nodes a user is not allowed to edit are hidden.<br />
Changed breadcrum path of 'edit relation': no longer directs you to the overview with relation nodes.<br /> 
Changed the lay-out of the search box 'edit node #'.<br />
21-01-2004 (v.0.7rc1): Edited optionlist in config.jsp.<br />
</p>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
