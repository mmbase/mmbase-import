<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 2.0//EN"  "TBD">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="postprocessor" /><mm:content postprocessor="$postprocessor" escaper="rich,censor,reducespace" language="nl">
<mm:import externid="escape" />
<mm:cloud>
<html xmlns="http://www.w3.org/2002/06/xhtml2" xml:lang="en">
<body>
<h1>content tag</h1>
<mm:write value="abc<" />
<mm:node number="a.news.article">
  <mm:formatter format="xhtml" wants="string" escape="none">
    <mmxf>
     <mm:field name="body" escape="rich" />   
    </mmxf>
  </mm:formatter>
</mm:node>

</body>
</html>
</mm:cloud>
</mm:content>
