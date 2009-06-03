<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" escaper="none" expires="0">
<mm:cloud name="mmbase" jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="pagetitle">configure &amp; help - my_editors</mm:import>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <%@ include file="inc/head.jsp" %>
</head>
<body class="config">
<div id="frame">
<%@ include file="inc/pageheader.jsp" %>
<div id="sidebar">
<div class="padsidebar">
  <div id="helpbox">
  <h3>About the icons</h3>
  <dl>
    <dt>new object</dt>
    <dd><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></dd>
  </dl><dl>
    <dt>delete object</dt>
    <dd><img src="img/mmbase-delete.png" alt="delete" width="21" height="20" /></dd>
  </dl><dl>
    <dt>edit object</dt>
    <dd><img src="img/mmbase-edit.png" alt="edit" width="21" height="20" /></dd>
  </dl><dl>
    <dt>relate object (child)</dt>
    <dd><img src="img/mmbase-relleft.png" alt="&larr; relate" width="21" height="20" /></dd>
  </dl><dl>
    <dt>relate object (parent)</dt>
    <dd><img src="img/mmbase-relright.png" alt="relate &rarr;" width="21" height="20" /></dd>
  </dl><dl>
    <dt>up</dt>
    <dd><img src="img/mmbase-up.png" alt="up" width="21" height="20" /></dd>
  </dl><dl>
    <dt>down</dt>
    <dd><img src="img/mmbase-down.png" alt="down" width="21" height="20" /></dd>
  </dl><dl>
    <dt>left</dt>
    <dd><img src="img/mmbase-left.png" alt="left" width="21" height="20" /></dd>
  </dl><dl>
    <dt>right</dt>
    <dd><img src="img/mmbase-right.png" alt="right" width="21" height="20" /></dd>
  </dl><dl>
    <dt>ok</dt>
    <dd><img src="img/mmbase-ok.png" alt="ok" width="21" height="20" /></dd>
  </dl><dl>
    <dt>help</dt>
    <dd><img src="img/mmbase-help.png" alt="help" width="21" height="20" /></dd>
  </dl><dl>
    <dt>home</dt>
    <dd><img src="img/mmbase-home.png" alt="home" width="21" height="20" /></dd>
  </dl><dl>
    <dt>cancel action</dt>
    <dd><img src="img/mmbase-cancel.png" alt="cancel" width="21" height="20" /></dd>
  </dl><dl>
    <dt>reload</dt>
	<dd><mm:link><a href="${_}"><img src="img/mmbase-reload.png" alt="reload" width="21" height="20" /></a></mm:link></dd>
  </dl><dl>
    <dt>search</dt>
    <dd><img src="img/mmbase-search.png" alt="search" width="21" height="20" /></dd>
  </dl>
  <div class="stopfloat">&nbsp;</div>
  </div><!-- /#helpbox -->
</div><!-- /.padsidebar -->
</div><!-- / #sidebar -->
<div id="content">
  <div class="padcontent">
<div id="config">

<%-- read defaults or configuration --%>
<mm:import externid="max_items" vartype="Integer"><mm:write referid="max" /></mm:import>
<mm:import externid="max_days" vartype="Integer"><mm:write referid="days" /></mm:import>
<mm:import externid="type_list"><mm:write referid="list" /></mm:import>
<mm:import externid="search_box"><mm:write referid="searchbox" /></mm:import>
<mm:import externid="columns_pos"><mm:write referid="columns" /></mm:import>

<mm:import externid="savethis" />
<mm:present referid="savethis">
  <mm:write cookie="my_editors_maxitems" referid="max_items" />
  <mm:write cookie="my_editors_maxdays"  referid="max_days" />
  <mm:write cookie="my_editors_typelist" referid="type_list" />
  <mm:write cookie="my_editors_searchbox" referid="search_box" />
  <mm:write cookie="my_editors_columns"  referid="columns_pos" />
</mm:present>

<h2>Configure my_editors</h2>

<p>Here you can configure the following preferences:</p>
<ul>
  <li>the maximum age of the items that will be found: <b><mm:write referid="max_days" /></b> days;</li>
  <li>maximum number of items in each list: <b><mm:write referid="max_items" /></b>;</li>
  <%-- li>if you want all the node types to be shown or only the ones you are allowed to edit: <b><mm:write referid="type_list" /></b>; and</li --%>
  <li>where you want the searchbox to be displayed: <strong><mm:write referid="search_box" /></strong> the searchresults; or</li>
  <li>
    the position of the content column vs. the sidebar, or vise versa: 
    <strong><mm:write referid="columns_pos" /></strong> 
  </li>
</ul>

<p>Your preferences are saved in several cookies starting with 'my_editors'. You'll find them in your browsers cookie jar.</p>

<form method="post" action="<mm:url />">
<fieldset>
<legend>Configure my_editors</legend>
<div class="row">
  <label><strong>Max days old</strong></label>
  <input type="text" name="max_days" value="<mm:write referid="max_days" />" size="9" maxlength="9" />
</div>
<div class="row">
  <label><strong>Max items per page</strong></label>
  <input type="text" name="max_items" value="<mm:write referid="max_items" />" size="9" maxlength="9" />
  or other lists.
</div>
<%-- div class="row">
  <label><strong>Show me</strong></label>
  <select name="type_list">
    <option label="all the node types" value="all"<mm:compare referid="type_list" value="all"> selected="selected"</mm:compare>>all the node types</option>
    <option label="only the editable node types" value="editable"<mm:compare referid="type_list" value="editable"> selected="selected"</mm:compare>>only the editable node types</option>
  </select>
</div --%>
<div class="row">
  <label><strong>Show the searchbox</strong></label>
  <select name="search_box">
    <option label="after" value="after"<mm:compare referid="search_box" value="after"> selected="selected"</mm:compare>>after</option>
    <option label="before" value="before"<mm:compare referid="search_box" value="before"> selected="selected"</mm:compare>>before</option>
  </select> the list with searchresults
</div>
<div class="row">
  <label><strong>Columns position</strong></label>
  <select name="columns_pos">
	<option label="content left" value="contentleft"<mm:compare referid="columns_pos" value="contentleft"> selected="selected"</mm:compare>>content left</option>
	<option label="content right" value="contentright"<mm:compare referid="columns_pos" value="contentright"> selected="selected"</mm:compare>>content right</option>
  </select>
</div>
<div class="lastrow">
  <input type="submit" name="savethis" value="Save" /> 
</div>
</fieldset>
</form>

    </div><!-- / #config -->  
  </div><!-- / .padcontent -->
  <div class="padfoot">&nbsp;</div>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud>
</mm:content>
