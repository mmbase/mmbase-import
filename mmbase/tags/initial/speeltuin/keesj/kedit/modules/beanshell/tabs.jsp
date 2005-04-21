<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Tabs</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../../css/style.css" rel="stylesheet" type="text/css">
<!-- http://www.simplebits.com/tips/css_tabs.html -->
<style type="text/css">
#tabnav
	{
		height: 20px;
		margin: 0;
		padding-left: 10px;
		background: url(pics/tab_bottom.gif) repeat-x bottom;
	}

#tabnav li {
		margin: 0;
		padding: 0;
  		display: inline;
  		list-style-type: none; 
}

#tabnav a:link, #tabnav a:visited {
		float: left;
		background: #B2CCB2;
		font-size: 10px;
		line-height: 14px;
		font-weight: bold;
		padding: 2px 10px 2px 10px;
		margin-right: 4px;
		border: 1px solid #000000;
		text-decoration: none;
		color: #666;
	}

#tabnav a:link.active, #tabnav a:visited.active {
		border-bottom: 1px solid #CBE5CB;
		background: #CBE5CB;
		color: #000000;
}

#tabnav a:hover {
		background: #CBE5CB;
}
</style>
</head>

<body>

<ul id="tabnav">
	<li><a href="#" class="active">one liners</a></li>
	<li><a href="#">create session</a></li>
</ul>


</body>
</html>
