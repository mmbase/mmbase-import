<html>
<head>
<title>help</title>
<link href="../../css/style.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../../js/i.js"></script>
</head>
<body>
<h1>MMBase BeanShell help</h1>
<p><a href="http://www.beanshell.org/">BeanShell</a> is a small, free, embeddable, Java source interpreter with object scripting language features, written in Java. BeanShell executes standard Java statements and expressions, in addition to obvious scripting commands and syntax. BeanShell supports scripted objects as simple method closures like those in Perl and JavaScript(tm). for more information about BeanShell go and visit the <a href="http://www.beanshell.org/">BeanShell website</a>
</p>
<p><a href="http://www.mmbase.org/">MMBase</a> on the other hand is an objects management system  lacking scripting capabilities. In combining both you can get a very flexible scripted object management system.this is exactely what this page does. You can put scripting code in the "execute" bar and make it run on the server.</p>

<p><em>Some examples(cut &amp; paste those to the execute input field)</em>
</p>
	<ul>
		<li>list all mmservers nodes
<pre>p(cloud.getNodeManager("mmservers").getList(null,null,null))</pre>
     </li>
		<li>show a node
<pre>p(cloud.getNode(2))</pre>
     </li>
   </ul>
</p>
	</body>
</html>
