<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="postprocessor" /><mm:content postprocessor="$postprocessor" escaper="text/html/rich,uppercase,censor,reducespace" language="nl">
<mm:import externid="escape" />
<mm:cloud>
<html>
<body>
<h1>content tag</h1>
<mm:write value="abc<" />

<mm:node number="a.news.article">
  <mm:field name="body" escape="$escape" />
</mm:node>

</body>
</html>
</mm:cloud>
</mm:content>
