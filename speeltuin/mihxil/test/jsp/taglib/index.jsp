<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<p>
  The goal of this example, is to provide a jsp-pages path which will
  touch as much as possible of the MMBase taglib functionality, and it
  will report if the taglib worked as could be expected.
</p>
<p>
  Note that it will start creating nodes in a transaction (it will
  prompt for user/password), which nodes will be used in further
  tests. The MyNews application should be installed.
</p>
<p>
  There are a few tests which test if an RunTimeexception is really
  occuring. It could be that these pages don't work well in the orion
  application server. 
</p>
<p>
  Start <a href="<mm:url page="transaction.jsp" />">here</a>.
</p>
<hr />
<p>
  An alternative <a href="<mm:url page="caches.jsp" />">Caches overview</a>
</p>
<a href="mailto:mihxil@komputilo.org">Michiel Meeuwissen</a>
</body>
</html>