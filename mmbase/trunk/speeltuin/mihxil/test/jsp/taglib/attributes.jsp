<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<mm:import externid="abcde">ABC</mm:import>
<mm:import externid="test" vartype="Integer" >1234</mm:import>

 Should be ABC: <mm:write referid="abcde" /><br />

 <mm:write value="$abcde">
    Should be ABC: <mm:write value="$_" /><br />
 </mm:write>
 <mm:remove referid="abcde" />
 <mm:import externid="abcde">DEF</mm:import>

  Should be DEF: <mm:write value="$abcde" /> (fails in mmbase 1.6.0/resin)<br />

  Should be ABCDEF: <mm:write value="ABC$abcde" /><br />
  
<hr />
<a href="<mm:url page="." />">back </a>
</body>
</html>
