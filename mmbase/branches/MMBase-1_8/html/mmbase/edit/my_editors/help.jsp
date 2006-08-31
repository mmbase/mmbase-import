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

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
