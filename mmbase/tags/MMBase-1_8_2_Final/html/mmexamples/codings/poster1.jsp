<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page contentType="text/html;charset=ISO-8859-1" pageEncoding="ISO-8859-1"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html><!-- a lie, this page is actually cp1252 -->
<head>
  <title>Codings examples</title>	 
  <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
</head>
<body>
<mm:content language="en" type="text/html" postprocessor="reducespace" encoding="ISO-8859-1" expires="0">
  <h1>Codings examples</h1>
  <p>
    This page serves as test-case for &lt;mm:import and encoding.
  </p>
    <mm:import id="default1">€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ, Café tweeëntwintig</mm:import>
    <mm:import id="default2" escape="trimmer">
cp1252: €‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ
latin-1: Café tweeëntwintig
    </mm:import>
    <mm:import externid="from">this</mm:import>
    <mm:import externid="field1" from="parameters"><mm:write referid="default1" /></mm:import>
    <mm:import externid="area1"  from="parameters"><mm:write referid="default2" /></mm:import>
    <mm:import externid="field2" from="parameters"><mm:write referid="default1" /></mm:import>
    <mm:import externid="area2"  from="parameters"><mm:write referid="default2" /></mm:import>
    <mm:import externid="field3" from="$from"><mm:write referid="default1" /></mm:import>
    <mm:import externid="area3"  from="$from"><mm:write referid="default2" /></mm:import>
    <mm:import externid="field4" from="$from"><mm:write referid="default1" /></mm:import>
    <mm:import externid="area4"  from="$from"><mm:write referid="default2" /></mm:import>

    <h2>get</h2>
    <form method="get" action="<mm:url />">
      <p>
        <input type="text" name="field1" value="<mm:write referid="field1" />" style="width: 100%;" />
        <textarea name="area1"  style="width: 100%;" rows="3" cols="50"><mm:write referid="area1" /></textarea>
        <input type="hidden" name="field1" value="<mm:write referid="field1" />" style="width: 100%;" /> <!-- submit as list..-->
        <input type="submit" />
      </p>
    </form>
    <h2>post</h2>
    <form method="post" action="<mm:url />">
      <p>
        <input type="text" name="field2" value="<mm:write referid="field2" />" style="width: 100%;" />
        <textarea name="area2"  style="width: 100%;" rows="3" cols="50"><mm:write referid="area2" /></textarea>
        <input type="submit" />
      </p>
    </form>
    <h2>multipart get</h2>
    <form enctype="multipart/form-data" method="get" action="<mm:url />">
      <p>
        <input type="text" name="field3" value="<mm:write referid="field3" />" style="width: 100%;" />
        <textarea name="area3"  style="width: 100%;" rows="3" cols="50"><mm:write referid="area3" /></textarea>
        <input type="hidden" name="from" value="parameters" />
        <input type="submit" />
      </p>
    </form>
    <h2>multipart post</h2>
    <form enctype="multipart/form-data" method="post" action="<mm:url />">
      <p>
        <input type="text" name="field4" value="<mm:write referid="field4" />" style="width: 100%;" />
        <textarea name="area4"  style="width: 100%;" rows="3" cols="50"><mm:write referid="area4" /></textarea>
        <input type="hidden" name="from" value="multipart" />
        <input type="submit" />
       </p>
    </form>
    <p>
      <a href="<mm:url />">Clean up</a>
    </p>
  <hr />
</mm:content>
<address><a href="mailto:Michiel.Meeuwissen@omroep.nl">mihxil'</a></address>
</body>
</html>

